package org.opengroove.xsm.web.client.lang;

public class XDouble extends XData
{
    private double value;
    
    public double getValue()
    {
        return value;
    }
    
    public void setValue(double value)
    {
        this.value = value;
    }
    
    public String toString()
    {
        return "" + value;
    }

    public XDouble(double value)
    {
        super();
        this.value = value;
    }
}
