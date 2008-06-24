package net.sf.opengroove.client.com;

import java.awt.Window;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.sf.opengroove.client.OpenGroove;
import net.sf.opengroove.client.SubversionFileFilter;
import net.sf.opengroove.client.workspace.SetList;


/**
 * this class represents a high-level communicator that wraps a low-level
 * communicator.
 * 
 * @author Alexander Boyd
 * 
 */
public class Communicator
{
	public LowLevelCommunicator communicator;

	public List<String> onlineUsers = SetList.cs(String.class);

	public List<String> offlineUsers = SetList.cs(String.class);

	public List<String> allUsers = SetList.cs(String.class);

	public String userStatusHash = "";

	private static File cacheFile = new File("appdata/hcinfCache")
			.getAbsoluteFile();

	private static File allUsersCache = new File(cacheFile, "hAllUsersCache")
			.getAbsoluteFile();

	private static File userMdCache = new File(cacheFile, "hUserMetadataCache")
			.getAbsoluteFile();

	public Communicator(LowLevelCommunicator communicator)
	{
		cacheFile.mkdirs();
		allUsersCache.mkdirs();
		userMdCache.mkdirs();
		cacheFile.setWritable(true);
		allUsersCache.setWritable(true);
		userMdCache.setWritable(true);
		this.communicator = communicator;
		onlineUsers.clear();
		allUsers.addAll(Arrays.asList(listObjectContentsAsArray(allUsersCache,
				String.class)));
		offlineUsers.addAll(allUsers);
		communicator.addSink(new LowLevelMessageSink()
		{

			public void process(String command, String arguments)
			{
				if (command.equalsIgnoreCase(execCommand))
				{
					execCache = arguments;
				}
				/*
				 * System.out .println("received line from the server to
				 * process: command was " + command + " and arguments are " +
				 * arguments.substring(0, Math.min(arguments .length(), 255)));
				 * 
				 */if (command.equalsIgnoreCase("RECEIVEMESSAGE"))
				{
					int sIndex = arguments.indexOf(" ");
					String from = arguments.substring(0, sIndex);
					String message = arguments.substring(sIndex + 1);
					for (MessageSink sink : listSinks())
					{
						sink.process(from, message);
					}
				} else if (command.equalsIgnoreCase("LISTALL"))
				{
					List<String> newUsers = Arrays.asList(arguments
							.split("\\s+"));// split around all white space
					if (newUsers.size() == 1
							&& newUsers.get(0).trim().equalsIgnoreCase(""))
					{
						allUsers.clear();
					}
					allUsers.addAll(newUsers);
					allUsers.retainAll(newUsers);
					setFolderContents(allUsersCache, allUsers);
					for (StatusListener listener : listStatusListeners())
					{
						listener.allUsersUpdated(Communicator.this);
						listener.anyUsersUpdated(Communicator.this);
					}
				} else if (command.equalsIgnoreCase("LISTONLINE"))
				{
					List<String> newUsers = Arrays.asList(arguments
							.split("\\s+"));// split around all white space
					if (newUsers.size() == 1
							&& newUsers.get(0).trim().equalsIgnoreCase(""))
					{
						onlineUsers.clear();
					}
					onlineUsers.addAll(newUsers);
					onlineUsers.retainAll(newUsers);
					for (StatusListener listener : listStatusListeners())
					{
						listener.onlineUsersUpdated(Communicator.this);
						listener.anyUsersUpdated(Communicator.this);
					}
				} else if (command.equalsIgnoreCase("LISTOFFLINE"))
				{
					List<String> newUsers = Arrays.asList(arguments
							.split("\\s+"));// split around all white space
					if (newUsers.size() == 1
							&& newUsers.get(0).trim().equalsIgnoreCase(""))
					{
						offlineUsers.clear();
					}
					offlineUsers.addAll(newUsers);
					offlineUsers.retainAll(newUsers);
					for (StatusListener listener : listStatusListeners())
					{
						listener.offlineUsersUpdated(Communicator.this);
						listener.anyUsersUpdated(Communicator.this);
					}
				} else if (command.equalsIgnoreCase("GETUSERSTATUSHASH"))
				{
					String oldHash = userStatusHash;
					userStatusHash = arguments;
					for (StatusListener listener : listStatusListeners())
					{
						listener.userStatusHashUpdated(Communicator.this);
					}
					if (!oldHash.equals(arguments))
						for (StatusListener listener : listStatusListeners())
						{
							listener.userStatusHashChanged(Communicator.this);
						}
				} else if (command.equalsIgnoreCase("GETUSERMETADATA"))
				{
					userCacheMetadata = arguments;
				} else if (command.equalsIgnoreCase("GETTIME"))
				{
					serverTimeLatency = System.currentTimeMillis()
							- Long.parseLong(arguments);
				} else if (command.equalsIgnoreCase("USERHERE"))
				{
					for (StatusListener listener : listStatusListeners())
					{
						listener.userHere(Communicator.this, arguments);
					}
				} else if (command.equalsIgnoreCase("USERGONE"))
				{
					for (StatusListener listener : listStatusListeners())
					{
						listener.userGone(Communicator.this, arguments);
					}
				}
			}
		});
		try
		{
			Thread.sleep(400);
		} catch (InterruptedException e)
		{
			// TODO Nov 27, 2007 Auto-generated catch block
			throw new RuntimeException("TODO auto generated on Nov 27, 2007 : "
					+ e.getClass().getName() + " - " + e.getMessage(), e);
		}
	}

