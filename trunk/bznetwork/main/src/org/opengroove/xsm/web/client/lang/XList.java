package org.opengroove.xsm.web.client.lang;

import java.util.ArrayList;

public class XList extends XData
{
    private ArrayList<XData> value = new ArrayList<XData>();
    
    public ArrayList<XData> getValue()
    {
        return value;
    }
    
    public String toString()
    {
        boolean first = true;
        StringBuffer buffer = new StringBuffer();
        buffer.append("[");
        for (XData data : value)
        {
            if (!first)
                buffer.append(",");
            first = false;
            buffer.append(data.toString());
        }
        buffer.append("]");
        return buffer.toString();
    }
    
    public void add(XData data)
    {
        /*
         * Make sure that there actually is a value. XNull is allowed in lists,
         * but null (which means no value was returned) is not.
         */
        if (data == null)
            throw new XException("Trying to add an element to a list when the "
                + "element didn't return a value to be added");
        value.add(data);
    }
    
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        XList other = (XList) obj;
        if (value == null)
        {
            if (other.value != null)
                return false;
        }
        else if (!value.equals(other.value))
            return false;
        return true;
    }
}
