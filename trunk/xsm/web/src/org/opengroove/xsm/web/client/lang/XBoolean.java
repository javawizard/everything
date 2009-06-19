package org.opengroove.xsm.web.client.lang;

public class XBoolean extends XData
{
    private boolean value;
    public String toString()
    {
        return "" + value;
    }
    public boolean isValue()
    {
        return value;
    }
    public void setValue(boolean value)
    {
        this.value = value;
    }
    public XBoolean(boolean value)
    {
        super();
        this.value = value;
    }
}