	protected synchronized <T extends Serializable> void setFolderContents(
			File folder, List<T> list)
	{
		recursiveDelete(folder);
		folder.mkdirs();
		for (T e : new ArrayList<T>(list))
		{
			writeObjectToFile(e, new File(folder, e.toString()));
		}
	}

	public void reloadTimeLatency()
	{
		try
		{
			communicator.sendMessage("GETTIME");
		} catch (IOException e)
		{
			// TODO Nov 27, 2007 Auto-generated catch block
			throw new RuntimeException("TODO auto generated on Nov 27, 2007 : "
					+ e.getClass().getName() + " - " + e.getMessage(), e);
		}
	}

	public void reloadOnlineUsers()
	{
		try
		{
			communicator.sendMessage("LISTONLINE");
		} catch (IOException e)
		{
			// TODO Nov 27, 2007 Auto-generated catch block
			throw new RuntimeException("TODO auto generated on Nov 27, 2007 : "
					+ e.getClass().getName() + " - " + e.getMessage(), e);
		}
	}

	public void reloadOfflineUsers()
	{
		try
		{
			communicator.sendMessage("LISTOFFLINE");
		} catch (IOException e)
		{
			// TODO Nov 27, 2007 Auto-generated catch block
			throw new RuntimeException("TODO auto generated on Nov 27, 2007 : "
					+ e.getClass().getName() + " - " + e.getMessage(), e);
		}
	}

	public void reloadAllUsers()
	{
		try
		{
			communicator.sendMessage("LISTALL");
		} catch (IOException e)
		{
			// TODO Nov 27, 2007 Auto-generated catch block
			throw new RuntimeException("TODO auto generated on Nov 27, 2007 : "
					+ e.getClass().getName() + " - " + e.getMessage(), e);
		}
	}

	public void reloadUserStatusHash()
	{
		try
		{
			communicator.sendMessage("GETUSERSTATUSHASH");
		} catch (IOException e)
		{
			// TODO Nov 27, 2007 Auto-generated catch block
			throw new RuntimeException("TODO auto generated on Nov 27, 2007 : "
					+ e.getClass().getName() + " - " + e.getMessage(), e);
		}
	}

	/*
	 * protected void notifyStatus(String string) {
	 * System.out.println("notifying for status " + string); for (StatusListener
	 * listener : listStatusListeners()) { try {
	 * listener.getClass().getDeclaredMethod(string, new Class[] { getClass()
	 * }).invoke(listener, new Object[] { this }); } catch (Exception e) {
	 * e.printStackTrace(); } } }
	 */
	private ArrayList<MessageSink> sinks = new ArrayList<MessageSink>();

	public void addSink(MessageSink sink)
	{
		sinks.add(sink);
	}

	public void removeSink(MessageSink sink)
	{
		sinks.remove(sink);
	}

	public MessageSink[] listSinks()
	{
		return sinks.toArray(new MessageSink[0]);
	}

	private ArrayList<StatusListener> statusListeners = new ArrayList<StatusListener>();

	public void addStatusListener(StatusListener listener)
	{
		statusListeners.add(listener);
	}

