package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XException;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;
import org.opengroove.xsm.web.client.lang.XList;
import org.opengroove.xsm.web.client.lang.XNull;
import org.opengroove.xsm.web.client.lang.XNumber;
import org.opengroove.xsm.web.client.lang.XString;

public class CXChar implements XCommand
{
    
    public String getName()
    {
        return "char";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        XData inputData = context.execute(element.getSingleElement());
        if (inputData instanceof XNumber)
        {
            return new XString("" + ((char) ((XNumber) inputData).getValue()));
        }
        else if (inputData instanceof XString)
        {
            String data = ((XString) inputData).getValue();
            if (data.length() == 1)
                return new XNumber(data.charAt(0));
            else if (data.length() == 0)
                return new XNull();
            else
            {
                XList list = new XList();
                for (char c : data.toCharArray())
                {
                    list.add(new XNumber(c));
                }
                return list;
            }
        }
        else if (inputData instanceof XNull)
        {
            return new XNull();
        }
        else if (inputData instanceof XList)
        {
            StringBuffer buffer = new StringBuffer();
            for (XData data : ((XList) inputData).getValue())
            {
                XNumber number = (XNumber) data;
                buffer.append("" + ((char) number.getValue()));
            }
            return new XString(buffer.toString());
        }
        else
        {
            throw new XException("Invalid input to char");
        }
    }
}
