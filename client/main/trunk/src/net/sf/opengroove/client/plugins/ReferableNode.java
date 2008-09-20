package net.sf.opengroove.client.plugins;

import java.util.ArrayList;

import org.jdom.Element;

/**
 * A node that can have children and attributes, and will download a replacement
 * for itself when constructed if the node that it is to be based on has only a
 * single attribute called "ref", which should contain a URL to another XML file
 * whos root node is to be used as the replacement. The replacement node's tag
 * name must be the same as the tag name of this node or an exception will be
 * thrown.
 * 
 * @author Alexander Boyd
 * 
 */
public class ReferableNode
{
    private volatile boolean isInitialized = false;
    
    private volatile Element element;
    
    private volatile ReferableNode[] children;
    
    /**
     * Creates a referable node, based on the element specified. If the element
     * contains only a ref attribute, the url referenced is downloaded, and it's
     * root node used in place of the element specified, unless the root node's
     * tag name is not the same as this node's tag name, in which case an
     * IllegalArgumentException is thrown.
     * 
     * @param element
     */
    public ReferableNode(Element element)
    {
        this.element = element;
    }
    
    private void resolveReference()
    {
        try
        {
            String ref = element.getAttributeValue("ref");
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException(
                "Element passed into this node ("
                    + element.getName()
                    + ") contained a reference that couldn't "
                    + "be obtained for some reason.", e);
        }
    }
    
    public synchronized void initialize()
    {
        if (isInitialized)
            return;
        while (element.getAttributes().size() == 1
            && element.getAttribute("ref") != null)
            resolveReference();
        ArrayList<ReferableNode> nodes = new ArrayList<ReferableNode>();
        for (Element childElement : new ArrayList<Element>(
            element.getChildren()))
        {
            ReferableNode child = new ReferableNode(
                childElement);
            nodes.add(child);
        }
        children = nodes.toArray(new ReferableNode[0]);
    }
}