	public void removeStatusListener(StatusListener listener)
	{
		statusListeners.remove(listener);
	}

	public StatusListener[] listStatusListeners()
	{
		return statusListeners.toArray(new StatusListener[0]);
	}

	private String userCacheMetadata = null;

	// kind of a misnomer because this is actually how fast our clock is instead
	// of how slow.
	private long serverTimeLatency = 0;

	/**
	 * gets the metadata for the specified user.
	 * 
	 * @param username
	 *            the username
	 * @return
	 */
	public synchronized String getUserMetadata(String username)
	{
		if (!communicator.isActive())
			return (String) readObjectFromFile(new File(userMdCache, username));
		userCacheMetadata = null;
		while (userCacheMetadata == null)
		{
			try
			{
				System.out.println("requesting user metadata");
				communicator.sendMessage("GETUSERMETADATA " + username);
				System.out.println("requested");
			} catch (IOException e)
			{
				// TODO Nov 27, 2007 Auto-generated catch block
				throw new RuntimeException(
						"TODO auto generated on Nov 27, 2007 : "
								+ e.getClass().getName() + " - "
								+ e.getMessage(), e);
			}
			System.out.println("waiting for user metadata");
			for (int i = 0; i < 1000; i++)
			{
				if (userCacheMetadata != null)
					break;
				try
				{
					Thread.sleep(10);
				} catch (InterruptedException e)
				{
					// TODO Nov 27, 2007 Auto-generated catch block
					throw new RuntimeException(
							"TODO auto generated on Nov 27, 2007 : "
									+ e.getClass().getName() + " - "
									+ e.getMessage(), e);
				}
			}
			System.out.println("wait completed");
			if (!communicator.isActive())
				return (String) readObjectFromFile(new File(userMdCache,
						username));
		}
		System.out.println("found metadata");
		String metadata = userCacheMetadata;
		userCacheMetadata = null;
		System.out.println("returning metadata");
		if (metadata.startsWith("OK"))
		{
			writeObjectToFile(metadata.substring(3), new File(userMdCache,
					username));
			return metadata.substring(3);
		}
		return null;
	}

	/**
	 * sets my metadata to the specified value.
	 */
	public synchronized void setUserMetadata(String metadata)
	{
		try
		{
			communicator.sendMessage("SETUSERMETADATA " + metadata);
		} catch (IOException e)
		{
			// TODO Nov 27, 2007 Auto-generated catch block
			throw new RuntimeException("TODO auto generated on Nov 27, 2007 : "
					+ e.getClass().getName() + " - " + e.getMessage(), e);
		}
	}

	public void sendMessage(String to, String message)
	{
		try
		{
			communicator.sendMessage("SENDMESSAGE " + to + " " + message);
		} catch (IOException e)
		{
			// TODO Nov 27, 2007 Auto-generated catch block
			throw new RuntimeException("TODO auto generated on Nov 27, 2007 : "
					+ e.getClass().getName() + " - " + e.getMessage(), e);
		}
	}

	public LowLevelCommunicator getCommunicator()
	{
		return communicator;
	}

	public long getServerTime()
	{
		return System.currentTimeMillis() - serverTimeLatency;
	}

