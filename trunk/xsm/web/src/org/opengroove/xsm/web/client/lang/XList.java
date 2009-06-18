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
}
