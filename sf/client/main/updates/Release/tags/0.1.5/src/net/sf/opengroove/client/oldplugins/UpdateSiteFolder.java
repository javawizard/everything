package net.sf.opengroove.client.oldplugins;

import java.net.URL;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * A class that connects to an update site. It is passed the url of an update
 * site, and can be queried for the update site's root folder, and the folders
 * within the update site. It will download additional referenced update sites
 * as necessary.
 * 
 * @author Alexander Boyd
 * 
 */
public class UpdateSiteFolder
{
    /*
     * An update site has a root folder that we can access, which represents the
     * root element. When we create an update site folder, we do so by passing
     * it a JDOM node that represents the element it is supposed to base itself
     * on. It can also be passed a URL that it is to base it's contents on, and
     * it will download the xml file at the url and use it's root node as the
     * JDOM element representing the folder.
     * 
     * UPDATE: This stuff will actually use ReferableNode, which handles file
     * referencing, so we don't need to worry about URL stuff anymore.
     */

    private ReferableNode node;
    
    private UpdateSiteFolder parent;
    
    private volatile UpdateSiteFolder[] children;
    
    private volatile boolean isInitialized = false;
    
    /**
     * Creates a new update site folder from the update site at the specified
     * location.
     * 
     * @param url
     *            An absolute url (IE beginning with a proper scheme) that
     *            points to an update site xml file. This url can point to an
     *            http url that returns a 307 redirect; such redirects will be
     *            followed until the appropriate location is found.
     */
    public UpdateSiteFolder(String url)
    {
        Element refElement = new Element("folder");
        refElement.setAttribute("ref", url);
        this.node = new ReferableNode(refElement);
    }
    
    private UpdateSiteFolder(UpdateSiteFolder parent,
        ReferableNode node)
    {
        this.node = node;
        this.parent = parent;
    }
    
    public UpdateSiteFolder[] getChildren()
    {
        return children;
    }
    
    public String getName()
    {
        initialize();
        return node.getAttribute("name");
    }
    
    public String getDescription()
    {
        initialize();
        return node.getAttribute("description");
    }
    
    public String getInitialReference()
    {
        /*
         * We don't need to call initialize() here because we're not accessing
         * any fields that would need to be initialized.
         */
        return node.getInitialReference();
    }
    
    /**
     * Initializes this folder. This is automatically called from any methods
     * that need initialization to be performed, so classes using this class
     * typically don't need to worry about this method. However, it may be an
     * advantage to use this method when a folder is to be initialized before it
     * is used. If this folder's backing xml tag references another xml file,
     * then the referant will be downloaded when this method is called.
     */
    public synchronized void initialize()
    {
        if (isInitialized)
            return;
        ReferableNode[] childNodes = node
            .getChildren("folder");
        children = new UpdateSiteFolder[childNodes.length];
        for (int i = 0; i < children.length; i++)
        {
            children[i] = new UpdateSiteFolder(this,
                childNodes[i]);
        }
        isInitialized = true;
    }
    
    /**
     * Returns this folder's parent, or null if this folder does not have a
     * parent. Folders returned from the getChildren() method have a parent,
     * namely the folder on which getChildren() was invoked. A folder created
     * using {@link #UpdateSiteFolder(String)} does not have a parent.
     * 
     * @return
     */
    public UpdateSiteFolder getParent()
    {
        return parent;
    }
    
}
