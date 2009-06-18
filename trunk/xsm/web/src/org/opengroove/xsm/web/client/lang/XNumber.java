package org.opengroove.xsm.web.client.lang;

public class XNumber extends XData
{
    private long value;
    
    public long getValue()
    {
        return value;
    }
    
    public void setValue(long value)
    {
        this.value = value;
    }
    
    public String toString()
    {
        return "" + value;
    }
}
