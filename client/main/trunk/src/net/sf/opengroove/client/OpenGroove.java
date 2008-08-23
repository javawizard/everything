package net.sf.opengroove.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.RenderingHints;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLSocket;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;

import org.awl.Wizard;

import net.sf.opengroove.client.com.AuthenticationException;
import net.sf.opengroove.client.com.CommandCommunicator;
import net.sf.opengroove.client.com.OldCommunicator;
import net.sf.opengroove.client.com.LowLevelCommunicator;
import net.sf.opengroove.client.download.PluginDownloadManager;
import net.sf.opengroove.client.features.FeatureComponentHandler;
import net.sf.opengroove.client.features.FeatureManager;
import net.sf.opengroove.client.help.HelpViewer;
import net.sf.opengroove.client.notification.NotificationAdapter;
import net.sf.opengroove.client.notification.TaskbarNotificationFrame;
import net.sf.opengroove.client.plugins.Plugin;
import net.sf.opengroove.client.plugins.PluginManager;
import net.sf.opengroove.client.text.TextManager;
import net.sf.opengroove.client.ui.ChooseLAFDialog;
import net.sf.opengroove.client.ui.ConfigureOpenGrooveDialog;
import net.sf.opengroove.client.ui.ConfigureWorkspaceDialog;
import net.sf.opengroove.client.ui.CreateWorkspaceDialog;
import net.sf.opengroove.client.ui.FillContainer;
import net.sf.opengroove.client.ui.ImportWorkspaceDialog;
import net.sf.opengroove.client.ui.InviteToWorkspaceDialog;
import net.sf.opengroove.client.ui.StandardWizardPage;
import net.sf.opengroove.client.ui.frames.LoginFrame;
import net.sf.opengroove.client.workspace.WorkspaceManager;
import net.sf.opengroove.client.workspace.WorkspaceWrapper;
import net.sf.opengroove.security.Hash;

import base64.Base64Coder;

import com.elevenworks.swing.panel.SimpleGradientPanel;
import com.jidesoft.dialog.AbstractDialogPage;
import com.jidesoft.dialog.PageList;
import com.jidesoft.wizard.WizardDialogPane;
import com.l2fprod.common.swing.JLinkButton;

/**
 * This is the class that does most of the thinking for Convergia. If you don't
 * want Convergia to update itself (for example, if you are developing it), then
 * you would run this class. If you do want Convergia to update itself, you
 * would call Loader, which then calls this class after a bit of processing
 * related to updates.
 * 
 * @author Alexander Boyd
 * 
 */
@SuppressWarnings("deprecation")
public class OpenGroove
{
    static
    {
        Statics.run();
    }
    static boolean updatesEnabled = false;
    /**
     * This file object is a folder that contains opengroove's data.
     */
    public static final File sfile = new File("appdata");
    
    public static final String WORKSPACE_DEFAULT_NAME = "Unnamed workspace";
    
    public static AuthenticationDialog authDialog;
    
    private static BufferedImage trayimage;
    /**
     * Each user that is currently logged in has an entry in this map. The keys
     * are userids and the values are user contexts that correspond to that
     * user.
     */
    public static final Hashtable<String, UserContext> userContextMap = new Hashtable<String, UserContext>();
    
    private static LoginFrame loginFrame;
    
    private static WizardDialogPane newAccountWizardPane;
    
    private static JFrame newAccountFrame;
    
    /**
     * This is the icon that should be used as the icon for all Convergia
     * windows, as well as the tray icon.
     * 
     * @return
     */
    public static Image getWindowIcon()
    {
        return trayimage;
    }
    
    /**
     * Each user has their own help folder, which can be retrieved via the
     * Storage.getHelpStore() method. This folder (the one in this field, not
     * the one in the method previously mentioned) contains the built-in help
     * files that should be copied over to the user's own help folder upon
     * OpenGroove startup. This makes it so that each user has the internal help
     * pages, as well as any help pages provided by the user's server and the
     * user's plugins.
     */
    public static final File INTERNAL_HELP_FOLDER = new File(
        "help");
    
    private static BufferedImage[] notificationTrayImages;
    
    private static int[] notificationTrayDelays;
    
    private static int currentNotificationIndex;
    
    private static TrayIcon trayicon;
    
    // used to ensure that only one OpenGroove Client is running at a time
    private static ServerSocket ss;
    
    /**
     * This enum represents all of the icons available to OpenGroove classes.
     * Sometime in the future the actual icon paths will be moved to
     * configuration, so that the user can change the icons used by OpenGroove.
     * 
     * @author Alexander Boyd
     * 
     */
    
    public static enum Icons
    {
        CONFIGURE_WORKSPACE_16("configure-workspace.png",
            16), DELETE_WORKSPACE_16(
            "delete-workspace.png", 16), INVITE_TO_WORKSPACE_16(
            "invite-to-workspace.png", 16), POP_OUT_16(
            "pop-out.png", 16), WORKSPACE_INFO_16(
            "workspace-info.png", 16), WORKSPACE_WARNING_16(
            "workspace-warning.png", 16), BACK_BUTTON_32(
            "back-button.png", 32), FOLDER_DOCS_16(
            "folder-docs.png", 16), EDIT_16("edit16.gif",
            16), NOTES_16("notes16.gif", 16);
        private int size;
        
        private Icons(String iconPath, int size)
        {
            this.iconPath = iconPath;
            this.size = size;
        }
        
        public int getSize()
        {
            return size;
        }
        
        private String iconPath;
        
        public String getIconPath()
        {
            return iconPath;
        }
        
        private Image image;
        
        public Image getImage()
        {
            return image;
        }
        
        public void setImage(Image image)
        {
            this.image = image;
        }
    }
    
    public static TaskbarNotificationFrame notificationFrame;
    
    private static boolean isNotificationAlertShowing = false;
    
    private static BufferedImage trayofflineimage;
    
    private static BufferedImage[] notificationTrayOfflineImages;
    
    private static int[] notificationTrayOfflineDelays;
    
    private static final String SYSTEM_UPDATE_SITE = "http://sysup.ogis.opengroove.org";
    
    private static final int LOCK_PORT = 61116;
    
    private static final String RESTART_CLASSPATH = "bin;*;lib/*";
    
    private static final Hashtable<String, UserContext> currentUsers = new Hashtable<String, UserContext>();
    
    // FIXME: needs to be localized to the user's operating system and java vm
    private static final String[] restartExecutableString = new String[] {
        "javaw.exe", "-cp", RESTART_CLASSPATH,
        "net.sf.opengroove.client.Loader", "wfl" };
    
    /**
     * restarts OpenGroove. This method takes a few seconds to run before
     * terminating this vm. This method should never return normally. It also
     * doesn't ask the user if they want to save changes to anything, although
     * in the future some sort of API for allowing things to register something
     * like ExitingListeners that check to see if it's ok to exit and save if
     * necessary could be added.
     * 
     * @param parent
     *            The window that should be used as the parent of any dialogs
     *            that report error messages. This can be null, but it's highly
     *            recommended not to do so.
     */
    public static void restartOpenGroove(Window parent)
    {
        try
        {
            System.out.println("closing socket");
            ss.close();
            Thread.sleep(3000);
            System.out.println("running rt.exec");
            Runtime.getRuntime().exec(
                restartExecutableString);
            System.out.println("waiting");
            Thread.sleep(1000);
            System.out.println("exiting");
            System.exit(0);
            System.out
                .println("exited (this should never be printed)");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JOptionPane
                .showMessageDialog(
                    parent,
                    "OpenGroove could not be restarted. You will need to manually restart OpenGroove.");
            
        }
        
    }
    
    private static final Object authLock = new Object();
    
    private static PopupMenu trayPopup;
    
