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
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (value ? 1231 : 1237);
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
        XBoolean other = (XBoolean) obj;
        if (value != other.value)
            return false;
        return true;
    }
}
