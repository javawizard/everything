package net.sf.convergia.client.features;

import java.io.File;
import java.util.ArrayList;

import net.sf.convergia.client.InTouch3;
import net.sf.convergia.client.com.Communicator;


public abstract class Feature
{
	private Communicator communicator;

	public Communicator getCommunicator()
	{
		return communicator;
	}

	public void setCommunicator(Communicator communicator)
	{
		this.communicator = communicator;
	}

	/**
	 * returns true if the user currently has an active connection to the
	 * server. when the user unplugs their internet cord, this will change to
	 * false. when the user plugs their internet cord back in, this will change
	 * to true after about 15 seconds.
	 * 
	 * @return
	 */
	protected boolean isOnline()
	{
		return communicator.getCommunicator().isActive();
	}

	/**
	 * receives a message from another user that uses this workspace.
	 * 
	 * @param from
	 * @param message
	 */
	public abstract void receiveMessage(String from, String message);

	/**
	 * sends a message. on the recipient's computer, receiveMessage will be
	 * called.
	 * 
	 * @param to
	 * @param message
	 */
	protected void sendMessage(String to, String message)
	{
		if (message.contains("\r") || message.contains("\n"))
			throw new IllegalArgumentException(
					"that message contains newlines, which isn't allowed.");
		if (message.length() > 145 * 1000)
			throw new IllegalArgumentException(
					"messages cannot be longer than 145,000 bytes");
		communicator.sendMessage(to, "fm|f|" + getRegisteredType()
				+ "|imessage|" + message);
	}

	/**
	 * same as sendMessage, but it sends it to all online users. Currently, this
	 * just calls sendMessage() for every member of getOnlineUsers(), so it
	 * offers no additional data transfer efficiency benefits.
	 * 
	 * @param message
	 */
	protected void broadcastMessage(String message)
	{
		for (String u : listOnlineUsers())
		{
			try
			{
				sendMessage(u, message);
			} catch (Exception ex1)
			{
				ex1.printStackTrace();
			}
		}
	}

	/**
	 * indicates that a user has signed on or off. the user may not be a member
	 * of this workspace, so no changes may have actually occured that concern
	 * this workspace.
	 * 
	 */
	public abstract void userStatusChanged();

	/**
	 * returns this user's username. if this is the same as getCreator(), then
	 * this is the user who created the workspace.
	 * 
	 * @return
	 */
	protected String getUsername()
	{
		return InTouch3.username;
	}

	/**
	 * lists all members of this workspace.
	 * 
	 * @return
	 */
	protected String[] listUsers()
	{
		return communicator.allUsers.toArray(new String[0]);
	}

	/**
	 * lists all members of this workspace who are online.
	 * 
	 * @return
	 */
	protected String[] listOnlineUsers()
	{
		return communicator.onlineUsers.toArray(new String[0]);
	}

	/**
	 * lists all members of this workspace who are offline.
	 * 
	 * @return
	 */
	protected String[] listOfflineUsers()
	{
		return communicator.offlineUsers.toArray(new String[0]);
	}

	/**
	 * lists the folder that this workspace can use for data storage. it is not
	 * required to use this folder (if it is a folder sync workspace, for
	 * example, it would probably only use this folder to hold configuration
	 * such as which folder to synchronize)
	 * 
	 * @return
	 */
	protected File getStorageFile()
	{
		wrapper.getDatastore().mkdirs();
		return wrapper.getDatastore();
	}

	/**
	 * initializes this workspace. this is called for each workspace that the
	 * user is a member of when the user runs Convergia. if this method throws
	 * an exception, the exception will be printed to stderr, but the workspace
	 * will continue to be used.
	 * 
	 */
	public abstract void initialize();

	/**
	 * gets the registered type of the workspace. this is usually specified in
	 * the workspace plugin descriptor, so it is usually known anyway.
	 * 
	 * @return
	 */
	protected String getRegisteredType()
	{
		return wrapper.getTypeId();
	}

}
