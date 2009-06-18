package org.opengroove.xsm.web.client.lang;

import java.util.ArrayList;
import java.util.HashMap;

public class XElement extends XNode
{
    private String tag;
    private HashMap<String,String> attributes = new HashMap<String,String>();
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
}
