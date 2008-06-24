package net.sf.opengroove.client.toolworkspace;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import net.sf.opengroove.client.OpenGroove;
import net.sf.opengroove.client.notification.TaskbarNotification;
import net.sf.opengroove.client.workspace.WorkspaceManager;
import net.sf.opengroove.client.workspace.WorkspaceNotification;

import base64.Base64Coder;


/**
 * A workspace tool. All workspace tool types implement this class.
 * 
 * @author Alexander Boyd
 * 
 */
public abstract class Tool
{
	private ToolWrapper wrapper;

	private Properties pluginMetadata;

	/**
	 * this method returns true if this computer has an active connection to the
	 * OpenGroove server, false otherwise.
	 * 
	 * @return
	 */
	protected boolean isOnline()
	{
		return getWrapper().getWorkspace().isOnline();
	}

	public ToolWrapper getWrapper()
	{
		return wrapper;
	}

	public void setWrapper(ToolWrapper wrapper)
	{
		this.wrapper = wrapper;
	}

	/**
	 * LEGACY - Instead of using this, consider getProperty, setProperty, and
	 * listProperties.
	 * 
	 * @param info
	 */
	public void setInfo(String info)
	{
		info = Base64Coder.encodeString(info);
		wrapper.setInfo(wrapper.getWorkspace().getUsername(), info);
		save();
		wrapper.getWorkspace().updateWorkspaceInfo();
	}

	/**
	 * see comment on setInfo()
	 * 
	 * @param user
	 * @return
	 */
	public String getInfo(String user)
	{
		return Base64Coder.decodeString(WorkspaceManager.parseMetadata(
				wrapper.getWorkspace().getInfo(user)).getProperty(
				"tool_" + getId() + "_info"));
	}

	/**
	 * returns this workspace tool's unique identifier. this is unique
	 * system-wide, not just workspace-wide.
	 * 
	 * @return
	 */
	protected String getId()
	{
		return wrapper.getId();
	}

	/**
	 * receives a message from another user with this tool.
	 * 
	 * @param from
	 * @param message
	 */
	public abstract void receiveMessage(String from, String message);

	/**
	 * this method should shut down this tool and release any resources used by
	 * it. this is called when the tool is deleted (or the workspace owning this
	 * tool is deleted), but currently not when OpenGroove shuts down. in the
	 * future, this will be called when OpenGroove shuts down.
	 */
	public abstract void shutdown();

	/**
	 * sends a message. on the recipient's computer, receiveMessage will be
	 * called.
	 * 
	 * @param to
	 * @param message
	 */
	protected void sendMessage(String to, String message)
	{
		wrapper.getWorkspace().sendMessage(to,
				"toolmanager|toolmessage|" + wrapper.getId() + "|" + message);
	}

	/**
	 * indicates that a user has signed on or off. the user may not be a member
	 * of this workspace, so no changes may have actually occured that concern
	 * this workspace.
	 * 
	 */
	public abstract void userStatusChanged();

	/**
	 * returns the user that initially created the workspace that this tool is
	 * in.
	 * 
	 * @return
	 */
	protected String getWorkspaceCreator()
	{
		return wrapper.getWorkspace().getCreator();
	}

	protected String getWorkspaceId()
	{
		return wrapper.getWorkspace().getId();
	}

	/**
	 * returns the user that initially created this tool within the workspace.
	 * 
	 * @return
	 */
	protected String getToolCreator()
	{
		// only the creator of a workspace can create tools, so the tool creator
		// is the workspace creator
		// TODO: this will need to be changed when code for allowing
		// participants to create tools is added
		return getWorkspaceCreator();
	}

	/**
	 * returns this user's username.
	 * 
	 * @return
	 */
	protected String getUsername()
	{
		return wrapper.getWorkspace().getUsername();
	}

	/**
	 * lists all members of this workspace.
	 * 
	 * @return
	 */
	protected String[] listUsers()
	{
		return wrapper.getWorkspace().listUsers();
	}

	/**
	 * lists all members of this workspace who are online.
	 * 
	 * @return
	 */
	protected String[] listOnlineUsers()
	{
		return wrapper.getWorkspace().listOnlineUsers();
	}

	/**
	 * lists all members of this workspace who are offline.
	 * 
	 * @return
	 */
	protected String[] listOfflineUsers()
	{
		return wrapper.getWorkspace().listOfflineUsers();
	}

	/**
	 * lists all members who are allowed to access this workspace. this may
	 * include users not included in listUsers(), which means that the user in
	 * question is allowed (via user specification) to use this workspace but
	 * has not chosen to do so.
	 * 
	 * @return
	 */
	protected String[] listAllowedUsers()
	{
		return wrapper.getWorkspace().listAllowedUsers();
	}

	/**
	 * lists the folder that this tool can use for data storage. the tool is not
	 * required to use this folder.
	 * 
	 * @return
	 */
	protected File getStorageFile()
	{
		return wrapper.getDatastore();
	}

	/**
	 * initializes this workspace tool.
	 * 
	 */
	public abstract void initialize();

