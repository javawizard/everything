package jw.bznetwork.client.x.lang;

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

    public XNumber(long value)
    {
        super();
        this.value = value;
    }

    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (value ^ (value >>> 32));
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
        XNumber other = (XNumber) obj;
        if (value != other.value)
            return false;
        return true;
    }
}
