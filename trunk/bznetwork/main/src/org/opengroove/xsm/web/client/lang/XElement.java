package org.opengroove.xsm.web.client.lang;

import java.util.ArrayList;
import java.util.HashMap;

public class XElement extends XNode
{
    private String tag;
    private HashMap<String, String> attributes = new HashMap<String, String>();
    private ArrayList<XNode> children = new ArrayList<XNode>();
    
    public HashMap<String, String> getAttributes()
    {
        return attributes;
    }
    
    public ArrayList<XNode> getChildren()
    {
        return children;
    }
    
    public String getTag()
    {
        return tag;
    }
    
    public void setTag(String tag)
    {
        this.tag = tag;
    }
    
    /**
     * Checks to make sure that this element has only one child, which is an
     * element, and then returns it.
     * 
     * @return
     */
    public XElement getSingleElement()
    {
        if (children.size() != 1)
            throw new RuntimeException("Element " + tag
                + " should have had one child but had " + children.size());
        if (!(children.get(0) instanceof XElement))
            throw new XException("Single child was not an element");
        return (XElement) children.get(0);
    }
    
    public String getAttribute(String name)
    {
        return attributes.get(name);
    }
    
    public XNode getChild(int i)
    {
        return children.get(i);
    }
    
    public void checkExactChildCount(int i)
    {
        if (i != children.size())
            throw new XException("Exactly " + i + " elements were needed, but "
                + children.size() + " were found");
    }
}
