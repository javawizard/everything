package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;
import org.opengroove.xsm.web.client.lang.XNull;

public class CXNull implements XCommand
{
    
    public String getName()
    {
        return "null";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        return new XNull();
    }
    
}
