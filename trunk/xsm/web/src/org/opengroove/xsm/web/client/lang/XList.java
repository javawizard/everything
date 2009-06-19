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
}
