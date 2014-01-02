package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XException;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;
import org.opengroove.xsm.web.client.lang.XList;
import org.opengroove.xsm.web.client.lang.XNumber;
import org.opengroove.xsm.web.client.lang.XString;

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
