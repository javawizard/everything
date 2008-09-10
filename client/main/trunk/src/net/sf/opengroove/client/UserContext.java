package net.sf.opengroove.client;

import java.awt.PopupMenu;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.sf.opengroove.client.com.CommandCommunicator;
import net.sf.opengroove.client.com.StatusListener;
import net.sf.opengroove.client.com.UserNotificationListener;
import net.sf.opengroove.client.help.HelpViewer;
import net.sf.opengroove.client.plugins.PluginManager;
import net.sf.opengroove.client.workspace.WorkspaceManager;
import net.sf.opengroove.common.concurrent.Conditional;
import net.sf.opengroove.common.concurrent.ConditionalTimer;

/**
 * A user context object is created for each user that logs in, and is passed
 * down through the hierarchy of objects created so that they can get a
 * reference to which user the objects are for. User objects also allow various
 * listeners, which can listen for events such as that the user is logging out.<br/><br/>
 * 
 * This class consists mostly of fields that used to be static fields on the
 * OpenGroove class, and that were used for management of the current user.
 * 
 * @author Alexander Boyd
 * 
 */
public class UserContext
{
    /**
     * This user's userid
     */
    private String userid;
    private UserNotificationListener userNotificationListener;
    /**
     * A JPanel that holds this user's contact list
     */
    private JPanel contactsPanel;
    /**
     * The plugin manager that manages this user's plugins
     */
    private PluginManager plugins;
    /**
     * A conditional that is true if there is currently a connection to the
     * server.
     */
    private Conditional connectionConditional;
    /**
     * A timer that downloads contact updates from the server, and uploads new
     * contact updates if there are any.
     */
    private ConditionalTimer contactTimer;
    /**
     * The menu that is added to the tray icon that shows all of the user's
     * workspace
     */
    private PopupMenu workspacesSubMenu;
    /**
     * The user's launchbar window
     */
    private JFrame launchbar;
    /**
     * The user's workspace panel that holds the list of the users workspaces
     */
    private JPanel workspacePanel;
    /**
     * The user's communicator that is used to communicate with the server. This
     * may change if the user decides to change something such as their
     * password, so objects that use a communicator should store a reference to
     * this usercontext instead of the communicator itself
     */
    private CommandCommunicator com;
    /**
     * This user's plain-text password
     */
    private String password;
    /**
     * The tabbed pane that resides in the launchbar
     */
    private JTabbedPane launchbarTabbedPane;
    /**
     * The user's help viewer
     */
    private HelpViewer helpViewer;
    /**
     * The user's workspace manager
     */
    private WorkspaceManager workspaceManager;
    /**
     * The user's status listener. This is created when the user signs on, and
     * does stuff like showing the user if they have the wrong password,
     * updating the taskbar icon when the communicator goes offline, and such.
     * When a new communicator is set on this user context, it should have this
     * status listener registered to it.
     */
    private StatusListener statusListener;
    /**
     * The popup menu for this user. On the tray icon's popup menu, there is a
     * menu item for each user, which is this popup menu.
     */
    private PopupMenu popupMenu;
    
    public String getUserid()
    {
        return userid;
    }
    
    public void setUserid(String userid)
    {
        this.userid = userid;
    }
    
    public String getUsername()
    {
        return Userids.toUsername(userid);
    }
    
    public String getRealm()
    {
        return Userids.toRealm(userid);
    }
    
    public JPanel getContactsPanel()
    {
        return contactsPanel;
    }
    
    public PluginManager getPlugins()
    {
        return plugins;
    }
    
    public PopupMenu getWorkspacesSubMenu()
    {
        return workspacesSubMenu;
    }
    
    public JFrame getLaunchbar()
    {
        return launchbar;
    }
    
    public JPanel getWorkspacePanel()
    {
        return workspacePanel;
    }
    
    public CommandCommunicator getCom()
    {
        return com;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public JTabbedPane getLaunchbarTabbedPane()
    {
        return launchbarTabbedPane;
    }
    
    public HelpViewer getHelpViewer()
    {
        return helpViewer;
    }
    
    public void setContactsPanel(JPanel contactsPanel)
    {
        this.contactsPanel = contactsPanel;
    }
    
    public void setPlugins(PluginManager plugins)
    {
        this.plugins = plugins;
    }
    
    public void setWorkspacesSubMenu(
        PopupMenu workspacesSubMenu)
    {
        this.workspacesSubMenu = workspacesSubMenu;
    }
    
    public void setLaunchbar(JFrame launchbar)
    {
        this.launchbar = launchbar;
    }
    
    public void setWorkspacePanel(JPanel workspacePanel)
    {
        this.workspacePanel = workspacePanel;
    }
    
    public void setCom(CommandCommunicator com)
    {
        this.com = com;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public void setLaunchbarTabbedPane(
        JTabbedPane launchbarTabbedPane)
    {
        this.launchbarTabbedPane = launchbarTabbedPane;
    }
    
    public void setHelpViewer(HelpViewer helpViewer)
    {
        this.helpViewer = helpViewer;
    }
    
    public Storage getStorage()
    {
        return Storage.get(userid);
    }
    
    public WorkspaceManager getWorkspaceManager()
    {
        return workspaceManager;
    }
    
    public void setWorkspaceManager(
        WorkspaceManager workspaceManager)
    {
        this.workspaceManager = workspaceManager;
    }
    
    public StatusListener getStatusListener()
    {
        return statusListener;
    }
    
    public void setStatusListener(
        StatusListener statusListener)
    {
        this.statusListener = statusListener;
    }
    
    public UserNotificationListener getUserNotificationListener()
    {
        return userNotificationListener;
    }
    
    public void setUserNotificationListener(
        UserNotificationListener userNotificationListener)
    {
        this.userNotificationListener = userNotificationListener;
    }
    
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
        "yyyy.MM.dd");
    private static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(
        "hh:mmaa");
    private static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat(
        "yyyy.MM.dd hh:mmaa");
    
    public String formatDate(long date)
    {
        return DATE_FORMAT.format(new Date(date));
    }
    
    public String formatTime(long time)
    {
        return TIME_FORMAT.format(new Date(time));
    }
    
    public String formatDateTime(long dateTime)
    {
        return DATE_TIME_FORMAT.format(new Date(dateTime));
    }

    public Conditional getConnectionConditional()
    {
        return connectionConditional;
    }

    public ConditionalTimer getContactTimer()
    {
        return contactTimer;
    }

    public void setConnectionConditional(
        Conditional connectionConditional)
    {
        this.connectionConditional = connectionConditional;
    }

    public void setContactTimer(ConditionalTimer contactTimer)
    {
        this.contactTimer = contactTimer;
    }
}
