package net.sf.opengroove.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JToolTip;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.jidesoft.swing.JideButton;

import net.sf.opengroove.client.com.CommandCommunicator;
import net.sf.opengroove.client.com.StatusListener;
import net.sf.opengroove.client.com.UserNotificationListener;
import net.sf.opengroove.client.help.HelpViewer;
import net.sf.opengroove.client.plugins.PluginManager;
import net.sf.opengroove.client.ui.ComponentUtils;
import net.sf.opengroove.client.ui.frames.SearchForUsersFrame;
import net.sf.opengroove.client.workspace.WorkspaceManager;
import net.sf.opengroove.common.concurrent.Conditional;
import net.sf.opengroove.common.concurrent.ConditionalTimer;
import net.sf.opengroove.common.utils.Userids;

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
     * A timer that uploads the current use status of this computer (whether or
     * not the computer is idle, how long it has been idle for, if the user is
     * currently using OpenGroove)
     */
    private ConditionalTimer myStatusTimer;
    /**
     * A timer that downloads the status updates for all of the contacts (IE the
     * status updates uploaded by those contacts' {@link #myStatusTimer}s), and
     * updates the icons in the launchbar's contact's pane.<br/><br/>
     * 
     * This timer, unlike most of the other timers, uses
     * {@link Conditional#True} instead of {@link #connectionConditional}, so
     * that it will run even if there is no connection to the server, so as to
     * set all of the user's statuses to offline.
     */
    private ConditionalTimer contactStatusTimer;
    /**
     * The menu that is added to the tray icon that shows all of the user's
     * workspace
     */
    private PopupMenu workspacesSubMenu;
    /**
     * A map that maps contact userids to the jidebutton that contains the
     * user's status icon.
     */
    private HashMap<String, JideButton> contactStatusLabelMap = new HashMap<String, JideButton>();
    /**
     * The search users frame (it's actually a dialog) associated with this
     * context.
     */
    private SearchForUsersFrame searchForUsersFrame;
    /**
     * The checkbox shown at the bottom of the contacts tab in the launchbar
     * window. When checked, the contacts pane should display known users as
     * well as contacts, and when not checked (the default), the contacts pane
     * should only display contacts, not known users. A known user is one where
     * the {@link Contact#isUserContact()} method returns false, and a contact
     * is one where that method returns true.
     */
    private JCheckBox showKnownUsersAsContacts;
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
    
    public void setContactTimer(
        ConditionalTimer contactTimer)
    {
        this.contactTimer = contactTimer;
    }
    
    private final Object refreshContactsLock = new Object();
    
    /**
     * Refreshes the contacts panel. This should generally not be run on the
     * EDT, as it may take some time to complete. It removes all components from
     * the contacts panel, adds an indeterminate progress bar to it, loads all
     * of the contacts below it, and then removes the progress bar.
     */
    public void refreshContactsPane()
    {
        synchronized (refreshContactsLock)
        {
            Contact[] contactList = getStorage()
                .getAllContacts();
            contactsPanel.removeAll();
            JProgressBar bar = new JProgressBar();
            bar.setIndeterminate(true);
            bar.setAlignmentX(0);
            if (contactList.length > 60)
                contactsPanel.add(bar);
            contactsPanel.invalidate();
            contactsPanel.validate();
            contactsPanel.repaint();
            launchbar.invalidate();
            launchbar.validate();
            launchbar.repaint();
            final JPopupMenu userStatusMenu = new JPopupMenu();
            JLabel onlineLabel = new JLabel("Online",
                new ImageIcon(
                    OpenGroove.Icons.USER_ONLINE_16
                        .getImage()), JLabel.LEFT);
            JLabel offlineLabel = new JLabel("Offline",
                new ImageIcon(
                    OpenGroove.Icons.USER_OFFLINE_16
                        .getImage()), JLabel.LEFT);
            JLabel idleLabel = new JLabel("Idle",
                new ImageIcon(OpenGroove.Icons.USER_IDLE_16
                    .getImage()), JLabel.LEFT);
            JLabel unknownLabel = new JLabel("Unknown",
                new ImageIcon(
                    OpenGroove.Icons.USER_UNKNOWN_16
                        .getImage()), JLabel.LEFT);
            JLabel nonexistantLabel = new JLabel(
                "Nonexistant", new ImageIcon(
                    OpenGroove.Icons.USER_NONEXISTANT_16
                        .getImage()), JLabel.LEFT);
            onlineLabel
                .setToolTipText("This means that the user is connected to the"
                    + " internet and is using their computer.");
            offlineLabel
                .setToolTipText("This means that the user is not connected "
                    + "to the internet, or their computer is off.");
            idleLabel
                .setToolTipText("This means that the user is connected "
                    + "to the internet, but they are not using their computer right now.");
            unknownLabel
                .setToolTipText("This means that OpenGroove doesn't know what the "
                    + "user's current status is.");
            nonexistantLabel
                .setToolTipText("This means that OpenGroove has determined that "
                    + "the user does not exist.");
            userStatusMenu.add(new JLabel(" Key:"));
            userStatusMenu.add(onlineLabel);
            userStatusMenu.add(offlineLabel);
            userStatusMenu.add(idleLabel);
            userStatusMenu.add(unknownLabel);
            userStatusMenu.add(nonexistantLabel);
            for (final Contact contact : contactList)
            {
                System.out.println("encountered a contact");
                if (contact.isUserContact()
                    || showKnownUsersAsContacts
                        .isSelected())
                {
                    System.out
                        .println("user contact with dn:"
                            + contact.getDisplayName());
                    // TODO: pick up here, create a panel for the contact (with
                    // right-click options) and add it to the contact pane
                    JPanel contactPanel = new JPanel(
                        new BorderLayout());
                    contactPanel.setOpaque(false);
                    contactPanel.setAlignmentX(0);
                    JideButton contactButton = new JideButton(
                        contact.getDisplayName())
                    {
                        @Override
                        public String getToolTipText(
                            MouseEvent e)
                        {
                            System.out
                                .println("getting text");
                            return "<html><b>"
                                + contact.getDisplayName()
                                + "</b><br/>"
                                + "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">"
                                + "<tr><td>Userid: &nbsp; &nbsp; </td><td>"
                                + contact.getUserid()
                                + "</td></tr>"
                                + "<tr><td>Real name: &nbsp; &nbsp; </td><td>"
                                + contact.getRealName()
                                + "</td></tr>"
                                + "<tr><td>Local name: &nbsp; &nbsp; </td><td>"
                                + contact.getLocalName()
                                + "</td></tr></table>";
                        }
                    };
                    contactButton
                        .setToolTipText("Loading...");
                    contactButton
                        .setHorizontalAlignment(SwingConstants.LEFT);
                    contactButton.setOpaque(false);
                    contactButton
                        .setButtonStyle(JideButton.HYPERLINK_STYLE);
                    System.out.println("perferred:"
                        + contactButton.getPreferredSize()
                        + ",minimum:"
                        + contactButton.getMinimumSize()
                        + ",maximum:"
                        + contactButton.getMaximumSize());
                    contactButton
                        .setForeground(Color.BLACK);
                    JPopupMenu menu = new JPopupMenu();
                    menu.add(new IMenuItem("Rename")
                    {
                        
                        @Override
                        public void actionPerformed(
                            ActionEvent e)
                        {
                            // TODO Auto-generated method stub
                            
                        }
                    });
                    ComponentUtils.addPopup(contactButton,
                        menu);
                    contactButton
                        .addActionListener(new ActionListener()
                        {
                            
                            @Override
                            public void actionPerformed(
                                ActionEvent e)
                            {
                                System.out
                                    .println("contact clicked");
                            }
                        });
                    contactButton
                        .setBorder(new EmptyBorder(2, 2, 2,
                            2));
                    contactPanel.add(contactButton,
                        BorderLayout.CENTER);
                    final JideButton statusButton = new JideButton(
                        new ImageIcon(
                            OpenGroove.Icons.USER_UNKNOWN_16
                                .getImage()));
                    statusButton
                        .setButtonStyle(statusButton.HYPERLINK_STYLE);
                    statusButton
                        .addActionListener(new ActionListener()
                        {
                            
                            @Override
                            public void actionPerformed(
                                ActionEvent e)
                            {
                                userStatusMenu.show(
                                    statusButton, 0, 0);
                            }
                        });
                    contactPanel.add(statusButton,
                        BorderLayout.WEST);
                    contactsPanel.add(contactPanel);
                    contactsPanel.invalidate();
                    contactsPanel.validate();
                    contactsPanel.repaint();
                    launchbar.invalidate();
                    launchbar.validate();
                    launchbar.repaint();
                }
            }
            contactsPanel.remove(bar);
            contactsPanel.invalidate();
            contactsPanel.validate();
            contactsPanel.repaint();
            launchbar.invalidate();
            launchbar.validate();
            launchbar.repaint();
        }
    }
    
    public JCheckBox getShowKnownUsersAsContacts()
    {
        return showKnownUsersAsContacts;
    }
    
    public void setShowKnownUsersAsContacts(
        JCheckBox showKnownUsersAsContacts)
    {
        this.showKnownUsersAsContacts = showKnownUsersAsContacts;
    }
    
    public ConditionalTimer getMyStatusTimer()
    {
        return myStatusTimer;
    }
    
    public ConditionalTimer getContactStatusTimer()
    {
        return contactStatusTimer;
    }
    
    public SearchForUsersFrame getSearchForUsersFrame()
    {
        return searchForUsersFrame;
    }
    
    public void setMyStatusTimer(
        ConditionalTimer myStatusTimer)
    {
        this.myStatusTimer = myStatusTimer;
    }
    
    public void setContactStatusTimer(
        ConditionalTimer contactStatusTimer)
    {
        this.contactStatusTimer = contactStatusTimer;
    }
    
    public void setSearchForUsersFrame(
        SearchForUsersFrame searchForUsersFrame)
    {
        this.searchForUsersFrame = searchForUsersFrame;
    }
}