    /*
     * More rambling by me on converting this stuff over to multiple users
     * logged in at a time...
     * 
     * The loop that logs a user in, that's part of the main method, and the
     * stuff following it that initializes a workspace manager, plugins, etc.
     * should be moved into it's own method. There should be a method that shows
     * an authentication dialog, which should probably be not a dialog but a
     * singleton window. When a button to log in is clicked on it, it does stuff
     * like make sure that the user's not already logged in, then hides itself
     * and runs the authentication loop. The method that shows the auth dialog
     * and the section that occurs when the auth button is clicked should be
     * synchronized on the same lock, so that the auth dialog will never be
     * shown if the system's currently logging a user in. It should be sync'd
     * until at least it sticks the user context into the map of user contexts
     * in OpenGroove, so that future authentications with the auth dialog won't
     * accidentally try to log the same user in twice.
     * 
     * The wizard for new registrations is a dialog, so it should either be
     * shown on top of the auth singleton window if the user clicks a button to
     * select another account, or on top of the splash screen window if, upon
     * startup, opengroove realizes the user doesn't have any accounts. If the
     * user cancels the wizard, opengroove will still continue running, but it
     * will appear in the taskbar as that no user is logged in, and when the
     * user shows the auth frame, it will show the dialog on top of the auth
     * frame since the user doesn't have any accounts yet.
     * 
     * Essentially, then, the main method of opengroove just loads the taskbar
     * and any global internal plugins, and shows the auth dialog once, and
     * loads the tray icon and notification frame. It then just sits there until
     * someone logs in, at which point the method that handles authenticating
     * gets stuff up and running, or until it gets exited.
     * 
     * For every known user, there's a menu item under the launchbar. It's
     * submenu is either one that contains stuff related to, for example,
     * wokspaces or showing a launchbar, or one that has an item for logging in
     * as that user if the user is not currently logged in.
     */
    /**
     * Starts OpenGroove, without updates. Run the main method of
     * net.sf.opengroove.client.Loader if you want OpenGroove to update itself.
     * 
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        JFrame frame3 = new JFrame("opengroovemain");
        frame3.setSize(800, 10);
        // frame3.show();
        // the above frame was used for debugging the restart feature of
        // opengroove, it needs to be removed some time soon
        boolean waitForLock = args.length > 0
            && args[0].equals("wfl");
        if (waitForLock)
        {
            while (true)
            {
                try
                {
                    frame3.setTitle("accepting");
                    ss = new ServerSocket(LOCK_PORT);
                    frame3.setTitle("breaking");
                    break;
                }
                catch (Exception e)
                {
                    frame3.setTitle("exception,"
                        + e.getClass() + " : "
                        + e.getMessage());
                    e.printStackTrace();
                    Thread.sleep(3000);
                }
            }
        }
        else
        {
            try
            {
                ss = new ServerSocket(LOCK_PORT);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                JFrame frame = new JFrame("OpenGroove");
                frame.setLocationRelativeTo(null);
                frame.show();
                JOptionPane
                    .showMessageDialog(
                        frame,
                        "OpenGroove is already running. You cannot start OpenGroove multiple times.");
                System.exit(0);
            }
        }
        if (waitForLock)
        {
            Thread.sleep(6000);
        }
        try
        {
            // the thread below solves a bug in swing where the first
            // JFileChooser
            // created takes about a minute to create. By doing this in a
            // thread, then by the time we need to actually create a
            // JFileChooser, this one will have finished creating and we won't
            // have to worry about the delay anymore.
            new Thread()
            {
                public void run()
                {
                    new JFileChooser();
                }
            }.start();
            initNewAccountWizard();
            initLoginFrame();
            sfile.mkdirs();
            // the setProperty call below is used to avoid problems with the
            // fade effect for the taskbar notification frame
            System
                .setProperty("sun.java2d.noddraw", "true");
            Storage.initStorage(sfile);
            // PluginManager.loadPlugins();
            // postPluginLoad();
            System.out.println("storage path is "
                + sfile.getCanonicalPath());
            // helpviewer = new HelpViewer(helpFolder);
            initLoginFrame();
            notificationFrame = new TaskbarNotificationFrame();
            new Thread("notification status updater")
            {
                public void run()
                {
                    while (true)
                    {
                        try
                        {
                            Thread.sleep(3000);
                            isNotificationAlertShowing = notificationFrame
                                .containsAlerts();
                        }
                        catch (Exception ex1)
                        {
                            ex1.printStackTrace();
                        }
                    }
                }
            }.start();
            new Thread("tray icon notification updater")
            {
                public void run()
                {
                    while (true)
                    {
                        try
                        {
                            Thread.sleep(3100);
                            if (trayicon != null)
                            {
                                if (isNotificationAlertShowing)
                                {
                                    Image[] nimages;
                                    int[] ndelays;
                                    if (anyServerConnections())
                                    {
                                        nimages = notificationTrayImages;
                                        ndelays = notificationTrayDelays;
                                    }
                                    else
                                    {
                                        nimages = notificationTrayOfflineImages;
                                        ndelays = notificationTrayOfflineDelays;
                                    }
                                    if (currentNotificationIndex >= nimages.length
                                        || currentNotificationIndex >= ndelays.length)
                                        currentNotificationIndex = 0;
                                    trayicon
                                        .setImage(nimages[currentNotificationIndex]);
                                    try
                                    {
                                        Thread
                                            .sleep(ndelays[currentNotificationIndex]);
                                    }
                                    catch (Exception ex1)
                                    {
                                        ex1
                                            .printStackTrace();
                                        try
                                        {
                                            Thread
                                                .sleep(3000);
                                        }
                                        catch (Exception ex12)
                                        {
                                            ex12
                                                .printStackTrace();
                                        }
                                    }
                                    if (++currentNotificationIndex >= notificationTrayImages.length)
                                        currentNotificationIndex = 0;
                                }
                                else
                                {
                                    currentNotificationIndex = 0;
                                    if (anyServerConnections())
                                    {
                                        trayicon
                                            .setImage(trayimage);
                                    }
                                    else
                                    {
                                        trayicon
                                            .setImage(trayofflineimage);
                                    }
                                    Thread.sleep(3100);
                                }
                            }
                        }
                        catch (Exception ex1)
                        {
                            ex1.printStackTrace();
                            try
                            {
                                Thread.sleep(5000);
                            }
                            catch (Exception ex12)
                            {
                                ex12.printStackTrace();
                            }
                        }
                    }
                }
            }.start();
            new Thread("update checker thread")
            {
                public void run()
                {
                    while (true)
                    {
                        try
                        {
                            Thread.sleep(10 * 1000);
                            // checkForUpdates();
                            Thread.sleep(4 * 60 * 1000);// TODO: this is how
                            // often updates are
                            // checked for.
                            // we probably should raise this to something like
                            // 30*60*1000 for a half an hour, or make it
                            // user-configurable but with a minimum limit to
                            // avoid overloading the server with update
                            // requests
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
            /*
             * FIXME: pick up here August 12, 2008, pretty much the rest of the
             * main method needs to be split into it's own method that runs
             * per-user, and replaced with the auto-login checks and stuff.
             * There should, however, still be stuff for initializing the tray
             * icon and putting a menu on it. Actually, this part might want to
             * be in a different method that takes all of th logged in users and
             * adds their context menu, and adds one for all other users that
             * allows to log in as that user. There should also be a check here
             * to see if there are no users, and if that's the case show the
             * wizard that allows a user to add their account.
             */
            trayimage = ImageIO.read(new File(
                "trayicon.gif"));
            trayofflineimage = ImageIO.read(new File(
                "trayoffline.gif"));
            File[] trayfiles = new File(".")
                .listFiles(new FileFilter()
                {
                    
                    public boolean accept(File pathname)
                    {
                        return pathname.getName()
                            .startsWith("traynotify")
                            && pathname.getName().endsWith(
                                ".gif");
                    }
                });
            Arrays.sort(trayfiles);
            notificationTrayImages = new BufferedImage[trayfiles.length];
            notificationTrayDelays = new int[trayfiles.length];
            for (int i = 0; i < trayfiles.length; i++)
            {
                File file = trayfiles[i];
                notificationTrayImages[i] = ImageIO
                    .read(file);
                String filename = file.getName();
                System.out.println(filename);
                int hIndex = filename.lastIndexOf("-");
                System.out.println(hIndex);
                String afterH = filename
                    .substring(hIndex + 1);
                System.out.println(afterH);
                int dIndex = afterH.lastIndexOf(".");
                System.out.println(dIndex);
                String delayString = afterH.substring(0,
                    dIndex);
                System.out.println(delayString);
                notificationTrayDelays[i] = Integer
                    .parseInt(delayString);
            }
            File[] trayofflinefiles = new File(".")
                .listFiles(new FileFilter()
                {
                    
                    public boolean accept(File pathname)
                    {
                        return pathname
                            .getName()
                            .startsWith("trayofflinenotify")
                            && pathname.getName().endsWith(
                                ".gif");
                    }
                });
            Arrays.sort(trayofflinefiles);
            notificationTrayOfflineImages = new BufferedImage[trayofflinefiles.length];
            notificationTrayOfflineDelays = new int[trayofflinefiles.length];
            for (int i = 0; i < trayofflinefiles.length; i++)
            {
                File file = trayofflinefiles[i];
                notificationTrayOfflineImages[i] = ImageIO
                    .read(file);
                String filename = file.getName();
                System.out.println(filename);
                int hIndex = filename.lastIndexOf("-");
                System.out.println(hIndex);
                String afterH = filename
                    .substring(hIndex + 1);
                System.out.println(afterH);
                int dIndex = afterH.lastIndexOf(".");
                System.out.println(dIndex);
                String delayString = afterH.substring(0,
                    dIndex);
                System.out.println(delayString);
                notificationTrayOfflineDelays[i] = Integer
                    .parseInt(delayString);
            }
            trayPopup = new PopupMenu();
            trayicon = new TrayIcon(trayimage,
                "OpenGroove", trayPopup);
            refreshTrayMenu();
            SystemTray.getSystemTray().add(trayicon);
            /*
             * These tooltip settings should be exported into either
             * user-specific settings, or global ones. If user specific, then
             * users can choose (by clicking a button in their launchbar) who's
             * settings are curently active at any given time. This would also
             * apply to looks and feels, although there might be some way with
             * looks and feels to make a particular user's windows appear with
             * one look and feel, and another user's windows appear with another
             * look and feel.
             */
            ToolTipManager.sharedInstance()
                .setDismissDelay(86400);
            ToolTipManager.sharedInstance().setReshowDelay(
                ToolTipManager.sharedInstance()
                    .getReshowDelay());
            ToolTipManager.sharedInstance()
                .setInitialDelay(
                    ToolTipManager.sharedInstance()
                        .getInitialDelay());
            try
            {
                for (Icons icon : Icons.values())
                {
                    icon.setImage(scaleImage(loadImage(icon
                        .getIconPath()), icon.getSize(),
                        icon.getSize()));
                }
            }
            catch (Exception e)
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e1)
                {
                    // TODO Dec 7, 2007 Auto-generated catch block
                    throw new RuntimeException(
                        "TODO auto generated on Dec 7, 2007 : "
                            + e.getClass().getName()
                            + " - " + e.getMessage(), e1);
                }
                e.printStackTrace();
            }
            trayicon
                .addMouseMotionListener(new MouseMotionAdapter()
                {
                    
                    @Override
                    public void mouseMoved(MouseEvent e)
                    {
                        try
                        {
                            if (notificationFrame.ignoreMouseOver
                                && notificationFrame.currentVisibilityLevel > 0)
                                return;
                            if (notificationFrame
                                .listAllNotifications().length == 0)
                                return;
                            notificationFrame
                                .requestDisplay();
                        }
                        catch (Throwable e2)
                        {
                            e2.printStackTrace();
                        }
                    }
                });
            trayicon.addMouseListener(new MouseListener()
            {
                
                public void mouseClicked(MouseEvent e)
                {
                    System.out.println("clicked");
                    if (e.getClickCount() == 2)
                    {
                        showChosenLaunchbar();
                    }
                }
                
                public void mouseEntered(MouseEvent e)
                {
                    System.out.println("entered");
                    notificationFrame.requestDisplay();
                }
                
                public void mouseExited(MouseEvent e)
                {
                    // TODO Auto-generated method stub
                    
                }
                
                public void mousePressed(MouseEvent e)
                {
                    // TODO Auto-generated method stub
                    
                }
                
                public void mouseReleased(MouseEvent e)
                {
                    // TODO Auto-generated method stub
                    
                }
            });
            System.out.println("***got to 1");
            WorkspaceManager.reloadWorkspaces();
            System.out.println("***got to 2");
            WorkspaceManager.reloadWorkspaceMembers();
            System.out.println("***got to 3");
            reloadLaunchbarWorkspaces();
            System.out.println("***GOT TO HERE");
            try
            {
                updateMetadata();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            NotificationAdapter loggedInNotification = new NotificationAdapter(
                new JLabel(
                    "You have successfully logged in to OpenGroove."),
                false, true);
            notificationFrame
                .removeNotification(loadingNotification);
            notificationFrame.addNotification(
                loggedInNotification, true);
            // trayicon.displayMessage(
            // tm("intouch3.started.tray.balloon.caption"),
            // tm("intouch3.started.tray.balloon.message"),
            // TrayIcon.MessageType.NONE);
        }
        catch (Throwable e)
        {
            Thread.sleep(2000);
            e.printStackTrace();
        }
    }
    
    /**
     * If no users are logged in, this method does nothing. If only one user is
     * logged in, this method shows that user's launchbar. If more than one user
     * is logged in, this method shows a dialog asking the user to pick which
     * launchbar they want to show. This dialog will hide itself if it loses
     * focus.
     */
    protected static void showChosenLaunchbar()
    {
        // TODO: implement this method
    }
    
    /**
     * This methods is called when the user clicks the "login" button in the
     * login window. It validates their password (if it's incorrect, a message
     * is shown on the login frame and this method returns) and creates a user
     * context for them. It then does stuff like setting up a launchbar for
     * them, refreshing the taskbar menu, etc.
     */
    protected static void doFrameLogin()
    {
        /*
         * FIXME: This needs to be changed to create a CommandCommunicator and
         * stick it into the com field
         */
        loginFrame.getLoginButton().setEnabled(false);
        loginFrame.getNewAccountButton().setEnabled(false);
        loginFrame.getCancelButton().setEnabled(false);
        try
        {
            synchronized (authLock)
            {
                String userid = loginFrame.getUserid();
                String password = loginFrame
                    .getPasswordField().getText();
                LocalUser user = Storage
                    .getLocalUser(userid);
                if (user.isLoggedIn())
                {
                    JOptionPane
                        .showMessageDialog(
                            loginFrame,
                            "<html>You're already logged in. This is probably a bug if <br/>"
                                + "you're seeing this, so be sure to contact us about it. <br/>"
                                + "Use the help / contact us option in the launchbar menu.");
                    return;
                }
                String encPassword = Hash.hash(password);
                if (!encPassword.equals(user
                    .getEncPassword()))
                {
                    JOptionPane
                        .showMessageDialog(loginFrame,
                            "Incorrect username and/or password.");
                    return;
                }
                /*
                 * Ok, we've checked that the user is not already logged in, and
                 * the user entered the correct password. Now we do all of the
                 * rest of the initialization stuff, such as creating a user
                 * context, hiding the auth dialog, creating the launchbar,
                 * loading plugins, re-generating the taskbar popup menu, etc.
                 */
                UserContext context = new UserContext();
                context.setUserid(userid);
                context.setPassword(password);
                // loadFeatures();
                // loadCurrentUserLookAndFeel();
                loadLaunchBar(userid, context);
                // WorkspaceManager workspaceManager = new
                // WorkspaceManager(context);
                // context.setWorkspaceManager(workspaceManager);
                // workspaceManager.reloadWorkspaces();
                // workspaceManager.reloadWorkspaceMembers();
                // reloadLaunchbarWorkspaces(context);
            }
        }
        finally
        {
            loginFrame.getLoginButton().setEnabled(true);
            loginFrame.getNewAccountButton().setEnabled(
                true);
            loginFrame.getCancelButton().setEnabled(true);
            refreshTrayMenu();
        }
    }
    
    /**
     * Refreshes the task tray menu. This involves removing all items from it,
     * and adding items back as they currently should be.
     */
    public static void refreshTrayMenu()
    {
        trayPopup.removeAll();
        // The popup menu should contain, in this order:
        //
        // --[online users] userid
        // ------workspaces
        // ----------[list of workspaces] workspace name
        // ------launchbar
        // --------------------------
        // ------[plugin-generated items]
        // --------------------------
        // ------logout
        // --[offline users] userid
        // ------login
        // --------------------------
        // --New Account
        // --------------------------
        // --About
        // --Restart
        // --Exit
        final LocalUser[] onlineUsers = Storage
            .getUsersLoggedIn();
        final LocalUser[] offlineUsers = Storage
            .getUsersNotLoggedIn();
        if (onlineUsers.length > 0)
        {
            for (final LocalUser user : onlineUsers)
            {
                
            }
            trayPopup.addSeparator();
        }
        if (offlineUsers.length > 0)
        {
            for (final LocalUser user : offlineUsers)
            {
                Menu userMenu = new Menu(user.getUserid());
                MenuItem loginItem = new MenuItem("Login");
                loginItem
                    .addActionListener(new ActionListener()
                    {
                        
                        @Override
                        public void actionPerformed(
                            ActionEvent e)
                        {
                            if (!user.isLoggedIn())
                                showLoginWindow(user
                                    .getUserid());
                        }
                    });
                userMenu.add(loginItem);
                trayPopup.add(userMenu);
            }
            trayPopup.addSeparator();
        }
        trayPopup.add(new AMenuItem("New Account")
        {
            
            @Override
            public void run(ActionEvent e)
            {
                showNewAccountWizard(onlineUsers.length == 0
                    && offlineUsers.length == 0);
            }
        });
        trayPopup.addSeparator();
        trayPopup.add(new AMenuItem("About")
        {
            
            @Override
            public void run(ActionEvent e)
            {
                showAboutWindow();
            }
        });
        trayPopup.add(new AMenuItem("Exit")
        {
            
            @Override
            public void run(ActionEvent e)
            {
                /*
                 * TODO: This should check with the user before exiting to make
                 * sure that they really want to exit. It should also make use
                 * of some sort of exit listener stuff (I think I talked about
                 * this in another comment in this file) to make sure that there
                 * aren't any unsaved changes in any workspaces or tools, or
                 * other plugins. It should also probably wait until all user
                 * communicators successfully shut down (within a reasonable
                 * time limit) to make sure that if a message chunk is in the
                 * process of being sent, it won't just quit right in the
                 * middle.
                 */
                System.exit(0);
            }
        });
    }
    
    /**
     * Shows the login window for the specified user. If the login window is
     * already showing, it is changed to this user. If this user is already
     * logged in, this method does nothing. If this user does not exist, or the
     * userid is null, the new account wizard is shown instead.<br/><br/>
     * 
     * If the new user wizard is currently showing, it is brought to the front,
     * and this method returns.
     * 
     * @param userid
     */
    public static void showLoginWindow(String userid)
    {
        if (newAccountFrame.isShowing())
        {
            bringToFront(newAccountFrame);
            return;
        }
        LocalUser user = userid == null ? null : Storage
            .getLocalUser(userid);
        if (user != null && user.isLoggedIn())
            return;
        if (user == null)
        {
            /*
             * Show the new user wizard.
             */
            show
        }
    }
    
    private static void initLoginFrame()
    {
        loginFrame = new LoginFrame();
        loginFrame.getCancelButton().addActionListener(
            new ActionListener()
            {
                
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    loginFrame.getPasswordField().setText(
                        "");
                    loginFrame
                        .getRememberPasswordCheckbox()
                        .setSelected(false);
                    loginFrame.hide();
                }
            });
        ActionListener loginActionListener = new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doFrameLogin();
            }
        };
        loginFrame.getLoginButton().addActionListener(
            loginActionListener);
        loginFrame.getPasswordField().addActionListener(
            loginActionListener);
        loginFrame.getNewAccountButton().addActionListener(
            new ActionListener()
            {
                
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    doNewAccountWizard(loginFrame);
                }
            });
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setResizable(false);
        loginFrame.setAlwaysOnTop(true);
        
    }
    
    private static void showLoginFrame(String userid)
    {
        showLoginWindow(userid);
    }
    
    private static void initNewAccountWizard()
    {
        newAccountFrame = new JFrame(
            "New Account - OpenGroove");
        newAccountFrame.setSize(650, 500);
        newAccountFrame.setLocationRelativeTo(null);
    }
    
    /**
     * Shows the new account wizard. If the wizard is already showing, it will
     * be brought to the front. If not, the current wizard page will be reset to
     * it's first page. If <code>welcome</code> is true, the wizard will have
     * an added initial screen that gives the user more information about
     * OpenGroove. If not, this initial screen won't be displayed, and the first
     * screen will instead be the one that allows users to choose between
     * creating a new account, importing one they have already created, or
     * entering an account configuration code. If the user finishes the wizard
     * successfully, the login box will be shown.
     */
    private static void showNewAccountWizard(boolean welcome)
    {
        if (newAccountWizardPane.isShowing())
        {
            bringToFront(newAccountFrame);
            return;
        }
        newAccountWizardPane = new WizardDialogPane()
        {
            private JLabel titleLabel;
            
            @Override
            protected void updateBannerPanel(
                JComponent bannerPanel,
                AbstractDialogPage page)
            {
                titleLabel.setText(page.getTitle());
            }
            
            @Override
            public JComponent createBannerPanel()
            {
                FillContainer fill = new FillContainer();
                fill.setFillImageName("newaccount");
                fill.setLayout(new BorderLayout());
                titleLabel = new JLabel(" ");
                fill.add(titleLabel);
                JPanel panel = new JPanel();
                panel.setLayout(new BorderLayout());
                panel.add(fill);
                panel.add(new JSeparator(),
                    BorderLayout.SOUTH);
                fill.setBorder(new EmptyBorder(12, 20, 12,
                    10));
                return panel;
            }
        };
        PageList pages = new PageList();
        // pages here
        if (welcome)
        {
            StandardWizardPage welcomePage = new StandardWizardPage(
                "Welcome to OpenGroove", false, true, true,
                false)
            {
                
                @Override
                protected void init()
                {
                    addText(getWelcomeWizardMessage(), Font
                        .decode(null));
                }
            };
            pages.append(welcomePage);
        }
        StandardWizardPage newOrExistPage = new StandardWizardPage(
            "New or Existing Account?", welcome, true,
            true, false)
        {
            public JComponent createWizardContent()
            {
                JPanel panel = new JPanel();
                JRadioButton newButton = new JRadioButton(
                    "Create a new OpenGroove account");
                JRadioButton existingButton = new JRadioButton(
                    "Use an OpenGroove account that you have already created");
                ButtonGroup newOrExistGroup = new ButtonGroup();
                newOrExistGroup.add(newButton);
                newOrExistGroup.add(existingButton);
                panel.setLayout(new BorderLayout());
                JPanel inner = new JPanel();
                inner.setLayout(new BoxLayout(inner,
                    BoxLayout.Y_AXIS));
                panel.add(inner, BorderLayout.NORTH);
                inner.add(newButton);
                JLabel newLabel = new JLabel(
                    "Choose this if this is your first time using OpenGroove");
                newLabel.setFont(Font.decode(null));
                inner.add(newLabel);
                inner.add(existingButton);
                JLabel existingLabel = new JLabel(
                    "Choose this if you already have an OpenGroove "
                        + "account and would like to use it on this computer.");
                existingLabel.setFont(Font.decode(null));
                inner.add(existingLabel);
                return panel;
            }
            
            @Override
            protected void init()
            {
            }
        };
        // end pages
        newAccountWizardPane.setPageList(pages);
        newAccountWizardPane.initComponents();
        newAccountFrame.getContentPane().setLayout(
            new BorderLayout());
        newAccountFrame.getContentPane().removeAll();
        newAccountFrame.getContentPane().add(
            newAccountWizardPane);
        newAccountFrame.setSize(650, 500);
        newAccountFrame.setResizable(false);
        newAccountFrame.setLocationRelativeTo(null);
        newAccountFrame.show();
    }
    
    protected static String getWelcomeWizardMessage()
    {
        return ""
            + "This appears to be your first time using OpenGroove "
            + "on this computer. Before you can use OpenGroove, you "
            + "need to create an account. Click Next to continue.";
    }
    
    protected static boolean anyServerConnections()
    {
        for (UserContext context : currentUsers.values())
        {
            if (context.getCom() != null
                && context.getCom().getCommunicator() != null
                && context.getCom().getCommunicator()
                    .isActive())
                return true;
        }
        return false;
    }
    
    /**
     * Loads all of the feature plugins and gets them up and running. The
     * PluginManager is responsible for actually loading the feature classes,
     * but the feature manager initializes them.
     */
    private static void loadFeatures()
    {
        FeatureManager.loadFeatures();
    }
    
    private static HashMap<String, Class<LookAndFeel>> lookAndFeelClasses = new HashMap<String, Class<LookAndFeel>>();
    
    private static void postPluginLoad()
    {
        // load all of the lookandfeel plugins into the swing UIManager and load
        // the systemwide look and feel
        for (Plugin<LookAndFeel> plugin : PluginManager
            .getByType("lookandfeel"))
        {
            try
            {
                String name = plugin.getMetadata()
                    .getProperty("name");
                if (name == null)
                    name = plugin.getImplClass().getName();
                LookAndFeelInfo info = new LookAndFeelInfo(
                    name, plugin.getImplClass().getName());
                System.out.println("lookandfeel name:"
                    + info.getName() + ",class:"
                    + info.getClassName());
                lookAndFeelClasses.put(info.getClassName(),
                    plugin.getImplClass());
                UIManager.installLookAndFeel(info);
            }
            catch (Exception ex1)
            {
                ex1.printStackTrace();
            }
        }
        String lookAndFeel = Storage
            .getSystemConfigProperty("lookandfeel");
        if (lookAndFeel != null)
        {
            try
            {
                if (lookAndFeelClasses.get(lookAndFeel) != null)
                    UIManager
                        .setLookAndFeel(lookAndFeelClasses
                            .get(lookAndFeel).newInstance());
                else
                    UIManager.setLookAndFeel(lookAndFeel);
            }
            catch (Exception ex1)
            {
                ex1.printStackTrace();
            }
        }
    }
    
    /**
     * Loads and applies the user's look and feel. Currently, this is always
     * called right after OpenGroove starts up. In the future, it could be
     * configured not to do anything if OpenGroove is started in safe mode, so
     * that if the look and feel messes up part of the ui it's not permanent.
     */
    private static void loadCurrentUserLookAndFeel()
    {
        String lookAndFeel = Storage
            .getConfigProperty("lookandfeel");
        if (lookAndFeel != null)
        {
            try
            {
                if (lookAndFeelClasses.get(lookAndFeel) != null)
                {
                    UIManager.put("ClassLoader",
                        lookAndFeelClasses.get(lookAndFeel)
                            .getClassLoader());
                    UIManager
                        .setLookAndFeel(lookAndFeelClasses
                            .get(lookAndFeel).newInstance());
                }
                else
                {
                    UIManager.put("ClassLoader", null);
                    UIManager.setLookAndFeel(lookAndFeel);
                }
            }
            catch (Exception ex1)
            {
                ex1.printStackTrace();
            }
        }
        swingUIReload();
    }
    
    /**
     * Asks the user to select a new look and feel to use for OpenGroove. This
     * method returns immediately, but a dialog will be open over the launchbar.
     */
    private static void promptForUserLookAndFeel()
    {
        final ChooseLAFDialog dialog = new ChooseLAFDialog(
            launchbar);
        final LookAndFeelInfo[] availableLafs = UIManager
            .getInstalledLookAndFeels();
        final JRadioButton[] rButtons = new JRadioButton[availableLafs.length];
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < rButtons.length; i++)
        {
            rButtons[i] = new JRadioButton(availableLafs[i]
                .getName());
            group.add(rButtons[i]);
            dialog.getLookAndFeelPanel().add(rButtons[i]);
            if (availableLafs[i].getClass().equals(
                Storage.getConfigProperty("lookandfeel")))
                rButtons[i].setSelected(true);
        }
        new Thread()
        {
            public void run()
            {
                dialog.show();
                dialog.dispose();
                if (!dialog.wasOkClicked())
                    return;
                int clickedIndex = -1;
                for (int i = 0; i < rButtons.length; i++)
                {
                    if (rButtons[i].isSelected())
                    {
                        clickedIndex = i;
                        break;
                    }
                }
                if (clickedIndex == -1)// didn't select an item
                    return;
                LookAndFeelInfo info = availableLafs[clickedIndex];
                Storage.setConfigProperty("lookandfeel",
                    info.getClassName());
                loadCurrentUserLookAndFeel();
            }
        }.start();
    }
    
    /**
     * gets the port to connect to the OpenGroove server on. This is obsolete
     * with the addition of realm servers, and will be removed shortly.
     * 
     * @return
     */
    private static int getConnectPort()
    {
        if (Storage.getConfigProperty("icport") == null)
            Storage.setConfigProperty("icport", "64482");
        return Integer.parseInt(Storage
            .getConfigProperty("icport"));
    }
    
    /**
     * not used right now, I'm not sure what it was for but it will be removed
     * once I'm absolutely sure it's not going to mess anything up.
     * 
     * @return
     */
    private static boolean getUseFirstConnect()

    {
        if (Storage.getConfigProperty("icuseFirstConnect") == null)
            Storage.setConfigProperty("icuseFirstConnect",
                "false");
        return Storage.getConfigProperty(
            "icuseFirstConnect").toLowerCase().startsWith(
            "t");
    }
    
    /**
     * gets the hostname or ip address to connect to. This is obsolete with the
     * addition of realm servers, and will be removed shortly.
     * 
     * @return
     */
    private static String getConnectHost()
    {
        if (Storage.getConfigProperty("ichost") == null)
            Storage.setConfigProperty("ichost",
                "trivergia.com");
        return Storage.getConfigProperty("ichost");
    }
    
    /**
     * Shows an about window that describes OpenGroove and it's current version.
     * In the future, this will also show the about screens for any plugins that
     * have an about screen.
     */
    protected static void showAboutWindow()
    {
        /*
         * TODO: This needs to show about info for plugins, credits for stuff
         * that require it (such as jide, arimaa, and the bezier algorithm used
         * for the fill containers), and info about who develops OpenGroove
         * (which is just me, Alex, right now, but could be additional people in
         * the future) and how to contribute. It also needs to be split into
         * it's own frame, instead of a dialog, that could be always-on-top.
         */
        showLaunchBar();
        JOptionPane.showMessageDialog(launchbar,
            "<html>OpenGroove<br/>Version "
                + getDisplayableVersion()

                + "<br/><br/>http://www.opengroove.org");// TODO:
    }
    
    private static final Object updateCheckLock = new Object();
    
    /**
     * checks for updates to OpenGroove, and downloads them if available. In the
     * future, this will be changed so that the user can choose whether to
     * download them automatically or prompt the user first. It will also be
     * changed to show a changelog to the user.
     * 
     * @return
     */
    public static boolean checkForUpdates()
    {
        if (updatesEnabled)
        {
            synchronized (updateCheckLock)
            {
                System.out.println("checking for updates");
                try
                {
                    URL updateUrl = new URL(
                        SYSTEM_UPDATE_SITE);
                    Properties p = new Properties();
                    p.load(updateUrl.openStream());
                    String localVersion;
                    try
                    {
                        localVersion = Storage
                            .readFile(new File("version"));
                    }
                    catch (Exception e)
                    {
                        localVersion = "0";
                    }
                    int localVersionNumber = Integer
                        .parseInt(localVersion);
                    int remoteVersionNumber = Integer
                        .parseInt(p
                            .getProperty("versionindex"));
                    boolean isAlreadyUpdated = new File(
                        "appdata/systemupdates/version")
                        .exists()
                        && Integer
                            .parseInt(Storage
                                .readFile(new File(
                                    "appdata/systemupdates/version"))) == remoteVersionNumber;
                    if (remoteVersionNumber > localVersionNumber
                        && !isAlreadyUpdated)// we need
                    // to
                    // update
                    {
                        URL updateJarUrl = new URL(p
                            .getProperty("url"));
                        UpdateNotification notification = new UpdateNotification();
                        JProgressBar bar = notification
                            .getProgressBar();
                        notificationFrame.addNotification(
                            notification, true);
                        File updateFile = new File(
                            "appdata/systemupdates/updates.jar");
                        File versionFile = new File(
                            "appdata/systemupdates/version");
                        if (!updateFile.getParentFile()
                            .exists())
                            updateFile.getParentFile()
                                .mkdirs();
                        if (!versionFile.getParentFile()
                            .exists())
                            versionFile.getParentFile()
                                .mkdirs();
                        versionFile.delete();
                        updateFile.delete();
                        FileOutputStream fos = new FileOutputStream(
                            updateFile);
                        byte[] buffer = new byte[1024];
                        int amount;
                        HttpURLConnection updateJarConn = (HttpURLConnection) updateJarUrl
                            .openConnection();
                        updateJarConn.connect();
                        int max = updateJarConn
                            .getContentLength();
                        if (max != -1)
                            bar.setMaximum(max / 1024);
                        else
                            bar.setIndeterminate(true);
                        InputStream in = updateJarConn
                            .getInputStream();
                        int amountSoFar = 0;
                        while ((amount = in.read(buffer)) != -1)
                        {
                            amountSoFar += amount;
                            bar
                                .setValue(amountSoFar / 1024);
                            fos.write(buffer, 0, amount);
                        }
                        fos.flush();
                        fos.close();
                        Storage.writeFile(""
                            + remoteVersionNumber,
                            versionFile);
                        notificationFrame
                            .removeNotification(notification);
                        final NotificationAdapter readyNotification = new NotificationAdapter(
                            new JLabel(
                                "OpenGroove has been updated. Restart\nfor updates to take effect."),
                            true, false)
                        {
                            public void clicked()
                            {
                                notificationFrame
                                    .removeNotification(this);
                            }
                        };
                        notificationFrame.addNotification(
                            readyNotification, true);
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                catch (Exception ex1)
                {
                    ex1.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }
    
    /**
     * reloads the panel in the launchbar that shows the user's workspaces.
     */
    public static void reloadLaunchbarWorkspaces(
        UserContext context)
    {
        JPanel workspacePanel = context.getWorkspacePanel();
        synchronized (workspacePanel)
        {
            System.out
                .println("repainting workspace panel");
            WorkspaceWrapper[] workspaces = context
                .getWorkspaceManager().getAll();
            workspacePanel.removeAll();
            PopupMenu workspacesSubMenu = context
                .getWorkspacesSubMenu();
            workspacesSubMenu.removeAll();
            for (final WorkspaceWrapper w : workspaces)
            {
                System.out.println("**got to 5");
                JLinkButton mainButton = new JLinkButton(w
                    .getName());
                String participantList = delimited(w
                    .getParticipants(), "<br/>");
                mainButton
                    .setToolTipText("<html><b>Type</b>: "
                        + w.getPluginMetadata()
                            .getProperty("name")
                        + "<br/><b>Creator</b>: "
                        + WorkspaceManager
                            .getWorkspaceCreator(w.getId())
                        + "<br/><b>Participants</b>:<br/>"
                        + participantList);
                mainButton.setFocusable(false);
                System.out.println("***got to 6");
                JLinkButton configureButton = new JLinkButton(
                    new ImageIcon(
                        Icons.CONFIGURE_WORKSPACE_16
                            .getImage()));
                System.out.println("***got to 7");
                JLinkButton deleteButton = new JLinkButton(
                    new ImageIcon(Icons.DELETE_WORKSPACE_16
                        .getImage()));
                JLinkButton inviteToButton = new JLinkButton(
                    new ImageIcon(
                        Icons.INVITE_TO_WORKSPACE_16
                            .getImage()));
                System.out.println("***got to 8");
                deleteButton.setFocusable(false);
                configureButton.setFocusable(false);
                inviteToButton.setFocusable(false);
                JPanel p = new JPanel();
                p.setLayout(new BoxLayout(p,
                    BoxLayout.X_AXIS));
                p.add(pad(deleteButton, 1, 1));
                p.add(pad(configureButton, 1, 1));
                if (w.isMine())
                    p.add(pad(inviteToButton, 1, 1));
                p.add(pad(mainButton, 1, 1));
                mainButton.setOpaque(false);
                configureButton.setOpaque(false);
                deleteButton.setOpaque(false);
                p.setOpaque(false);
                configureButton
                    .setToolTipText("Configure this workspace and edit settings");
                inviteToButton
                    .setToolTipText("Invite someone to this workspace");
                deleteButton
                    .setToolTipText("Delete this workspace");
                configureButton
                    .addActionListener(new ActionListener()
                    {
                        public void actionPerformed(
                            ActionEvent e)
                        {
                            showConfigWindow(w);
                        }
                    });
                mainButton
                    .addActionListener(new ActionListener()
                    {
                        public void actionPerformed(
                            ActionEvent e)
                        {
                            w.getWorkspace().userActivate();
                        }
                    });
                MenuItem item = new MenuItem(w.getName());
                workspacesSubMenu.add(item);
                item.addActionListener(mainButton
                    .getActionListeners()[mainButton
                    .getActionListeners().length - 1]);
                inviteToButton
                    .addActionListener(new ActionListener()
                    {
                        
                        public void actionPerformed(
                            ActionEvent e)
                        {
                            final InviteToWorkspaceDialog dialog = new InviteToWorkspaceDialog(
                                launchbar);
                            dialog.getWorkspaceLabel()
                                .setText(w.getName());
                            dialog.getWorkspaceIdLabel()
                                .setText(w.getId());
                            dialog.getMessageTextArea()
                                .setText(
                                    "I'd like to invite you to my workspace, "
                                        + w.getName());
                            dialog.getCancelButton()
                                .addActionListener(
                                    new ActionListener()
                                    {
                                        
                                        public void actionPerformed(
                                            ActionEvent e)
                                        {
                                            dialog
                                                .dispose();
                                        }
                                    });
                            dialog.getSendButton()
                                .addActionListener(
                                    new ActionListener()
                                    {
                                        
                                        public void actionPerformed(
                                            ActionEvent e)
                                        {
                                            String toUser = dialog
                                                .getUserTextField()
                                                .getText();
                                            if (!ocom
                                                .getCommunicator()
                                                .isActive())
                                            {
                                                JOptionPane
                                                    .showMessageDialog(
                                                        dialog,
                                                        "You are not connected to the internet.");
                                            }
                                            else if (!ocom.allUsers
                                                .contains(toUser))
                                            {
                                                JOptionPane
                                                    .showMessageDialog(
                                                        dialog,
                                                        "The username specified is not a valid username.");
                                            }
                                            else if (!ocom.onlineUsers
                                                .contains(toUser))
                                            {
                                                JOptionPane
                                                    .showMessageDialog(
                                                        dialog,
                                                        "<html>The username specified is a valid username, "
                                                            + "but that user is not online. A<br/>user must be online "
                                                            + "to accept an invitation.");
                                            }
                                            else
                                            {
                                                try
                                                {
                                                    w
                                                        .getAllowedUsers()
                                                        .add(
                                                            toUser);
                                                    w
                                                        .getWorkspace()
                                                        .save();
                                                    updateMetadata();
                                                }
                                                catch (Exception e4)
                                                {
                                                    e4
                                                        .printStackTrace();
                                                }
                                                String inviteText = Base64Coder
                                                    .encodeString(dialog
                                                        .getMessageTextArea()
                                                        .getText());
                                                String workspaceId = w
                                                    .getId();
                                                try
                                                {
                                                    ocom
                                                        .sendMessage(
                                                            toUser,
                                                            "wsi|workspaceinvite|"
                                                                + workspaceId
                                                                + "|"
                                                                + inviteText);
                                                    JOptionPane
                                                        .showMessageDialog(
                                                            dialog,
                                                            "The invite was successfully sent. Contact the recipient to make sure they received it.");
                                                    dialog
                                                        .dispose();
                                                    
                                                }
                                                catch (Exception e3)

                                                {
                                                    e3
                                                        .printStackTrace();
                                                    JOptionPane
                                                        .showMessageDialog(
                                                            dialog,
                                                            "The invite sending failed for an unknown reason.");
                                                }
                                            }
                                        }
                                    });
                            new Thread()
                            {
                                public void run()
                                {
                                    dialog.show();
                                }
                            }.start();
                        }
                    });
                deleteButton
                    .addActionListener(new ActionListener()
                    {
                        
                        public void actionPerformed(
                            ActionEvent e)
                        {
                            if (w.isMine()
                                && (w.getAllowedUsers()
                                    .size() > 1 || (w
                                    .getAllowedUsers()
                                    .size() > 0 && !(w
                                    .getAllowedUsers().get(
                                        0).equals(username)))))// if there is 2
                            // or
                            // more users, or
                            // there is 1 user
                            // and it is not
                            // this user
                            //
                            // AND, in addition to all of the above, i am the
                            // creator of
                            // the workspace
                            {
                                JOptionPane
                                    .showMessageDialog(
                                        launchbar,
                                        "You must delete all allowed users (except for yourself) from the workspace before deleting it");
                                return;
                            }
                            // we are the only user using this workspace, so
                            // confirm
                            // deleting
                            if (JOptionPane
                                .showConfirmDialog(
                                    launchbar,
                                    "Are you sure you want to delete the workspace? This operation cannot be undone.",
                                    null,
                                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
                            {
                                try
                                {
                                    w.getWorkspace()
                                        .shutdown();
                                    if (w.isMine())
                                        ocom
                                            .deleteWorkspace(w
                                                .getId());
                                }
                                catch (Exception e1)
                                {
                                    e1.printStackTrace();
                                    JOptionPane
                                        .showMessageDialog(
                                            launchbar,
                                            "<html>The workspace encountered an error while deleting. Make sure<br/>"
                                                + "you are connected to the internet, then try again. If you still have a problem,"
                                                + "<br/>send an email to webmaster@trivergia.com to report this problem.");
                                    return;
                                }
                                Storage.removeWorkspace(w);
                                WorkspaceManager
                                    .removeWorkspace(w);
                                reloadLaunchbarWorkspaces();
                                Storage.recursiveDelete(w
                                    .getDatastore());
                                JOptionPane
                                    .showMessageDialog(
                                        launchbar,
                                        "The workspace has been deleted.");
                            }
                        }
                    });
                if (w.getWorkspace().isNeedsAttention())
                    p.add(pad(new JLabel(new ImageIcon(
                        Icons.WORKSPACE_WARNING_16
                            .getImage())), 1, 1));
                if (w.getWorkspace().isHasNewInformation())
                    p.add(pad(
                        new JLabel(new ImageIcon(
                            Icons.WORKSPACE_INFO_16
                                .getImage())), 1, 1));
                p.setAlignmentX(0);
                p.setAlignmentY(0);
                workspacePanel.add(p);
            }
            workspacePanel.invalidate();
            workspacePanel.validate();
            workspacePanel.repaint();
            try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException e)
            {
                // TODO Dec 7, 2007 Auto-generated catch block
                throw new RuntimeException(
                    "TODO auto generated on Dec 7, 2007 : "
                        + e.getClass().getName() + " - "
                        + e.getMessage(), e);
            }
            workspacePanel.invalidate();
            workspacePanel.validate();
            workspacePanel.repaint();
            launchbar.invalidate();
            launchbar.validate();
            launchbar.repaint();
        }
        System.out.println("workspace panel repainted.");
    }
    
    /**
     * Shows the Options window (more appropriately dialog) to the user, so that
     * they can configure how OpenGroove works.
     * 
     * @param w
     * @return
     */
    public static boolean showConfigWindow(
        WorkspaceWrapper w)
    {
        return showConfigWindow(w, launchbar);
    }
    
    /**
     * gets a string that represents the version of OpenGroove.
     * 
     * @return
     */
    protected static String getDisplayableVersion()
    {
        String buildNumberString;
        try
        {
            buildNumberString = "-b"
                + Storage.readFile(new File("version"));
        }
        catch (Exception ex1)
        {
            buildNumberString = "";
        }
        return "" + Version.MAJOR + "." + Version.MINOR
            + "." + Version.UPDATE + buildNumberString;
    }
    
    /**
     * Exits OpenGroove. currently, this just calls System.exit(0), but it will
     * check for unsaved changes (via an API to register ExitingListeners or
     * such) in the future.
     */
    protected static void exit()
    {
        System.exit(0);
    }
    
    /**
     * shows the launchbar. If it's not showing (IE hidden) it will be shown. If
     * it's showing, it will be focused. This method does not deiconify the
     * window right now, this will be added later.
     */
    protected static void showLaunchBar()
    {
        launchbar.show();
    }
    
    /**
     * Loads the launchbar. This is called for each user when they log in.
     */
    private static void loadLaunchBar(String userid,
        final UserContext context)
    {
        // TODO: move the icon loading into it's own method
        final JFrame launchbar = new JFrame(userid
            + " - Launchbar - OpenGroove");
        context.setLaunchbar(launchbar);
        launchbar.setIconImage(trayimage);
        launchbar.setSize(300, 500);
        loadLaunchbarMenus(userid, context, launchbar);
        SimpleGradientPanel workspacesGradientPanel = new SimpleGradientPanel(
            new Color(180, 200, 255), new Color(245, 249,
                255), SimpleGradientPanel.VERTICAL);
        workspacesGradientPanel
            .setLayout(new BorderLayout());
        workspacesGradientPanel.setOpaque(true);
        launchbar.getContentPane().setLayout(
            new BorderLayout());
        JTabbedPane launchbarTabbedPane = new JTabbedPane();
        context.setLaunchbarTabbedPane(launchbarTabbedPane);
        launchbarTabbedPane.setFocusable(false);
        launchbar.getContentPane().add(launchbarTabbedPane);
        launchbarTabbedPane.add("Workspaces",
            new JScrollPane(workspacesGradientPanel));
        JPanel contactsPanel = new JPanel();
        context.setContactsPanel(contactsPanel);
        contactsPanel.setLayout(new BoxLayout(
            contactsPanel, BoxLayout.Y_AXIS));
        JPanel p3 = new SimpleGradientPanel(new Color(180,
            200, 255), new Color(245, 249, 255),
            SimpleGradientPanel.VERTICAL);
        JPanel p4 = new JPanel();
        p4.setOpaque(false);
        p4.setLayout(new BoxLayout(p4, BoxLayout.Y_AXIS));
        p4.setBorder(new EmptyBorder(15, 15, 15, 15));
        p3.setLayout(new BoxLayout(p3, BoxLayout.Y_AXIS));
        JLinkButton addContactButton = new JLinkButton(
            "Add a contact");
        addContactButton.setFocusable(false);
        addContactButton
            .addActionListener(new ActionListener()
            {
                
                public void actionPerformed(ActionEvent e)
                {
                    runAddContactWizard();
                }
            });
        setPlainFont(addContactButton);
        p4.add(pad(addContactButton, 2, 2));
        p4.add(pad(contactsPanel, 2, 6));
        contactsPanel.setOpaque(false);
        p3.add(p4);
        launchbarTabbedPane.add("Contacts",
            new JScrollPane(p3));
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15,
            15, 15));
        workspacesGradientPanel.add(p);
        p.setOpaque(false);
        // p.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JPanel workspacePanel = new JPanel();
        context.setWorkspacePanel(workspacePanel);
        workspacePanel.setOpaque(false);
        // workspacePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        workspacePanel.setLayout(new BoxLayout(
            workspacePanel, BoxLayout.Y_AXIS));
        JLinkButton createWorkspaceButton = new JLinkButton(
            tm("launchbar.workspaces.create.workspace.link"));
        createWorkspaceButton.setFocusable(false);
        importWorkspaceButton.setFocusable(false);
        createWorkspaceButton
            .addActionListener(new ActionListener()
            {
                
                public void actionPerformed(ActionEvent e)
                {
                    runNewWorkspaceWizard();
                }
            });
        setPlainFont(createWorkspaceButton);
        createWorkspaceButton
            .setAlignmentX(JComponent.LEFT_ALIGNMENT);
        workspacePanel
            .setAlignmentX(JComponent.LEFT_ALIGNMENT);
        p.add(pad(wrap(createWorkspaceButton), 2, 2));
        p.add(pad(workspacePanel, 2, 6));
        launchbar
            .setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        if (context.getStorage().getConfigProperty(
            "launchbarx") != null
            && context.getStorage().getConfigProperty(
                "launchbary") != null)
        {
            launchbar.setLocation(Integer.parseInt(context
                .getStorage().getConfigProperty(
                    "launchbarx")), Integer
                .parseInt(context.getStorage()
                    .getConfigProperty("launchbary")));
        }
        if (context.getStorage().getConfigProperty(
            "launchbarwidth") != null
            && context.getStorage().getConfigProperty(
                "launchbarheight") != null)
        {
            launchbar.setSize(Integer.parseInt(context
                .getStorage().getConfigProperty(
                    "launchbarwidth")), Integer
                .parseInt(context.getStorage()
                    .getConfigProperty("launchbarheight")));
        }
        launchbar
            .addComponentListener(new ComponentListener()
            {
                
                public void componentHidden(ComponentEvent e)
                {
                    // TODO Auto-generated method stub
                    
                }
                
                public void componentMoved(ComponentEvent e)
                {
                    context.getStorage()
                        .setConfigProperty("launchbarx",
                            "" + launchbar.getX());
                    context.getStorage()
                        .setConfigProperty("launchbary",
                            "" + launchbar.getY());
                }
                
                public void componentResized(
                    ComponentEvent e)
                {
                    context.getStorage().setConfigProperty(
                        "launchbarwidth",
                        "" + launchbar.getWidth());
                    context.getStorage().setConfigProperty(
                        "launchbarheight",
                        "" + launchbar.getHeight());
                }
                
                public void componentShown(ComponentEvent e)
                {
                    // TODO Auto-generated method stub
                    
                }
            });
    }
    
    protected static void runAddContactWizard()
    {
        // TODO Auto-generated method stub
        
    }
    
    protected void reloadLaunchbarContacts()
    {
        
    }
    
    /**
     * Loads the menu bar on the launchbar.
     */
    private static void loadLaunchbarMenus(String userid,
        UserContext context, JFrame launchbar)
    {
        JMenuBar bar = new JMenuBar();
        launchbar.setJMenuBar(bar);
        final JMenu convergiaMenu = new IMenu("OpenGroove",
            new IMenuItem[] {
                new IMenuItem("Check for updates")
                {
                    
                    public void actionPerformed(
                        ActionEvent e)
                    {
                        new Thread()
                        {
                            public void run()
                            {
                                if (!checkForUpdates())
                                    JOptionPane
                                        .showMessageDialog(
                                            launchbar,
                                            "No updates were found. OpenGroove is up to date.");
                            }
                        }.start();
                    }
                }, new IMenuItem("Options")
                {
                    
                    public void actionPerformed(
                        ActionEvent e)
                    {
                        showOptionsWindow();
                    }
                } });
        final JMenu pluginsMenu = new IMenu("Plugins",
            new IMenuItem[] { new IMenuItem(
                "Manage plugins")
            {
                
                public void actionPerformed(ActionEvent e)
                {
                    PluginManager
                        .showManageInstalledPluginsDialog(launchbar);
                }
            } });
        JMenu lafMenu = new JMenu("Choose look and feel");
        convergiaMenu.add(lafMenu);
        final LookAndFeelInfo[] availableLafs = UIManager
            .getInstalledLookAndFeels();
        final JRadioButtonMenuItem[] rButtons = new JRadioButtonMenuItem[availableLafs.length];
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < rButtons.length; i++)
        {
            rButtons[i] = new JRadioButtonMenuItem(
                availableLafs[i].getName());
            group.add(rButtons[i]);
            lafMenu.add(rButtons[i]);
            final LookAndFeelInfo laf = availableLafs[i];
            final JRadioButtonMenuItem rItem = rButtons[i];
            rButtons[i]
                .addActionListener(new ActionListener()
                {
                    
                    public void actionPerformed(
                        ActionEvent e)
                    {
                        new Thread()
                        {
                            public void run()
                            {
                                final WaitingDialog dialog = new WaitingDialog(
                                    launchbar);
                                dialog
                                    .getMainLabel()
                                    .setText(
                                        "Please wait while the look and feel is applied...");
                                dialog
                                    .setLocationRelativeTo(null);
                                dialog.pack();
                                dialog.setSize(dialog
                                    .getWidth() + 30,
                                    dialog.getHeight() + 5);
                                dialog.invalidate();
                                dialog.validate();
                                dialog.repaint();
                                try
                                {
                                    Thread.sleep(200);
                                }
                                catch (InterruptedException e)
                                {
                                }// give time to validate and repaint before
                                // we
                                // start updating the look and feel
                                new Thread()
                                {
                                    public void run()
                                    {
                                        dialog.show();
                                    }
                                }.start();
                                Storage.setConfigProperty(
                                    "lookandfeel", laf
                                        .getClassName());
                                rItem.setSelected(true);
                                loadCurrentUserLookAndFeel();
                                dialog.dispose();
                                if (JOptionPane
                                    .showConfirmDialog(
                                        launchbar,
                                        "The look and feel has been changed. It is\n"
                                            + "reccomended that you restart OpenGroove. Would\n"
                                            + "you like to restart OpenGroove now?",
                                        null,
                                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                                    restartOpenGroove();
                                
                            }
                        }.start();
                    }
                });
            if (availableLafs[i].getClassName().equals(
                Storage.getConfigProperty("lookandfeel")))
                rButtons[i].setSelected(true);
        }
        
        final JCheckBoxMenuItem alwaysOnTopItem = new JCheckBoxMenuItem(
            "Always on top");
        if (Storage.getConfigProperty("alwaysontop") != null)
        {
            alwaysOnTopItem.setSelected(true);
            launchbar.setAlwaysOnTop(true);
        }
        alwaysOnTopItem
            .addActionListener(new ActionListener()
            {
                
                public void actionPerformed(ActionEvent e)
                {
                    if (alwaysOnTopItem.isSelected())
                    {
                        launchbar.setAlwaysOnTop(true);
                        Storage.setConfigProperty(
                            "alwaysontop", "");
                    }
                    else
                    {
                        launchbar.setAlwaysOnTop(false);
                        Storage.setConfigProperty(
                            "alwaysontop", null);
                    }
                }
            });
        convergiaMenu.add(alwaysOnTopItem);
        
        final JCheckBoxMenuItem useWindowTransparencyItem = new JCheckBoxMenuItem(
            "Use window transparency");
        useWindowTransparencyItem
            .setToolTipText("<html>"
                + "Some windows will fade in and out instead of just<br/>"
                + "appearing, for example, the OpenGroove alerts window.<br/>"
                + "This may not be compatible with all operating systems.");
        if (Storage.getConfigProperty("windowtrans") != null
            && Storage.getConfigProperty("windowtrans")
                .startsWith("t"))
        {
            // use
            useWindowTransparencyItem.setSelected(true);
            useWindowTransparency = WindowTransparencyMode.ENABLED;
        }
        else if (Storage.getConfigProperty("windowtrans") == null)
        {
            // unknown
            useWindowTransparency = (getDefaultTransparencyMode() ? WindowTransparencyMode.ENABLED
                : WindowTransparencyMode.DISABLED);
            useWindowTransparencyItem
                .setSelected(getDefaultTransparencyMode());
        }
        else
        {
            // don't use
            useWindowTransparency = WindowTransparencyMode.DISABLED;
        }
        useWindowTransparencyItem
            .addActionListener(new ActionListener()
            {
                
                public void actionPerformed(ActionEvent e)
                {
                    if (useWindowTransparencyItem
                        .isSelected())
                    {
                        useWindowTransparency = WindowTransparencyMode.ENABLED;
                        Storage.setConfigProperty(
                            "windowtrans", "true");
                    }
                    else
                    {
                        useWindowTransparency = WindowTransparencyMode.DISABLED;
                        Storage.setConfigProperty(
                            "windowtrans", "false");
                    }
                }
            });
        convergiaMenu.add(useWindowTransparencyItem);
        if (Storage
            .getSystemConfigProperty("autologinuser") != null
            && Storage
                .getSystemConfigProperty("autologinpass") != null)
        {
            convergiaMenu.add(new IMenuItem(
                "Don't auto log me in")
            {
                
                public void actionPerformed(ActionEvent e)
                {
                    if (JOptionPane
                        .showConfirmDialog(
                            launchbar,
                            "Are you sure you want to stop auto-logging you in?",
                            null, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                    {
                        Storage.setSystemConfigProperty(
                            "autologinuser", null);
                        Storage.setSystemConfigProperty(
                            "autologinpass", null);
                        convergiaMenu.remove(this);
                        convergiaMenu.invalidate();
                        convergiaMenu.validate();
                        convergiaMenu.repaint();
                    }
                }
            });
        }
        JMenu helpMenu = new IMenu("Help", new IMenuItem[] {
            new IMenuItem("Help")
            {
                
                public void actionPerformed(ActionEvent e)
                {
                    helpviewer.show();
                }
            }, new IMenuItem("Contact us")
            {
                
                public void actionPerformed(ActionEvent e)
                {
                    JOptionPane
                        .showMessageDialog(
                            launchbar,
                            "Questions? Comments? Send an email to webmaster@trivergia.com");
                }
            }, new IMenuItem("About")
            {
                
                public void actionPerformed(ActionEvent e)
                {
                    showAboutWindow();
                }
            } });
        bar.add(convergiaMenu);
        bar.add(pluginsMenu);
        bar.add(helpMenu);
    }
    
    /**
     * shows a dialog to the user where they can select plugins from the ptl to
     * download. (ptl stands for Public Tool List, which is somewhat of a
     * misnomer because it contains lots of types of plugins, not just tools)
     * this method will return once the user has closed the dialog. if the user
     * chooses to restart Convergia after installing the plugin, then this
     * method will not return normally.hb
     * 
     * @param frame
     *            the frame to pass to the dialog's constructor. the dialog will
     *            be shown on top of this frame.
     * @param types
     *            the types of plugins to show in the list, or null to show all
     *            plugins. for example, if you only wanted the user to download
     *            tools, you could pass a String[] that contains one string,
     *            "tool".
     */
    public static void findNewPlugins(JFrame frame,
        String[] types)
    {
        try
        {
            PluginDownloadManager.promptForDownload(frame,
                types, PluginManager.pluginFolder
                    .list(new SubversionFilenameFilter()));
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }
    
    protected static void showOptionsWindow()
    {
        final ConfigureOpenGrooveDialog dialog = new ConfigureOpenGrooveDialog(
            launchbar);
        showStatusInfo(dialog);
        new Thread()
        {
            public void run()
            {
                dialog.show();
                dialog.dispose();
            }
        }.start();
    }
    
    /**
     * adds status concerning OpenGroove's connectivity to the
     * ConfigureOpenGrooveDialog specified. This needs to be made less
     * hard-coded, and will change significantly with the addition of realm
     * servers.
     * 
     * @param dialog
     */
    private static void showStatusInfo(
        ConfigureOpenGrooveDialog dialog)
    {
        Socket socket = ocom.getCommunicator().getSocket();
        dialog.getMConnectedServerLabel().setText(
            socket == null ? "N/A" : ocom.getCommunicator()
                .getConnectedHost());
        dialog.getMConnectedPortLabel()
            .setText(
                socket == null ? "N/A" : ""
                    + ocom.getCommunicator()
                        .getConnectedPort());
        dialog.getMConnectivityStatus().setText(
            socket == null ? "offline" : "online");
        dialog.getMConnectionSecurityStatus().setText(
            socket == null ? "N/A"
                : (socket instanceof SSLSocket ? "secure"
                    : "non-secure"));
    }
    
    /**
     * Does nothing. I can't remember why it's here, but I'll remove it once I'm
     * sure it won't mess anything up.
     * 
     * @param c
     * @return
     */
    private static JComponent wrap(JComponent c)
    {
        return c;
    }
    
    /**
     * Scales the image specified to the size specified, discarding aspect
     * ratio.
     * 
     * @param image
     *            the image to scale
     * @param width
     *            the new width
     * @param height
     *            the new height
     * @return a new image, which is the original image scaled to the width and
     *         height specified
     */
    private static Image scaleImage(Image image, int width,
        int height)
    {
        BufferedImage b = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        g.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(image, 0, 0, width, height, null);
        return b;
    }
    
    /**
     * loads the image specified from the file specified. The string passed in
     * is relative to the icons folder.
     * 
     * @param string
     * @return
     */
    private static Image loadImage(String string)
    {
        System.out.println("********about to load "
            + string);
        try
        {
            return ImageIO.read(new File("icons", string));
        }
        catch (MalformedURLException e)
        {
            // TODO Dec 7, 2007 Auto-generated catch block
            throw new RuntimeException(
                "TODO auto generated on Dec 7, 2007 : "
                    + e.getClass().getName() + " - "
                    + e.getMessage(), e);
        }
        catch (IOException e)
        {
            // TODO Dec 7, 2007 Auto-generated catch block
            throw new RuntimeException(
                "TODO auto generated on Dec 7, 2007 : "
                    + e.getClass().getName() + " - "
                    + e.getMessage(), e);
        }
    }
    
    /**
     * sets the font on the component specified to the plain font (IE a font
     * that is not bold).
     * 
     * @param c
     */
    public static void setPlainFont(JComponent c)
    {
        c.setFont(c.getFont().deriveFont(Font.PLAIN));
    }
    
    /**
     * Adds an EmptyBorder to the component specified and returns it.
     * 
     * @param <T>
     * @param c
     * @param w
     * @param h
     * @return
     */
    public static <T extends JComponent> T pad(T c, int w,
        int h)
    {
        c.setBorder(BorderFactory.createEmptyBorder(h, w,
            h, w));
        return c;
    }
    
    /**
     * Shows a screen welcoming the user to OpenGroove. This is obsolete, and
     * will be replaced by the wizard-style introduction that is coming with the
     * addition of realm servers.
     */
    private static void showWelcomeToInTouchScreen()
    {
        WelcomeFirstTimeFrame wframe = new WelcomeFirstTimeFrame();
        wframe.setLocationRelativeTo(null);
        wframe
            .setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        wframe.show();
        while (!wframe.getOkButton().isSelected())
        {
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
            }
        }
        wframe.dispose();
    }
    
    /**
     * opens the "create new user wizard" account. when the wizard is finished,
     * or cancelled, this method returns. the return value is null if the wizard
     * ws cancelled or the name of the user just created or downloaded if the
     * wizard finished.
     * 
     * @return
     */
    private static String runCreateUserWizard()
    {
        AddUserFrame uframe = new AddUserFrame();
        uframe
            .setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        uframe.setLocationRelativeTo(null);
        uframe.show();
        while (true)
        {
            System.out.println("t-loop");
            while (!(uframe.getOkButton().isSelected()
                || uframe.getNewAccountButton()
                    .isSelected() || uframe
                .getCancelButton().isSelected()))
            {
                try
                {
                    Thread.sleep(200);
                }
                catch (InterruptedException e)
                {
                    // TODO Oct 22, 2007 Auto-generated catch block
                    throw new RuntimeException(
                        "TODO auto generated on Oct 22, 2007 : "
                            + e.getClass().getName()
                            + " - " + e.getMessage(), e);
                }
            }
            boolean wasOk = uframe.getOkButton()
                .isSelected();
            boolean wasCancel = uframe.getCancelButton()
                .isSelected();
            boolean wasNewAccount = uframe
                .getNewAccountButton().isSelected();
            uframe.getOkButton().setSelected(false);
            uframe.getCancelButton().setSelected(false);
            uframe.getNewAccountButton().setSelected(false);
            if (wasOk)
            {
                if (uframe.getUsernameField().getText()
                    .length() < 1
                    || uframe.getPasswordField().getText()
                        .length() < 1)
                {
                    JOptionPane
                        .showMessageDialog(
                            uframe,
                            tm("intouch3.adduser.window.popup.youmustenterinfo"));
                    continue;
                }
                final WaitingDialog wdialog = new WaitingDialog(
                    uframe);
                wdialog
                    .getMainLabel()
                    .setText(
                        tm("intouch3.adduser.window.popup.lookingupinfo"));
                wdialog.setLocationRelativeTo(uframe);
                new Thread()
                {
                    public void run()
                    {
                        wdialog.show();
                    }
                }.start();
                try
                {
                    Thread.sleep(800);
                }
                catch (InterruptedException e)
                {
                }
                boolean wasSuccessfulAuth = false;
                try
                {
                    LowLevelCommunicator com2 = null;
                    try
                    {
                        com2 = new LowLevelCommunicator(
                            getConnectHost(),
                            getConnectPort(), true);
                        com2.authenticate(uframe
                            .getUsernameField().getText(),
                            uframe.getPasswordField()
                                .getText());
                        wasSuccessfulAuth = true;
                    }
                    catch (AuthenticationException e)
                    {
                        
                    }
                    try
                    {
                        com2.close();
                    }
                    catch (Exception e)
                    {
                        
                    }
                }
                catch (Exception e)
                {
                    JOptionPane
                        .showMessageDialog(
                            uframe,
                            "<html>You do not appear to be connected to the internet. Please<br/>"
                                + "connect to the internet, and try again. If this does<br/>"
                                + "not solve your problem, we are probably experiencing<br/>"
                                + "techincal difficulties with our server.");
                    continue;
                }
                wdialog.hide();
                wdialog.dispose();
                if (wasSuccessfulAuth)
                {
                    Storage
                        .storeUser(uframe
                            .getUsernameField().getText(),
                            uframe.getPasswordField()
                                .getText());
                    uframe.hide();
                    uframe.dispose();
                    return uframe.getUsernameField()
                        .getText();
                }
                else
                {
                    JOptionPane
                        .showMessageDialog(
                            uframe,
                            tm("intouch3.adduser.window.popup.wronguserorpassword"));
                    continue;
                }
            }
            else if (wasNewAccount)
            {
                try
                {
                    Desktop
                        .getDesktop()
                        .browse(
                            new URI(
                                "http://register.opengroove.org"));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    JOptionPane
                        .showMessageDialog(
                            uframe,
                            "OpenGroove may not have been able to open the new user web page. To create an account, visit http://register.opengroove.org");
                }
                catch (URISyntaxException e)
                {
                    e.printStackTrace();
                    JOptionPane
                        .showMessageDialog(uframe,
                            "An internal error has occured. OpenGroove will shut down.");
                    System.exit(0);
                }
            }
            else if (wasCancel)
            {
                uframe.hide();
                uframe.dispose();
                return null;
            }
        }
    }
    
    private static long nextGenId = 0;
    
    /**
     * creates a new id. the id should be unique for the whole OpenGroove
     * system. the first part of the id, up until the first hyphen, is this
     * user's username.
     * 
     * TODO: work out what defines the user's "username" with the addition of
     * realm servers, IE is it the user's userid (probably) or just their
     * username. This would result in a colon ( : ) character in their username
     * which may cause problems.
     * 
     * @return
     */
    public static synchronized String generateId()
    {
        String d = Double.toString(Math.random()).replace(
            ".", "");
        return username + "-" + System.currentTimeMillis()
            + "-" + nextGenId++ + "-"
            + d.substring(1, Math.min(d.length(), 7));
    }
    
    /**
     * shorthand for TextManager.text(key);
     * 
     * @param key
     * @return
     */
    public static String tm(String key)
    {
        return TextManager.text(key);
    }
    
    /**
     * shorthand for TextManager.tm(key,params);
     * 
     * @param key
     * @param params
     * @return
     */
    public static String tm(String key, String... params)
    {
        return TextManager.text(key, params);
    }
    
    private static Dialog currentDialog = null;
    
    /**
     * shows the config window for a specified workspace. if there is currently
     * a dialog showing (IE another config dialog, create workspace, import
     * workspace, etc) then false is returned and the config dialog is not
     * shown. if there is no current dialog, true is returned almost
     * immediately, IE the dialog is shown in a separate thread so this thread
     * does not block while the dialog is showing.
     * 
     * @param workspace
     * @param frame
     * @return
     */
    public static boolean showConfigWindow(
        final WorkspaceWrapper workspace, JFrame frame)
    {
        if (currentDialog != null
            && currentDialog.isShowing())
            return false;
        final ConfigureWorkspaceDialog dialog = new ConfigureWorkspaceDialog(
            frame);
        dialog.getIdLabel().setText(workspace.getId());
        dialog.getTypeLabel().setText(
            PluginManager.getById(workspace.getTypeId())
                .getMetadata().getProperty("name"));
        dialog.setAllUsers(ocom.allUsers
            .toArray(new String[0]));
        currentDialog = dialog;
        Map<String, JComponent> customComponents = workspace
            .getWorkspace().getConfigurationComponents();
        if (customComponents != null)
        {
            for (Map.Entry<String, JComponent> entry : customComponents
                .entrySet())
            {
                dialog.getMainTabbedPane().addTab(
                    entry.getKey(), entry.getValue());
            }
        }
        dialog.setTitle("" + workspace.getName()
            + " - Configure - OpenGroove");
        dialog.setLocationRelativeTo(null);
        dialog.getManagerLabel().setText(
            WorkspaceManager.getWorkspaceCreator(workspace
                .getId()));
        dialog.getWorkspaceNameField().setText(
            workspace.getName());
        dialog.getRemoveMemberButton().setEnabled(
            workspace.isMine());
        dialog.getAddMemberButton().setEnabled(
            workspace.isMine());
        addAllToModel(dialog.getAllowedMembersModel(),
            workspace.getAllowedUsers());
        addAllToModel(dialog.getParticipantModel(),
            workspace.getParticipants());
        new Thread()
        {
            public void run()
            {
                dialog.show();
                dialog.dispose();
                // finished configuration, now save the data reported
                currentDialog = null;
                String newName = dialog
                    .getWorkspaceNameField().getText();
                workspace.setName(newName);
                ArrayList<String> allowedUsers = modelToList(dialog
                    .getAllowedMembersModel());
                if (workspace.isMine())
                {
                    workspace.getAllowedUsers().addAll(
                        allowedUsers);
                    workspace.getAllowedUsers().retainAll(
                        allowedUsers);
                }
                // now notify the workspace
                try
                {
                    workspace.getWorkspace()
                        .configurationSaved();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                // send our new metadata to the server
                try
                {
                    updateMetadata();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                // reload the workspaces showing up on the launchbar in case
                // they changed the name
                try
                {
                    reloadLaunchbarWorkspaces();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                workspace.getWorkspace().save();
            }
        }.start();
        return true;
    }
    
    /**
     * converts the contents of this DefaultListModel to an ArrayList.
     * 
     * @param allowedMembersModel
     * @return
     */
    protected static ArrayList modelToList(
        DefaultListModel allowedMembersModel)
    {
        ArrayList list = new ArrayList();
        for (Object o : allowedMembersModel.toArray())
        {
            list.add(o);
        }
        return list;
    }
    
    /**
     * creates the user's public metadata. This is obsolete with the addition of
     * realm servers and will be removed.
     * 
     * @return
     */
    private static Properties createMetadata()
    {
        Properties p = new Properties();
        p.setProperty("stat_version_major", ""
            + Version.MAJOR);
        p.setProperty("stat_version_minor", ""
            + Version.MINOR);
        p.setProperty("stat_version_update", ""
            + Version.UPDATE);
        try
        {
            p.setProperty("stat_version_build", Storage
                .readFile(new File("version")));
        }
        catch (Exception ex1)
        {
            p.setProperty("stat_version_build", "unknown");
        }
        p.setProperty("stat_version_string", ""
            + getDisplayableVersion());
        p.setProperty("stat_jvm_version", System
            .getProperty("java.vm.version"));
        p.setProperty("stat_jvm_runtime_name", System
            .getProperty("java.runtime.name"));
        p.setProperty("stat_jvm_vendor", System
            .getProperty("java.vm.vendor"));
        p.setProperty("stat_jvm_name", System
            .getProperty("java.vm.name"));
        p.setProperty("stat_jvm_runtime_version", System
            .getProperty("java.runtime.version"));
        p.setProperty("stat_java_version", System
            .getProperty("java.version"));
        p.setProperty("stat_java_vendor", System
            .getProperty("java.vendor"));
        p.setProperty("stat_last_online", ""
            + ocom.getServerTime());
        ArrayList<String> myWorkspaces = new ArrayList<String>();
        List<WorkspaceWrapper> allW = Arrays
            .asList(WorkspaceManager.getAll());
        ArrayList<String> allWIds = new ArrayList<String>();
        for (WorkspaceWrapper w : allW)
        {
            allWIds.add(w.getId());
            if (w.isMine())
            {
                myWorkspaces.add(w.getId());
            }
        }
        p.setProperty("workspaces_owner", delimited(
            myWorkspaces, ","));
        p.setProperty("workspaces_participant", delimited(
            allWIds, ","));
        for (WorkspaceWrapper w : allW)
        {
            if (w.isMine())
            {
                p.setProperty("workspace_" + w.getId()
                    + "_users", delimited(w
                    .getAllowedUsers(), ","));
                p.setProperty("workspace_" + w.getId()
                    + "_type", w.getTypeId());
            }
            if (w.getInfo(username) != null)
                p.setProperty("workspace_" + w.getId()
                    + "_info", w.getInfo(username));
        }
        return p;
    }
    
    /**
     * returns a string containing each of the items in the list specified,
     * separated by <code>delimiter</code>. if there are no items, the empty
     * string is returned. this method is designed to be approximately the
     * opposite of String.split, except that split uses regex instead of literal
     * strings.
     * 
     * @param items
     * @param delimiter
     * @return
     */
    public static String delimited(List<String> items,
        String delimiter)
    {
        String s = "";
        for (String i : items)
        {
            if (!s.equals(""))
                s += delimiter;
            s += i;
        }
        return s;
    }
    
    /**
     * adds the contents of the List specified to the list model specified.
     * 
     * @param allowedMembersModel
     * @param allowedUsers
     */
    private static void addAllToModel(
        DefaultListModel allowedMembersModel,
        List allowedUsers)
    {
        for (Object o : allowedUsers)
        {
            allowedMembersModel.addElement(o);
        }
    }
    
    /**
     * regenerates this user's metadata, and sends it to the server. This is
     * obsolete with the addition of realm servers, and will be removed shortly.
     * 
     */
    public static void updateMetadata()
    {
        ocom.setUserMetadata(WorkspaceManager
            .generateMetadata(createMetadata()));
        for (WorkspaceWrapper workspace : WorkspaceManager
            .getAll())
        {
            if (workspace.isMine())
                try
                {
                    ocom.setWorkspacePermissions(workspace
                        .getId(), workspace
                        .getAllowedUsers().toArray(
                            new String[0]));
                }
                catch (Exception ex1)
                {
                    ex1.printStackTrace();
                }
        }
    }
    
    /**
     * Shows the user a wizard for creating a new workspace. This is expected to
     * undergo heavy modification with the addition of realm servers.
     * 
     * @return
     */
    public static boolean runNewWorkspaceWizard()
    {
        if (!ocom.getCommunicator().isActive())
        {
            JOptionPane
                .showMessageDialog(
                    launchbar,
                    "You must be connected to the internet to create a new workspace.");
        }
        if (currentDialog != null
            && currentDialog.isShowing())
        {
            return false;
        }
        final CreateWorkspaceDialog dialog = new CreateWorkspaceDialog(
            launchbar);
        dialog.setLocationRelativeTo(launchbar);
        JPanel p = dialog.getTypePanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        final Plugin[] plugins = PluginManager.getByType(
            "workspace").toArray(new Plugin[0]);
        final JRadioButton[] buttons = new JRadioButton[plugins.length];
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < buttons.length; i++)
        {
            JPanel op = new JPanel();
            op.setLayout(new BorderLayout());
            JPanel ip = new JPanel();
            ip.setLayout(new BorderLayout());
            op.add(ip, BorderLayout.CENTER);
            JPanel lp = new JPanel();
            lp.setLayout(new BorderLayout());
            op.add(lp, BorderLayout.WEST);
            final JRadioButton b = new JRadioButton("");
            group.add(b);
            if (i == 0)
                b.setSelected(true);
            lp.add(b, BorderLayout.NORTH);
            JLabel mainLabel = new JLabel(plugins[i]
                .getMetadata().getProperty("name"));
            JLabel descriptionLabel = new JLabel(plugins[i]
                .getMetadata().getProperty("description"));
            MouseListener ls = new MouseAdapter()
            {
                
                @Override
                public void mousePressed(MouseEvent e)
                {
                    b.setSelected(true);
                }
            };
            descriptionLabel.addMouseListener(ls);
            mainLabel.addMouseListener(ls);
            setPlainFont(descriptionLabel);
            ip.add(mainLabel, BorderLayout.NORTH);
            ip.add(descriptionLabel, BorderLayout.CENTER);
            buttons[i] = b;
            p.add(pad(op, 1, 12));
        }
        new Thread()
        {
            public void run()
            {
                dialog.show();
                dialog.dispose();
                if (!dialog.wasOkClicked())
                    return;
                String name = dialog.getNameField()
                    .getText();
                Plugin p = null;
                for (int i = 0; i < plugins.length; i++)
                {
                    if (buttons[i].isSelected())
                        p = plugins[i];
                }
                if (p == null)// should never happen unless something is wrong
                    // with swing or there are no workspace plugins
                    // to begin with
                    return;
                WorkspaceWrapper w = new WorkspaceWrapper();
                w.setId(generateId());
                w.setDatastore(new File(Storage
                    .getWorkspaceDataStore(), w.getId()
                    + "-" + System.currentTimeMillis()));
                w.setName(name);
                w.setTypeId(p.getId());
                w.getAllowedUsers().add(username);
                w.getParticipants().add(username);
                try
                {
                    ocom.createWorkspace(w.getId());
                    ocom.checkAccess(w.getId());
                }
                catch (Exception e)
                {
                    JOptionPane
                        .showMessageDialog(
                            launchbar,
                            "The workspace could not be created. Make sure you are connected to the internet, then try again.");
                    return;
                }
                Storage.addOrUpdateWorkspace(w);
                try
                {
                    WorkspaceManager.reloadWorkspaces();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                try
                {
                    WorkspaceManager
                        .reloadWorkspaceMembers();
                }
                catch (Exception ex1)
                {
                    ex1.printStackTrace();
                }
                try
                {
                    reloadLaunchbarWorkspaces();
                }
                catch (Exception ex1)
                {
                    ex1.printStackTrace();
                }
                JOptionPane
                    .showMessageDialog(
                        launchbar,
                        "<html>The workspace was successfully created. click on the<br/>"
                            + "settings icon next to the workspace's name to add additional participants.");
            }
        }.start();
        return true;
    }
    
    /**
     * This method shows a wizard for participating in a workspace where the
     * creator of the workspace has given you permission to participate but has
     * not sent you an invitation. Workspaces are moving towards cryptographic
     * keys for security, which will make it impossible for this method to serve
     * it's intended function, and so it will be removed once the transition is
     * complete.
     * 
     * @return
     */
    public static boolean runImportWorkspaceWizard()
    {
        if (currentDialog != null
            && currentDialog.isShowing())
            return false;
        final ImportWorkspaceDialog dialog = new ImportWorkspaceDialog(
            launchbar);
        dialog.setLocationRelativeTo(launchbar);
        currentDialog = dialog;
        new Thread()
        {
            public void run()
            {
                dialog.setLocationRelativeTo(launchbar);
                dialog.show();
                dialog.dispose();
                if (!dialog.isWasOkClicked())
                    return;
                if (!ocom.getCommunicator().isActive())
                {
                    launchbar.show();
                    JOptionPane
                        .showMessageDialog(launchbar,
                            "You must be connected to the internet to add a workspace");
                    return;
                }
                String id = dialog.getWorkspaceId()
                    .getText();
                if (WorkspaceManager.getById(id) != null)
                {
                    launchbar.show();
                    JOptionPane
                        .showMessageDialog(launchbar,
                            "You are already participating in this workspace");
                    return;
                }
                boolean incorrectId = false;
                if (id.contains("-"))
                {
                    String creator = WorkspaceManager
                        .getWorkspaceCreator(id);
                    String mdString = ocom
                        .getUserMetadata(creator);
                    if (mdString != null)
                    {
                        Properties md = WorkspaceManager
                            .parseMetadata(mdString);
                        String mdWorkspaceAllowedUsers = md
                            .getProperty("workspace_" + id
                                + "_users");
                        if (mdWorkspaceAllowedUsers != null)
                        {
                            String[] allowedUsers = mdWorkspaceAllowedUsers
                                .split("\\,");
                            boolean allowed = false;
                            for (String u : allowedUsers)
                            {
                                if (u
                                    .equals(OpenGroove.username))
                                    allowed = true;
                            }
                            if (!allowed)
                            {
                                launchbar.show();
                                JOptionPane
                                    .showMessageDialog(
                                        launchbar,
                                        "<html>The creator of that workspace, "
                                            + creator
                                            + ", has <br/> not allowed you to participate in this workspace. Contact<br/>"
                                            + "that user, and ask them to add you to the workspace's list<br/>"
                                            + "of allowed users.");
                                return;
                            }
                            String mdWorkspaceType = md
                                .getProperty("workspace_"
                                    + id + "_type");
                            if (mdWorkspaceType != null)
                            {
                                if (PluginManager
                                    .getById(mdWorkspaceType) != null)
                                {
                                    WorkspaceWrapper ws = new WorkspaceWrapper();
                                    ws
                                        .setDatastore(new File(
                                            Storage
                                                .getWorkspaceDataStore(),
                                            id
                                                + "_dstore_"
                                                + System
                                                    .currentTimeMillis()));
                                    ws.setId(id);
                                    ws
                                        .setName(WORKSPACE_DEFAULT_NAME);
                                    ws
                                        .setTypeId(mdWorkspaceType);
                                    Storage
                                        .addOrUpdateWorkspace(ws);
                                    WorkspaceManager
                                        .reloadWorkspaces();
                                    WorkspaceManager
                                        .reloadWorkspaceMembers();
                                    reloadLaunchbarWorkspaces();
                                    for (String u : WorkspaceManager
                                        .getById(id)
                                        .getAllowedUsers())
                                    {
                                        try
                                        {
                                            ocom
                                                .sendMessage(
                                                    u,
                                                    "wsi|reloadusers");
                                        }
                                        catch (Exception e)
                                        {
                                            e
                                                .printStackTrace();
                                        }
                                    }
                                    launchbar.show();
                                    JOptionPane
                                        .showMessageDialog(
                                            launchbar,
                                            "<html>The workspace has been successfully imported. Click<br/>on the configure icon next to it in the launchbar to edit it's settings.<br/><br/>"
                                                + "");
                                    return;
                                }
                                else
                                {
                                    launchbar.show();
                                    JOptionPane
                                        .showMessageDialog(
                                            launchbar,
                                            "<html>You don't have this workspace's type installed.<br/>It's type is "
                                                + mdWorkspaceType
                                                + ".<br/>Please install this type, then try again.");
                                    return;
                                }
                            }
                            else
                            {
                                incorrectId = true;
                            }
                        }
                        else
                        {
                            incorrectId = true;
                        }
                    }
                    else
                    {
                        incorrectId = true;
                    }
                }
                else
                {
                    incorrectId = true;
                }
                if (incorrectId)
                {
                    launchbar.show();
                    JOptionPane
                        .showMessageDialog(launchbar,
                            "The ID specified is not a valid ID.");
                }
            }
        }.start();
        return true;
    }
    
    /**
     * Opens the help viewer, if it is not already open, and shows the specified
     * help topic. Shorthand for helpviewer.showHelpTopic(path) where helpviewer
     * is OpenGroove's singleton HelpViewer.
     * 
     * @param path
     */
    public static void showHelpTopic(String path)
    {
        helpviewer.showHelpTopic(path);
    }
    
    /**
     * Shows information about the user specified. This method is expected to
     * undergo heavy modification with the addition of realm servers.
     * 
     * @param username
     */
    public static void showUserInformationDialog(
        String username)
    {
        showUserInformationDialog(username, launchbar);
    }
    
    private static final String[][] userInfoLabels = new String[][] {
        new String[] { "version_major",
            "Major version number" },
        new String[] { "version_minor",
            "Minor version number" },
        new String[] { "version_update",
            "Update version number" },
        new String[] { "version_string", "Version" },
        new String[] { "version_build", "Build number" },
        new String[] { "", "" }, new String[] { "", "" },
        new String[] { "", "" }, new String[] { "", "" },
        new String[] { "", "" } };
    
    /**
     * see showUserInformationDialog(username).
     * 
     * @param username
     * @param parent
     */
    public static void showUserInformationDialog(
        String username, JFrame parent)
    {
        final JDialog dialog = new JDialog(parent, true);
        Container content = dialog.getContentPane();
        JPanel properties = new JPanel();
        properties.setLayout(new GridLayout(0, 2));
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout());
        content.setLayout(new FlowLayout());
        content.add(wrapper);
        wrapper.add(properties, BorderLayout.CENTER);
        wrapper.add(new JLabel(username),
            BorderLayout.NORTH);
        Properties md = WorkspaceManager.parseMetadata(ocom
            .getUserMetadata(username));
        properties
            .add(new JLabel("OpenGroove Version:   "));
        properties.add(new JLabel(md
            .getProperty("stat_version_string")));
        properties.add(new JLabel("Last Online:   "));
        try
        {
            properties.add(new JLabel(""
                + formatDate(new Date(Long.parseLong(md
                    .getProperty("stat_last_online"))))));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            properties.add(new JLabel("unknown"));
        }
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.invalidate();
        dialog.validate();
        dialog.repaint();
        dialog
            .setDefaultCloseOperation(dialog.DISPOSE_ON_CLOSE);
        new Thread()
        {
            public void run()
            {
                dialog.show();
            }
        }.start();
    }
    
    /**
     * gets the user's preferred date format, suitable for passing into
     * <code>new SimpleDateFormat(String)</code>.
     * 
     * @return
     */
    public static String getDateFormatString()
    {
        // TODO change this so that the user can select their date format,
        // possibly using SettingsManager.
        return "yyyy.MM.dd h:mm:ss aa";
    }
    
    /**
     * formats a date into a string using the format specified by
     * getDateFormatString().
     * 
     * @param date
     * @return
     */
    public static String formatDate(Date date)
    {
        return new SimpleDateFormat(getDateFormatString())
            .format(date);
    }
    
    /**
     * The values of the window transparency setting. This is expected to be
     * moved to SettingsManager, and will be removed then. It's also expected
     * that other things besides fading (such as window slide, just appearing,
     * spinning in, random, etc) will be added, especially since fading is only
     * supported on Windows.
     * 
     * @author Alexander Boyd
     * 
     */
    private static enum WindowTransparencyMode
    {
        ENABLED, DISABLED, UNKNOWN
    }
    
    private static WindowTransparencyMode useWindowTransparency = WindowTransparencyMode.UNKNOWN;
    
    /**
     * returns true if window transparency is to be used, false otherwise.
     * 
     * @return
     */
    public static boolean useWindowTransparency()
    {
        if (useWindowTransparency
            .equals(WindowTransparencyMode.ENABLED))
            return true;
        else if (useWindowTransparency
            .equals(WindowTransparencyMode.DISABLED))
            return false;
        else
            return getDefaultTransparencyMode();
    }
    
    /**
     * returns whether or not transparency is to be used on windows. This is
     * mostly for the fading effect of the TaskbarNotificationFrame.
     * 
     * @return
     */
    private static boolean getDefaultTransparencyMode()
    {
        // return System.getProperty("os.name").contains("Vista");
        return false;
    }
    
    public static void swingUIReload()
    {
        for (Window window : Window.getWindows())
        {
            updateComponentTreeUI(window);
        }
    }
    
    public static void updateComponentTreeUI(Component c)
    {
        updateComponentTreeUI0(c);
        c.invalidate();
        try
        {
            c.validate();
            c.repaint();
        }
        catch (Exception ex1)
        {
            ex1.printStackTrace();
            c.invalidate();
        }
    }
    
    private static void updateComponentTreeUI0(Component c)
    {
        if (c instanceof JComponent)
        {
            JComponent jc = (JComponent) c;
            try
            {
                jc.updateUI();
            }
            catch (Exception ex1)
            {
                ex1.printStackTrace();
                try
                {
                    Thread.sleep(400);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            try
            {
                Thread.sleep(5);// fixes a problem i was having where not
                // all components would get updated,
                // costly in terms of time so:
                // TODO: fix the above problem
            }
            catch (InterruptedException e)
            {
            }
            JPopupMenu jpm = jc.getComponentPopupMenu();
            if (jpm != null && jpm.isVisible()
                && jpm.getInvoker() == jc)
            {
                updateComponentTreeUI(jpm);
            }
        }
        Component[] children = null;
        if (c instanceof JMenu)
        {
            children = ((JMenu) c).getMenuComponents();
        }
        else if (c instanceof Container)
        {
            children = ((Container) c).getComponents();
        }
        if (children != null)
        {
            for (int i = 0; i < children.length; i++)
            {
                updateComponentTreeUI0(children[i]);
            }
        }
    }
    
    public static final String PUBLIC_ADDON_LIST_URL = "http://trivergia.com:8080/ptl.properties";
    
    private static long lastUsedTime = 0;
    
    /**
     * if the frame is hidden. shows it. If the frame is showing, brings it to
     * the front, and if it's iconified, deiconifies it.
     * 
     * @param frame
     */
    public static void bringToFront(JFrame frame)
    {
        frame.show();
        frame.setExtendedState(frame.NORMAL);
        frame.show();// the double show ensures that the frame is flashing or
        // focused
    }
    
    /**
     * returns a formatted string indicating the size of the data specified.
     * String look like 1B, 500B, 350KB, 250GB, etc. Strings only go up to
     * gigabytes, so 2 terabytes (2000 gigabytes) shows up as 2000GB.
     * 
     * @param size
     * @return
     */
    public static String formatDataSize(double size)
    {
        if (size < 1000)
            return "" + size + "B";
        if (size < (1000 * 1000))
            return "" + (size / 1000) + "KB";
        if (size < (1000 * 1000 * 1000))
            return "" + (size / (1000 * 1000)) + "MB";
        return "" + (size / (1000 * 1000 * 1000)) + "GB";
    }
    
    public static void saveJarFile(File target,
        JarFile source, Manifest mf)
    {
        File tempDiscard = null;
        try
        {
            File tempWrite = File.createTempFile(
                "opengroovetemp", ".jar");
            System.out.println("tempwrite:" + tempWrite);
            tempWrite.deleteOnExit();
            JarOutputStream output = new JarOutputStream(
                new FileOutputStream(tempWrite));
            output.putNextEntry(new ZipEntry(
                "META-INF/MANIFEST.MF"));
            mf.write(output);
            System.out.println("manifest:");
            mf.write(System.out);
            System.out.println("---end manifest");
            output.closeEntry();
            for (JarEntry entry : Collections.list(source
                .entries()))
            {
                if (entry.getName().equals(
                    "META-INF/MANIFEST.MF")
                    || entry.getName().equals(
                        "META-INF/MANIFEST.MF"))
                {
                    System.out
                        .println("entry is a manifest");
                    continue;
                }
                output.putNextEntry(entry);
                Storage.copy(source.getInputStream(entry),
                    output);
                output.closeEntry();
            }
            output.flush();
            output.close();
            System.out.println("source is "
                + source.getName());
            System.out.println("target is "
                + target.getPath());
            if (source.getName() != null
                && source.getName()
                    .equals(target.getPath()))
            {
                System.out.println("names are the same");
                source.close();
            }
            if (target.exists())
            {
                System.out.println("about to rename");
                tempDiscard = File.createTempFile(
                    "convergiatemp", ".jar");
                System.out.println(tempDiscard.delete());
                tempDiscard.deleteOnExit();
                System.out.println("renaming target "
                    + target + " to tempdiscard "
                    + tempDiscard);
                longRename(target, tempDiscard);
            }
            System.out.println("renaming tempwrite "
                + tempWrite + " to target " + target);
            longRename(tempWrite, target);
            tempDiscard.delete();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            if (tempDiscard != null && tempDiscard.exists()
                && !target.exists())
                tempDiscard.renameTo(target);
            throw new RuntimeException(
                "couldn't save to the jar file", e);
        }
    }
    
    /**
     * renames the source file to the target file, but using pure java to do so
     * as opposed to the File.renameTo() method. It essentially opens a
     * FileInputStream and FileOutputStream for the source and target files,
     * streams the data from one to the other, and then deletes the source file.
     * 
     * @param src
     * @param target
     * @throws IOException
     */
    public static void longRename(File src, File target)
        throws IOException
    {
        if (target.exists())
            target.delete();
        FileInputStream in = new FileInputStream(src);
        FileOutputStream out = new FileOutputStream(target);
        Storage.copy(in, out);
        out.flush();
        out.close();
        in.close();
        src.delete();
    }
}
