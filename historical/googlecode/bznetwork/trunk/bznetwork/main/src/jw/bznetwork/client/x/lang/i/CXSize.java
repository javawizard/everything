package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XException;
import jw.bznetwork.client.x.lang.XInterpreterContext;
import jw.bznetwork.client.x.lang.XList;
import jw.bznetwork.client.x.lang.XNumber;
import jw.bznetwork.client.x.lang.XString;

public class CXSize implements XCommand
{
    
    public String getName()
    {
        return "size";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        XData data = context.execute(element.getSingleElement());
        if (data instanceof XString)
        {
            return new XNumber(((XString) data).getValue().length());
        }
        else if (data instanceof XList)
        {
            return new XNumber(((XList) data).getValue().size());
        }
        else
        {
            throw new XException("Input to size must be a string or a list");
        }
    }
    
}