	private static void writeObjectToFile(Serializable object, File file)
	{
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(file));
			oos.writeObject(object);
			oos.flush();
			oos.close();
		} catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

	private static Serializable readObjectFromFile(File file)
	{
		try
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
					file));
			Object object = ois.readObject();
			ois.close();
			return (Serializable) object;
		} catch (Exception ex)
		{
			return null;
		}
	}

	private static void recursiveDelete(File transmissionFolder)
	{
		if (transmissionFolder.isDirectory())
		{
			for (File file : transmissionFolder.listFiles())
			{
				recursiveDelete(file);
			}
		}
		transmissionFolder.delete();
	}

	private static int cIdVar = 0;

	public static synchronized String createIdentifier()
	{
		return "i" + System.currentTimeMillis() + "z" + cIdVar++;
	}

	/**
	 * lists all unread messages, messages that are in mInCache.
	 * 
	 * @return
	 */
	/*
	 * public static synchronized InstantMessage[] listUnreadMessages() { return
	 * listObjectContentsAsArray(mInCache, InstantMessage.class); }
	 */
	/**
	 * lists all messages in the archive. these are both incoming and outgoing
	 * messages. it is best to trim the archive when it gets excessively large.
	 * the messages are listed in chronological order, oldest first. you may
	 * want to reverse the ordering (see Collections.reverse()) of the messages,
	 * to show newest first, before presenting the archive to the end user.
	 * 
	 * @return
	 */
	/*
	 * public static synchronized InstantMessage[] listArchivedMessages() {
	 * return listObjectContentsAsArray(mArchive, InstantMessage.class); }
	 */
	/**
	 * marks this message as read, in otherwords, moves this message from
	 * mInCache to mArchive.
	 * 
	 * @param messageId
	 */
	/*
	 * public static synchronized void markRead(String messageId) { if (!new
	 * File(mInCache, messageId).renameTo(new File(mArchive, messageId))) throw
	 * new RuntimeException("could not move message " + messageId + " to the
	 * message archive."); }
	 */
	/**
	 * deletes the message. the message must be in mArchive, in otherwords, if
	 * it is an outgoing message, it must already have been sent, and if it is
	 * an incoming message, it must already have been marked read.
	 * 
	 * @param messageId
	 */
	/*
	 * public static synchronized void deleteMessage(String messageId) {
	 * InstantMessage message = (InstantMessage) readObjectFromFile(new File(
	 * mArchive, messageId)); if (message.isHasAttachments()) {
	 * recursiveDelete(new File(mAttachments, message
	 * .getAttachmentSetIdentifier())); } recursiveDelete(new File(mArchive,
	 * messageId)); }
	 * 
	 * public static synchronized InstantMessage[] listOutgoingMessages() {
	 * return listObjectContentsAsArray(mOutCache, InstantMessage.class); }
	 * 
	 * public static synchronized void markSent(String messageId) { if (!new
	 * File(mOutCache, messageId).renameTo(new File(mArchive, messageId))) throw
	 * new RuntimeException("could not move message " + messageId + " to the
	 * message archive."); }
	 */
	/**
	 * returns the real storage locations of the attachments on this message.
	 * NOTE that the filenames ARE NOT the actual names of the attachments. the
	 * filenames are the names of the attachments, prefixed with a "d" if the
	 * file is a zip file and was originally sent as a directory, or "f" if it
	 * is a regular attachment file.
	 * 
	 * @param attachmentSetIdentifier
	 * @return
	 */
	/*
	 * public static synchronized File[] listAttachmentStorageFiles( String
	 * attachmentSetIdentifier) { return new File(mAttachments,
	 * attachmentSetIdentifier).listFiles(); }
	 */
	public static <T> HashMap<String, T> listObjectContents(File folder,
			Class<T> c)
	{
		HashMap<String, T> map = new HashMap<String, T>();
		for (File file : folder.listFiles(new SubversionFileFilter()))
		{
			if (file.isFile())
				map.put(file.getName(), (T) readObjectFromFile(file));
		}
		return map;
	}

	/**
	 * lists the contents of the specified folder. first, all files of this
	 * folder are listed. then, an array is created, that is the same length as
	 * those files. the array element type is the class specified in this
	 * method. then, for each file, the file is deserialized (the contents of
	 * the file are read as an object through an ObjectInputStream created on
	 * that file), and added to the array. then the array is returned.
	 * 
	 * @param folder
	 *            the folder to list
	 * @param c
	 *            the class of the objects to be deserialized, also the class of
	 *            the return array.
	 * @return an array of deserialized objects, one for each file in this
	 *         folder.
	 * @throws ClassCastException
	 *             if the deserialized objects from any of the files are not
	 *             instances of class c.
	 */
	public static <T> T[] listObjectContentsAsArray(File folder, Class<T> c)
	{
		return listObjectContents(folder, c).values().toArray(
				(T[]) Array.newInstance(c, 0));
	}

	private String execCache = null;

	private String execCommand = null;

	/**
	 * executes a command, then waits for a response from the server for that
	 * command. if a response is not received within 5 seconds, an exception is
	 * thrown.
	 * 
	 * @param command
	 * @return
	 */
	private synchronized String execCommand(String command, String arguments)
	{
//		System.out.println("executing " + command + " with arguments " + (arguments.length() > 256 ? arguments.substring(0,255) : arguments));
		execCommand = command;
		execCache = null;
		if (!communicator.isActive())
			throw new IllegalStateException(
					"The communicator is not connected to the internet");
		try
		{
			communicator.sendMessage(command
					+ (arguments == null || "".equals(arguments) ? "" : " "
							+ arguments));
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		System.out.println("sent, waiting for receipt");
		for (int i = 0; i < 1000; i++)
		{
			if (execCache != null)
				break;
			try
			{
				Thread.sleep(10);
			} catch (InterruptedException e)
			{
				// TODO Jan 8, 2008 Auto-generated catch block
				throw new RuntimeException(
						"TODO auto generated on Jan 8, 2008 : "
								+ e.getClass().getName() + " - "
								+ e.getMessage(), e);
			}
		}
//		System.out.println("finished waiting. response was " + (execCache.length() > 64 ? execCache.substring(0,63) : execCache) + " (possibly truncated)");
		if (execCache == null)
			throw new RuntimeException(
					"A response was not received within 5 seconds");
		execCommand = null;
		return execCache;
	}

	public void createWorkspace(String workspaceId)
	{
		if (!execCommand("CREATEWORKSPACE", workspaceId).startsWith("OK"))
			throw new RuntimeException("Creating the workspace failed");
	}

	public void setWorkspacePermissions(String workspaceId, String[] users)
	{
		String commandString = workspaceId + " "
				+ OpenGroove.delimited(Arrays.asList(users), " ");
		if (!execCommand("SETWORKSPACEPERMISSIONS", commandString).startsWith(
				"OK"))
			throw new RuntimeException("Setting permissions failed");
	}

	public void deleteWorkspace(String workspaceId)
	{
		if (!execCommand("DELETEWORKSPACE", workspaceId).startsWith("OK"))
			throw new RuntimeException("deleting the workspace failed");
	}

	/**
	 * same as canAccess, but throws an exception if access is not allowed
	 * instead of returning false.
	 * 
	 * @param workspaceId
	 */
	public void checkAccess(String workspaceId)
	{
		if (!canAccess(workspaceId))
			throw new RuntimeException(
					"You do not have permissions to access this workspace");
	}

	public boolean canAccess(String workspaceId)
	{
		return execCommand("CANACCESS", workspaceId).startsWith("OK");
	}

	/**
	 * returns the property, or null if it doesn't exist. an exception will be
	 * thrown only if the server cannot be connected to.
	 * 
	 * @param workspaceId
	 * @param key
	 * @return
	 */
	public String getWorkspaceProperty(String workspaceId, String key)
	{
		String command = workspaceId + " " + key;
		String response = execCommand("GETWORKSPACEPROPERTY", command);
		if (!response.startsWith("OK"))
			return null;
		return response.substring(3);
	}

	public void setWorkspaceProperty(String workspaceId, String key,
			String value)
	{
		String request = workspaceId + " " + key + (value == null ? "" : " " + value);
		if (!execCommand("SETWORKSPACEPROPERTY", request).startsWith("OK"))
			throw new RuntimeException("could not set workspace property");
	}

	/**
	 * lists all properties in the workspace. if the prefix is not null, only
	 * properties whos key starts with that prefix will be returned. if prefix
	 * is null, all properties will be returned. <br/><br/> the returned value
	 * will never be null. if there are no properties, the returned value will
	 * be a String[] with length 0. if a problem occurs with getting the
	 * properties, an exception will be thrown.
	 * 
	 * @param workspaceId
	 * @param prefix
	 * @return a String[] of the keys of all of the properties that matched the
	 *         prefix, or the keys of all of the properties in the workspace if
	 *         prefix was null.
	 */
	public String[] listWorkspaceProperties(String workspaceId, String prefix)
	{
		String request = workspaceId + (prefix == null ? "" : " " + prefix);
		String response = execCommand("LISTWORKSPACEPROPERTIES", request);
		if (!response.startsWith("OK"))
			throw new RuntimeException(
					"Could not successfully get workspace properties list.");
		response = response.substring(2);
		response = response.trim();
		String[] items = response.split("\n");
		for(int i = 0; i < items.length; i++)
		{
			items[i] = items[i].trim();
		}
		if (items.length == 1 && items[0].equals(""))
			return new String[0];
		return items;
	}
}
