package net.sf.opengroove.client.features;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

import net.sf.opengroove.client.Convergia;
import net.sf.opengroove.client.com.Communicator;

public abstract class Feature
{
	private Properties pluginMetadata;

	String typeId;

	private Communicator communicator;

	public Communicator getCommunicator()
	{
		return communicator;
	}

	void setCommunicator(Communicator communicator)
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

	void receiveMessage0(String from, String message)
	{
		if (message.startsWith("imessage|"))
			receiveMessage(from, message.substring("imessage|".length()));
	}

	/**
	 * same as sendMessage, but it sends it to all online users. Currently, this
	 * just calls sendMessage() for every member of getOnlineUsers(), so it
	 * offers no additional data transfer efficiency benefits. in the future, it
	 * will actually call a different server command so that the message is only
	 * sent once to the server.
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
	 * indicates that a user has signed on or off. since features don't have a
	 * specific list of users assigned to them, this method is called when any
	 * known user signs on or off. a known user is a user used in some part of
	 * the system. currently, this is the local user's contacts and any users in
	 * the local user's workspaces.
	 * 
	 */
	public abstract void userStatusChanged();

	/**
	 * returns this user's username.
	 * 
	 * @return
	 */
	protected String getUsername()
	{
		return Convergia.username;
	}

	/**
	 * lists all users signed up for Convergia. this list could be potentially
	 * large, so this method should not be called frequently.
	 * 
	 * @return
	 */
	protected String[] listUsers()
	{
		return communicator.allUsers.toArray(new String[0]);
	}

	/**
	 * lists all users who are online.
	 * 
	 * @return
	 */
	protected String[] listOnlineUsers()
	{
		return communicator.onlineUsers.toArray(new String[0]);
	}

	/**
	 * lists all users who are offline.
	 * 
	 * @return
	 */
	protected String[] listOfflineUsers()
	{
		return communicator.offlineUsers.toArray(new String[0]);
	}

	/**
	 * returns the folder that the Feature can use for local storage.<br/>
	 * <Br/> <b>Note:</b> Even if the feature is uninstalled and then
	 * reinstalled, this folder will still have the same contents. It is
	 * therefore important to include some sort of option in the UI that allows
	 * the user to clear all data associated with this Feature.
	 * 
	 * @return
	 */
	protected File getStorageFile()
	{
		File f = new File(FeatureManager.getBaseStorageFolder(),
				getRegisteredType());
		if (!f.exists())
			f.mkdirs();
		return f;
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
	protected final String getRegisteredType()
	{
		return typeId;
	}

	Properties getPluginMetadata()
	{
		return pluginMetadata;
	}

	void setPluginMetadata(Properties pluginMetadata)
	{
		this.pluginMetadata = pluginMetadata;
	}

	void setTypeId(String typeId)
	{
		this.typeId = typeId;
	}

	protected <T> void registerComponent(String handler, T component)
	{
		FeatureManager.registerComponent(handler, component);
	}

}
