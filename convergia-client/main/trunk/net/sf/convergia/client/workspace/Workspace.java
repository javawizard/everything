package net.sf.convergia.client.workspace;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JFrame;

import net.sf.convergia.client.Convergia;
import net.sf.convergia.client.Storage;
import net.sf.convergia.client.com.Communicator;
import net.sf.convergia.client.notification.TaskbarNotification;
import net.sf.convergia.client.plugins.Plugin;

import base64.Base64Coder;

public abstract class Workspace implements Serializable
{
	private static final long serialVersionUID = -6525261980589892668L;

	private WorkspaceWrapper wrapper;

	private Communicator communicator;

	private boolean needsAttention;

	private boolean hasNewInformation;

	// this is the metadata for the plugin that the workspace was loaded from
	private Properties pluginMetadata;

	/**
	 * returns this workspace's unique identifier.
	 * 
	 * @return
	 */
	protected String getId()
	{
		return wrapper.getId();
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
		if (message.length() > 997 * 1000)
			throw new IllegalArgumentException(
					"messages cannot be longer than 997,000 bytes");
		if (!wrapper.getParticipants().contains(to))
			throw new IllegalArgumentException("that user (" + to
					+ ") isn't a participant of this workspace");
		communicator.sendMessage(to, "wsi|w|" + wrapper.getId() + "|imessage|"
				+ message);
	}

