package jw.bznetwork.client.x.lang;

public abstract class XData
{
    public double getAsDouble()
    {
        if (!((this instanceof XDouble) || (this instanceof XNumber)))
            throw new XException("Not a numeric type");
        if (this instanceof XDouble)
            return ((XDouble) this).getValue();
        else
            return ((XNumber) this).getValue();
    }
    
    public long getAsLong()
    {
        if (!(this instanceof XNumber))
            throw new XException("Not a long, possibly a double");
        return ((XNumber) this).getValue();
    }
}
