package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;

public class CXVar implements XCommand
{
    
    public String getName()
    {
        return "var";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        String name = element.getAttribute("name");
        if (name == null)
            name = context.executeForString(element.getSingleElement());
        XData value = context.getVariable(name);
        return value;
    }
    
}
