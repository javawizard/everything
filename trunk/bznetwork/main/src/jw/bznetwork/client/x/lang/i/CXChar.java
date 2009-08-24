package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XException;
import jw.bznetwork.client.x.lang.XInterpreterContext;
import jw.bznetwork.client.x.lang.XList;
import jw.bznetwork.client.x.lang.XNull;
import jw.bznetwork.client.x.lang.XNumber;
import jw.bznetwork.client.x.lang.XString;

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
