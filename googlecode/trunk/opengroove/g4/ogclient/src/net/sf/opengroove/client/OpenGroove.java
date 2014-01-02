package net.sf.opengroove.client;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.RenderingHints;
import java.awt.SplashScreen;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.sf.opengroove.client.notification.FrameShowingNotification;
import net.sf.opengroove.client.notification.GroupLabelResolver;
import net.sf.opengroove.client.notification.NotificationAdapter;
import net.sf.opengroove.client.notification.TaskbarNotificationFrame;
import net.sf.opengroove.client.oldplugins.PluginManager;
import net.sf.opengroove.client.settings.SettingListener;
import net.sf.opengroove.client.settings.SettingSpec;
import net.sf.opengroove.client.settings.SettingStore;
import net.sf.opengroove.client.settings.SettingsManager;
import net.sf.opengroove.client.settings.types.CheckboxParameters;
import net.sf.opengroove.client.storage.Contact;
import net.sf.opengroove.client.storage.LocalUser;
import net.sf.opengroove.client.storage.Storage;
import net.sf.opengroove.client.text.TextManager;
import net.sf.opengroove.client.ui.FillContainer;
import net.sf.opengroove.client.ui.ItemChooser;
import net.sf.opengroove.client.ui.SVGConstraints;
import net.sf.opengroove.client.ui.SVGPanel;
import net.sf.opengroove.client.ui.StandardWizardPage;
import net.sf.opengroove.client.ui.StatusDialog;
import net.sf.opengroove.client.ui.WebsiteButton;
import net.sf.opengroove.client.ui.frames.LoginFrame;
import net.sf.opengroove.client.ui.frames.MessageHistoryFrame;
import net.sf.opengroove.client.ui.transitions.included.SlideInNotificationFrameTransition;
import net.sf.opengroove.common.concurrent.Conditional;
import net.sf.opengroove.common.security.Hash;
import net.sf.opengroove.common.security.RSA;
import net.sf.opengroove.common.ui.ComponentUtils;
import net.sf.opengroove.common.utils.Userids;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.opengroove.g4.common.TemporaryFileStore;
import org.opengroove.g4.common.user.Userid;

import com.jidesoft.dialog.AbstractDialogPage;
import com.jidesoft.dialog.ButtonNames;
import com.jidesoft.dialog.PageEvent;
import com.jidesoft.dialog.PageList;
import com.jidesoft.dialog.PageListener;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.MultilineLabel;
import com.jidesoft.wizard.WizardDialogPane;
import com.l2fprod.common.swing.JLinkButton;

