package net.sf.opengroove.client.plugins;

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
    
    public synchronized void initialize()
    {
        if (isInitialized)
            return;
        ReferableNode[] childNodes = node
            .getChildren("folder");
        children = new UpdateSiteFolder[childNodes.length];
        for (int i = 0; i < children.length; i++)
        {
            children[i] = new UpdateSiteFolder(
                childNodes[i]);
        }
    }
    
    private UpdateSiteFolder(ReferableNode node)
    {
        this.node = node;
    }
}
