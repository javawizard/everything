package net.sf.opengroove.client.plugins;

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
    private Element element;
    
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
        
    }
}
