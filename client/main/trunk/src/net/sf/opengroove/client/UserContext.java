package net.sf.opengroove.client;

import java.awt.PopupMenu;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.sf.opengroove.client.com.CommandCommunicator;
import net.sf.opengroove.client.help.HelpViewer;
import net.sf.opengroove.client.plugins.PluginManager;

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
    private String userid;
    
    private JPanel contactsPanel;
    
    private PluginManager plugins;
    
    private PopupMenu workspacesSubMenu;
    
    private JFrame launchbar;
    
    private JPanel workspacePanel;
    
    private CommandCommunicator com;
    
    private String password;
    
    private JTabbedPane launchbarTabbedPane;
    
    private HelpViewer helpViewer;
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
        return UserIds.toUsername(userid);
    }
    
    public String getRealm()
    {
        return UserIds.toRealm(userid);
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
}
