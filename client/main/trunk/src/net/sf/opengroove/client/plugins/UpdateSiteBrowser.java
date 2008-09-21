package net.sf.opengroove.client.plugins;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.sf.opengroove.client.UserContext;
import net.sf.opengroove.client.ui.Breadcrumb;

/**
 * This class is an update site browser. It allows the user to browse through
 * update sites and select a list of plugins to install. It will take care of
 * installing those plugins and downloading and installing their dependancies,
 * and then prompting the user to restart OpenGroove.
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
     * user's current path. If this is null, then the 
     */
    private UpdateSiteFolder folder;
    private String[] fixedUpdateSites;
    private File userUpdateSites;
    
    
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
        setLayout(new BorderLayout());
        JPanel contents = new JPanel();
        contents.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(contents);
        
    }
    /**
     * The panel that allows the user to navigate update sites. 
     * @author Alexander Boyd
     *
     */
    protected class NavPane extends JPanel
    {
        
    }
}
