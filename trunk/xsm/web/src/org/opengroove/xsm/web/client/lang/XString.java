package org.opengroove.xsm.web.client.lang;

public class XString extends XData
{
    private String value;
    
    public String getValue()
    {
        return value;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }
    
    public String toString()
    {
        return value;
    }
}