/**
 * This is the class that does most of the setup for OpenGroove. It's the class
 * that you run to get OpenGroove going.
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
    public static boolean updatesEnabled = false;
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
    public static final Hashtable<Userid, UserContext> userContextMap =
        new Hashtable<Userid, UserContext>();
    
    public static UserContext getUserContext(Userid userid)
    {
        return userContextMap.get(userid);
    }
    
    private static LoginFrame loginFrame;
    
    private static WizardDialogPane newAccountWizardPane;
    
    private static JFrame newAccountFrame;
    /**
     * The number of bits that should be in RSA security keys generated for the
     * user. Workspace key sizes are decided by the creator of the workspace,
     * but default to this value as well.
     */
    public static final int SECURITY_KEY_SIZE = 3072;
    
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
    public static final File INTERNAL_HELP_FOLDER = new File("help");
    
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
        UP_FOLDER_16("upfolder16.png", 16), GENERIC_ADD_16("add.png", 16),
        GENERIC_REMOVE_16("remove.png", 16), CONFIGURE_WORKSPACE_16(
            "configure-workspace.png", 16), DELETE_WORKSPACE_16("delete-workspace.png",
            16), INVITE_TO_WORKSPACE_16("invite-to-workspace.png", 16), POP_OUT_16(
            "pop-out.png", 16), WORKSPACE_INFO_16("workspace-info.png", 16),
        WORKSPACE_WARNING_16("workspace-warning.png", 16), BACK_BUTTON_32(
            "back-button.png", 32), FOLDER_DOCS_16("folder-docs.png", 16), EDIT_16(
            "edit16.gif", 16), NOTES_16("notes16.gif", 16), USER_ONLINE_16(
            "user-green.png", 16), USER_IDLE_16("user-yellow.png", 16),
        USER_NONEXISTANT_16("user-red.png", 16),
        USER_UNKNOWN_16("user-purple.png", 16), USER_OFFLINE_16("user-gray.png", 16),
        SETTINGS_48("settings48.png", 48), SETTINGS_16("settings16.png", 16),
        MESSAGE_CONFIG_16("messageconfig16.png", 16), MESSAGE_CONFIG_48(
            "messageconfig48.png", 48), FILE_16("gfile2-16.png", 16), FOLDER_16(
            "gfolder16.png", 16);
        private int size;
        
        private File scaledFile;
        
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
        
        public File getScaledFile()
        {
            return scaledFile;
        }
        
        public void setScaledFile(File scaledFile)
        {
            this.scaledFile = scaledFile;
        }
        
        public Icon getIcon()
        {
            return new ImageIcon(getImage());
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
    
    // FIXME: needs to be localized to the user's operating system and java vm
    private static final String[] restartExecutableString =
        new String[] { "javaw.exe", "-cp", RESTART_CLASSPATH,
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
            Runtime.getRuntime().exec(restartExecutableString);
            System.out.println("waiting");
            Thread.sleep(1000);
            System.out.println("exiting");
            System.exit(0);
            System.out.println("exited (this should never be printed)");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JOptionPane
                .showMessageDialog(parent,
                    "OpenGroove could not be restarted. You will need to manually restart OpenGroove.");
            
        }
        
    }
    
    private static final Object authLock = new Object();
    
    private static PopupMenu trayPopup;
    private static final Thread gcThread = new Thread("OpenGroove-gc")
    {
        public void run()
        {
            while (true)
            {
                try
                {
                    Thread.sleep(20 * 1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                System.gc();
            }
        }
    };
    
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
        System.out.println("vm starting");
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        gcThread.setDaemon(true);
        gcThread.start();
        TemporaryFileStore.init(new File("appdata/tmp-tfs"));
        Security.addProvider(new BouncyCastleProvider());
        JFrame frame3 = new JFrame("opengroovemain");
        frame3.setSize(800, 10);
        // frame3.show();
        // the above frame was used for debugging the restart feature of
        // opengroove, it needs to be removed some time soon
        boolean waitForLock = args.length > 0 && args[0].equals("wfl");
        if (waitForLock)
        {
            while (true)
            {
                try
                {
                    frame3.setTitle("accepting");
                    System.out.println("creating socket");
                    ss = new ServerSocket(LOCK_PORT);
                    System.out.println("created socket");
                    frame3.setTitle("breaking");
                    break;
                }
                catch (Exception e)
                {
                    frame3.setTitle("exception," + e.getClass() + " : "
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
                System.out.println("creating socket");
                ss = new ServerSocket(LOCK_PORT);
                System.out.println("created socket");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                JFrame frame = new JFrame("OpenGroove");
                frame.setLocationRelativeTo(null);
                frame.show();
                JOptionPane
                    .showMessageDialog(frame,
                        "OpenGroove is already running. You cannot start OpenGroove multiple times.");
                System.exit(0);
            }
        }
        JFrame splashScreenWindow = new JFrame("OpenGroove");
        splashScreenWindow.setUndecorated(true);
        splashScreenWindow.getContentPane().setLayout(new BorderLayout());
        Icon splashScreenIcon = new ImageIcon("icons/splashscreen.png");
        splashScreenWindow.getContentPane().add(new JLabel(splashScreenIcon),
            BorderLayout.CENTER);
        JProgressBar splashScreenProgress = new JProgressBar();
        splashScreenProgress.setStringPainted(true);
        splashScreenProgress.setIndeterminate(true);
        splashScreenProgress.setString("Initializing...");
        splashScreenWindow.pack();
        splashScreenWindow.setLocationRelativeTo(null);
        splashScreenWindow.getContentPane().add(splashScreenProgress,
            BorderLayout.SOUTH);
        /*
         * It's important to pack and setlocationrelativeto BEFORE adding the
         * progress bar, to get the positioning correct with respect to the
         * vm-shown splash screen
         */
        splashScreenWindow.pack();
        splashScreenWindow.show();
        if (waitForLock)
        {
            System.out.println("waiting");
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
            sfile.mkdirs();
            splashScreenProgress.setString("Loading storage...");
            System.out.println("loading storage");
            Storage.initStorage(sfile);
            splashScreenProgress.setString("Loading initial images...");
            System.out.println("loading initial images");
            trayimage =
                scaleImage(blockTransparify(ImageIO.read(new File("trayicon.png")),
                    new Color(255, 0, 0)), 16, 16);
            trayofflineimage =
                scaleImage(blockTransparify(ImageIO.read(new File("trayoffline.png")),
                    new Color(255, 0, 0)), 16, 16);
            splashScreenProgress.setString("Loading default windows...");
            initNewAccountWizard();
            initLoginFrame();
            // the setProperty call below is used to avoid problems with the
            // fade effect for the taskbar notification frame
            System.setProperty("sun.java2d.noddraw", "true");
            // PluginManager.loadPlugins();
            // postPluginLoad();
            System.out.println("storage path is " + sfile.getCanonicalPath());
            // helpviewer = new HelpViewer(helpFolder);
            initLoginFrame();
            notificationFrame =
                new TaskbarNotificationFrame(new SlideInNotificationFrameTransition());
            notificationFrame.setGroupLabelResolver(new GroupLabelResolver()
            {
                
                @Override
                public String resolveLabel(String group)
                {
                    if (group.equalsIgnoreCase("opengroove"))
                        return "OpenGroove";
                    /*
                     * TODO: change this to resolve the real name of the local
                     * user in question, if it is indeed a local user
                     */
                    if (Userids.isUserid(group))
                    {
                        LocalUser user = Storage.getLocalUser(group);
                        if (user != null)
                        {
                            return user.getDisplayName();
                        }
                    }
                    return group;
                }
            });
            new Thread("notification status updater")
            {
                public void run()
                {
                    while (true)
                    {
                        try
                        {
                            Thread.sleep(3000);
                            isNotificationAlertShowing =
                                notificationFrame.containsAlerts();
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
                            if (!isNotificationAlertShowing)
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
                                        Thread.sleep(ndelays[currentNotificationIndex]);
                                    }
                                    catch (Exception ex1)
                                    {
                                        ex1.printStackTrace();
                                        try
                                        {
                                            Thread.sleep(3000);
                                        }
                                        catch (Exception ex12)
                                        {
                                            ex12.printStackTrace();
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
                                        trayicon.setImage(trayimage);
                                    }
                                    else
                                    {
                                        trayicon.setImage(trayofflineimage);
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
            splashScreenProgress.setString("Loading icons...");
            File[] trayfiles = new File(".").listFiles(new FileFilter()
            {
                
                public boolean accept(File pathname)
                {
                    return pathname.getName().startsWith("traynotify")
                        && pathname.getName().endsWith(".png");
                }
            });
            Arrays.sort(trayfiles);
            notificationTrayImages = new BufferedImage[trayfiles.length];
            notificationTrayDelays = new int[trayfiles.length];
            for (int i = 0; i < trayfiles.length; i++)
            {
                File file = trayfiles[i];
                notificationTrayImages[i] = ImageIO.read(file);
                notificationTrayImages[i] =
                    scaleImage(blockTransparify(notificationTrayImages[i], new Color(
                        255, 0, 0)), 16, 16);
                String filename = file.getName();
                System.out.println(filename);
                int hIndex = filename.lastIndexOf("-");
                System.out.println(hIndex);
                String afterH = filename.substring(hIndex + 1);
                System.out.println(afterH);
                int dIndex = afterH.lastIndexOf(".");
                System.out.println(dIndex);
                String delayString = afterH.substring(0, dIndex);
                System.out.println(delayString);
                notificationTrayDelays[i] = Integer.parseInt(delayString);
            }
            File[] trayofflinefiles = new File(".").listFiles(new FileFilter()
            {
                
                public boolean accept(File pathname)
                {
                    return pathname.getName().startsWith("trayofflinenotify")
                        && pathname.getName().endsWith(".png");
                }
            });
            Arrays.sort(trayofflinefiles);
            notificationTrayOfflineImages = new BufferedImage[trayofflinefiles.length];
            notificationTrayOfflineDelays = new int[trayofflinefiles.length];
            for (int i = 0; i < trayofflinefiles.length; i++)
            {
                File file = trayofflinefiles[i];
                notificationTrayOfflineImages[i] = ImageIO.read(file);
                notificationTrayOfflineImages[i] =
                    scaleImage(blockTransparify(notificationTrayOfflineImages[i],
                        new Color(255, 0, 0)), 16, 16);
                String filename = file.getName();
                System.out.println(filename);
                int hIndex = filename.lastIndexOf("-");
                System.out.println(hIndex);
                String afterH = filename.substring(hIndex + 1);
                System.out.println(afterH);
                int dIndex = afterH.lastIndexOf(".");
                System.out.println(dIndex);
                String delayString = afterH.substring(0, dIndex);
                System.out.println(delayString);
                notificationTrayOfflineDelays[i] = Integer.parseInt(delayString);
            }
            trayPopup = new PopupMenu();
            trayicon = new TrayIcon(trayimage, "OpenGroove", trayPopup);
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
            ToolTipManager.sharedInstance().setDismissDelay(86400);
            ToolTipManager.sharedInstance().setReshowDelay(
                ToolTipManager.sharedInstance().getReshowDelay());
            ToolTipManager.sharedInstance().setInitialDelay(
                ToolTipManager.sharedInstance().getInitialDelay());
            try
            {
                for (Icons icon : Icons.values())
                {
                    BufferedImage image =
                        scaleImage(loadImage(icon.getIconPath()), icon.getSize(), icon
                            .getSize());
                    icon.setImage(image);
                    boolean createdTemp = false;
                    int attempt = 0;
                    while (attempt++ < 10 && !createdTemp)
                    {
                        try
                        {
                            File tfile = File.createTempFile("og-icon-", ".png");
                            tfile.deleteOnExit();
                            ImageIO.write(image, "PNG", tfile);
                            icon.setScaledFile(tfile);
                            createdTemp = true;
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            Thread.sleep(200);
                        }
                    }
                    if (!createdTemp)
                        throw new RuntimeException(
                            "temp not created, see previous stack trace");
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
                    throw new RuntimeException("TODO auto generated on Dec 7, 2007 : "
                        + e.getClass().getName() + " - " + e.getMessage(), e1);
                }
                e.printStackTrace();
                System.exit(0);
            }
            splashScreenProgress.setString("Loading tray icon...");
            trayicon.addMouseMotionListener(new MouseMotionAdapter()
            {
                
                @Override
                public void mouseMoved(MouseEvent e)
                {
                    try
                    {
                        if (notificationFrame.ignoreMouseOver
                            && notificationFrame.currentVisibilityLevel > 0)
                            return;
                        if (notificationFrame.listAllNotifications().length == 0)
                            return;
                        notificationFrame.requestDisplay();
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
            splashScreenWindow.dispose();
            /*
             * We've set everything up at this point. Now we check to see if
             * there aren't any users, in which case we show the new account
             * wizard.
             */
            if (Storage.getUsers().length == 0)
            {
                showNewAccountWizard(true);
            }
            else
            {
                /*
                 * If we get here then there is at least one user. Now we need
                 * to check to see if any of those users have requested that
                 * they be automatically logged in.
                 */
                // TODO: actually do what the above comment says.
                notificationFrame.addNotification("OpenGroove",
                    new NotificationAdapter(notificationFrame, new JLabel(
                        "OpenGroove has successfully started up."), false, true), true);
            }
            /*
             * We should get rid of the splash screen so that the user isn't
             * left wondering what to do
             */
            SplashScreen splash = SplashScreen.getSplashScreen();
            if (splash != null)
                splash.close();
        }
        catch (Throwable e)
        {
            Thread.sleep(2000);
            e.printStackTrace();
        }
    }
    
    /**
     * Changes all pixels in the image that are within five color points of the
     * color specified to transparent. The image passed in is copied before any
     * of the transparifying occurs.
     * 
     * @param image
     *            The image to transparify
     * @param color
     *            The color to convert to transparent
     * @return A new image with transparency applied
     */
    private static BufferedImage blockTransparify(BufferedImage oldImage, Color color)
    {
        BufferedImage image =
            new BufferedImage(oldImage.getWidth(null), oldImage.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        int tr = color.getRed();
        int tg = color.getGreen();
        int tb = color.getBlue();
        int rMin = tr - 6;
        int rMax = tr + 6;
        int gMin = tg - 6;
        int gMax = tg + 6;
        int bMin = tb - 6;
        int bMax = tb + 6;
        for (int x = 0; x < image.getWidth(); x++)
        {
            for (int y = 0; y < image.getHeight(); y++)
            {
                Color at = new Color(oldImage.getRGB(x, y));
                int r = at.getRed();
                int g = at.getGreen();
                int b = at.getBlue();
                if (r > rMin && r < rMax && g > gMin && g < gMax && b > bMin
                    && b < bMax)
                {
                    Color sub = new Color(r, g, b, 0);
                    image.setRGB(x, y, sub.getRGB());
                }
                else
                {
                    image.setRGB(x, y, at.getRGB());
                }
            }
        }
        return image;
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
        loginFrame.getLoginButton().setEnabled(false);
        loginFrame.getNewAccountButton().setEnabled(false);
        loginFrame.getCancelButton().setEnabled(false);
        loginFrame.getPasswordField().setEnabled(false);
        try
        {
            synchronized (authLock)
            {
                String userid = loginFrame.getUserid();
                String password = loginFrame.getPasswordField().getText();
                LocalUser user = Storage.getLocalUser(userid);
                if (user.isLoggedIn())
                {
                    JOptionPane
                        .showMessageDialog(
                            loginFrame,
                            "<html>You're already logged in. This is probably a bug if <br/>"
                                + "you're seeing this, so be sure to contact us about it. <br/>"
                                + "Use the help &rarr; contact us option in the launchbar menu.");
                    return;
                }
                String encPassword = Hash.hash(password);
                if (!encPassword.equals(user.getEncPassword()))
                {
                    JOptionPane.showMessageDialog(loginFrame,
                        "Incorrect username and/or password.");
                    loginFrame.getPasswordField().setText("");
                    return;
                }
                /*
                 * Ok, we've checked that the user is not already logged in, and
                 * the user entered the correct password. Now we do all of the
                 * rest of the initialization stuff, such as creating a user
                 * context, hiding the auth dialog, creating the launchbar,
                 * loading plugins, re-generating the taskbar popup menu, etc.
                 */
                final UserContext context = new UserContext();
                loadContextConnectionConditional(context);
                context.setUserid(userid);
                context.setPassword(password);
                loadContextNonexistantContactNotification(context);
                loadContextStatusListener(context);
                loadContextUserNotificationListener(context);
                JFrame launchbar = new JFrame(context.createLaunchbarTitle());
                launchbar.setLocationRelativeTo(null);
                context.setLaunchbar(launchbar);
                SettingStore settingStore = user.getSettingStore();
                if (settingStore == null)
                {
                    settingStore = user.createSettingStore();
                    user.setSettingStore(settingStore);
                }
                SettingsManager settingsManager =
                    new SettingsManager(launchbar, "Settings", settingStore);
                context.setSettingsManager(settingsManager);
                loadBuiltInSettings(settingsManager, user);
                context.setRootMessageHierarchy(new NullHierarchy(""));
                context.setInternalMessageHierarchy(new NullHierarchy("internal"));
                context.setPluginMessageHierarchy(new NullHierarchy("plugins"));
                context.getRootMessageHierarchy().add(
                    context.getInternalMessageHierarchy());
                context.getRootMessageHierarchy().add(
                    context.getPluginMessageHierarchy());
                // context.getRootMessageHierarchy().add(
                // context.getUserMessageHierarchy());
                Communicator com =
                    new Communicator(launchbar, Userids.toRealm(userid), true, false,
                        "normal", Userids.toUsername(userid), user.getComputer(),
                        password, user.getTrustedCertificates(), context
                            .getStatusListener(), null);
                CommandCommunicator commandCom = new CommandCommunicator(com);
                MessageManager messageManager =
                    new MessageManager(userid, commandCom, context
                        .getRootMessageHierarchy());
                context.setMessageManager(messageManager);
                context.setMessageHistoryFrame(new MessageHistoryFrame(Storage
                    .get(context.getUserid())));
                commandCom.addUserNotificationListener(context
                    .getUserNotificationListener());
                context.setCom(commandCom);
                loadContextSubscriptionListener(context);
                setupInboundUserMessaging(context, userid);
                // loadFeatures();
                // loadCurrentUserLookAndFeel();
                setupOutboundUserMessaging(context, userid);
                loadLaunchBar(userid, context);
                context.refreshContactsPane();
                context.startTimers();
                messageManager.start();
                // WorkspaceManager workspaceManager = new
                // WorkspaceManager(context);
                // context.setWorkspaceManager(workspaceManager);
                // workspaceManager.reloadWorkspaces();
                // workspaceManager.reloadWorkspaceMembers();
                // reloadLaunchbarWorkspaces(context);
                loginFrame.dispose();
                userContextMap.put(userid, context);
                JLabel successLoggedInLabel =
                    new JLabel("You have successfully logged into OpenGroove.");
                successLoggedInLabel.setCursor(Cursor
                    .getPredefinedCursor(Cursor.HAND_CURSOR));
                notificationFrame.addNotification(context.getUserid(),
                    new NotificationAdapter(notificationFrame, successLoggedInLabel,
                        false, true), true);
                context.getLaunchbar().show();
                bringToFront(context.getLaunchbar());
            }
        }
        finally
        {
            loginFrame.getLoginButton().setEnabled(true);
            loginFrame.getNewAccountButton().setEnabled(true);
            loginFrame.getCancelButton().setEnabled(true);
            loginFrame.getPasswordField().setEnabled(true);
            loginFrame.getPasswordField().requestFocusInWindow();
            refreshTrayMenu();
        }
    }
    
    private static void loadContextSubscriptionListener(final UserContext context)
    {
        /*
         * TODO: set this as an object on the user context instead of just
         * adding it, in case we replace the communicator while the user is
         * logged in (an unlikely but possible event if OpenGroove goes the way
         * I'm thinking it will go). I'm thinking this could happen if they
         * change their password. Then again, perhaps I could just include
         * methods on CommandCommunicator for doing this.
         */
        context.getCom().addSubscriptionListener(new SubscriptionListener()
        {
            
            @Override
            public void event(final Subscription subscription)
            {
                new Thread()
                {
                    public void run()
                    {
                        System.out.println("received subscription");
                        boolean isRelatedSubscription =
                            subscription.getType().equalsIgnoreCase("userstatus")
                                || (subscription.getType().equalsIgnoreCase(
                                    "computersetting") && (subscription.getOnSetting()
                                    .equalsIgnoreCase("public-active") || subscription
                                    .getOnSetting().equalsIgnoreCase("public-idle")));
                        /*
                         * We're not checking for public-lag since it will
                         * change so infrequently.
                         */
                        if (isRelatedSubscription)
                        {
                            /*
                             * This subscription is for one of the events that
                             * we're interested in that relates to contact
                             * management. We need to tell the context to update
                             * it's contact status.
                             * 
                             * TODO: in the future, we should just update the
                             * status of the contact that had the subscription
                             * event.
                             */
                            System.out.println("subscription is related");
                            synchronized (context.contactStatusLock)
                            {
                                Contact contact =
                                    context.getStorage().getLocalUser().getContact(
                                        subscription.getOnUser());
                                if (contact != null)
                                    context.updateOneContactStatus(contact);
                                else
                                {
                                    System.out
                                        .println("subscription received for nonexistant contact with userid "
                                            + subscription.getOnUser());
                                }
                            }
                        }
                    }
                }.start();
            }
        });
    }
    
    private static void loadContextNonexistantContactNotification(
        final UserContext context)
    {
        context.setNonexistantContactNotification(new NotificationAdapter(
            notificationFrame, new JLabel("One or more of your contacts do not exist"),
            false, false)
        {
            
            @Override
            public void clicked()
            {
                context.showNonexistantContactInfoDialog();
            }
        });
    }
    
    private static void loadContextUserNotificationListener(final UserContext context)
    {
        context.setUserNotificationListener(new UserNotificationListener()
        {
            
            @Override
            public void receive(long dateIssued, long dateExpires, Priority priority,
                String subject, String message)
            {
                /*
                 * TODO: the notification should dismiss itself if the current
                 * date passes the date that the notification expires
                 */
                UserNotificationFrame uframe =
                    new UserNotificationFrame(context.formatDateTime(dateIssued),
                        context.formatDateTime(dateExpires), priority, subject, message);
                uframe.setTitle("Server Notification - OpenGroove");
                uframe.setIconImage(getWindowIcon());
                uframe.setLocationRelativeTo(null);
                if (priority == Priority.CRITICAL)
                {
                    uframe.show();
                }
                else
                {
                    JLabel component = new JLabel("Server Notification: " + subject);
                    component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    if (priority == Priority.ALERT)
                        component.setIcon(new ImageIcon(Icons.WORKSPACE_WARNING_16
                            .getImage()));
                    FrameShowingNotification tn =
                        new FrameShowingNotification(notificationFrame, component,
                            uframe, true, false);
                    notificationFrame.addNotification(context.getUserid(), tn, true);
                }
            }
        });
    }
    
    private static void loadContextStatusListener(final UserContext context)
    {
        context.setStatusListener(new StatusListener()
        {
            
            @Override
            public void authenticationFailed(Communicator c, Packet packet)
            {
                System.out.println("Persistant authentication failed");
                /*
                 * TODO: the user should be notified that they have an incorrect
                 * local passwod, and that they need to change it. This would
                 * occur if they change their password on a computer, since
                 * OpenGroove doesn't propegate password changes to other
                 * computers for security reasons. All other computers would get
                 * this method called, at which point they could show to the
                 * user that an incorrect local password is present, and that
                 * they need to enter their correct local password (IE the
                 * password that they changed theirs to on the server).
                 */
            }
            
            @Override
            public void authenticationSuccessful(Communicator c)
            {
                System.out.println("persistant auth successful");
            }
            
            @Override
            public void communicatorShutdown(Communicator c)
            {
                System.err.println("Communicator shutdown prematurely, "
                    + "OpenGroove needs to be restarted to work correctly");
            }
            
            @Override
            public void connectionEstablished(Communicator c, ServerContext server)
            {
                System.out.println("persistant connection established");
            }
            
            @Override
            public void connectionLost(Communicator c)
            {
                System.out.println("persistant connection lost");
                context.updateLocalStatusIcon();
                new Thread()
                {
                    public void run()
                    {
                        context.updateContactStatus();
                    }
                }.start();
            }
            
            @Override
            public void connectionReady(Communicator c)
            {
                System.out.println("persistant connection ready");
                context.updateLocalStatusIcon();
                new Thread()
                {
                    public void run()
                    {
                        System.out
                            .println("server conection established, waiting 2 seconds to update");
                        try
                        {
                            Thread.sleep(2000);
                        }
                        catch (InterruptedException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        System.out.println("now running update stuff");
                        try
                        {
                            context.uploadCurrentStatus();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        try
                        {
                            context.updateContactStatus();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        try
                        {
                            context.updateSubscriptions();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        try
                        {
                            context.getMessageManager().notifyAllThreads();
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }
    
    private static void loadContextConnectionConditional(final UserContext context)
    {
        context.setConnectionConditional(new Conditional()
        {
            
            @Override
            public boolean query()
            {
                CommandCommunicator c2 = context.getCom();
                if (c2 == null)
                    return false;
                Communicator c3 = c2.getCommunicator();
                if (c3 == null)
                    return false;
                return c3.isActive();
            }
        });
    }
    
    public static final SettingSpec SPEC_MESSAGE_REPLY_PREFIX =
        new SettingSpec("msg", "compose", "", "replyprefix");
    
    private static void loadBuiltInSettings(SettingsManager settingsManager,
        LocalUser user)
    {
        settingsManager.addTab("g", Icons.SETTINGS_48.getIcon(), "General", "");
        
        settingsManager.addTab("msg", Icons.MESSAGE_CONFIG_48.getIcon(), "Messaging",
            "");
        settingsManager.addSubnav("msg", "compose", "Composing", "");
        settingsManager.addSubnav("g", "lb", "Launchbar", "");
        settingsManager.addSetting("g", "lb", "", "alwaysontop", "Always on top",
            "If this is checked, the launchbar will "
                + "show up on top of all other windows.", "checkbox",
            new CheckboxParameters(false));
    }
    
    /**
     * Sets up outbound user messaging.
     * 
     * @param context
     * @param userid
     */
    private static void setupOutboundUserMessaging(UserContext context, String userid)
    {
        /*
         * TODO do we actually need to do anything here?
         */
    }
    
    /**
     * Sets up inbound user messaging. This creates a message hierarchy and adds
     * it to the context specified. It also creates the message history window
     * (for both inbound and outbound messages) and adds it to the context.
     * 
     * @param context
     * @param userid
     */
    private static void setupInboundUserMessaging(final UserContext context,
        String userid)
    {
        /*
         * TODO: actually implement this method. Some sort of "preferences"
         * windos in OpenGroove should probably be done first, so the user can
         * specify how long to retain message attachments and how long to retain
         * messages themselves.
         */
        context.setUserMessageHierarchy(new MessageHierarchy("umsg")
        {
            
            public void handleMessage(InboundMessage message)
            {
                System.out.println("received message from " + message.getSender() + "/"
                    + message.getSendingComputer());
            }
        });
        context.getInternalMessageHierarchy().add(context.getUserMessageHierarchy());
        context.getCom().addMessageAvailableListener(new MessageAvailableListener()
        {
            
            public void messageAvailable(String messageId)
            {
                context.getMessageManager().notifyInboundImporter();
            }
        });
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
        final LocalUser[] onlineUsers = Storage.getUsersLoggedIn();
        final LocalUser[] offlineUsers = Storage.getUsersNotLoggedIn();
        if (onlineUsers.length > 0)
        {
            for (final LocalUser user : onlineUsers)
            {
                Menu userMenu = new Menu(user.getDisplayName());
                MenuItem launchbarItem = new MenuItem("Launchbar");
                launchbarItem.addActionListener(new ActionListener()
                {
                    
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        user.getContext().getLaunchbar().show();
                        bringToFront(user.getContext().getLaunchbar());
                    }
                });
                userMenu.add(launchbarItem);
                MenuItem sendMessageItem = new MenuItem("Send Message");
                sendMessageItem.addActionListener(new ActionListener()
                {
                    
                    public void actionPerformed(ActionEvent e)
                    {
                        user.getContext().composeMessage("", "", "", "",
                            new String[] {});
                    }
                });
                userMenu.add(sendMessageItem);
                trayPopup.add(userMenu);
                userMenu.add(new AMenuItem("Message History")
                {
                    
                    public void run(ActionEvent e)
                    {
                        user.getContext().getMessageHistoryFrame().show();
                    }
                });
            }
            trayPopup.addSeparator();
        }
        if (offlineUsers.length > 0)
        {
            for (final LocalUser user : offlineUsers)
            {
                Menu userMenu = new Menu(user.getDisplayName());
                MenuItem loginItem = new MenuItem("Login");
                loginItem.addActionListener(new ActionListener()
                {
                    
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        if (!user.isLoggedIn())
                            showLoginWindow(user.getUserid());
                    }
                });
                userMenu.add(loginItem);
                trayPopup.add(userMenu);
            }
            trayPopup.addSeparator();
        }
        trayPopup.add(new AMenuItem("Show Notifications")
        {
            
            @Override
            public void run(ActionEvent e)
            {
                if (notificationFrame.ignoreMouseOver
                    && notificationFrame.currentVisibilityLevel > 0)
                    return;
                if (notificationFrame.listAllNotifications().length == 0)
                    return;
                notificationFrame.requestDisplay();
            }
        });
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
     * userid is null, the new account wizard is shown instead.<br/>
     * <br/>
     * 
     * If the new user wizard is currently showing, it is brought to the front,
     * and this method returns.
     * 
     * @param userid
     */
    public static void showLoginWindow(String userid)
    {
        if ((!loginFrame.isShowing()) && (!loginFrame.getUserid().equals(userid)))
            /*
             * If the login frame is showing and we're switching it to a
             * different user, or if it's not showing at all, then we clear the
             * password field. If it is already showing and with the same user
             * as the argument to this method, then we won't clear the password
             * field.
             */
            loginFrame.getPasswordField().setText("");
        if (newAccountFrame.isShowing())
        {
            /*
             * FIXME: This makes it so that the new account frame can't show the
             * login frame when it's completed.
             */
            bringToFront(newAccountFrame);
            return;
        }
        LocalUser user = userid == null ? null : Storage.getLocalUser(userid);
        if (user != null && user.isLoggedIn())
        {
            /*
             * Somehow the user has tried to log in but they are already logged
             * in. The most probable cause of this is that a glitch occured and
             * the taskbar menu (currently the only way to choose to login as a
             * user except for creating a new account) didn't get reloaded, so
             * we'll try to reload it to see if that helps.
             */
            refreshTrayMenu();
            return;
        }
        if (user == null)
        {
            /*
             * Either an invalid user was specified, or there aren't any user
             * accounts currently. In either case, we show the new account
             * wizard instead.
             */
            showNewAccountWizard(Storage.getUsers().length == 0);
            return;
        }
        /*
         * We've finished all of our checks and tests. It's now time to set up
         * the login frame and actually show it.
         */
        loginFrame.setUserid(userid);
        loginFrame.setPasswordHint(user.getPasswordHint());
        // loginFrame.setAlwaysOnTop(true);
        loginFrame.show();
        bringToFront(loginFrame);
        loginFrame.getPasswordField().requestFocusInWindow();
    }
    
    private static void initLoginFrame()
    {
        loginFrame = new LoginFrame();
        loginFrame.setIconImage(getWindowIcon());
        loginFrame.getIconLabel().setIcon(new ImageIcon("trayicon48.png"));
        loginFrame.getCancelButton().addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                loginFrame.getPasswordField().setText("");
                loginFrame.getRememberPasswordCheckbox().setSelected(false);
                loginFrame.dispose();
            }
        });
        ActionListener loginActionListener = new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                new Thread()
                {
                    public void run()
                    {
                        doFrameLogin();
                    }
                }.start();
            }
        };
        loginFrame.getLoginButton().addActionListener(loginActionListener);
        loginFrame.getPasswordField().addActionListener(loginActionListener);
        loginFrame.getNewAccountButton().addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                loginFrame.dispose();
                showNewAccountWizard(false);
            }
        });
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setResizable(false);
        // loginFrame.setAlwaysOnTop(true);
        
    }
    
    private static void showLoginFrame(String userid)
    {
        showLoginWindow(userid);
    }
    
    private static void initNewAccountWizard()
    {
        newAccountFrame = new JFrame("New Account - OpenGroove");
        newAccountFrame.setIconImage(getWindowIcon());
        newAccountFrame.setSize(650, 500);
        newAccountFrame.setLocationRelativeTo(null);
    }
    
    /**
     * Shows the new account wizard. If the wizard is already showing, it will
     * be brought to the front. If not, the current wizard page will be reset to
     * it's first page. If <code>welcome</code> is true, the wizard will have an
     * added initial screen that gives the user more information about
     * OpenGroove. If not, this initial screen won't be displayed, and the first
     * screen will instead be the one that allows users to choose between
     * creating a new account, importing one they have already created, or
     * entering an account configuration code. If the user finishes the wizard
     * successfully, the login box will be shown.
     */
    @SuppressWarnings("serial")
    private static void showNewAccountWizard(boolean welcome)
    {
        if (newAccountFrame.isShowing())
        {
            bringToFront(newAccountFrame);
            return;
        }
        /*
         * The general flow of the wizard is as follows, not counting the
         * initial welcome screen:
         * 
         * 1. The user is asked if they want to create a new account, or use an
         * existing one. If they choose to use an existing one, flow proceeds to
         * step 2. If they choose to create a new account, flow proceeds to step
         * 7.
         * 
         * 2. The user is prompted for their userid and password.
         * 
         * 3. The user's realm server is contacted to verify the information
         * that the user entered. If they could not be authenticated, flow goes
         * back to step 2.
         * 
         * 4. If any of the encryption key user properties are already present
         * on the account, the user is informed that they need to supply
         * OpenGroove with their private keys. A test message will be encoded
         * with each key and decrypted with the public key and modulus found on
         * the server to validate that the keys specified are correct. If the
         * user's security keys don't exist, they are prompted to create some
         * keys.
         * 
         * --TBD: general info screen, such as contact card if the user chooses
         * (vCard), and email address (possibly derived from contact card).
         * Allow import of vCard from external file but only allow up to
         * TBD(suggest:4096KB) in size for their contact card.
         * 
         * 5. The user is prompted for a name that they would like to assign to
         * this computer. The text field for accepting a name is initially
         * populated with the environment variable COMPUTERNAME, a hyphen, and
         * the system property user.name, or just user.name if COMPUTERNAME
         * couldn't be found. Anyway, before they can advance, the server is
         * contacted to make sure that the computer specified doesn't already
         * exist.
         * 
         * 6. The computer specified is created, and account addition is
         * successful. The user can then log in. The new account wizard then
         * closes.
         * 
         * 7. If the user chose to create a new account instead of using an
         * existing one, TBD.
         */
        final NewAccountWizardVars vars = new NewAccountWizardVars();
        newAccountWizardPane = new WizardDialogPane()
        {
            private JLabel titleLabel;
            
            @Override
            protected void updateBannerPanel(JComponent bannerPanel,
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
                panel.add(new JSeparator(), BorderLayout.SOUTH);
                fill.setBorder(new EmptyBorder(12, 20, 12, 10));
                return panel;
            }
        };
        PageList pages = new PageList();
        // pages here
        final String LABEL_WELCOME = "Welcome to OpenGroove";
        final String LABEL_NEW_OR_EXIST = "New or Existing Account?";
        final String LABEL_EXIST_AUTH = "Enter your userid and password";
        final String LABEL_NEW_AUTH = "Select a realm, username, and password";
        final String LABEL_ENTER_KEYS = "Select your security keys";
        final String LABEL_MORE_INFO = "Enter some additional information";
        final String LABEL_COMPUTER = "Choose a name for this computer";
        final String LABEL_DONE = "Successfully added account";
        if (welcome)
        {
            StandardWizardPage welcomePage =
                new StandardWizardPage(LABEL_WELCOME, false, true, true, false)
                {
                    
                    @Override
                    protected void init()
                    {
                        addText(getWelcomeWizardMessage(), Font.decode(null));
                    }
                    
                };
            pages.append(welcomePage);
        }
        StandardWizardPage newOrExistPage =
            new StandardWizardPage(LABEL_NEW_OR_EXIST, welcome, true, true, false)
            {
                private JRadioButton newButton;
                private JRadioButton existingButton;
                
                public JComponent createWizardContent()
                {
                    JPanel panel = new JPanel();
                    newButton =
                        new JRadioButton(
                            "<html><b>Create a new OpenGroove account</b><br/>"
                                + "Choose this if this is your first time using OpenGroove");
                    existingButton =
                        new JRadioButton(
                            "<html><b>Use an OpenGroove account that you have already created</b><br/>"
                                + "Choose this if you already have an OpenGroove "
                                + "account and would like to use it on this computer");
                    newButton.setFont(Font.decode(null));
                    existingButton.setFont(Font.decode(null));
                    ButtonGroup newOrExistGroup = new ButtonGroup();
                    newOrExistGroup.add(newButton);
                    newOrExistGroup.add(existingButton);
                    panel.setLayout(new BorderLayout());
                    JPanel inner = new JPanel();
                    inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
                    panel.add(inner, BorderLayout.NORTH);
                    newButton.setVerticalTextPosition(newButton.TOP);
                    existingButton.setVerticalTextPosition(existingButton.TOP);
                    newButton.setFocusable(false);
                    existingButton.setFocusable(false);
                    inner.add(newButton);
                    inner.add(new JLabel(" "));
                    inner.add(new JLabel(" "));
                    inner.add(existingButton);
                    ActionListener listener = new ActionListener()
                    {
                        
                        @Override
                        public void actionPerformed(ActionEvent e)
                        {
                            if (newButton.isSelected())
                            {
                                newAccountWizardPane.setNextPage(newAccountWizardPane
                                    .getPageByTitle(LABEL_NEW_AUTH));
                            }
                            else if (existingButton.isSelected())
                            {
                                newAccountWizardPane.setNextPage(newAccountWizardPane
                                    .getPageByTitle(LABEL_EXIST_AUTH));
                            }
                        }
                    };
                    newButton.addActionListener(listener);
                    existingButton.addActionListener(listener);
                    addPageListener(new PageListener()
                    {
                        
                        @Override
                        public void pageEventFired(PageEvent e)
                        {
                            if (e.getID() != PageEvent.PAGE_CLOSING)
                                return;
                            boolean isOneSelected =
                                newButton.isSelected() || existingButton.isSelected();
                            if (!(((JButton) e.getSource()).getName()
                                .equals(ButtonNames.NEXT)))
                                isOneSelected = true;
                            if (!isOneSelected)
                            {
                                JOptionPane.showMessageDialog(newAccountWizardPane,
                                    "You must select an option before continuing.");
                            }
                            setAllowClosing(isOneSelected);
                        }
                    });
                    return panel;
                }
                
                @Override
                protected void init()
                {
                }
            };
        pages.append(newOrExistPage);
        StandardWizardPage existAuthPage =
            new StandardWizardPage(LABEL_EXIST_AUTH, true, true, true, false)
            {
                
                @Override
                protected void init()
                {
                    addText("Enter your existing userid and password below, then click "
                        + "next. Make sure you are connected to the internet before "
                        + "you proceed.");
                    addText(" ");
                    final JTextField useridField = new JTextField(30);
                    final JPasswordField passwordField = new JPasswordField(30);
                    JPanel useridPanel = new JPanel();
                    JPanel passwordPanel = new JPanel();
                    useridPanel.setLayout(new BorderLayout());
                    passwordPanel.setLayout(new BorderLayout());
                    JPanel useridInnerPanel = new JPanel();
                    JPanel passwordInnerPanel = new JPanel();
                    useridPanel.setLayout(new BorderLayout());
                    passwordPanel.setLayout(new BorderLayout());
                    useridPanel.add(useridInnerPanel, BorderLayout.WEST);
                    passwordPanel.add(passwordInnerPanel, BorderLayout.WEST);
                    JLabel useridLabel = new JLabel("    Userid:");
                    useridLabel.setPreferredSize(new Dimension(100, useridLabel
                        .getPreferredSize().height));
                    JLabel passwordLabel = new JLabel("    Password:");
                    passwordLabel.setPreferredSize(new Dimension(100, passwordLabel
                        .getPreferredSize().height));
                    useridInnerPanel.add(useridLabel, BorderLayout.WEST);
                    passwordInnerPanel.add(passwordLabel, BorderLayout.WEST);
                    useridInnerPanel.add(useridField, BorderLayout.EAST);
                    passwordInnerPanel.add(passwordField, BorderLayout.EAST);
                    addComponent(useridPanel);
                    addComponent(passwordPanel);
                    addPageListener(new PageListener()
                    {
                        
                        @Override
                        public void pageEventFired(PageEvent e)
                        {
                            if (e.getID() != PageEvent.PAGE_CLOSING)
                                return;
                            if (!(e.getSource() instanceof JButton))
                            {
                                /*
                                 * If we get here then the page close is a
                                 * result of someone calling setCurrenPage on
                                 * the wizard, so we want to honor that request
                                 */
                                setAllowClosing(true);
                                return;
                            }
                            if (!((JButton) e.getSource()).getName().equals(
                                ButtonNames.NEXT))
                            {
                                setAllowClosing(true);
                                return;
                            }
                            /*
                             * If we're here, the user is trying to leave the
                             * page by way of the next button. We'll show a
                             * status dialog over the frame as we contact the
                             * user's realm server to validate their username
                             * and password. If all of this fails, we show a
                             * JOptionPane message dialog alerting that user
                             * that they're realm server is offline (or they
                             * provided a userid with an incorrect realm
                             * server), or the username or password was
                             * incorrect.
                             */
                            setAllowClosing(false);
                            // The above is to deny closing by default. Most of
                            // the
                            // cases that this method can terminate result in
                            // closing denied, so we'll explicitly set when we
                            // want
                            // to allow it instead of explicitly setting when we
                            // want to deny it.
                            final String userid = useridField.getText().toLowerCase();
                            final String password = passwordField.getText();
                            if (Storage.getLocalUser(userid) != null)
                            {
                                JOptionPane
                                    .showMessageDialog(newAccountFrame,
                                        "You've already added that userid to this computer.");
                                return;
                            }
                            if (userid.equals("") || password.equals(""))
                            {
                                JOptionPane.showMessageDialog(newAccountFrame,
                                    "You didn't enter a userid and password.");
                                return;
                            }
                            if (!userid.contains(":"))
                            {
                                JOptionPane
                                    .showMessageDialog(
                                        newAccountFrame,
                                        "Userids are of the format realm:username . The "
                                            + "userid you specified did not contain a : character.");
                                return;
                            }
                            if (userid.indexOf(":") != userid.lastIndexOf(":"))
                            {
                                JOptionPane
                                    .showMessageDialog(
                                        newAccountFrame,
                                        "Userids are of the format realm:username . The "
                                            + "userid you specified containes more than one : character.");
                                return;
                            }
                            /*
                             * At this point, the userid and password are valid
                             * input. We need to construct a one-time
                             * CommandCommunicator to the realm server and
                             * authenticate, to make sure that they entered a
                             * correct userid and password.
                             */
                            final StatusDialog statusDialog =
                                new StatusDialog(newAccountFrame,
                                    "Please wait while we validate your username and password...");
                            statusDialog.showImmediate();
                            new Thread()
                            {
                                public void run()
                                {
                                    try
                                    {
                                        vars.userid = userid;
                                        vars.password = password;
                                        System.out.println();
                                        CommandCommunicator lcom = null;
                                        try
                                        {
                                            try
                                            {
                                                lcom =
                                                    new CommandCommunicator(
                                                        new Communicator(
                                                            newAccountFrame, Userids
                                                                .toRealm(userid),
                                                            false, true, "normal",
                                                            Userids.toUsername(userid),
                                                            "", password,
                                                            vars.trustedCerts, null,
                                                            null));
                                            }
                                            catch (Exception e2)
                                            {
                                                /*
                                                 * Clear the security key in
                                                 * case that was the cause of
                                                 * the problem, might want to
                                                 * set it to just clear it if
                                                 * the communicator handshake
                                                 * failed in the future
                                                 */
                                                vars.serverKey = null;
                                                e2.printStackTrace();
                                                statusDialog.dispose();
                                                JOptionPane
                                                    .showMessageDialog(
                                                        newAccountFrame,
                                                        "A connection to the server could not be established. Make "
                                                            + "sure you're connected to the internet and that you entered "
                                                            + "a correct userid.\n"
                                                            + "If you entered the security keys for this server, you may have entered them incorrectly.");
                                                return;
                                            }
                                            try
                                            {
                                                String res =
                                                    lcom.authenticate("normal", Userids
                                                        .toUsername(userid), "",
                                                        password);
                                                if (!res.equalsIgnoreCase("OK"))
                                                    throw new RuntimeException(
                                                        "Expected status OK, but received "
                                                            + res);
                                            }
                                            catch (Exception e2)
                                            {
                                                e2.printStackTrace();
                                                statusDialog.dispose();
                                                JOptionPane
                                                    .showMessageDialog(newAccountFrame,
                                                        "Your realm server reported that your userid or password was incorrect.");
                                                return;
                                            }
                                            statusDialog.dispose();
                                            newAccountWizardPane
                                                .setCurrentPage(LABEL_ENTER_KEYS);
                                        }
                                        finally
                                        {
                                            if (lcom != null)
                                                try
                                                {
                                                    lcom.getCommunicator().shutdown();
                                                }
                                                catch (Exception exception)
                                                {
                                                    exception.printStackTrace();
                                                }
                                        }
                                    }
                                    finally
                                    {
                                        newAccountWizardPane
                                            .setNextPage(newAccountWizardPane
                                                .getPageByTitle(LABEL_ENTER_KEYS));
                                        statusDialog.dispose();
                                    }
                                }
                            }.start();
                        }
                    });
                }
            };
        pages.append(existAuthPage);
        StandardWizardPage newAuthPage =
            new StandardWizardPage(LABEL_NEW_AUTH, true, true, false, false)
            {
                
                @Override
                protected void init()
                {
                    addText("Right now, you can't create a new user account. We're "
                        + "still working on this functionality. If you'd like "
                        + "a user account, contact the owner of a realm server, and have "
                        + "them create an account for you. Then, choose \"Use an "
                        + "OpenGroove account that you have already created\" "
                        + "on the previous step.");
                    addText("For more information, or to get an OpenGroove account at "
                        + "the opengroove.org realm, contact us at support@opengroove.org");
                }
                
            };
        pages.append(newAuthPage);
        /*
         * This page checks to see if the user has security keys present on
         * their account. If they do, the user on the local machine is prompted
         * for a .ogva file that contains their account's keys. It then checks
         * to make sure that the private keys in the file match with the public
         * keys (by encrypting a random number, decrypting it, and validating
         * that it is still the same), and that the public keys in the file
         * match the public keys on the user's account. If the user does not
         * already have keys on their account, some keys are generated for them,
         * with an indeterminate progress bar in the window. Once keygen is
         * done, the next button is enabled, and a message appears to the user
         * that keygen is done and they can proceed. The keys are stored on the
         * account on the server.
         */
        StandardWizardPage securityKeysPage =
            new StandardWizardPage(LABEL_ENTER_KEYS, false, true, false, false)
            {
                private JProgressBar progress;
                
                private JButton button;
                
                private MultilineLabel label;
                
                private Thread startKeyGenThread = new Thread()
                {
                    public void run()
                    {
                        progress
                            .setString("Generating encryption key (step 1 of 3), this may take a few minutes...");
                        progress.setIndeterminate(true);
                        System.out.println("enc");
                        RSA enc = new RSA(SECURITY_KEY_SIZE);
                        progress
                            .setString("Generating signature key (step 2 of 3), this may take a few minutes...");
                        System.out.println("sig");
                        RSA sig = new RSA(SECURITY_KEY_SIZE);
                        progress
                            .setString("Uploading public keys (step 3 of 3), this may take a few minutes...");
                        System.out.println("upload");
                        CommandCommunicator com = null;
                        try
                        {
                            vars.encPub = enc.getPublicKey();
                            vars.encPrv = enc.getPrivateKey();
                            vars.encMod = enc.getModulus();
                            vars.sigPub = sig.getPublicKey();
                            vars.sigPrv = sig.getPrivateKey();
                            vars.sigMod = sig.getModulus();
                            String encPubString = vars.encPub.toString(16);
                            String encModString = vars.encMod.toString(16);
                            String sigPubString = vars.sigPub.toString(16);
                            String sigModString = vars.sigMod.toString(16);
                            com =
                                new CommandCommunicator(new Communicator(
                                    newAccountFrame, Userids.toRealm(vars.userid),
                                    false, true, "normal", "", "", "",
                                    vars.trustedCerts, null, null));
                            com.authenticate("normal", Userids.toUsername(vars.userid),
                                "", vars.password);
                            com.setUserSetting("" + UserSettings.KEY_ENC_PUB,
                                encPubString);
                            com.setUserSetting("" + UserSettings.KEY_ENC_MOD,
                                encModString);
                            com.setUserSetting("" + UserSettings.KEY_SIG_PUB,
                                sigPubString);
                            com.setUserSetting("" + UserSettings.KEY_SIG_MOD,
                                sigModString);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            progress.setString("");
                            progress.setIndeterminate(false);
                            label
                                .setText("An error occured. This is probably because you were disconnected "
                                    + "from the internet while running this wizard. Close the wizard, "
                                    + "and then open it again. If you had chosen to create a new account, "
                                    + "choose to use an existing account when you open the wizard again, "
                                    + "and enter the userid and password you used while creating the account. "
                                    + "If the error keeps occuring, send an email to support@opengroove.org"
                                    + " and we will help you to resolve the problem.");
                            label.setVisible(true);
                            return;
                        }
                        finally
                        {
                            try
                            {
                                if (com != null)
                                    com.getCommunicator().shutdown();
                            }
                            catch (Exception exception)
                            {
                                exception.printStackTrace();
                            }
                        }
                        progress.setString("");
                        progress.setIndeterminate(false);
                        label
                            .setText("Your security keys have been successfully generated. Click "
                                + "next to continue.");
                        label.setVisible(true);
                        newAccountWizardPane.setNextPage(newAccountWizardPane
                            .getPageByTitle(LABEL_COMPUTER));
                        setNextAllowed(true);
                    }
                };
                
                private Thread browseForKeyThread = new Thread()
                {
                    public void run()
                    {
                        JFileChooser fc = new JFileChooser();
                        fc.setFileFilter(new FileNameExtensionFilter(
                            "OpenGroove Account Key Files", "ogva"));
                        int fcResult = fc.showOpenDialog(newAccountFrame);
                        if (fcResult != JFileChooser.APPROVE_OPTION)
                            return;
                        button.setVisible(false);
                        label.setVisible(false);
                        progress.setIndeterminate(true);
                        progress.setString("Reading file...");
                        FieldFile file;
                        BigInteger encPub;
                        BigInteger encPrv;
                        BigInteger encMod;
                        BigInteger sigPub;
                        BigInteger sigPrv;
                        BigInteger sigMod;
                        try
                        {
                            file = new FieldFile(fc.getSelectedFile());
                            if (!file.checkExists(Fields.encMod, Fields.encPrv,
                                Fields.encPub, Fields.sigMod, Fields.sigPrv,
                                Fields.sigPub))
                                throw new Exception();
                            encPub = new BigInteger(file.getField(Fields.encPub), 16);
                            encPrv = new BigInteger(file.getField(Fields.encPrv), 16);
                            encMod = new BigInteger(file.getField(Fields.encMod), 16);
                            sigPub = new BigInteger(file.getField(Fields.sigPub), 16);
                            sigPrv = new BigInteger(file.getField(Fields.sigPrv), 16);
                            sigMod = new BigInteger(file.getField(Fields.sigMod), 16);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(newAccountFrame,
                                "The file you selected is not valid, "
                                    + "could not be opened, " + "or is corrupt.");
                            label.setVisible(true);
                            progress.setIndeterminate(false);
                            progress.setString("");
                            button.setVisible(true);
                            return;
                        }
                        /*
                         * TODO: start the progress bar going, load the keys in
                         * to BigIntegers, encrypt a random number with the
                         * public key and verify decryption with the private
                         * key, for the enc key, and do the reverse for the sig
                         * key, then check to see if the public keys match the
                         * public keys on the server (and show a warning that
                         * the keys are for the wrong account if they don't),
                         * and if all of this works out, stick the private keys
                         * (and the public keys too) onto the wizard vars and
                         * continue on to the account info page.
                         */
                        progress.setString("Validating encryption key...");
                        boolean isEncValid = RSA.verifySet(encPub, encMod, encPrv);
                        if (!isEncValid)
                        {
                            label.setVisible(true);
                            progress.setIndeterminate(false);
                            progress.setString("");
                            JOptionPane.showMessageDialog(newAccountFrame,
                                "The account file you provided contains "
                                    + "a mismatched encryption keypair.");
                            button.setVisible(true);
                            return;
                        }
                        progress.setString("Validating signature key...");
                        boolean isSigValid = RSA.verifySet(sigPub, sigMod, sigPrv);
                        if (!isSigValid)
                        {
                            label.setVisible(true);
                            progress.setIndeterminate(false);
                            progress.setString("");
                            JOptionPane.showMessageDialog(newAccountFrame,
                                "The acount file you provided contains "
                                    + "a mismatched signature keypair.");
                            button.setVisible(true);
                            return;
                        }
                        /*
                         * The file itself, and the key contained therein, are
                         * valid, so we need to validate the public keys against
                         * the ones on the server and then stick them into the
                         * new account wizard vars.
                         */
                        CommandCommunicator com = null;
                        try
                        {
                            com =
                                new CommandCommunicator(new Communicator(
                                    newAccountFrame, Userids.toRealm(vars.userid),
                                    false, true, "normal", "", "", "",
                                    vars.trustedCerts, null, null));
                            com.authenticate("normal", Userids.toUsername(vars.userid),
                                "", vars.password);
                            BigInteger existingEncPub =
                                new BigInteger(com.getUserSetting("", ""
                                    + UserSettings.KEY_ENC_PUB), 16);
                            BigInteger existingEncMod =
                                new BigInteger(com.getUserSetting("", ""
                                    + UserSettings.KEY_ENC_MOD), 16);
                            BigInteger existingSigPub =
                                new BigInteger(com.getUserSetting("", ""
                                    + UserSettings.KEY_SIG_PUB), 16);
                            BigInteger existingSigMod =
                                new BigInteger(com.getUserSetting("", ""
                                    + UserSettings.KEY_SIG_MOD), 16);
                            if (!(existingEncPub.equals(encPub)
                                && existingEncMod.equals(encMod)
                                && existingSigPub.equals(sigPub) && existingSigMod
                                .equals(sigMod)))
                            {
                                label.setVisible(true);
                                progress.setIndeterminate(false);
                                progress.setString("");
                                JOptionPane.showMessageDialog(newAccountFrame,
                                    "The account file you provided is for "
                                        + "a different account, not yours.");
                                button.setVisible(true);
                                return;
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            label.setVisible(true);
                            progress.setIndeterminate(false);
                            progress.setString("");
                            JOptionPane.showMessageDialog(newAccountFrame,
                                "An error occured while connecting to your server.");
                            button.setVisible(true);
                            return;
                        }
                        finally
                        {
                            if (com != null)
                                com.getCommunicator().shutdown();
                        }
                        vars.encPub = encPub;
                        vars.encMod = encMod;
                        vars.encPrv = encPrv;
                        vars.sigPub = sigPub;
                        vars.sigMod = sigMod;
                        vars.sigPrv = sigPrv;
                        label.setText("Your account security key file has been "
                            + "successfully verified and imported. "
                            + "Click next to continue.");
                        label.setVisible(true);
                        progress.setString("");
                        progress.setIndeterminate(false);
                        setNextAllowed(true);
                        newAccountWizardPane.setNextPage(newAccountWizardPane
                            .getPageByTitle(LABEL_COMPUTER));
                    }
                };
                
                @Override
                protected void init()
                {
                    System.out.println("about to check account");
                    progress = new JProgressBar();
                    progress.setIndeterminate(true);
                    progress.setStringPainted(true);
                    progress.setString("Checking your account, please wait...");
                    button = new JButton();
                    label = new MultilineLabel();
                    label.setVisible(false);
                    button.setVisible(false);
                    addText("OpenGroove uses security keys to encrypt your correspondence "
                        + "with other people. This prevents others from reading your "
                        + "correspondence, or from corresponding with someone else while "
                        + "pretending to be you. If your account already has security keys "
                        + "on it (this is usually the case if you're already using your account "
                        + "on another computer), then you will need to provide OpenGroove"
                        + " with a file that contains those keys. If your account does not "
                        + "have security keys on it (this is usually the case if you're"
                        + " creating a new account, or if you never got to this step when"
                        + " creating your account before), then OpenGroove will generate "
                        + "security keys for you.");
                    addComponent(progress);
                    addComponent(label);
                    addComponent(button);
                    addPageListener(new PageListener()
                    {
                        
                        @Override
                        public void pageEventFired(PageEvent e)
                        {
                            if (e.getID() == PageEvent.PAGE_OPENED)
                            {
                                new Thread()
                                {
                                    public void run()
                                    {
                                        /*
                                         * The page has just been shown. We need
                                         * to check the server to see if the
                                         * user already has security keys.
                                         */
                                        CommandCommunicator com = null;
                                        try
                                        {
                                            System.out
                                                .println("checking for security keys");
                                            com =
                                                new CommandCommunicator(
                                                    new Communicator(newAccountFrame,
                                                        Userids.toRealm(vars.userid),
                                                        false, true, "normal", "", "",
                                                        "", vars.trustedCerts, null,
                                                        null));
                                            System.out.println("security key auth");
                                            com.authenticate("normal", Userids
                                                .toUsername(vars.userid), "",
                                                vars.password);
                                            System.out
                                                .println("downloading remote keys");
                                            String existingEncPub =
                                                com.getUserSetting("", ""
                                                    + UserSettings.KEY_ENC_PUB);
                                            String existingEncMod =
                                                com.getUserSetting("", ""
                                                    + UserSettings.KEY_ENC_MOD);
                                            String existingSigPub =
                                                com.getUserSetting("", ""
                                                    + UserSettings.KEY_SIG_PUB);
                                            String existingSigMod =
                                                com.getUserSetting("", ""
                                                    + UserSettings.KEY_SIG_MOD);
                                            System.out
                                                .println("performing checks for new keys");
                                            boolean needsNewKeys =
                                                existingEncPub == null
                                                    || existingEncMod == null
                                                    || existingSigPub == null
                                                    || existingSigMod == null
                                                    || com.listComputers("").length == 0;
                                            if (needsNewKeys)
                                            {
                                                label
                                                    .setText("You do not have any security keys on your account. "
                                                        + "OpenGroove is ready to generate security keys for you. "
                                                        + "When you're ready, click start.");
                                                button.setText("Start");
                                                button
                                                    .addActionListener(new ActionListener()
                                                    {
                                                        
                                                        @Override
                                                        public void actionPerformed(
                                                            ActionEvent e)
                                                        {
                                                            button.setVisible(false);
                                                            label.setVisible(false);
                                                            startKeyGenThread.start();
                                                        }
                                                    });
                                            }
                                            else
                                            {
                                                label
                                                    .setText("Your account already has security keys. You'll need to provide "
                                                        + "OpenGroove with a file that contains your account's "
                                                        + "private keys. This should have the file extension .ogva, "
                                                        + "and can be created by choosing "
                                                        + Conventions.formatMenuPath(
                                                            "File", "Export Account")
                                                        + " in the launchbar on a computer on which you are "
                                                        + "already using this account. If you aren't using "
                                                        + "this account on any computers, send us an email at "
                                                        + "support@opengroove.org, and we will help you restore"
                                                        + " your account.");
                                                button.setText("Browse");
                                                button
                                                    .addActionListener(new ActionListener()
                                                    {
                                                        
                                                        @Override
                                                        public void actionPerformed(
                                                            ActionEvent e)
                                                        {
                                                            browseForKeyThread.start();
                                                        }
                                                    });
                                            }
                                            label.setVisible(true);
                                            button.setVisible(true);
                                            progress.setIndeterminate(false);
                                            progress.setString("");
                                        }
                                        catch (Exception exception)
                                        {
                                            exception.printStackTrace();
                                            fail();
                                        }
                                        finally
                                        {
                                            if (com != null)
                                                com.getCommunicator().shutdown();
                                        }
                                    }
                                }.start();
                            }
                        }
                    });
                }
                
                protected void fail()
                {
                    /*
                     * TODO: probably split this out so that it handles issues
                     * with connecting to the user's server by allowing them to
                     * re-try the keygen stuff or something.
                     */
                    progress.setIndeterminate(false);
                    progress.setMinimum(0);
                    progress.setMaximum(1);
                    progress.setValue(0);
                    progress
                        .setString("An error has occured. Cancel the wizard, then open it again.");
                    JOptionPane
                        .showMessageDialog(
                            newAccountFrame,
                            "A problem occured during security key processing. You'll need\n"
                                + " to close the new account wizard and then open it again. If you chose to\n"
                                + "create a new account instead of using an existing one, then the new account\n"
                                + "has already been created for you, and you should choose to use an existing\n"
                                + "account when you open this wizard again.");
                    setBackAllowed(false);
                    setNextAllowed(false);
                    setLastStep(false);
                    setCancelAllowed(true);
                    setAllowClosing(true);
                }
            };
        pages.append(securityKeysPage);
        // I'll worry about the user's general info later. For now, their userid
        // is shown where their real name (if they specify it in their contact
        // card) would be shown once this is added, and there won't be any way
        // to contact the user via email. Before I open up public registration,
        // I'll finish this up.
        // StandardWizardPage generalInfoPage = new StandardWizardPage(
        // "Enter your user information", false, true, false,
        // false)
        // {
        // @Override
        // protected void init()
        // {
        // // TODO Auto-generated method stub
        //                
        // }
        // };
        // pages.append(generalInfoPage);
        StandardWizardPage computerNamePage =
            new StandardWizardPage(LABEL_COMPUTER, false, true, true, false)
            {
                private JTextField field;
                
                @Override
                protected void init()
                {
                    field = new JTextField(30);
                    addText("Select the name that you want for this computer. "
                        + "This should contain only letters, numbers, and "
                        + "hyphens, and will be converted to lowercase. "
                        + "You cannot change this later.");
                    String physicalComputerName = System.getenv("COMPUTERNAME");
                    String username = System.getProperty("user.name");
                    String suggestedComputerName;
                    if (physicalComputerName == null && username == null)
                        suggestedComputerName = "computer";
                    else if (physicalComputerName == null)// &&username !=
                        // null
                        suggestedComputerName = username;
                    else
                        // if(physicalComputerName != null && username != null)
                        suggestedComputerName = physicalComputerName + "-" + username;
                    suggestedComputerName =
                        suggestedComputerName.replaceAll("[^a-zA-Z0-9]", "-");
                    suggestedComputerName = suggestedComputerName.toLowerCase();
                    field.setText(suggestedComputerName);
                    JPanel panel = new JPanel();
                    panel.setLayout(new BorderLayout());
                    JPanel inner = new JPanel();
                    inner.setLayout(new BorderLayout());
                    panel.add(inner, BorderLayout.CENTER);
                    inner.add(field, BorderLayout.WEST);
                    panel.add(new JLabel("            "), BorderLayout.WEST);
                    addComponent(new JLabel(""));
                    addComponent(panel);
                    addPageListener(new PageListener()
                    {
                        
                        @Override
                        public void pageEventFired(PageEvent e)
                        {
                            if (!(e.getSource() instanceof JButton))
                            {
                                setAllowClosing(true);
                                return;
                            }
                            if (e.getID() == PageEvent.PAGE_CLOSING)
                            {
                                setAllowClosing(false);
                                setNextAllowed(false);
                                new Thread()
                                {
                                    public void run()
                                    {
                                        /*
                                         * We'll try to create the computer on
                                         * the server. If this fails, we don't
                                         * allow closing, and we show a message
                                         * telling the user that it failed, and
                                         * the reason (IE an internet error, a
                                         * computer with that name already
                                         * exists, etc.). If it succeeds, then
                                         * we create a local user, inject all of
                                         * the info we've collected thus far,
                                         * and forward on to the next page,
                                         * which is a page telling the user that
                                         * they've successfully created an
                                         * OpenGroove account, and they can
                                         * click finish to open the login screen
                                         * for their account.
                                         */
                                        String computerName = field.getText();
                                        if (!computerName.replaceAll("[^a-zA-Z0-9]",
                                            "-").equalsIgnoreCase(computerName))
                                        {
                                            JOptionPane
                                                .showMessageDialog(newAccountFrame,
                                                    "The computer name you specified contains invalid characters.");
                                            return;
                                        }
                                        computerName = computerName.toLowerCase();
                                        field.setText(computerName);
                                        CommandCommunicator com = null;
                                        try
                                        {
                                            com =
                                                new CommandCommunicator(
                                                    new Communicator(newAccountFrame,
                                                        Userids.toRealm(vars.userid),
                                                        false, true, "normal", "", "",
                                                        "", vars.trustedCerts, null,
                                                        null));
                                            com.authenticate("normal", Userids
                                                .toUsername(vars.userid), "",
                                                vars.password);
                                            try
                                            {
                                                com.createComputer(computerName, "pc");
                                            }
                                            catch (Exception e2)
                                            {
                                                if (!(e2 instanceof FailedResponseException))
                                                    throw e2;
                                                JOptionPane
                                                    .showMessageDialog(newAccountFrame,
                                                        "That computer name is already in use.");
                                                return;
                                            }
                                        }
                                        catch (Exception e2)
                                        {
                                            e2.printStackTrace();
                                            JOptionPane
                                                .showMessageDialog(newAccountFrame,
                                                    "An error occured while connecting to your server.");
                                            return;
                                        }
                                        finally
                                        {
                                            if (com != null)
                                                try
                                                {
                                                    com.getCommunicator().shutdown();
                                                }
                                                catch (Exception exception)
                                                {
                                                    exception.printStackTrace();
                                                }
                                        }
                                        /*
                                         * Ok, we've successfully created the
                                         * computer. Now we store everything in
                                         * a LocalUser object, and add it to the
                                         * storage.
                                         */
                                        LocalUser user =
                                            Storage.getStore().createUser();
                                        user.setAutoSignOn(false);
                                        user.setComputer(computerName);
                                        user.setEmailAddress("");
                                        user.setEncPassword(Hash.hash(vars.password));
                                        user.setLag(0);
                                        user.setLocalVisible(false);
                                        user.setPasswordHint(null);
                                        user.setRasEncMod(vars.encMod);
                                        user.setRsaEncPrv(vars.encPrv);
                                        user.setRsaEncPub(vars.encPub);
                                        user.setRsaSigMod(vars.sigMod);
                                        user.setRsaSigPrv(vars.sigPrv);
                                        user.setRsaSigPub(vars.sigPub);
                                        user.setSearchVisible(false);
                                        user.setStoredPassword(null);
                                        user.setUserid(vars.userid);
                                        user.getTrustedCertificates().addAll(
                                            vars.trustedCerts);
                                        Storage.addUser(user);
                                        refreshTrayMenu();
                                        newAccountWizardPane.setCurrentPage(LABEL_DONE);
                                        vars.finishedWizard = true;
                                    }
                                }.start();
                            }
                        }
                    });
                }
            };
        pages.append(computerNamePage);
        StandardWizardPage finishedPage =
            new StandardWizardPage(LABEL_DONE, false, false, true, true)
            {
                
                @Override
                protected void init()
                {
                    addText("You have successfully added your OpenGroove account. "
                        + "When you click finish, you will be prompted for your "
                        + "password. When you start OpenGroove next time, you can "
                        + "log in by right-clicking the tray icon, clicking on "
                        + "your userid, and clicking \"log in\".");
                }
            };
        pages.append(finishedPage);
        // end pages
        newAccountWizardPane.setPageList(pages);
        newAccountWizardPane.setCancelAction(new AbstractAction("Cancel")
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                newAccountFrame.dispose();
            }
        });
        newAccountWizardPane.setFinishAction(new AbstractAction("Finish")
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (vars.finishedWizard)
                    showLoginWindow(vars.userid);
                newAccountFrame.dispose();
            }
        });
        newAccountWizardPane.initComponents();
        newAccountFrame.getContentPane().setLayout(new BorderLayout());
        newAccountFrame.getContentPane().removeAll();
        newAccountFrame.getContentPane().add(newAccountWizardPane);
        newAccountFrame.setSize(650, 500);
        newAccountFrame.setResizable(false);
        newAccountFrame.setLocationRelativeTo(null);
        newAccountFrame.show();
    }
    
    protected static String getWelcomeWizardMessage()
    {
        return "" + "This appears to be your first time using OpenGroove "
            + "on this computer. Before you can use OpenGroove, you "
            + "need to create an account. Click Next to continue.";
    }
    
    protected static boolean anyServerConnections()
    {
        for (UserContext context : userContextMap.values())
        {
            if (context.getCom() != null && context.getCom().getCommunicator() != null
                && context.getCom().getCommunicator().isActive())
            {
                return true;
            }
        }
        return false;
    }
    
    private static HashMap<String, Class<LookAndFeel>> lookAndFeelClasses =
        new HashMap<String, Class<LookAndFeel>>();
    
    private static JFrame aboutWindow;
    
    protected static void initAboutWindow()
    {
        /*
         * TODO: This needs to show about info for plugins, credits for stuff
         * that require it (such as jide, arimaa, and the bezier algorithm used
         * for the fill containers), and info about who develops OpenGroove
         * (which is just me, Alex, right now, but could be additional people in
         * the future) and how to contribute. It also needs to be split into
         * it's own frame, instead of a dialog, that could be always-on-top.
         */
        aboutWindow = new JFrame("About OpenGroove");
        // aboutWindow.setAlwaysOnTop(true);
        aboutWindow.setSize(400, 300);
        aboutWindow.setLocationRelativeTo(null);
        aboutWindow.getContentPane().setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        aboutWindow.getContentPane().add(panel, BorderLayout.NORTH);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("OpenGroove version " + getDisplayableVersion()));
        panel.add(Box.createVerticalStrut(3));
        try
        {
            panel.add(new WebsiteButton("www.opengroove.org", new URI(
                "http://www.opengroove.org")));
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
        panel.add(Box.createVerticalStrut(3));
        panel.add(new JLabel("Created by Alexander Boyd"));
    }
    
    /**
     * Shows an about window that describes OpenGroove and it's current version.
     * In the future, this will also show the about screens for any plugins that
     * have an about screen.
     */
    protected static synchronized void showAboutWindow()
    {
        if (aboutWindow == null)
            initAboutWindow();
        aboutWindow.show();
        bringToFront(aboutWindow);
    }
    
    private static final Object updateCheckLock = new Object();
    private static final SettingSpec SETTING_SPEC_LAUNCHBAR_ALWAYS_ON_TOP =
        new SettingSpec("g", "lb", "", "alwaysontop");
    
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
                    URL updateUrl = new URL(SYSTEM_UPDATE_SITE);
                    Properties p = new Properties();
                    p.load(updateUrl.openStream());
                    System.out.println("version properties loaded:");
                    p.list(System.out);
                    String localVersion;
                    try
                    {
                        localVersion = Storage.readFile(new File("version"));
                    }
                    catch (Exception e)
                    {
                        localVersion = "0";
                    }
                    int localVersionNumber = Integer.parseInt(localVersion);
                    int remoteVersionNumber =
                        Integer.parseInt(p.getProperty("versionindex"));
                    boolean isAlreadyUpdated =
                        new File("appdata/systemupdates/version").exists()
                            && Integer.parseInt(Storage.readFile(new File(
                                "appdata/systemupdates/version"))) == remoteVersionNumber;
                    if (remoteVersionNumber > localVersionNumber && !isAlreadyUpdated)// we
                    // need
                    // to
                    // update
                    {
                        URL updateJarUrl = new URL(p.getProperty("url"));
                        UpdateNotification notification = new UpdateNotification();
                        JProgressBar bar = notification.getProgressBar();
                        notificationFrame.addNotification("OpenGroove", notification,
                            true);
                        File updateFile = new File("appdata/systemupdates/updates.jar");
                        File versionFile = new File("appdata/systemupdates/version");
                        if (!updateFile.getParentFile().exists())
                            updateFile.getParentFile().mkdirs();
                        if (!versionFile.getParentFile().exists())
                            versionFile.getParentFile().mkdirs();
                        versionFile.delete();
                        updateFile.delete();
                        FileOutputStream fos = new FileOutputStream(updateFile);
                        byte[] buffer = new byte[1024];
                        int amount;
                        HttpURLConnection updateJarConn =
                            (HttpURLConnection) updateJarUrl.openConnection();
                        updateJarConn.connect();
                        int max = updateJarConn.getContentLength();
                        if (max != -1)
                            bar.setMaximum(max / 1024);
                        else
                            bar.setIndeterminate(true);
                        InputStream in = updateJarConn.getInputStream();
                        int amountSoFar = 0;
                        while ((amount = in.read(buffer)) != -1)
                        {
                            amountSoFar += amount;
                            bar.setValue(amountSoFar / 1024);
                            fos.write(buffer, 0, amount);
                        }
                        fos.flush();
                        fos.close();
                        Storage.writeFile("" + remoteVersionNumber, versionFile);
                        notificationFrame.removeNotification(notification);
                        final NotificationAdapter readyNotification =
                            new NotificationAdapter(
                                notificationFrame,
                                new JLabel(
                                    "OpenGroove has been updated. Restart\nfor updates to take effect."),
                                true, false)
                            {
                                public void clicked()
                                {
                                    notificationFrame.removeNotification(this);
                                }
                            };
                        notificationFrame.addNotification("OpenGroove",
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
     * gets a string that represents the version of OpenGroove.
     * 
     * @return
     */
    protected static String getDisplayableVersion()
    {
        String buildNumberString;
        try
        {
            buildNumberString = "-b" + Storage.readFile(new File("version"));
        }
        catch (Exception ex1)
        {
            buildNumberString = "";
        }
        return "" + Version.MAJOR + "." + Version.MINOR + "." + Version.UPDATE
            + buildNumberString;
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
    protected static void showLaunchBar(UserContext context)
    {
        context.getLaunchbar().show();
        bringToFront(context.getLaunchbar());
    }
    
    /**
     * Loads the launchbar. This is called for each user when they log in.
     */
    private static void loadLaunchBar(String userid, final UserContext context)
    {
        final JFrame launchbar = context.getLaunchbar();
        launchbar.setIconImage(trayimage);
        launchbar.setSize(300, 500);
        JPanel rightPanel = new JPanel();
        /*
         * In the future, rightPanel could contain some sort of OpenGroove icon
         * that animates while a complex task is in progress, similar to
         * Microsoft Groove. It could also contain additional controls. For now,
         * though, we'll just leave it blank.
         * 
         * UPDATE: actually, we're going to add some controls to it, mainly the
         * message button that allows us to see message history, and the
         * settings button which allows us to edit settings.
         */
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));
        JLinkButton messageHistoryButton =
            new JLinkButton(Icons.MESSAGE_CONFIG_16.getIcon());
        messageHistoryButton.addActionListener(new ActionListener()
        {
            
            public void actionPerformed(ActionEvent e)
            {
                context.getMessageHistoryFrame().show();
            }
        });
        JLinkButton settingsLinkButton = new JLinkButton(Icons.SETTINGS_16.getIcon());
        messageHistoryButton.setFocusable(false);
        settingsLinkButton.setFocusable(false);
        messageHistoryButton.setToolTipText(ComponentUtils
            .htmlTipWrap("View message history and drafts"));
        settingsLinkButton.setToolTipText(ComponentUtils
            .htmlTipWrap("Edit OpenGroove's Settings"));
        messageHistoryButton.setMargin(new Insets(0, 0, 0, 0));
        settingsLinkButton.setMargin(new Insets(0, 0, 0, 0));
        settingsLinkButton.addActionListener(new ActionListener()
        {
            
            public void actionPerformed(ActionEvent e)
            {
                context.getSettingsManager().showDialog();
            }
        });
        rightPanel.add(messageHistoryButton);
        rightPanel.add(settingsLinkButton);
        JPanel lowerPanel = new JPanel();
        lowerPanel.setOpaque(false);
        lowerPanel.setLayout(new BorderLayout());
        lowerPanel.setBorder(new EmptyBorder(6, 3, 6, 20));
        context.setLocalStatusButton(new JideButton());
        context.getLocalStatusButton().setOpaque(false);
        context.getLocalStatusButton().setButtonStyle(JideButton.HYPERLINK_STYLE);
        context.getLocalStatusButton().setIcon(
            new ImageIcon(OpenGroove.Icons.USER_OFFLINE_16.getImage()));
        context.getLocalStatusButton().addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                context.getUserStatusMenu().show(context.getLocalStatusButton(), 0, 0);
            }
        });
        context.getLocalStatusButton().setToolTipText(
            ComponentUtils.htmlTipWrap("This icon represents your current status. "
                + "Click for more info. Note that you cannot have "
                + "the status <i>nonexistant</i> or "
                + "<i>unknown</i> (only contacts can have those statuses)."));
        lowerPanel.add(context.getLocalStatusButton(), BorderLayout.WEST);
        context.setLocalUsernameButton(new JideButton());
        context.getLocalUsernameButton().setOpaque(false);
        context.getLocalUsernameButton().setButtonStyle(JideButton.HYPERLINK_STYLE);
        context.getLocalUsernameButton().setText(
            Storage.getLocalUser(userid).getDisplayName());
        context.getLocalUsernameButton().setToolTipText(
            ComponentUtils.htmlTipWrap("Click here to change your name. Changing "
                + "your name does not affect your userid."));
        final JPopupMenu contactRenamePopup = new JPopupMenu();
        final JTextField contactRenameField = new JTextField(15);
        context.getLocalUsernameButton().addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                contactRenameField.setText(context.getDisplayName());
                contactRenamePopup.show(context.getLocalUsernameButton(), 0, 0);
                contactRenameField.requestFocusInWindow();
            }
        });
        contactRenamePopup.add(contactRenameField);
        contactRenameField.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                contactRenamePopup.setVisible(false);
            }
        });
        contactRenamePopup.addPopupMenuListener(new PopupMenuListener()
        {
            
            @Override
            public void popupMenuCanceled(PopupMenuEvent e)
            {
            }
            
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
            {
                String newName = contactRenameField.getText();
                if (newName.trim().equals(""))
                    newName = "";
                context.getLocalUser().setRealName(newName);
            }
            
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e)
            {
            }
        });
        context.getLocalUser().addChangeListener("realName",
            new PropertyChangeListener()
            {
                
                @Override
                public void propertyChange(PropertyChangeEvent evt)
                {
                    context.getLocalUsernameButton().setText(context.getDisplayName());
                    context.getLaunchbar().setTitle(context.createLaunchbarTitle());
                    notificationFrame.reloadNotifications();
                    refreshTrayMenu();
                }
            });
        contactRenameField.setToolTipText(ComponentUtils
            .htmlTipWrap("Type a new name for yourself here. If you leave "
                + "this blank, then your userid will be used. "
                + "Everyone can see this."));
        lowerPanel.add(context.getLocalUsernameButton(), BorderLayout.CENTER);
        JMenuBar bar =
            loadLaunchbarMenus(userid, context, launchbar, rightPanel, lowerPanel);
        SVGPanel workspacesGradientPanel =
            new SVGPanel(new File[] { new File("icons/backdrops/workspacestab.svg") },
                new SVGConstraints[] { new SVGConstraints(true, 0, 0) });
        workspacesGradientPanel.setLayout(new BorderLayout());
        workspacesGradientPanel.setOpaque(true);
        launchbar.getContentPane().setLayout(new BorderLayout());
        launchbar.getContentPane().add(bar, BorderLayout.NORTH);
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        JTabbedPane launchbarTabbedPane = new JTabbedPane();
        context.setLaunchbarTabbedPane(launchbarTabbedPane);
        launchbarTabbedPane.setFocusable(false);
        content.add(launchbarTabbedPane);
        launchbar.getContentPane().add(content);
        launchbarTabbedPane.add("Workspaces", new JScrollPane(workspacesGradientPanel));
        JPanel contactsPanel = new JPanel();
        context.setContactsPanel(contactsPanel);
        contactsPanel.setLayout(new BoxLayout(contactsPanel, BoxLayout.Y_AXIS));
        SVGPanel contactsTab =
            new SVGPanel(new File[] { new File("icons/backdrops/contactstab.svg") },
                new SVGConstraints[] { new SVGConstraints(true, 0, 0) });
        contactsTab.setLayout(new BorderLayout());
        JPanel contactsNorth = new JPanel();
        contactsNorth.setOpaque(false);
        contactsNorth.setLayout(new BoxLayout(contactsNorth, BoxLayout.Y_AXIS));
        contactsNorth.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLinkButton addContactButton = new JLinkButton("Add a contact");
        addContactButton.setFocusable(false);
        addContactButton.addActionListener(new ActionListener()
        {
            
            public void actionPerformed(ActionEvent e)
            {
                runAddContactWizard(context);
            }
        });
        setPlainFont(addContactButton);
        contactsNorth.add(pad(addContactButton, 2, 2));
        JCheckBox showKnownUsers = new JCheckBox("Show known users");
        showKnownUsers.setFont(Font.decode(null));
        showKnownUsers.setOpaque(false);
        showKnownUsers.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                new Thread()
                {
                    public void run()
                    {
                        context.refreshContactsPane();
                    }
                }.start();
            }
        });
        showKnownUsers.setToolTipText("<html>"
            + ComponentUtils.lineWrap(
                "If you check this, then users that you've interacted with "
                    + "will be shown, in addition to your contacts. If "
                    + "it's not checked, only your contacts will be shown.", "<br/>",
                60));
        context.setShowKnownUsersAsContacts(showKnownUsers);
        contactsNorth.add(pad(showKnownUsers, 2, 2));
        contactsNorth.add(pad(contactsPanel, 2, 6));
        contactsPanel.setOpaque(false);
        contactsTab.add(contactsNorth, BorderLayout.NORTH);
        contactsNorth.setOpaque(false);
        System.out.println("p3preferred:" + contactsTab.getPreferredSize()
            + ",p4preferred:" + contactsNorth.getPreferredSize() + ",c:"
            + contactsPanel.getPreferredSize());
        launchbarTabbedPane.add("Contacts", new JScrollPane(contactsTab));
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        workspacesGradientPanel.add(p);
        p.setOpaque(false);
        // p.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JPanel workspacePanel = new JPanel();
        context.setWorkspacePanel(workspacePanel);
        workspacePanel.setOpaque(false);
        // workspacePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        workspacePanel.setLayout(new BoxLayout(workspacePanel, BoxLayout.Y_AXIS));
        JLinkButton createWorkspaceButton =
            new JLinkButton(tm("launchbar.workspaces.create.workspace.link"));
        createWorkspaceButton.setFocusable(false);
        createWorkspaceButton.addActionListener(new ActionListener()
        {
            
            public void actionPerformed(ActionEvent e)
            {
                runNewWorkspaceWizard();
            }
        });
        setPlainFont(createWorkspaceButton);
        createWorkspaceButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        workspacePanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        p.add(pad(wrap(createWorkspaceButton), 2, 2));
        p.add(pad(workspacePanel, 2, 6));
        launchbar.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        if (context.getStorage().getConfigProperty("launchbarx") != null
            && context.getStorage().getConfigProperty("launchbary") != null)
        {
            launchbar.setLocation(Integer.parseInt(context.getStorage()
                .getConfigProperty("launchbarx")), Integer.parseInt(context
                .getStorage().getConfigProperty("launchbary")));
        }
        if (context.getStorage().getConfigProperty("launchbarwidth") != null
            && context.getStorage().getConfigProperty("launchbarheight") != null)
        {
            launchbar.setSize(Integer.parseInt(context.getStorage().getConfigProperty(
                "launchbarwidth")), Integer.parseInt(context.getStorage()
                .getConfigProperty("launchbarheight")));
        }
        launchbar.addComponentListener(new ComponentListener()
        {
            
            public void componentHidden(ComponentEvent e)
            {
                // TODO Auto-generated method stub
                
            }
            
            public void componentMoved(ComponentEvent e)
            {
                context.getStorage().setConfigProperty("launchbarx",
                    "" + launchbar.getX());
                context.getStorage().setConfigProperty("launchbary",
                    "" + launchbar.getY());
            }
            
            public void componentResized(ComponentEvent e)
            {
                context.getStorage().setConfigProperty("launchbarwidth",
                    "" + launchbar.getWidth());
                context.getStorage().setConfigProperty("launchbarheight",
                    "" + launchbar.getHeight());
            }
            
            public void componentShown(ComponentEvent e)
            {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
    protected static void runAddContactWizard(final UserContext context)
    {
        String choice =
            ItemChooser.showItemChooser(context.getLaunchbar(),
                "How would you like to add a contact?", new String[] { "search",
                    "userid" }, new String[] {
                    "<html><b>Search for other users</b><br/>"
                        + ComponentUtils.lineWrap(
                            "You can search for users on your server or on "
                                + "other servers, "
                                + "or find users connected to the same network "
                                + "that you are. You can only find a user this "
                                + "way if the user has chosen to be publicly visible.",
                            "<br/>", 60),
                    "<html><b>Enter the user's userid</b><br/>"
                        + ComponentUtils.lineWrap("If you know the user's userid, you "
                            + "can enter it instead of searching. OpenGroove "
                            + "won't check to see if the user really does "
                            + "exist until you connect to the internet. If the "
                            + "user that you want to add is not publicly "
                            + "visible, then this is the only way to add that user.",
                            "<br/>", 60) }, true);
        if (choice == null)
            return;
        if (choice.equals("search"))
        {
            JOptionPane.showMessageDialog(context.getLaunchbar(), "<html>"
                + ComponentUtils.lineWrap("We don't currently support searching for "
                    + "users. Make sure that you've chosen to receive updates "
                    + "for OpenGroove, and try again after the next update "
                    + "is downloaded to see if we've added support yet.", "<br/>", 80));
            return;
        }
        assert (choice.equals("userid"));
        boolean isValidContact = false;
        String contactId = null;
        while (!isValidContact)
        {
            contactId =
                JOptionPane.showInputDialog(context.getLaunchbar(),
                    "Enter the contact's userid.");
            if (contactId == null)
                return;
            contactId = contactId.trim();
            if (!Userids.isUserid(contactId))
            {
                JOptionPane.showMessageDialog(context.getLaunchbar(),
                    "The userid entered is not a valid userid.");
                isValidContact = false;
                continue;
            }
            assert contactId != null;
            if (context.getStorage().getLocalUser().getContact(contactId) != null
                && context.getStorage().getLocalUser().getContact(contactId)
                    .isUserContact())
            {
                JOptionPane.showMessageDialog(context.getLaunchbar(),
                    "The contact specified already exists.");
                isValidContact = false;
                continue;
            }
            /*
             * The rest of this method should be split into it's own method, so
             * that contacts can be added via other buttons and stuff, such as a
             * right-click menu item on known users or users within a workspace
             */
            else if (context.getStorage().getContact(contactId) != null)
            {
                Contact contact = context.getStorage().getContact(contactId);
                contact.setUserContact(true);
            }
            else
            {
                Contact contact = context.getStorage().getLocalUser().createContact();
                contact.setHasKeys(false);
                contact.setLocalName("");
                contact.setRealName("");
                contact.setUserContact(true);
                contact.setUserid(contactId);
                contact.setUserVerified(false);
                context.getStorage().getLocalUser().getContacts().add(contact);
            }
            new Thread()
            {
                public void run()
                {
                    context.refreshContactsPane();
                    context.updateContactStatus();
                    context.updateSubscriptions();
                }
            }.start();
            return;
        }
    }
    
    protected void reloadLaunchbarContacts()
    {
        
    }
    
    /**
     * Loads the menu bar on the launchbar.
     */
    private static JMenuBar loadLaunchbarMenus(String userid,
        final UserContext context, final JFrame launchbar, JPanel rightPanel,
        JPanel lowerPanel)
    {
        JMenuBar bar = new JMenuBar();
        final JMenu convergiaMenu =
            new IMenu("OpenGroove", new IMenuItem[] {
                new IMenuItem("Check for updates")
                {
                    
                    public void actionPerformed(ActionEvent e)
                    {
                        new Thread()
                        {
                            public void run()
                            {
                                if (!checkForUpdates())
                                    JOptionPane
                                        .showMessageDialog(launchbar,
                                            "No updates were found. OpenGroove is up to date.");
                            }
                        }.start();
                    }
                }, new IMenuItem("Settings")
                {
                    
                    public void actionPerformed(ActionEvent e)
                    {
                        showOptionsWindow(context);
                    }
                } });
        final JMenu pluginsMenu =
            new IMenu("Plugins", new IMenuItem[] { new IMenuItem("Manage plugins")
            {
                
                public void actionPerformed(ActionEvent e)
                {
                    PluginManager.showManageInstalledPluginsDialog();
                }
            } });
        launchbar.setAlwaysOnTop((Boolean) context
            .getSetting(SETTING_SPEC_LAUNCHBAR_ALWAYS_ON_TOP));
        context.getSettingsManager().addSettingListener(
            SETTING_SPEC_LAUNCHBAR_ALWAYS_ON_TOP, new SettingListener()
            {
                
                public void settingChanged(SettingSpec spec, Object newValue)
                {
                    launchbar.setAlwaysOnTop((Boolean) newValue);
                }
            });
        JMenu helpMenu = new IMenu("Help", new IMenuItem[] { new IMenuItem("Help")
        {
            
            public void actionPerformed(ActionEvent e)
            {
                context.getHelpViewer().show();
            }
        }, new IMenuItem("Contact us")
        {
            
            public void actionPerformed(ActionEvent e)
            {
                JOptionPane.showMessageDialog(launchbar,
                    "Questions? Comments? Send an email to support@opengroove.org");
            }
        }, new IMenuItem("About")
        {
            
            public void actionPerformed(ActionEvent e)
            {
                showAboutWindow();
            }
        } });
        JComponent[] menus = new JComponent[] { convergiaMenu, pluginsMenu, helpMenu };
        double[] colSpecs = new double[menus.length + 1];
        Arrays.fill(colSpecs, 0, menus.length, TableLayout.PREFERRED);
        colSpecs[menus.length] = TableLayout.FILL;
        bar.setLayout(new TableLayout(colSpecs, new double[] { TableLayout.PREFERRED,
            6, TableLayout.PREFERRED, 3 }));
        for (int i = 0; i < menus.length; i++)
        {
            // menus[i].setOpaque(false);
            menus[i].setBackground(new Color(0, 0, 0, 0));
            bar.add(menus[i], "" + i + ", 0");
        }
        bar.add(rightPanel, "" + menus.length + ", 0, r, t");
        bar.add(new JSeparator(), "0, 1, " + menus.length + ", 1, f, c");
        bar.add(lowerPanel, "0, 2, " + menus.length + ", 2, c, c");
        bar.add(new JSeparator(), "0, 3, " + menus.length + ", 3, f, b");
        bar.invalidate();
        bar.validate();
        return bar;
    }
    
    /**
     * shows a dialog to the user where they can select plugins from the ptl to
     * download. (ptl stands for Public Tool List, which is somewhat of a
     * misnomer because it contains lots of types of plugins, not just tools)
     * this method will return once the user has closed the dialog.
     * 
     * @param frame
     *            the frame to pass to the dialog's constructor. the dialog will
     *            be shown on top of this frame.
     * @param context
     *            The user context of the user that wants to search for new
     *            plugins.
     */
    public static void findNewPlugins(JFrame frame, UserContext context)
    {
        context.getPlugins().promptForDownload(frame);
    }
    
    /**
     * Shows a dialog that allows the user to configure OpenGroove.
     */
    protected static void showOptionsWindow(UserContext context)
    {
        context.getSettingsManager().showDialog();
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
    public static BufferedImage scaleImage(Image image, int width, int height)
    {
        BufferedImage b = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        /*
         * Now we do the actual scaling. The buffered image construction is
         * really only for other methods that use one; the actual scaling is
         * done by the Image class itself.
         */
        g.drawImage(image.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING),
            0, 0, width, height, null);
        return b;
    }
    
    /**
     * loads the image specified from the file specified. The string passed in
     * is relative to the icons folder.
     * 
     * @param string
     * @return
     */
    public static Image loadImage(String string)
    {
        System.out.println("********about to load " + string);
        try
        {
            return ImageIO.read(new File("icons", string));
        }
        catch (MalformedURLException e)
        {
            // TODO Dec 7, 2007 Auto-generated catch block
            throw new RuntimeException("TODO auto generated on Dec 7, 2007 : "
                + e.getClass().getName() + " - " + e.getMessage(), e);
        }
        catch (IOException e)
        {
            // TODO Dec 7, 2007 Auto-generated catch block
            throw new RuntimeException("TODO auto generated on Dec 7, 2007 : "
                + e.getClass().getName() + " - " + e.getMessage(), e);
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
    public static <T extends JComponent> T pad(T c, int w, int h)
    {
        c.setBorder(BorderFactory.createEmptyBorder(h, w, h, w));
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
        wframe.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
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
    
    private static final AtomicLong nextGenId = new AtomicLong(1);
    
    /**
     * creates a new id. the id should be unique for the whole OpenGroove
     * system. the first part of the id, up until the first hyphen, is this
     * user's userid, with the ":" character replaced by two dots, IE ".." .
     * 
     * @return
     */
    public static synchronized String generateId(UserContext context)
    {
        return context.getUserid().replace(":", "..") + "-"
            + System.currentTimeMillis() + "-" + nextGenId.getAndIncrement();
    }
    
    /**
     * This is now obsolete (it will be replaced by the concept of
     * LanguageContexts) and will be removed once I'm sure it's not used by
     * anything else. However, I may end up deciding to keep it and have it
     * delegate to the current language context for OpenGroove.<br/>
     * <br/>
     * 
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
     * converts the contents of this DefaultListModel to an ArrayList.
     * 
     * @param allowedMembersModel
     * @return
     */
    protected static ArrayList modelToList(DefaultListModel allowedMembersModel)
    {
        ArrayList list = new ArrayList();
        for (Object o : allowedMembersModel.toArray())
        {
            list.add(o);
        }
        return list;
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
    public static String delimited(List<String> items, String delimiter)
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
    private static void addAllToModel(DefaultListModel allowedMembersModel,
        List allowedUsers)
    {
        for (Object o : allowedUsers)
        {
            allowedMembersModel.addElement(o);
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
        /*
         * TODO: re-implement this method
         */
        return false;
    }
    
    /**
     * Opens the help viewer, if it is not already open, and shows the specified
     * help topic. Shorthand for helpviewer.showHelpTopic(path) where helpviewer
     * is OpenGroove's singleton HelpViewer.
     * 
     * @param path
     */
    public static void showHelpTopic(UserContext context, String path)
    {
        context.getHelpViewer().showHelpTopic(path);
    }
    
    /**
     * Shows information about the user specified. This method is expected to
     * undergo heavy modification with the addition of realm servers.
     * 
     * @param username
     */
    public static void showUserInformationDialog(UserContext context, String username)
    {
        showUserInformationDialog(username, context.getLaunchbar());
    }
    
    private static final String[][] userInfoLabels =
        new String[][] { new String[] { "version_major", "Major version number" },
            new String[] { "version_minor", "Minor version number" },
            new String[] { "version_update", "Update version number" },
            new String[] { "version_string", "Version" },
            new String[] { "version_build", "Build number" }, new String[] { "", "" },
            new String[] { "", "" }, new String[] { "", "" }, new String[] { "", "" },
            new String[] { "", "" } };
    
    /**
     * see showUserInformationDialog(username).
     * 
     * @param username
     * @param parent
     */
    public static void showUserInformationDialog(String username, JFrame parent)
    {
        /*
         * TODO: re-implement this method
         */
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
        return new SimpleDateFormat(getDateFormatString()).format(date);
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
    
    private static WindowTransparencyMode useWindowTransparency =
        WindowTransparencyMode.UNKNOWN;
    
    /**
     * returns true if window transparency is to be used, false otherwise.
     * 
     * @return
     */
    public static boolean useWindowTransparency()
    {
        if (useWindowTransparency.equals(WindowTransparencyMode.ENABLED))
            return true;
        else if (useWindowTransparency.equals(WindowTransparencyMode.DISABLED))
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
            if (jpm != null && jpm.isVisible() && jpm.getInvoker() == jc)
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
    
    public static final String PUBLIC_ADDON_LIST_URL =
        "http://trivergia.com:8080/ptl.properties";
    
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
    
    public static void saveJarFile(File target, JarFile source, Manifest mf)
    {
        File tempDiscard = null;
        try
        {
            File tempWrite = File.createTempFile("opengroovetemp", ".jar");
            System.out.println("tempwrite:" + tempWrite);
            tempWrite.deleteOnExit();
            JarOutputStream output =
                new JarOutputStream(new FileOutputStream(tempWrite));
            output.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
            mf.write(output);
            System.out.println("manifest:");
            mf.write(System.out);
            System.out.println("---end manifest");
            output.closeEntry();
            for (JarEntry entry : Collections.list(source.entries()))
            {
                if (entry.getName().equals("META-INF/MANIFEST.MF")
                    || entry.getName().equals("META-INF/MANIFEST.MF"))
                {
                    System.out.println("entry is a manifest");
                    continue;
                }
                output.putNextEntry(entry);
                Storage.copy(source.getInputStream(entry), output);
                output.closeEntry();
            }
            output.flush();
            output.close();
            System.out.println("source is " + source.getName());
            System.out.println("target is " + target.getPath());
            if (source.getName() != null && source.getName().equals(target.getPath()))
            {
                System.out.println("names are the same");
                source.close();
            }
            if (target.exists())
            {
                System.out.println("about to rename");
                tempDiscard = File.createTempFile("convergiatemp", ".jar");
                System.out.println(tempDiscard.delete());
                tempDiscard.deleteOnExit();
                System.out.println("renaming target " + target + " to tempdiscard "
                    + tempDiscard);
                longRename(target, tempDiscard);
            }
            System.out.println("renaming tempwrite " + tempWrite + " to target "
                + target);
            longRename(tempWrite, target);
            tempDiscard.delete();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            if (tempDiscard != null && tempDiscard.exists() && !target.exists())
                tempDiscard.renameTo(target);
            throw new RuntimeException("couldn't save to the jar file", e);
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
    public static void longRename(File src, File target) throws IOException
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