	/**
	 * if this is true, then the tool is somehow marked as needing the users
	 * immediate (or quick) attention. this would usually involve displaying a
	 * warning icon next to the parent workspace in the launchbar, or bolding
	 * it, and displaying a balloon over the tray icon.
	 * 
	 * @param attention
	 *            true if the tool needs attention, false to clear the attention
	 *            needed status
	 */
	protected void setAttentionStatus(boolean attention)
	{
		// FIXME: implement this
	}

	/**
	 * indicates that the user should be notified that the tool has new
	 * information, or otherwise requires the users (non-urgent) attention. this
	 * is pretty much the same as setAttentionStatus, except that this means
	 * that attention is not immediately required, in otherwords, this is not as
	 * urgent.
	 * 
	 * @param newInfo
	 *            true if this tool has new info, or otherwise requires the
	 *            user's (non-immediate) attention, false to clear the new info
	 *            status
	 */
	protected void setNewInformationStatus(boolean newInfo)
	{
		// FIXME: implement this
	}

	/**
	 * returns true if this user is the creator of the workspace, false
	 * otherwise.
	 * 
	 * @return
	 */
	protected boolean isWorkspaceCreator()
	{
		return wrapper.getWorkspace().isCreator();
	}

	protected boolean isToolCreator()
	{
		return getToolCreator().equals(getUsername());
	}

	/**
	 * gets the version of the tool that i am running. this is specified in the
	 * plugin descriptor for this workspace, in the version property. this can
	 * be used to send a message to the tool creator or other computer to check
	 * that this tool plugin is up-to-date enough to be used for this tool.
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
	 * gets the registered type of the tool. this is usually specified in the
	 * tool plugin descriptor, so it is usually known anyway.
	 * 
	 * @return
	 */
	protected String getRegisteredType()
	{
		return wrapper.getTypeId();
	}

	/**
	 * returns the component that should be used to render this Tool. this is
	 * the component that shows up in the workspace. This method should not take
	 * a long time to return, and should consistently return the same (IE
	 * identity equal) component. if the component returned is not the same
	 * every time, the results are undefined, but usually it will cause some
	 * huge problem with the workspace UI. it is guaranteed that initalize()
	 * will be called before getComponent(), so it is reccomended to build the
	 * component in the initialize() method, and just return the component here.
	 * this method may be called frequently, so it is best to return a component
	 * stored in a field.
	 * 
	 * @return
	 */
	public abstract JComponent getComponent();

	/**
	 * returns the name of this tool. this may be configured by setName, or the
	 * user can configure it using an option in the workspace UI.
	 * 
	 * @return
	 */
	protected String getName()
	{
		return wrapper.getName();
	}

	/**
	 * returns the specified storage property. Storage properties are a way for
	 * tools to store small portions of data on the server. tools should not
	 * assume that a particular key will be stored indefinately. they should
	 * only use keys for short term storage, or for synchronization purposes.
	 * generally, a given tool should try to avoid storing more than 100KB in
	 * properties at a time. this limit will be raised soon, but don't expect it
	 * to go anywhere above about 2MB. a mechanism for sending changes in tools
	 * that are large in size but only exist until all users have received the
	 * change will be available soon.<br/><br/>
	 * 
	 * storage properties are not cached, so this method will throw an exception
	 * if an active connection to the OpenGroove server is not available. if you
	 * need data to be available offline, consider using getStorageFile() to
	 * store data.
	 * 
	 * @param key
	 * @return
	 */
	protected String getProperty(String key)
	{
		return wrapper.getWorkspace().getProperty(
				"t_" + wrapper.getId() + "_" + key);
	}

	/**
	 * sets the specified storage property. see the comment on getProperty().
	 * 
	 * @param key
	 * @param value
	 */
	protected void setProperty(String key, String value)
	{
		wrapper.getWorkspace().setProperty("t_" + wrapper.getId() + "_" + key,
				value);
	}

	/**
	 * lists the storage properties that this tool has set. this includes
	 * properties set by other users that participate in the workspace that owns
	 * this tool. see the comment on getProperty().
	 * 
	 * @return
	 */
	protected String[] listProperties()
	{
		return listProperties(null);
	}

	/**
	 * lists the storage properties that start with the string specified. this
	 * method should be used over manually filtering out properties by prefix
	 * yourself, because since the prefix is sent to the server, the server will
	 * only send back the ones that start with the prefix specified, thereby
	 * avoiding unneccessarry network transfer.
	 * 
	 * @param prefix
	 * @return
	 */
	protected String[] listProperties(String prefix)
	{
		if (prefix == null)
			prefix = "";
		String[] old = wrapper.getWorkspace().listProperties(
				"t_" + wrapper.getId() + "_" + prefix);
		String[] toReturn = new String[old.length];
		for (int i = 0; i < old.length; i++)
		{
			toReturn[i] = old[i].substring(("t_" + wrapper.getId() + "_")
					.length());
		}
		return toReturn;
	}

