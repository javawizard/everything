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
import net.sf.opengroove.client.com.Communicator;
import net.sf.opengroove.client.com.OldCommunicator;
import net.sf.opengroove.client.com.LowLevelCommunicator;
import net.sf.opengroove.client.com.Packet;
import net.sf.opengroove.client.com.ServerContext;
import net.sf.opengroove.client.com.StatusListener;
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
import com.jidesoft.dialog.ButtonNames;
import com.jidesoft.dialog.PageEvent;
import com.jidesoft.dialog.PageList;
import com.jidesoft.dialog.PageListener;
import com.jidesoft.swing.JideButton;
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
            sfile.mkdirs();
            Storage.initStorage(sfile);
            trayimage = scaleImage(blockTransparify(ImageIO
                .read(new File("trayicon.png")), new Color(
                255, 0, 0)), 16, 16);
            trayofflineimage = scaleImage(blockTransparify(
                ImageIO.read(new File("trayoffline.png")),
                new Color(255, 0, 0)), 16, 16);
            initNewAccountWizard();
            initLoginFrame();
            // the setProperty call below is used to avoid problems with the
            // fade effect for the taskbar notification frame
            System
                .setProperty("sun.java2d.noddraw", "true");
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
                                    System.out
                                        .println("sleeping for 3100");
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
            File[] trayfiles = new File(".")
                .listFiles(new FileFilter()
                {
                    
                    public boolean accept(File pathname)
                    {
                        return pathname.getName()
                            .startsWith("traynotify")
                            && pathname.getName().endsWith(
                                ".png");
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
                notificationTrayImages[i] = scaleImage(
                    blockTransparify(
                        notificationTrayImages[i],
                        new Color(255, 0, 0)), 16, 16);
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
                                ".png");
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
                notificationTrayOfflineImages[i] = scaleImage(
                    blockTransparify(
                        notificationTrayOfflineImages[i],
                        new Color(255, 0, 0)), 16, 16);
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
            // TODO: remove the following line, it's just for testing purposes
            notificationFrame.addNotification("OpenGroove",
                new NotificationAdapter(notificationFrame,
                    new JLabel("Test notification"), true,
                    false), false);
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
            }
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
    private static BufferedImage blockTransparify(
        BufferedImage oldImage, Color color)
    {
        BufferedImage image = new BufferedImage(oldImage
            .getWidth(null), oldImage.getHeight(null),
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
                if (r > rMin && r < rMax && g > gMin
                    && g < gMax && b > bMin && b < bMax)
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
                context
                    .setStatusListener(new StatusListener()
                    {
                        
                        @Override
                        public void authenticationFailed(
                            Communicator c, Packet packet)
                        {
                            System.err
                                .println("Persistant authentication failed");
                            /*
                             * TODO: the user should be notified that they have
                             * an incorrect local passwod, and that they need to
                             * change it.
                             */
                        }
                        
                        @Override
                        public void authenticationSuccessful(
                            Communicator c)
                        {
                            // TODO Auto-generated method stub
                            
                        }
                        
                        @Override
                        public void communicatorShutdown(
                            Communicator c)
                        {
                            System.err
                                .println("Communicator shutdown prematurely, "
                                    + "OpenGroove needs to be restarted to work correctly");
                        }
                        
                        @Override
                        public void connectionEstablished(
                            Communicator c,
                            ServerContext server)
                        {
                            // TODO Auto-generated method stub
                            
                        }
                        
                        @Override
                        public void connectionLost(
                            Communicator c)
                        {
                            // TODO Auto-generated method stub
                            
                        }
                        
                        @Override
                        public void connectionReady(
                            Communicator c)
                        {
                            // TODO Auto-generated method stub
                            
                        }
                    });
                Communicator com = new Communicator(Userids
                    .toRealm(userid), true, false,
                    "normal", Userids.toUsername(userid),
                    user.getComputer(), password, user
                        .getServerRsaPub(), user
                        .getServerRsaMod(), context
                        .getStatusListener(), null);
                CommandCommunicator commandCom = new CommandCommunicator(
                    com);
                context.setCom(commandCom);
                // loadFeatures();
                // loadCurrentUserLookAndFeel();
                loadLaunchBar(userid, context);
                // WorkspaceManager workspaceManager = new
                // WorkspaceManager(context);
                // context.setWorkspaceManager(workspaceManager);
                // workspaceManager.reloadWorkspaces();
                // workspaceManager.reloadWorkspaceMembers();
                // reloadLaunchbarWorkspaces(context);
                loginFrame.hide();
                userContextMap.put(userid, context);
                notificationFrame
                    .addNotification(
                        context.getUserid(),
                        new NotificationAdapter(
                            notificationFrame,
                            new JLabel(
                                "You have successfully logged into OpenGroove."),
                            false, true), true);
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
        if (loginFrame.isShowing()
            && (!loginFrame.getUserid().equals(userid)))
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
             * The user is currently using the create new account wizard, so we
             * bring it to the front instead.
             */
            bringToFront(newAccountFrame);
            return;
        }
        LocalUser user = userid == null ? null : Storage
            .getLocalUser(userid);
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
        loginFrame.setAlwaysOnTop(true);
        loginFrame.show();
        bringToFront(loginFrame);
    }
    
    private static void initLoginFrame()
    {
        loginFrame = new LoginFrame();
        loginFrame.setIconImage(getWindowIcon());
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
                new Thread()
                {
                    public void run()
                    {
                        doFrameLogin();
                    }
                }.start();
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
                    showNewAccountWizard(false);
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
        newAccountFrame.setIconImage(getWindowIcon());
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
        final String LABEL_WELCOME = "Welcome to OpenGroove";
        final String LABEL_NEW_OR_EXIST = "New or Existing Account?";
        final String LABEL_EXIST_AUTH = "Enter your userid and password";
        final String LABEL_NEW_AUTH = "Select a realm, username, and password";
        final String LABEL_ENTER_KEYS = "Select your security keys";
        final String LABEL_MORE_INFO = "Enter some additional information";
        final String LABEL_COMPUTER = "Choose a name for this computer";
        final String LABEL_DONE = "Finished";
        if (welcome)
        {
            StandardWizardPage welcomePage = new StandardWizardPage(
                LABEL_WELCOME, false, true, true, false)
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
            LABEL_NEW_OR_EXIST, welcome, true, true, false)
        {
            private JRadioButton newButton;
            private JRadioButton existingButton;
            
            public JComponent createWizardContent()
            {
                JPanel panel = new JPanel();
                newButton = new JRadioButton(
                    "<html><b>Create a new OpenGroove account</b><br/>"
                        + "Choose this if this is your first time using OpenGroove, or if ");
                existingButton = new JRadioButton(
                    "<html><b>Use an OpenGroove account that you have already created</b><br/>"
                        + "Choose this if you already have an OpenGroove "
                        + "account and would like to use it on this computer.");
                newButton.setFont(Font.decode(null));
                existingButton.setFont(Font.decode(null));
                ButtonGroup newOrExistGroup = new ButtonGroup();
                newOrExistGroup.add(newButton);
                newOrExistGroup.add(existingButton);
                panel.setLayout(new BorderLayout());
                JPanel inner = new JPanel();
                inner.setLayout(new BoxLayout(inner,
                    BoxLayout.Y_AXIS));
                panel.add(inner, BorderLayout.NORTH);
                newButton
                    .setVerticalTextPosition(newButton.TOP);
                existingButton
                    .setVerticalTextPosition(existingButton.TOP);
                newButton.setFocusable(false);
                existingButton.setFocusable(false);
                inner.add(newButton);
                inner.add(new JLabel(" "));
                inner.add(new JLabel(" "));
                inner.add(existingButton);
                ActionListener listener = new ActionListener()
                {
                    
                    @Override
                    public void actionPerformed(
                        ActionEvent e)
                    {
                        if (newButton.isSelected())
                        {
                            newAccountWizardPane
                                .setNextPage(newAccountWizardPane
                                    .getPageByTitle(LABEL_NEW_AUTH));
                        }
                        else if (existingButton
                            .isSelected())
                        {
                            newAccountWizardPane
                                .setNextPage(newAccountWizardPane
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
                        boolean isOneSelected = newButton
                            .isSelected()
                            || existingButton.isSelected();
                        if (!(((JButton) e.getSource())
                            .getName()
                            .equals(ButtonNames.NEXT)))
                            isOneSelected = true;
                        if (!isOneSelected)
                        {
                            JOptionPane
                                .showMessageDialog(
                                    newAccountWizardPane,
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
        StandardWizardPage existAuthPage = new StandardWizardPage(
            LABEL_EXIST_AUTH, true, true, true, false)
        {
            
            @Override
            protected void init()
            {
            }
        };
        pages.append(existAuthPage);
        StandardWizardPage newAuthPage = new StandardWizardPage(
            LABEL_NEW_AUTH, true, true, false, false)
        {
            
            @Override
            protected void init()
            {
                addText("Right now, you can't create a new user account. If you'd like "
                    + "a user account, contact the owner of a realm server, and have "
                    + "them create an account for you. Then, choose \"Use an "
                    + "OpenGroove account that you have already created\" "
                    + "on the previous step.");
                addText("For more information, or to get an OpenGroove account at " +
                		"the opengroove.org realm, contact us at support@opengroove.org");
            }
            
        };
        pages.append(newAuthPage);
        StandardWizardPage securityKeysPage = new StandardWizardPage(
            LABEL_ENTER_KEYS, false, true, true, false)
        {
            
            @Override
            protected void init()
            {
                
            }
        };
        pages.append(securityKeysPage);
        // end pages
        newAccountWizardPane.setPageList(pages);
        newAccountWizardPane
            .setCancelAction(new AbstractAction("Cancel")
            {
                
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    newAccountFrame.hide();
                }
            });
        newAccountWizardPane
            .setFinishAction(new AbstractAction("Finish")
            {
                
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    /*
                     * TODO: implement this method
                     */
                    newAccountFrame.hide();
                }
            });
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
        aboutWindow.setAlwaysOnTop(true);
        aboutWindow.setSize(400, 300);
        aboutWindow.setLocationRelativeTo(null);
        aboutWindow.getContentPane().setLayout(
            new BorderLayout());
        JPanel panel = new JPanel();
        aboutWindow.getContentPane().add(panel,
            BorderLayout.NORTH);
        panel.setLayout(new BoxLayout(panel,
            BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("OpenGroove version "
            + getDisplayableVersion()));
        JideButton websiteButton = new JideButton(
            "www.opengroove.org");
        panel.add(new JLabel(" "));
        panel.add(websiteButton);
        websiteButton
            .setButtonStyle(websiteButton.HYPERLINK_STYLE);
        websiteButton.setAlwaysShowHyperlink(true);
        websiteButton.setForeground(Color.BLUE);
        websiteButton
            .addActionListener(new ActionListener()
            {
                
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    try
                    {
                        Desktop
                            .getDesktop()
                            .browse(
                                new URI(
                                    "http://www.opengroove.org"));
                    }
                    catch (IOException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    catch (URISyntaxException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            });
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
                            "OpenGroove", notification,
                            true);
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
                            notificationFrame,
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
                            "OpenGroove",
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
        final UserContext context)
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
                            showConfigWindow(context, w);
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
            JFrame launchbar = context.getLaunchbar();
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
        UserContext context, WorkspaceWrapper w)
    {
        return showConfigWindow(context, w, context
            .getLaunchbar());
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
    protected static void showLaunchBar(UserContext context)
    {
        context.getLaunchbar().show();
        bringToFront(context.getLaunchbar());
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
        final UserContext context, final JFrame launchbar)
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
                        showOptionsWindow(context);
                    }
                } });
        final JMenu pluginsMenu = new IMenu("Plugins",
            new IMenuItem[] { new IMenuItem(
                "Manage plugins")
            {
                
                public void actionPerformed(ActionEvent e)
                {
                    PluginManager
                        .showManageInstalledPluginsDialog();
                }
            } });
        final JCheckBoxMenuItem alwaysOnTopItem = new JCheckBoxMenuItem(
            "Always on top");
        if (context.getStorage().getConfigProperty(
            "alwaysontop") != null)
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
                        context.getStorage()
                            .setConfigProperty(
                                "alwaysontop", "");
                    }
                    else
                    {
                        launchbar.setAlwaysOnTop(false);
                        context.getStorage()
                            .setConfigProperty(
                                "alwaysontop", null);
                    }
                }
            });
        convergiaMenu.add(alwaysOnTopItem);
        JMenu helpMenu = new IMenu("Help", new IMenuItem[] {
            new IMenuItem("Help")
            {
                
                public void actionPerformed(ActionEvent e)
                {
                    context.getHelpViewer().show();
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
     * this method will return once the user has closed the dialog.
     * 
     * @param frame
     *            the frame to pass to the dialog's constructor. the dialog will
     *            be shown on top of this frame.
     * @param context
     *            The user context of the user that wants to search for new
     *            plugins.
     */
    public static void findNewPlugins(JFrame frame,
        UserContext context)
    {
        context.getPlugins().promptForDownload(frame);
    }
    
    /**
     * Shows a dialog that allows the user to configure OpenGroove.
     */
    protected static void showOptionsWindow(
        UserContext context)
    {
        final ConfigureOpenGrooveDialog dialog = new ConfigureOpenGrooveDialog(
            context);
        // showStatusInfo(dialog);
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
    private static void showStatusInfo(UserContext context,
        ConfigureOpenGrooveDialog dialog)
    {
        CommandCommunicator ocom = context.getCom();
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
            "secure");
        /*
         * All connections are secured with the change to the new realm server
         * framework, so there's no need to imform the user of whether they're
         * using security anymore.
         */
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
    private static BufferedImage scaleImage(Image image,
        int width, int height)
    {
        BufferedImage b = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        /*
         * Now we do the actual scaling. The buffered image construction is
         * really only for other methods that use one; the actual scaling is
         * done by the Image class itself.
         */
        g.drawImage(image.getScaledInstance(width, height,
            Image.SCALE_AREA_AVERAGING), 0, 0, width,
            height, null);
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
    
    private static volatile long nextGenId = 0;
    
    /**
     * creates a new id. the id should be unique for the whole OpenGroove
     * system. the first part of the id, up until the first hyphen, is this
     * user's userid, with the ":" character replaced by two dots, IE ".." .
     * 
     * @return
     */
    public static synchronized String generateId(
        UserContext context)
    {
        String d = Double.toString(Math.random()).replace(
            ".", "");
        return context.getUserid().replace(":", "..") + "-"
            + System.currentTimeMillis() + "-"
            + nextGenId++ + "-"
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
        UserContext context,
        final WorkspaceWrapper workspace, JFrame frame)
    {
        /*
         * TODO: actually implement this method. It was implemented, but it was
         * going to have to be changed so much with the new realm server model
         * that I decided just to scrap it.
         */
        return false;
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
    public static void showHelpTopic(UserContext context,
        String path)
    {
        context.getHelpViewer().showHelpTopic(path);
    }
    
    /**
     * Shows information about the user specified. This method is expected to
     * undergo heavy modification with the addition of realm servers.
     * 
     * @param username
     */
    public static void showUserInformationDialog(
        UserContext context, String username)
    {
        showUserInformationDialog(username, context
            .getLaunchbar());
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
