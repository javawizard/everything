package net.sf.opengroove.client.plugins;

import java.awt.Frame;

import javax.swing.JDialog;

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
     * reflect this whenever it changes.
     */
    private ArrayList<UpdatePath> trail = new ArrayList<UpdatePath>();
    
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
        String[] fixedUpdateSites)
    {
        super(parent, true);
    }
}
