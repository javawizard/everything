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
    
    public UpdateSiteFolder(URL url)
    {
        try
        {
            Element element = new SAXBuilder().build(url)
                .getRootElement();
            this.node = new ReferableNode(element);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                "Exception occured while creating folder for site "
                    + url, e);
        }
    }
    
    private UpdateSiteFolder(ReferableNode node)
    {
        
    }
}
