package net.sf.opengroove.client.plugins;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

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
     * Contains the actual breadcrumb trail. <code>breadcrumb</code> is set to
     * reflect this whenever it changes. This should always have at least one
     * element. If the user is viewing the toplevel (IE they are currently
     * viewing the list of available update sites), then this list should have
     * only one element, which is an UpdatePath with it's update site and
     * folders set to null, and it's name set to
     */
    private ArrayList<UpdatePath> trail = new ArrayList<UpdatePath>();
    private String[] fixedUpdateSites;
    private File userUpdateSites;
    
    /**
     * Creates a new modal UpdateSiteBrowser.
     * 
     * @param parent
     *            The frame to use as the parent of this dialog
     * @param fixedUpdateSites
     *            A String[] which can contain the urls to a number of fixed
     *            update sites. These are update sites that always appear in the
     *            top level list and that the user cannot delete. For example,
     *            OpenGroove typically has the OpenGroove update site as a fixed
     *            update site.
     */
    public UpdateSiteBrowser(Frame parent,
        String[] fixedUpdateSites, File userUpdateSites)
    {
        super(parent, true);
        setLayout(new BorderLayout());
        JPanel contents = new JPanel();
        contents.setBorder(new EmptyBorder(10, 10, 10, 10));
    }
}
