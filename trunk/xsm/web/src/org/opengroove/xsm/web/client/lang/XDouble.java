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
    
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = (long) value;
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
    
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        XDouble other = (XDouble) obj;
        if (Double.compare(other.value, value) != 0)
            return false;
        return true;
    }
}
