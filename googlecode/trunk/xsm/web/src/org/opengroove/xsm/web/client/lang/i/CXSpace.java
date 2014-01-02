package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;
import org.opengroove.xsm.web.client.lang.XString;

public class CXSpace implements XCommand
{
    
    public String getName()
    {
        return "space";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        return new XString(" ");
    }
}
