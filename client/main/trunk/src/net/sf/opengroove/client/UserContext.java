package net.sf.opengroove.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.jidesoft.swing.JideButton;

import net.sf.opengroove.client.com.CommandCommunicator;
import net.sf.opengroove.client.com.StatusListener;
import net.sf.opengroove.client.com.Subscription;
import net.sf.opengroove.client.com.TimeoutException;
import net.sf.opengroove.client.com.UserNotificationListener;
import net.sf.opengroove.client.help.HelpViewer;
import net.sf.opengroove.client.notification.NotificationAdapter;
import net.sf.opengroove.client.notification.TaskbarNotification;
import net.sf.opengroove.client.plugins.PluginManager;
import net.sf.opengroove.client.storage.Contact;
import net.sf.opengroove.client.storage.ContactComputer;
import net.sf.opengroove.client.storage.ContactStatus;
import net.sf.opengroove.client.storage.LocalUser;
import net.sf.opengroove.client.storage.Storage;
import net.sf.opengroove.client.ui.ComponentUtils;
import net.sf.opengroove.client.ui.frames.SearchForUsersFrame;
import net.sf.opengroove.client.workspace.WorkspaceManager;
import net.sf.opengroove.common.concurrent.Conditional;
import net.sf.opengroove.common.concurrent.ConditionalTimer;
import net.sf.opengroove.common.utils.StringUtils;
import net.sf.opengroove.common.utils.Userids;
import net.sf.opengroove.common.utils.StringUtils.ToString;

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
    /*
     * TODO: currently 30 seconds for testing purposes, probably change to
     * something like 5 minutes when OpenGroove is released, or make it
     * user-configurable
     */
    /**
     * The time that a contact must not move their mouse for in order to be
     * marked idle. This should be low enough that it doesn't take forever for
     * the contact to be marked as idle, but high enough that a user pausing to
     * read over some information won't be marked idle immediately.
     */
    private static final long IDLE_THRESHOLD = 1000 * 30;
    private TaskbarNotification nonexistantContactNotification;
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
    private Conditional connectionConditional = new Conditional()
    {
        
        @Override
        public boolean query()
        {
            return com != null
                && com.getCommunicator() != null
                && com.getCommunicator().isActive();
        }
    };
    /**
     * A JideButton that contains the icon that represents the local user's
     * current status
     */
    private JideButton localStatusButton;
    /**
     * A JideButton that contains the local user's username. It is displayed at
     * the top of the screen, and can be clicked to change the local user's real
     * name.
     */
    private JideButton localUsernameButton;
    /**
     * A timer that downloads contact updates from the server, and uploads new
     * contact updates if there are any. This timer is interrupted any time an
     * imessage is received from another computer that indicates updates to
     * contacts.
     */
    @TimerField
    private ConditionalTimer contactTimer;
    /**
     * A timer that uploads the current use status of this computer (whether or
     * not the computer is idle, how long it has been idle for, if the user is
     * currently using OpenGroove)
     */
    /**
     * The last X coordinate of the mouse. The mouse position is checked every
     * few seconds, and if the mouse has moved since the last check, the idle
     * time is reset. If it hasn't, the idle time is not reset, so that if the
     * mouse isn't moved for an extended period of time, the computer will end
     * up being marked as idle.
     */
    private int lastMouseX;
    /**
     * The last Y coordinate of the mouse. The mouse position is checked every
     * few seconds, and if the mouse has moved since the last check, the idle
     * time is reset. If it hasn't, the idle time is not reset, so that if the
     * mouse isn't moved for an extended period of time, the computer will end
     * up being marked as idle.
     */
    private int lastMouseY;
    /**
     * The last time, in terms of server time, that the mouse was moved. This is
     * generated and uploaded to the server by myStatusTimer.
     */
    private long lastIdle;
    /**
     * True if, on the last check, the user had not moved their mouse, false
     * otherwise
     */
    private boolean wasLastIdle = true;
    /**
     * Whether the contact status icon has been set to idle for this timeg oing
     * idle.
     */
    private boolean markedIdleIcon = false;
    @TimerField
    /**
     * checks to see if the computer is idle, and updates the idle-related
     * fields. If the computer has just stopped being idle, then this timer will
     * also trigger an immediate upload of user presence information.
     */
    private ConditionalTimer myStatusCheckTimer = new ConditionalTimer(
        1000, Conditional.True)
    {
        
        @Override
        public void execute()
        {
            PointerInfo info = MouseInfo.getPointerInfo();
            Point location = info.getLocation();
            boolean oldWasLastIdle = wasLastIdle;
            wasLastIdle = true;
            if (location.x != lastMouseX
                || location.y != lastMouseY)
            {
                lastIdle = getServerTime();
                wasLastIdle = false;
                markedIdleIcon = false;
            }
            else if (!markedIdleIcon
                && (lastIdle + IDLE_THRESHOLD) < getServerTime())
            {
                markedIdleIcon = true;
                updateLocalStatusIcon();
            }
            lastMouseX = location.x;
            lastMouseY = location.y;
            if (oldWasLastIdle && !wasLastIdle)
            {
                updateLocalStatusIcon();
                uploadCurrentStatus();
            }
        }
    };
    @TimerField
    /**
     * Uploads the current idle and active status.
     */
    private ConditionalTimer myStatusUploadTimer = new ConditionalTimer(
        1000 * 30, connectionConditional)
    {
        /*
         * TODO: change the upload time to be more like 3 minutes, or even
         * longer than that (and add logic for the idle-checker to perform a
         * single upload when the idle time passes the idle threshold)
         */
        @Override
        public void execute()
        {
            uploadCurrentStatus();
        }
    };
    /**
     * A timer that downloads the status updates for all of the contacts (IE the
     * status updates uploaded by those contacts' {@link #myStatusTimer}s), and
     * updates the icons in the launchbar's contact's pane. <br/><br/>
     * 
     * This timer, unlike most of the other timers, uses
     * {@link Conditional#True} instead of {@link #connectionConditional}, so
     * that it will run even if there is no connection to the server, so as to
     * set all of the user's statuses to offline.<br/><br/>
     * 
     * This timer runs every 3 minutes.
     */
    @TimerField
    private ConditionalTimer contactStatusTimer = new ConditionalTimer(
        1000 * 60 * 3, Conditional.True)
    {
        
        @Override
        public void execute()
        {
            updateContactStatus();
        }
    };
    /**
     * A timer that updates the icons for contacts in the contacts pane with the
     * contact's current status, such as offline or idle.
     */
    @TimerField
    private ConditionalTimer contactIconTimer = new ConditionalTimer(
        1000 * 9, Conditional.True)
    {
        
        @Override
        public void execute()
        {
            Contact[] contacts = getStorage()
                .getLocalUser().getContacts().toArray(
                    new Contact[0]);
            for (Contact contact : contacts)
            {
                updateContactIcon(contact.getUserid());
            }
        }
        
    };
    /**
     * A timer that gets the server's time and sets the lag of this user's
     * backing LocalUser to be the difference between the server's time and the
     * local time.
     */
    @TimerField
    private ConditionalTimer timeSyncTimer;
    /**
     * A timer that checks to make sure that subscriptions are present for all
     * of the contacts that exist.
     */
    @TimerField
    private ConditionalTimer subscriptionTimer = new ConditionalTimer(
        1000 * 60 * 2, connectionConditional)
    {
        public void execute()
        {
            updateSubscriptions();
        }
    };
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
    
    /**
     * Starts all of this context's timers. Specifically, starts all timers in
     * any field on this user context that are annotated with {@link TimerField}.
     */
    public void startTimers()
    {
        ConditionalTimer[] timers = getTimers();
        for (ConditionalTimer timer : timers)
        {
            timer.start();
        }
    }
    
    private final Object subscriptionUpdateLock = new Object();
    
    /**
     * Scans through all of the contacts and ensures that subscriptions are
     * present to the contacts' computer settings public-idle and public-active,
     * and makes sure that the contact's user status is subscribed to. Right
     * now, other subscriptions aren't deleted, mainly since this could clobber
     * another computer that has subscribed to a contact recently added on that
     * computer but that we are still unaware about.
     * 
     * Exceptions generated due to a lack of an internet connection are not
     * returned, but are dumped to standard error.
     */
    public void updateSubscriptions()
    {
        synchronized (subscriptionUpdateLock)
        {
            if (!com.getCommunicator().isActive())
                /*
                 * No point in updating subscriptions if we don't have an
                 * internet connection
                 */
                return;
            try
            {
                Contact[] contacts = getStorage()
                    .getLocalUser().getContacts().toArray(
                        new Contact[0]);
                Subscription[] subscriptions = com
                    .listSubscriptions();
                for (Contact contact : contacts)
                {
                    /*
                     * First, add a subscription to the contact's current status
                     */
                    putSubscription(subscriptions,
                        new Subscription("userstatus",
                            contact.getUserid(), "", "",
                            false));
                    /*
                     * The userstatus subscription will take care of the status
                     * of all of the user's computers. Now we need to subscribe
                     * to public-idle and public-active.
                     */
                    putSubscription(subscriptions,
                        new Subscription("computersetting",
                            contact.getUserid(), "",
                            "public-idle", false));
                    putSubscription(subscriptions,
                        new Subscription("computersetting",
                            contact.getUserid(), "",
                            "public-active", false));
                    /*
                     * Leaving the target computer empty is intentional; if the
                     * target computer is empty, it notifies us of changes to
                     * any of the user's computer's properties under that name,
                     * which is what we want.
                     */
                }
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
    }
    
    /**
     * Checks to see if the subscription indicated is present in the list of
     * subscriptions passed in. If it is not, the subscription is uploaded to
     * the server. If it is, nothing happens.
     * 
     * @param existing
     *            An array of existing subscriptions
     * @param subscription
     *            The new subscription that will be uploaded if it's not a
     *            member of <code>existing</code>
     */
    protected void putSubscription(Subscription[] existing,
        Subscription subscription)
    {
        if (!com.getCommunicator().isActive())
            /*
             * No sense in trying to send a subscription to the server if we
             * don't have a connection to the server anyway
             */
            return;
        for (Subscription test : existing)
        {
            if (test.absolute(Userids.toRealm(getUserid()))
                .equals(
                    subscription.absolute(Userids
                        .toRealm(getUserid()))))
                /*
                 * The subscription already exists, so we don't need to put it
                 * on the server
                 */
                return;
        }
        /*
         * If we get here, the subscription doesn't exist. We'll go ahead and
         * send it to the server now. We'll wrap it in a try/catch in case the
         * server has gone offline while we were in the above for loop.
         * 
         * Hmm, on second thought, we should check this, and if it's a failed
         * response exception that we receive, then we should either throw it
         * out, or make note in the javadoc for this method that it will swallow
         * any exceptions generated.
         */
        try
        {
            com.createSubscription(subscription);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }
    
    /**
     * Gets the LocalUser that this context is for.
     * 
     * @return the local user that this context is for.
     */
    public LocalUser getLocalUser()
    {
        return getStorage().getLocalUser();
    }
    
    /**
     * Returns a list of all timers for this UserContext.
     * 
     * @return All of this context's timers; specifically, the contents of all
     *         of this context's fields annotated with {@link TimerField}
     */
    public ConditionalTimer[] getTimers()
    {
        Field[] fields = getClass().getDeclaredFields();
        ArrayList<ConditionalTimer> timers = new ArrayList<ConditionalTimer>();
        for (Field field : fields)
        {
            if (field.isAnnotationPresent(TimerField.class)
                && field.getType().equals(
                    ConditionalTimer.class))
            {
                try
                {
                    if (field.get(this) != null)
                        timers.add((ConditionalTimer) field
                            .get(this));
                }
                catch (IllegalAccessException e)
                {
                    // should never be thrown
                    e.printStackTrace();
                }
            }
            else if (field
                .isAnnotationPresent(TimerField.class))
            {
                System.err
                    .println("Field "
                        + field.getName()
                        + "declared in UserContext as a timer field but is "
                        + "not an instance of ConditionalTimer");
            }
        }
        return timers.toArray(new ConditionalTimer[0]);
    }
    
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
    
    /**
     * Gets the storage instance associated with the user represented by this
     * context.
     * 
     * @return
     */
    public Storage getStorage()
    {
        return Storage.get(userid);
    }
    
    /**
     * Gets the WorkspaceManager associated with this context.
     * 
     * @return
     */
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
    
    /**
     * The date format used to format dates
     */
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
        "yyyy.MM.dd");
    /**
     * The date format used to format times
     */
    private static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(
        "hh:mmaa");
    /**
     * The date format used to format date/time combinations
     */
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
    
    public UserContext()
    {
        userStatusMenu = new JPopupMenu();
        JLabel onlineLabel = new JLabel("Online",
            new ImageIcon(OpenGroove.Icons.USER_ONLINE_16
                .getImage()), JLabel.LEFT);
        JLabel offlineLabel = new JLabel("Offline",
            new ImageIcon(OpenGroove.Icons.USER_OFFLINE_16
                .getImage()), JLabel.LEFT);
        JLabel idleLabel = new JLabel("Idle",
            new ImageIcon(OpenGroove.Icons.USER_IDLE_16
                .getImage()), JLabel.LEFT);
        JLabel unknownLabel = new JLabel("Unknown",
            new ImageIcon(OpenGroove.Icons.USER_UNKNOWN_16
                .getImage()), JLabel.LEFT);
        JLabel nonexistantLabel = new JLabel("Nonexistant",
            new ImageIcon(
                OpenGroove.Icons.USER_NONEXISTANT_16
                    .getImage()), JLabel.LEFT);
        onlineLabel
            .setToolTipText(ComponentUtils
                .htmlTipWrap("This means that the user is connected to the"
                    + " internet and is using their computer."));
        offlineLabel
            .setToolTipText(ComponentUtils
                .htmlTipWrap("This means that the user is not connected "
                    + "to the internet, or their computer is off."));
        idleLabel
            .setToolTipText(ComponentUtils
                .htmlTipWrap("This means that the user is connected "
                    + "to the internet, but they are not using their computer right now."));
        unknownLabel
            .setToolTipText(ComponentUtils
                .htmlTipWrap("This means that OpenGroove doesn't know what the "
                    + "user's current status is. This typically happens when you "
                    + "have added the contact but haven't connected to the"
                    + " internet since then."));
        nonexistantLabel
            .setToolTipText(ComponentUtils
                .htmlTipWrap("This means that OpenGroove has determined that "
                    + "the user does not exist. The user may have "
                    + "been deleted, or you might have entered the user's "
                    + "userid incorrectly when adding them as a contact."));
        userStatusMenu.add(new JLabel(" Key:"));
        userStatusMenu.add(onlineLabel);
        userStatusMenu.add(offlineLabel);
        userStatusMenu.add(idleLabel);
        userStatusMenu.add(unknownLabel);
        userStatusMenu.add(nonexistantLabel);
        
    }
    
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
                .getLocalUser().getContacts().toArray(
                    new Contact[0]);
            contactsPanel.removeAll();
            JProgressBar bar = new JProgressBar();
            bar.setIndeterminate(true);
            bar.setAlignmentX(0);
            if (contactList.length > 60)
            {
                contactsPanel.add(bar);
                contactsPanel.invalidate();
                contactsPanel.validate();
                contactsPanel.repaint();
                launchbar.invalidate();
                launchbar.validate();
                launchbar.repaint();
            }
            final JPopupMenu userStatusMenu = getUserStatusMenu();
            int contactsAdded = 0;
            boolean hasNonexistantContacts = false;
            for (final Contact contact : contactList)
            {
                if (contact.isUserContact()
                    || showKnownUsersAsContacts
                        .isSelected())
                {
                    contactsAdded++;
                    if (contact.getStatus().isNonexistant())
                        hasNonexistantContacts = true;
                    JPanel contactPanel = new JPanel(
                        new BorderLayout());
                    contactPanel.setOpaque(false);
                    contactPanel.setAlignmentX(0);
                    final JideButton contactButton = new JideButton()
                    {
                        @Override
                        public String getToolTipText(
                            MouseEvent e)
                        {
                            String computersString = "";
                            int computersAdded = 0;
                            for (ContactComputer computer : contact
                                .getComputers().isolate())
                            {
                                String statusUri;
                                try
                                {
                                    statusUri = getStatusIcon(
                                        computer
                                            .getStatus())
                                        .getScaledFile()
                                        .toURI().toURL()
                                        .toString();
                                }
                                catch (MalformedURLException e1)
                                {
                                    e1.printStackTrace();
                                    statusUri = null;
                                }
                                computersAdded++;
                                computersString += " &nbsp; <img width=\"16\" height=\"16\" src=\""
                                    + statusUri
                                    + "\"/> "
                                    + computer.getName()
                                    + "<br/>";
                            }
                            if (computersAdded == 0)
                            {
                                computersString = " &nbsp; <font color=\"#707070\">(No computers)</font>";
                            }
                            return "<html><b>"
                                + contact.getDisplayName()
                                + "</b><br/><br/>"
                                + "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">"
                                + "<tr><td>Userid: &nbsp; &nbsp; </td><td>"
                                + contact.getUserid()
                                + "</td></tr>"
                                + "<tr><td>Real name: &nbsp; &nbsp; </td><td>"
                                + (contact.getRealName()
                                    .equals("") ? "<font color=\"#707070\">(Not specified)</font>"
                                    : contact.getRealName())
                                + "</td></tr>"
                                + "<tr><td>Local name: &nbsp; &nbsp; </td><td>"
                                + (contact.getLocalName()
                                    .equals("") ? "<font color=\"#707070\">(Not specified)</font>"
                                    : contact
                                        .getLocalName())
                                + "</td></tr></table><br/>Computers:<br/>"
                                + computersString;
                        }
                    };
                    setContactButtonText(contactButton,
                        contact);
                    contactButton
                        .setToolTipText("Loading...");
                    contactButton
                        .setHorizontalAlignment(SwingConstants.LEFT);
                    contactButton.setOpaque(false);
                    contactButton
                        .setButtonStyle(JideButton.HYPERLINK_STYLE);
                    contactButton
                        .setForeground(Color.BLACK);
                    final JPopupMenu contactRenamePopup = new JPopupMenu();
                    final JTextField contactRenameField = new JTextField(
                        15);
                    contactRenamePopup
                        .add(contactRenameField);
                    contactRenameField
                        .addActionListener(new ActionListener()
                        {
                            
                            @Override
                            public void actionPerformed(
                                ActionEvent e)
                            {
                                contactRenamePopup
                                    .setVisible(false);
                            }
                        });
                    contactRenamePopup
                        .addPopupMenuListener(new PopupMenuListener()
                        {
                            
                            @Override
                            public void popupMenuCanceled(
                                PopupMenuEvent e)
                            {
                            }
                            
                            @Override
                            public void popupMenuWillBecomeInvisible(
                                PopupMenuEvent e)
                            {
                                String newName = contactRenameField
                                    .getText();
                                if (newName.trim().equals(
                                    ""))
                                    newName = "";
                                contact
                                    .setLocalName(newName);
                                setContactButtonText(
                                    contactButton, contact);
                            }
                            
                            @Override
                            public void popupMenuWillBecomeVisible(
                                PopupMenuEvent e)
                            {
                            }
                        });
                    contactRenameField
                        .setToolTipText(ComponentUtils
                            .htmlTipWrap("You can set a name you want for this contact "
                                + "here. If you leave it blank, the contact's "
                                + "name will be the contact's userid."));
                    if (contact.isUserContact())
                    {
                        JPopupMenu menu = new JPopupMenu();
                        menu.add(new IMenuItem("Rename")
                        {
                            
                            @Override
                            public void actionPerformed(
                                ActionEvent e)
                            {
                                contactButton
                                    .scrollRectToVisible(new Rectangle(
                                        0, 0, 0, 0));
                                contactRenameField
                                    .setText(contact
                                        .getLocalName());
                                contactRenamePopup.show(
                                    contactButton, 0, 0);
                                contactRenameField
                                    .requestFocusInWindow();
                            }
                        });
                        menu.add(new IMenuItem("Delete")
                        {
                            
                            @Override
                            public void actionPerformed(
                                ActionEvent e)
                            {
                                if (JOptionPane
                                    .showConfirmDialog(
                                        getLaunchbar(),
                                        "Are you sure you want to delete the contact "
                                            + contact
                                                .getDisplayName()
                                            + "?",
                                        null,
                                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                                {
                                    contact
                                        .setUserContact(false);
                                    refreshContactsPaneAsynchronously();
                                }
                            }
                            
                        });
                        ComponentUtils.addPopup(
                            contactButton, menu);
                    }
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
                    Font contactFont = contactButton
                        .getFont();
                    if (contactFont == null)
                        contactFont = Font.decode(null);
                    if (contact.isUserContact())
                        contactFont = contactFont
                            .deriveFont(Font.BOLD);
                    else
                        contactFont = contactFont
                            .deriveFont(Font.PLAIN);
                    contactButton.setFont(contactFont);
                    contactPanel.add(contactButton,
                        BorderLayout.CENTER);
                    OpenGroove.Icons statusIcon = getStatusIcon(contact
                        .getStatus());
                    final JideButton statusButton = new JideButton(
                        new ImageIcon(statusIcon.getImage()));
                    contactStatusLabelMap.put(contact
                        .getUserid(), statusButton);
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
                    // contactsPanel.invalidate();
                    // contactsPanel.validate();
                    // contactsPanel.repaint();
                    // launchbar.invalidate();
                    // launchbar.validate();
                    // launchbar.repaint();
                }
            }
            if (hasNonexistantContacts)
            {
                if (!OpenGroove.notificationFrame
                    .containsNotification(userid,
                        nonexistantContactNotification))
                    OpenGroove.notificationFrame
                        .addNotification(userid,
                            nonexistantContactNotification,
                            true);
            }
            else
            {
                if (OpenGroove.notificationFrame
                    .containsNotification(userid,
                        nonexistantContactNotification))
                    OpenGroove.notificationFrame
                        .removeNotification(nonexistantContactNotification);
            }
            if (contactsAdded == 0)
                contactsPanel
                    .add(new JLabel(
                        "<html><font color=\"#707070\">(No contacts)</font>"));
            contactsPanel.remove(bar);
            contactsPanel.invalidate();
            contactsPanel.validate();
            contactsPanel.repaint();
            launchbar.invalidate();
            launchbar.validate();
            launchbar.repaint();
        }
    }
    
    private JPopupMenu userStatusMenu;
    
    public JPopupMenu getUserStatusMenu()
    {
        return userStatusMenu;
    }
    
    protected void setContactButtonText(
        JideButton contactButton, Contact contact)
    {
        contactButton.setText(contact.getDisplayName());
    }
    
    protected void refreshContactsPaneAsynchronously()
    {
        new Thread()
        {
            public void run()
            {
                refreshContactsPane();
            }
        }.start();
    }
    
    final Object contactStatusLock = new Object();
    
    /**
     * Updates the status icons for the contacts in the contacts pane, based on
     * the contact's current statuses. If a connection to the server is not
     * present, the contact will be shown as if the contact was currently
     * offline, or as a nonexistant contact if it is already marked as being
     * nonexistant. A new thread is started for each contact, so that even if
     * some contacts take some time to update, not all of them will be delayed
     * by the one that took a lot of time. all of the newly-started threads are
     * {@link Thread#join() joined} before this method returns, so that when it
     * returns, all contacts in the contacts list at the time the method was
     * started will have their status icons updated.<br/><br/>
     * 
     * UPDATE: this no longer runs in separate threads, due to issues that were
     * occuring. Instead, it runs the cotact updates one after another.
     */
    public void updateContactStatus()
    {
        synchronized (contactStatusLock)
        {
            Contact[] contacts = getStorage()
                .getLocalUser().getContacts().toArray(
                    new Contact[0]);
            ArrayList<Thread> threads = new ArrayList<Thread>();
            for (final Contact contact : contacts)
            {
                updateOneContactStatus(contact);
            }
        }
    }
    
    /**
     * Synchronously updates a contact's status. This contacts the server if a
     * connection is present, and downloads a list of the contact's computers,
     * and for each computer, whether or not it is online, when it was last
     * online, whether or not it is idle, etc. This information is then stored
     * into the contact, and the contact is stored into this context's storage
     * object. The icon in the contacts pane is then updated to reflect the
     * information.<br/><br/>
     * 
     * This <b>must not</b> be called by any method besides
     * {@link #updateContactStatus()}, unless the caller explicitly
     * synchronizes on {@link #contactStatusLock} first. This is to avoid two
     * threads trying to update the same contact at the same time.
     * 
     * @param contact
     */
    protected void updateOneContactStatus(Contact contact)
    {
        /*
         * First, the downloading of new updates.
         */
        if (com.getCommunicator().isActive())
        {
            try
            {
                ContactStatus status = contact.getStatus();
                boolean serverUserExists = false;
                /*
                 * The loop that calls the command over and over again is to fix
                 * some sort of weird bug that I've been getting where a
                 * userexists packet is getting corrupted en route to the
                 * server, but it seems to be random which packets get corrupted
                 * so I figured this might help.
                 */
                for (int i = 0; i < 3; i++)
                {
                    try
                    {
                        serverUserExists = com
                            .userExists(contact.getUserid());
                        break;
                    }
                    catch (TimeoutException e)
                    {
                        new Exception(
                            "Timeout while getting user status, trying again...",
                            e).printStackTrace();
                        if (i == 2)
                            throw e;
                    }
                }
                if (!serverUserExists)
                {
                    /*
                     * contact doesn't exist
                     */
                    status.setNonexistant(true);
                }
                else
                {
                    /*
                     * contact does exist, so check the rest of the information
                     */
                    status.setNonexistant(false);
                    String[] contactComputers = com
                        .listComputers(contact.getUserid());
                    for (String computerName : contactComputers)
                    {
                        ContactComputer computer = null;
                        for (ContactComputer test : contact
                            .getComputers().isolate())
                        {
                            if (test.getName().equals(
                                computerName))
                            {
                                if (computer != null)
                                    /*
                                     * For some reason, we've wound up with two
                                     * contact computer objects that represent
                                     * the same computer. We'll delete this one
                                     * and keep the first one.
                                     */
                                    contact.getComputers()
                                        .remove(test);
                                else
                                    computer = test;
                            }
                        }
                        if (computer == null)
                        {
                            computer = contact
                                .createComputer();
                            computer
                                .setLag(Long.MAX_VALUE - 100);
                            computer.setName(computerName);
                            computer.getStatus()
                                .setIdleTime(-1);
                            // TODO: set the computer type
                            contact.getComputers().add(
                                computer);
                        }
                        String lagString = com
                            .getComputerSetting(contact
                                .getUserid(), computerName,
                                "public-lag");
                        if (lagString != null)
                            computer.setLag(Long
                                .parseLong(lagString));
                        // TODO: implement active stuff, which is true if one of
                        // the OpenGroove windows is the focused window
                        computer.getStatus().setActive(
                            false);
                        String idleTimeString = com
                            .getComputerSetting(contact
                                .getUserid(), computerName,
                                "public-idle");
                        if (idleTimeString != null)
                            computer
                                .getStatus()
                                .setIdleTime(
                                    Long
                                        .parseLong(idleTimeString));
                        computer.getStatus().setKnown(true);
                        computer.getStatus()
                            .setNonexistant(false);
                        computer.getStatus().setOnline(
                            com.getUserStatus(
                                contact.getUserid(),
                                computerName).isOnline());
                    }// end of computer foreach
                    for (ContactComputer computer : new ArrayList<ContactComputer>(
                        contact.getComputers()))
                    {
                        if (!StringUtils
                            .isMemberOfIgnoreCase(computer
                                .getName(),
                                contactComputers))
                            /*
                             * We have here a computer which is present locally
                             * but not on the server. This means that the owner
                             * of the computer deleted it, so we should delete
                             * our local copy too. Note that we won't get an
                             * empty array if we are not connected to the
                             * internet; we'll get an exception instead, so we
                             * don't havew to worry that we'll clobber any local
                             * computers when we're offline.
                             */
                            contact.getComputers().remove(
                                computer);
                    }// end of contact computer deleting loop
                    /*
                     * Now we'll iterate over the computers again. This time,
                     * we're going to add their status stuff together to get the
                     * value that should be used for the contact overall.
                     */
                    long idleTime = -1;
                    boolean isActive = false;
                    boolean isOnline = false;
                    for (ContactComputer computer : contact
                        .getComputers().isolate())
                    {
                        if (computer.getStatus().isActive())
                            isActive = true;
                        if (computer.getStatus().isOnline())
                            isOnline = true;
                        idleTime = Math.max(idleTime,
                            computer.getStatus()
                                .getIdleTime());
                    }
                    if (idleTime == -1)
                        idleTime = getServerTime();
                    contact.getStatus().setActive(isActive);
                    contact.getStatus().setIdleTime(
                        idleTime);
                    contact.getStatus().setOnline(isOnline);
                }// end of (if contact exists) statement
                status.setKnown(true);
            }
            catch (Exception exception)
            {
                /*
                 * This just means that we had an exception while connecting to
                 * the server. It's no big deal, as the server probably just
                 * disconnected us while we were trying to talk with it. We'll
                 * just proceed with the updates we've downloaded so far.
                 */
                exception.printStackTrace();
            }
        }
        else
        {
            /*
             * There's no connection to the server, so we'll mark the contact as
             * offline if it's known, otherwise we'll leave it as unknown.
             * 
             * TODO: we might actually want to just set it to unknown here.
             */
            contact.getStatus().setOnline(false);
            for (ContactComputer computer : new ArrayList<ContactComputer>(
                contact.getComputers()))
            {
                computer.getStatus().setOnline(false);
            }
        }
        /*
         * Now, the updating of the launchbar contact icons with contact status.
         * The tooltip for each contact will update itself automatically, so
         * there's no need to do that in this method.
         */
        updateContactIcon(contact.getUserid());
    }
    
    private final Object contactIconLock = new Object();
    
    private void updateContactIcon(String contactUserid)
    {
        synchronized (contactIconLock)
        {
            JideButton button = contactStatusLabelMap
                .get(contactUserid);
            if (button != null)
                button.setIcon(new ImageIcon(getStatusIcon(
                    getStorage().getContact(contactUserid)
                        .getStatus()).getImage()));
        }
    }
    
    /**
     * Returns an icon that should be displayed as the user's icon, based on the
     * user's current status.
     * 
     * @param contact
     * @return
     */
    public OpenGroove.Icons getStatusIcon(
        ContactStatus status)
    {
        OpenGroove.Icons statusIcon;
        if (!status.isKnown())
            statusIcon = OpenGroove.Icons.USER_UNKNOWN_16;
        else if (status.isNonexistant())
            statusIcon = OpenGroove.Icons.USER_NONEXISTANT_16;
        else if (!status.isOnline())
            statusIcon = OpenGroove.Icons.USER_OFFLINE_16;
        else if ((status.getIdleTime() + IDLE_THRESHOLD) < getServerTime())
            statusIcon = OpenGroove.Icons.USER_IDLE_16;
        else
            statusIcon = OpenGroove.Icons.USER_ONLINE_16;
        return statusIcon;
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
    
    public ConditionalTimer getContactStatusTimer()
    {
        return contactStatusTimer;
    }
    
    public SearchForUsersFrame getSearchForUsersFrame()
    {
        return searchForUsersFrame;
    }
    
    public long getServerTime()
    {
        return getStorage().getLocalUser().getServerTime();
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
    
    public ConditionalTimer getTimeSyncTimer()
    {
        return timeSyncTimer;
    }
    
    public void setTimeSyncTimer(
        ConditionalTimer timeSyncTimer)
    {
        this.timeSyncTimer = timeSyncTimer;
    }
    
    public ConditionalTimer getSubscriptionTimer()
    {
        return subscriptionTimer;
    }
    
    public void setSubscriptionTimer(
        ConditionalTimer subscriptionTimer)
    {
        this.subscriptionTimer = subscriptionTimer;
    }
    
    public void uploadCurrentStatus()
    {
        try
        {
            if (com.getCommunicator().isActive())
                com.setComputerSetting(getLocalUser()
                    .getComputer(), "public-idle", ""
                    + lastIdle);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public TaskbarNotification getNonexistantContactNotification()
    {
        return nonexistantContactNotification;
    }
    
    public void setNonexistantContactNotification(
        TaskbarNotification nonexistantContactNotification)
    {
        this.nonexistantContactNotification = nonexistantContactNotification;
    }
    
    public void showNonexistantContactInfoDialog()
    {
        ArrayList<Contact> contacts = getStorage()
            .getLocalUser().getContacts().isolate();
        ArrayList<Contact> nonexistantContacts = new ArrayList<Contact>();
        for (Contact contact : contacts)
        {
            if (contact.getStatus().isNonexistant())
                nonexistantContacts.add(contact);
        }
        /*
         * We've built our list of nonexistant contacts. Now we show a
         * JOptionPane with the list of contacts in it. If there are none,
         * however, then we refresh the contacts pane (which should get rid of
         * the taskbar notification).
         */
        if (nonexistantContacts.size() == 0)
        {
            refreshContactsPaneAsynchronously();
            return;
        }
        launchbar.show();
        JOptionPane
            .showMessageDialog(
                launchbar,
                "<html>The following contacts do not exist:<br/><br/>"
                    + StringUtils.delimited(
                        nonexistantContacts
                            .toArray(new Contact[0]),
                        new ToString<Contact>()
                        {
                            
                            @Override
                            public String toString(
                                Contact object)
                            {
                                return object
                                    .getDisplayName();
                            }
                        }, "<br/>")
                    + "<br/><br/>You should consider deleting "
                    + (nonexistantContacts.size() == 1 ? "this contact"
                        : "these contacts") + ".");
    }
    
    public String getDisplayName()
    {
        return getLocalUser().getDisplayName();
    }
    
    public JideButton getLocalStatusButton()
    {
        return localStatusButton;
    }
    
    public JideButton getLocalUsernameButton()
    {
        return localUsernameButton;
    }
    
    public void setLocalStatusButton(
        JideButton localStatusButton)
    {
        this.localStatusButton = localStatusButton;
    }
    
    public void setLocalUsernameButton(
        JideButton localUsernameButton)
    {
        this.localUsernameButton = localUsernameButton;
    }
    
    public String createLaunchbarTitle()
    {
        return getLocalUser().getDisplayName()
            + " - Launchbar - OpenGroove";
    }
    
    public void updateLocalStatusIcon()
    {
        if (!com.getCommunicator().isActive())
        {
            /*
             * Offline
             */
            localStatusButton
                .setIcon(new ImageIcon(
                    OpenGroove.Icons.USER_OFFLINE_16
                        .getImage()));
            return;
        }
        if ((lastIdle + IDLE_THRESHOLD) < getServerTime())
        {
            /*
             * Idle
             */
            localStatusButton.setIcon(new ImageIcon(
                OpenGroove.Icons.USER_IDLE_16.getImage()));
            return;
        }
        /*
         * Online
         */
        localStatusButton.setIcon(new ImageIcon(
            OpenGroove.Icons.USER_ONLINE_16.getImage()));
        return;
    }
}
