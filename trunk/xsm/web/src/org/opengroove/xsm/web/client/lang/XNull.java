package org.opengroove.xsm.web.client.lang;

public class XNull extends XData
{
    public String toString()
    {
        return "null";
    }
    
    public boolean equals(Object obj)
    {
        return obj != null && obj instanceof XNull;
    }
}
