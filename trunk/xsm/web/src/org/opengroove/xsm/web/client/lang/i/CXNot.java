package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XBoolean;
import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;

public class CXNot implements XCommand
{
    
    public String getName()
    {
        return "not";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        return new XBoolean(!((XBoolean) context.execute(element.getSingleElement()))
            .isValue());
    }
    
}