	/**
	 * sets the name of this tool. this is the name that shows up in the tabbed
	 * pane below the tools. usually, this is configured by the user by using
	 * the workspace UI.
	 * 
	 * currently, this can only be called by the workspace creator. calling it
	 * by a workspace participant will throw an exception.
	 * 
	 * @param name
	 *            the name to set (this method may take a bit to return, by
	 *            which time the name will show up on the tabbed pane in the
	 *            workspace)
	 */
	protected void setName(String name)
	{
		if (!wrapper.getWorkspace().isCreator())
			throw new RuntimeException(
					"You are not the creator of the workspace owning this tool, so you cannot change it's name");
		wrapper.setName(name);
		save();
		wrapper.getWorkspace().buildAndSetInfo();
	}

	/**
	 * saves any changes to this workspace's information to the file system.
	 * this usually is called by OpenGroove, and should not be called directly by
	 * the tool implementation.
	 * 
	 */
	public void save()
	{
		wrapper.getManager().storage.addOrUpdateTool(wrapper);
		wrapper.getWorkspace().checkNeedsUpdateTabs();
	}

	/**
	 * gets the plugin metadata. this is usually called by OpenGroove, and should
	 * not be called directly by the tool implementation.
	 * 
	 * @return
	 */
	public Properties getPluginMetadata()
	{
		return pluginMetadata;
	}

	/**
	 * this is usually called by OpenGroove, and should not be called directly by
	 * the tool implementation.
	 * 
	 * @param pluginMetadata
	 */
	public void setPluginMetadata(Properties pluginMetadata)
	{
		this.pluginMetadata = pluginMetadata;
	}

	/**
	 * adds the specified taskbar notification. Taskbar notifications are a
	 * means of notifying the user of something. A taskbar notification is
	 * created, and added using this method. The component supplied by the
	 * taskbar notification shows up in a special window that is shown in the
	 * lower right corner of the screen. if the notification is an alert (see
	 * TaskbarNotification.isAlert()) then the OpenGroove taskbar icon will flash
	 * while this notification is added.<br/><br/>
	 * 
	 * The best way to start off with Taskbar Notifications is to try them out
	 * yourself. You can use NotificationAdapter as a concrete implementation of
	 * TaskbarNotification. You can override the clicked() method of
	 * NotificationAdapter to remove the notification from the taskbar.
	 * 
	 * @param notification
	 * @param requestDisplay
	 */
	protected void addNotification(TaskbarNotification notification,
			boolean requestDisplay)
	{
		System.out.println("creating wrapper");
		ToolNotification nWrapper = new ToolNotification(wrapper.getId(),
				notification);
		System.out.println("adding");
		getWrapper().getWorkspace().addNotification(nWrapper, requestDisplay);
		System.out.println("added");
	}

	/**
	 * removes the notification specified. see addNotification() for more info
	 * on notifications.
	 * 
	 * @param notification
	 */
	protected void removeNotification(TaskbarNotification notification)
	{
		ToolNotification nWrapper = new ToolNotification(wrapper.getId(),
				notification);
		// this works because nWrapper.equals() is true if the workspaceId of
		// the wrapper and the wrapper notification are the same
		getWrapper().getWorkspace().removeNotification(nWrapper);
	}

	/**
	 * lists the notifications that have been added but not yet removed. see
	 * addNotification() for more info on notifications.
	 * 
	 * @return
	 */
	protected TaskbarNotification[] listNotifications()
	{
		ArrayList<TaskbarNotification> notifications = new ArrayList<TaskbarNotification>();
		for (TaskbarNotification n : getWrapper().getWorkspace()
				.listNotifications())
		{
			if (!(n instanceof ToolNotification))
				continue;
			ToolNotification notification = (ToolNotification) n;
			if (notification.getToolId().equals(getWrapper().getId()))
				notifications.add(notification.getNotification());
		}
		return notifications.toArray(new TaskbarNotification[0]);
	}

	/**
	 * returns the frame that is currently displaying this tool, or null if none
	 * was found. this will usually be the tool workspace frame, unless the tool
	 * is popped out, in which case it will be the tool's frame.
	 * 
	 * @return
	 */
	protected JFrame getParentFrame()
	{
		return (JFrame) SwingUtilities.getWindowAncestor(getComponent());
	}

	/**
	 * switches to this tool's tab and calls show on the workspace's window.
	 * this could be called, for example, after a taskbar notification
	 * concerning this tool was clicked.<br/><br/>
	 * 
	 * if the tool is popped out, then the frame that the tool is in will have
	 * it's show() method called, instead of the workspace window.
	 * 
	 */
	public void switchTo()
	{
		System.out.println("switchto");
		PoppableTabbedPane tp = getWrapper().getWorkspace().getFrame()
				.getToolsTabbedPane();
		int index = -1;
		for (int i = 0; i < tp.getTabCount(); i++)
		{
			if (tp.getTabComponentAt(i) instanceof ToolComponentWrapper
					&& ((ToolComponentWrapper) tp.getTabComponentAt(i))
							.getWrapper().getId().equals(getWrapper().getId()))
			{
				index = i;
				break;
			}
		}
		if (index == -1)
			throw new RuntimeException(
					"The tool is not a member of any tool workspace");
		tp.setSelectedIndex(index);
	}
}
