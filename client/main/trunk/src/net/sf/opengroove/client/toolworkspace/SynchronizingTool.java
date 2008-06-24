package net.sf.opengroove.client.toolworkspace;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/*
 * OUTDATED DOCUMENTATION
 * 
 * this class is an extention to tool that makes available to you a File,
 * similar to the storage file, that will be synchronized accross multiple
 * computers. note that synchronization between two computers only occurs when
 * both of them are online at the same time.
 * 
 * instead of calling getStorageFile, you should call getLocalStorageFile and
 * getSyncStorageFile. the local storage file is a folder that does not get
 * synchronized, so it can be used for storing information specific to this
 * computer, such as user preferences. the sync storage file is the folder that
 * gets synchronized.
 * 
 * this class makes available a lock object, which you can synchronize on for
 * actions that would modify the sync folder. it is critical that you
 * synchronize on this object in every location that you would do a file read or
 * write.
 * 
 * in general, synchronization will occur in small steps, so that synchronizing
 * on the sync lock object will never block for long. the intent of this class
 * is to make the wait no longer than 1 second, although it may sometimes be a
 * bit longer when synchronizing a large file.
 * 
 * synchronization is done using a pull-like mechanism. this means that every
 * synchronization is initiated by the user wishing to download changes from
 * another user, not by the user wishing to upload changes to another user.
 * however, when the folder
 */
/**
 * This class is an extention to tool that allows you to synchronize data
 * accross computers with this tool. the data to be synchronized is stored as
 * keys and values. the key and the value can both contain any visible ASCII
 * character, including the forward slash.
 * 
 * instead of calling getStorageFile() to write files that will not be
 * synchronized, you should call getLocalStorageFile().
 * 
 * synchronization is done using a pull-like mechanism. this means that every
 * synchronization is initiated by the user wishing to receive changes from
 * another user, not by the user wishing to send changes. however, when data on
 * a user's computer changes, the computer may send a message to the other
 * computers alerting them that they need to synchronize.
 * 
 * in general, this class will wait a few moments (by default, 2 seconds) before
 * sending out notifications that data has changed after data has really
 * changed. this is so that if a lot of keys are to be changed in quick
 * succession, multiple synchronizations do not occur until after all of the
 * keys have been updated.
 * 
 * synchronization also occurs once every 10 minutes. this is in case some
 * information was missed the last time synchronization occured, for some
 * reason.
 * 
 * synchronization also occurs when the user connects to the internet after
 * being offline.
 * 
 * the subclass of this tool can also start synchronization using the sync()
 * method. this will synchronize with every remote online member of the
 * workspace, as well as send them a message telling them to synchronize too.
 * 
 * synchronization may also be "paused". this does not pause a currently running
 * synchronization, but no synchronizations will occur while synchronization is
 * paused, and any attempts of other computers to synchronize with this computer
 * will be rejected. when synchronization is paused. manual calls to sync() will
 * silently not do anything.
 * 
 * if a conflict is detected between synchronized keys, the newest version is
 * used.
 * 
 * NOTE: receiveMessage() should not be overridden. this is used in
 * synchronizing. for equivalent functionality, use receiveInternalMessage().
 * similarly, don't use sendMessage(), use sendInternalMessage(). also, don't
 * use initialize(), use initializeInternal(). in addition, don't use
 * shutdown(), use shutdownInternal().
 * 
 * ALSO NOTE: you should take into account that operating systems may impose
 * restrictions on the length of filenames. the formula for calculating the size
 * of the largest filename used when persisting a key is as follows:
 * 
 * (KEYNAMELENGTH * 2) + 24
 * 
 * so make sure not to have key names that would cause this formula to yield a
 * larger value then your operating system allows.
 * 
 * @deprecated This tool has been superseded by
 *             net.sf.opengroove.client.toolworkspace.sync.SynchronizingTool. Use
 *             that class instead.
 * 
 * @author Alexander Boyd
 * 
 */
public abstract class SynchronizingTool extends Tool
{
	private boolean isShutdown = false;

	private Thread syncProcessingThread = new Thread()
	{
		public void run()
		{
			while (!isShutdown)
			{
				try
				{
					String userToSync = pendingSynchronizations.take();
					pendingSynchronizations.removeAll(Arrays
							.asList(new String[]
							{ userToSync }));// removes duplicates of this
					// user in the pending sync list, we need to do this instead
					// of remove() because this will remove all instances
					// whereas remove() will only remove one
					syncWith(userToSync);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	};

	private Thread syncLaterProcessingThread = new Thread()
	{
		public void run()
		{
			while (!isShutdown)
			{
				try
				{
					Thread.sleep(20 * 1000);
					if (isShutdown)
						continue;
					String string;
					while ((string = syncWithLater.poll()) != null)
					{
						pendingSynchronizations.add(string);
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	};

	private ArrayBlockingQueue<String> syncMessages = new ArrayBlockingQueue<String>(
			1);

	/**
	 * holds a list of users that the program needs to synchronize with.
	 * syncProcessingThread pops from this and calls syncWith()
	 */
	private ArrayBlockingQueue<String> pendingSynchronizations = new ArrayBlockingQueue<String>(
			500);//

	/**
	 * holds a list of users to sync with later. users are put here if, when
	 * attempting to synchronize, the user reports that they are already engaged
	 * in synchronizing with someone. every 20 seconds, a thread moves these
	 * items to pendingSynchronizations.
	 */
	private ArrayBlockingQueue<String> syncWithLater = new ArrayBlockingQueue<String>(
			500);

	@Override
	public final void initialize()
	{
		syncProcessingThread.start();
		syncLaterProcessingThread.start();
		initializeInternal();
	}

	protected abstract void initializeInternal();

	protected void sendMessageInternal(String from, String message)
	{
		sendMessage(from, "i|" + message);
	}

	@Override
	public void receiveMessage(String from, String message)
	{
		if (message.startsWith("i|"))
		{
			receiveMessageInternal(from, message.substring(2));
		} else if (message.equals("syncrequest"))
		{
			pendingSynchronizations.offer(from);
		} else if (message.startsWith("syncmessage|"))
		{
			syncMessages.offer(message.substring("syncmessage|".length()));
		} else if (message.equals("beginsync"))
		{

		}
	}

	protected abstract void receiveMessageInternal(String from, String string);

	@Override
	public final void shutdown()
	{
		isShutdown = true;
		shutdownInternal();
	}

	protected abstract void shutdownInternal();

	/**
	 * sends a sync message. this is basically the same as sendMessage, but it
	 * prepends syncmessage| to the beginning of this message.
	 * 
	 * @param to
	 * @param message
	 */
	private void sendSyncMessage(String to, String message)
	{
		sendMessage(to, "syncmessage|" + message);
	}

	protected final void sync()
	{

	}

	/*
	 * this should only be called from syncProcessingThread
	 */
	private void syncWith(String username)
	{
		// first, make sure that the other computer isn't synchronizing with
		// anyone right now
		// if we don't get a response, we assume that they are synchronizing, or
		// at any rate, are not available to synchronize with us
		String response;
		sendMessage(username, "beginsync");
		response = pResponse(5000);
		if (response == null)
		{
			return;
		} else if (response.equals("notavailable"))
		{
			syncWithLater.offer(username);
			return;
		} else if (!response.equals("ready"))
		{

		}
	}

	private String pResponse(int i)
	{
		try
		{
			return syncMessages.poll(i, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private void respondToSync(String username)
	{

	}
}
