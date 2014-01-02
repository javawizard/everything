package net.sf.opengroove.client.oldplugins;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import net.sf.opengroove.client.OpenGroove;
import net.sf.opengroove.client.UserContext;
import net.sf.opengroove.client.ui.Breadcrumb;

/**
 * This class is an update site browser. It allows the user to browse through
 * update sites and select a list of plugins to install. It does not, however,
 * actually install those plugins. It simply provides a list of plugins that the
 * user chose to have installed, and the caller is responsible for installing
 * those and downloading any dependencies if needed.
 * 
 * @author Alexander Boyd
 * 
 */
public class UpdateSiteBrowser extends JDialog
{
    /**
     * This is the breadcrumb component that displays at the top of the dialog.
     */
    private Breadcrumb breadcrumb;
    /**
     * Contains the current folder that the user is viewing. Since
     * UpdateSiteFolders track the folders used to drill down to them, not just
     * the xml file that they were sourced from (they don't actually know about
     * xml references), this can be used to show a breadcrumb representing the
     * user's current path. If this is null, then the user is viewing the list
     * of update sites.
     */
    private UpdateSiteFolder folder;
    private String[] fixedUpdateSites = new String[] {};
    private File userUpdateSites;
    private JPanel navContent;
    /**
     * An array list of the plugins that the user has chosen to install.
     */
    private ArrayList<UpdateSitePlugin> toInstall = new ArrayList<UpdateSitePlugin>();
    
    /**
     * Creates a new modal UpdateSiteBrowser. The browser will allow the user to
     * add update sites, browse through update sites that they've added, and
     * install plugins from those update sites.
     * 
     * @param parent
     *            The frame to use as the parent of this dialog
     * @param context
     *            The context of the user that this update site browser is for.
     *            Plugins installed using this browser will be placed into this
     *            user's plugin folder.
     */
    public UpdateSiteBrowser(Frame parent,
        UserContext context)
    {
        super(parent, true);
        setSize(400, 500);
        getContentPane().setLayout(new BorderLayout());
        JPanel contents = new JPanel();
        contents.setLayout(new BorderLayout());
        contents.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(contents);
        JPanel navPane = new JPanel();
        navPane.setLayout(new BorderLayout());
        JPanel navTop = new JPanel();
        navTop.setLayout(new BoxLayout(navTop,
            BoxLayout.X_AXIS));
        navContent = new JPanel();
        navContent.setLayout(new BoxLayout(navContent,
            BoxLayout.Y_AXIS));
        navPane.add(navTop, BorderLayout.NORTH);
        navPane.add(new JScrollPane(navContent),
            BorderLayout.CENTER);
        JButton upFolder = new JButton(new ImageIcon(
            OpenGroove.Icons.UP_FOLDER_16.getImage()));
        upFolder.setToolTipText("Up one folder");
        upFolder.setMargin(new Insets(1, 1, 1, 1));
        navTop.add(upFolder);
        JSplitPane split = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT, true);
        contents.add(split);
        split.setTopComponent(navPane);
        split.setResizeWeight(0.65);
        split.setDividerLocation(0.65);
        /*
         * TODO: pick up here September 21, 2008: create a method that will
         * build the contents of navContent based on the current folder (if the
         * folder's null, it should pull the list of update sites), add a
         * mechanism to Storage.java for storing update sites and user plugins,
         * each plugin and folder in navContent has a light-gray or light-blue
         * border around it, and it's name is a link and bold but it's
         * description is not, and for folders clicking on their name link opens
         * that folder, for plugins clicking on it asks the user if they want to
         * install it. Plugins the user chooses to install are added to a JList
         * that is at the bottom of the screen, that bases it's model off of
         * toInstall. When the user is ready to install, the update site browser
         * is closed, and they are informed of download status. If a plugin is
         * depended, it will be fetched (after the user confirms it, of course)
         * if an update site is provided, and searched for if an update site is
         * not provided or the provided update site does not contain the
         * dependency. Once all dependedncies are here, we ask for version
         * choice (IE does the user want release updates or beta or whatever),
         * and install, then prompt for re-start.
         */
    }
}
