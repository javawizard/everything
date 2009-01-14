package net.sf.opengroove.client.oldplugins;

import java.net.URL;
import java.util.ArrayList;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

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
    
    private String initialReference;
    
    public String getInitialReference()
    {
        return initialReference;
    }
    
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
        if (element.getAttribute("ref") != null)
            initialReference = element
                .getAttributeValue("ref");
    }
    
    private void resolveReference()
    {
        try
        {
            String ref = element.getAttributeValue("ref");
            URL url = new URL(ref);
            Document doc = new SAXBuilder().build(url);
            Element newElement = doc.getRootElement();
            if (!newElement.getName().equalsIgnoreCase(
                element.getName()))
                throw new RuntimeException(
                    "Referrer has name "
                        + element.getName()
                        + " but referant has name "
                        + newElement.getName()
                        + ". The elements must have the same name.");
            element = newElement;
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
        isInitialized = true;
    }
    
    public ReferableNode[] getChildren()
    {
        initialize();
        return children;
    }
    
    public ReferableNode[] getChildren(String tag)
    {
        initialize();
        ArrayList<ReferableNode> results = new ArrayList<ReferableNode>();
        for (ReferableNode child : children)
        {
            if (child.getTagName().equalsIgnoreCase(tag))
                results.add(child);
        }
        return results.toArray(new ReferableNode[0]);
    }
    
    public String getAttribute(String name)
    {
        initialize();
        return element.getAttributeValue(name);
    }
    
    public String getText()
    {
        initialize();
        return element.getText();
    }
    
    public String getTagName()
    {
        /*
         * This doesn't need to call initialize() as the tag name will be the
         * same on both the referrer and the referant.
         */
        return element.getName();
    }
}