	/**
	 * same as sendMessage, but it sends it to all online users (excluding yourself). Currently, this
	 * just calls sendMessage() for every member of getOnlineUsers() that is not equal to getUsername(), so it
	 * offers no additional data transfer efficiency benefits.
	 * 
	 * @param message
	 */
	protected void broadcastMessage(String message)
	{
		for (String u : listOnlineUsers())
		{
			if(!u.equals(getUsername()))
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
	 * returns the user that initially created this workspace.
	 * 
	 * @return
	 */
	protected String getCreator()
	{
		return WorkspaceManager.getWorkspaceCreator(wrapper.getId());
	}

	/**
	 * returns this user's username. if this is the same as getCreator(), then
	 * this is the user who created the workspace.
	 * 
	 * @return
	 */
	protected String getUsername()
	{
		return Convergia.username;
	}

	/**
	 * indicates that the user took some action to activate this workspace, such
	 * as clicking on it's name in the launchbar.
	 * 
	 */
	public abstract void userActivate();

	/**
	 * lists all members of this workspace.
	 * 
	 * @return
	 */
	protected String[] listUsers()
	{
		return wrapper.getParticipants().toArray(new String[0]);
	}

	/**
	 * lists all members of this workspace who are online.
	 * 
	 * @return
	 */
	protected String[] listOnlineUsers()
	{
		ArrayList<String> list = new ArrayList<String>(wrapper
				.getParticipants());
		list.retainAll(communicator.onlineUsers);
		return list.toArray(new String[0]);
	}

	/**
	 * lists all members of this workspace who are offline.
	 * 
	 * @return
	 */
	protected String[] listOfflineUsers()
	{
		ArrayList<String> list = new ArrayList<String>(wrapper
				.getParticipants());
		list.retainAll(communicator.offlineUsers);
		return list.toArray(new String[0]);
	}

	/**
	 * lists all members who are allowed to access this workspace. this may
	 * include users not included in listUsers(), which means that the user in
	 * question is allowed (via user specification) to use this workspace but
	 * has not chosen to do so. for example, when the workspace creator adds a
	 * user to the list of allowed users, it shows up here. when the user
	 * chooses to import or participate in this workspace, the user shows up in
	 * this list and listUsers().
	 * 
	 * @return
	 */
	protected String[] listAllowedUsers()
	{
		return wrapper.getAllowedUsers().toArray(new String[0]);
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
	 * if this is true, then the workspace is somehow marked as needing the
	 * users immediate (or quick) attention. this would usually involve
	 * displaying a warning icon next to the workspace, or bolding it, and
	 * displaying a balloon over the tray icon.
	 * 
	 * @param attention
	 *            true if the workspace needs attention, false to clear the
	 *            attention needed status
	 */
	protected void setAttentionStatus(boolean attention)
	{
		this.needsAttention = attention;
		Convergia.reloadLaunchbarWorkspaces();
	}

	/**
	 * indicates that the user should be notified that the workspace has new
	 * information, or otherwise requires the users (non-urgent) attention. this
	 * is pretty much the same as setAttentionStatus, except that this means
	 * that attention is not immediately required, inotherwords, this is not as
	 * urgent.
	 * 
	 * @param newInfo
	 *            true if this workspace has new info, or otherwise requires the
	 *            user's (non-immediate) attention, false to clear the new info
	 *            status
	 */
	protected void setNewInformationStatus(boolean newInfo)
	{
		this.hasNewInformation = newInfo;
		Convergia.reloadLaunchbarWorkspaces();
	}

	/**
	 * returns true if this user is the creator of the workspace, false
	 * otherwise.
	 * 
	 * @return
	 */
	protected boolean isCreator()
	{
		return wrapper.isMine();
	}

	/**
	 * gets the version of the workspace that i am running. this is specified in
	 * the plugin descriptor for this workspace, in the version property. this
	 * can be used to send a message to the workspace creator or other computer
	 * to check that this workspace plugin is up-to-date enough to be used for
	 * this workspace.
	 * 
	 * if the plugin descriptor file does not specify a version, this method
	 * returns null.
	 * 
	 * @return
	 */
	protected String getMyVersion()
	{
		return pluginMetadata.getProperty("version");
	}

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

	/**
	 * this method shows the configuration window. the configuration window has
	 * a tabbed pane in it. it has a few built-in tabs, such as choosing the
	 * workspace's name, choosing allowed users, inviting users, etc. then it
	 * puts one tab for each entry in getConfigurationComponents() in the config
	 * window. the config window has an OK button (currently no cancel button).
	 * the configuration window can also be shown by an option in the launchbar.
	 * 
	 * if the configuration could not be shown, typically because another dialog
	 * is currently showing on the screen, false is returned.
	 */
	protected boolean showConfigurationWindow()
	{
		return Convergia.showConfigWindow(wrapper);
	}

	/**
	 * this method shows the configuration window. the configuration window has
	 * a tabbed pane in it. it has a few built-in tabs, such as choosing the
	 * workspace's name, choosing allowed users, inviting users, etc. then it
	 * puts one tab for each entry in getConfigurationComponents() in the config
	 * window. the config window has an OK button (currently no cancel button).
	 * the configuration window can also be shown by an option in the launchbar.
	 * 
	 * if the configuration could not be shown, typically because another dialog
	 * is currently showing on the screen, false is returned.
	 * 
	 * this method is the same as showConfigurationWindow with no arguments,
	 * except that instead of the configuration dialog being owned by the
	 * launchbar, it is owned by the frame passed in.
	 */
	protected boolean showConfigurationWindow(JFrame frame)
	{
		return Convergia.showConfigWindow(wrapper, frame);
	}

	/**
	 * gets the configuration tabs that should be added to the configuration
	 * window, in addition to the default tab. the key of each entry is the name
	 * of the tab to show and the value is the JComponent (usually a JPanel) to
	 * show inside the tabbed pane, when the tab is shown.
	 * 
	 * @return
	 */
	public abstract Map<String, JComponent> getConfigurationComponents();

	/**
	 * indicates that the user clicked the OK button in the configuration
	 * window. settings in the JComponents returned from
	 * getConfigurationComponents should be saved.
	 * 
	 */
	public abstract void configurationSaved();

	/**
	 * NOT USED RIGHT NOW. when used, this method will be called when the cancel
	 * button is clicked in the configuration window. the configuration window
	 * doesn't have a cancel button right now, so this method is unused.
	 */
	public abstract void configurationCancelled();

	/**
	 * returns the name of this workspace. this may be configured by setName, or
	 * the user can configure it using an option in Convergia.
	 * 
	 * @return
	 */
	protected String getName()
	{
		return wrapper.getName();
	}

	/**
	 * sets the name of this workspace. this is the name that shows up in the
	 * launchbar. usually, this is configured by the user (Convergia takes care
	 * of that), but if the workspace is unnamed (see isUnnamed()), then this
	 * method could check what the creator named it and set it's name to that.
	 * 
	 * @param name
	 *            the name to set (this method may take a bit to return, by
	 *            which time the name will show up on the launchbar)
	 */
	protected void setName(String name)
	{
		wrapper.setName(name);
		Convergia.reloadLaunchbarWorkspaces();
		save();
	}

	/**
	 * stores this workspace's info to disk. this generally should be called
	 * only from within Convergia itself, and not by the workspace
	 * implementation.
	 * 
	 */
	public void save()
	{
		Storage.addOrUpdateWorkspace(wrapper);
	}

	/**
	 * returns true if the name of this workspace is the name that the system
	 * gives to workspaces when it first downloads them or creates them.
	 * 
	 * @return
	 */
	protected boolean isUnnamed()
	{
		return wrapper.getName().equalsIgnoreCase(
				Convergia.WORKSPACE_DEFAULT_NAME);
	}

	/**
	 * returns the information for this workspace, set by the user specified, or
	 * null if that user hasn't specified any info. this should not be called
	 * frequently, as it incures a request to the server.
	 * 
	 * @return
	 */
	protected String getInfo(String user)
	{
		return Base64Coder.decodeString(WorkspaceManager.parseMetadata(
				communicator.getUserMetadata(user)).getProperty(
				"workspace_" + getId() + "_info"));
	}

	/**
	 * sets the info for this workspace. this is your personal info, and any
	 * member of the workspace can read it. actually, anyone, with a bit of
	 * skill at coding, can read this even if they are not a member of a
	 * workspace, as long as they have an Convergia account.
	 * 
	 * @param info
	 */
	protected void setInfo(String info)
	{
		info = Base64Coder.encodeString(info);
		wrapper.setInfo(Convergia.username, info);
		save();
		Convergia.updateMetadata();
	}

	protected String getProperty(String key)
	{
		return communicator.getWorkspaceProperty(wrapper.getId(), key);
	}

	/**
	 * keys can only contain ascii visible chars, but not / or \ or .
	 */
	protected void setProperty(String key, String value)
	{
		communicator.setWorkspaceProperty(wrapper.getId(), key, value);
	}

	protected String[] listProperties()
	{
		return listProperties(null);
	}

	protected String[] listProperties(String prefix)
	{
		return communicator.listWorkspaceProperties(wrapper.getId(), prefix);
	}

	Communicator getCommunicator()
	{
		return communicator;
	}

	void setCommunicator(Communicator communicator)
	{
		this.communicator = communicator;
	}

	WorkspaceWrapper getWrapper()
	{
		return wrapper;
	}

	void setWrapper(WorkspaceWrapper wrapper)
	{
		this.wrapper = wrapper;
	}

	public boolean isHasNewInformation()
	{
		return hasNewInformation;
	}

	void setHasNewInformation(boolean hasNewInformation)
	{
		this.hasNewInformation = hasNewInformation;
	}

	public boolean isNeedsAttention()
	{
		return needsAttention;
	}

	void setNeedsAttention(boolean needsAttention)
	{
		this.needsAttention = needsAttention;
	}

	Properties getPluginMetadata()
	{
		return pluginMetadata;
	}

	void setPluginMetadata(Properties pluginMetadata)
	{
		this.pluginMetadata = pluginMetadata;
	}

	/**
	 * shuts down this workspace. there is no guarantee that this will be
	 * called, in particular, if Convergia exits as a result of the computer
	 * shutting down, then this will not be called. it is only guaranteed that
	 * it will be called if the workspace is to be shut down but the vm will
	 * continue running, such as if the workspace is deleted.
	 */
	public abstract void shutdown();

	protected void addNotification(TaskbarNotification notification,
			boolean requestDisplay)
	{
		System.out.println("creating wrapper W");
		WorkspaceNotification nWrapper = new WorkspaceNotification(wrapper
				.getId(), notification);
		System.out.println("adding W");
		Convergia.notificationFrame.addNotification(nWrapper, requestDisplay);

		System.out.println("added W");
	}

	protected void removeNotification(TaskbarNotification notification)
	{
		WorkspaceNotification nWrapper = new WorkspaceNotification(wrapper
				.getId(), notification);
		// this works because nWrapper.equals() is true if the workspaceId of
		// the wrapper and the wrapper notification are the same
		Convergia.notificationFrame.removeNotification(nWrapper);
	}

	protected TaskbarNotification[] listNotifications()
	{
		ArrayList<TaskbarNotification> notifications = new ArrayList<TaskbarNotification>();
		for (WorkspaceNotification notification : Convergia.notificationFrame
				.listNotificationsByClass(WorkspaceNotification.class))
		{
			if (notification.getWorkspaceId().equals(getWrapper().getId()))
				notifications.add(notification.getNotification());
		}
		return notifications.toArray(new TaskbarNotification[0]);
	}

	/**
	 * NOT IMPLEMENTED YET<br/><br/>
	 * 
	 * requests additional messaging capacity between members of this workspace.
	 * this means that over the next few seconds, members of the workspace will
	 * be switched to a server that does not have many people on it. this should
	 * be called before a lot of messages will be exchanged, for example, if
	 * this workspace implements a 3D game, then when the game is about to
	 * start, this method should be called to alert Convergia that a lot of
	 * messages will be exchanged between the workspaces about where each person
	 * is in the 3D world.
	 * 
	 */
	protected void requestMessagingCapacity()
	{

	}
}
