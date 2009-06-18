package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;

public class CXSet implements XCommand
{
    
    public String getName()
    {
        return "set";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        String name = element.getAttribute("name");
        XData value;
        if (name == null)
        {
            name = context.executeForString((XElement) element.getChild(0));
            value = context.execute((XElement) element.getChild(1));
        }
        else
        {
            value = context.execute(element.getSingleElement());
        }
        context.getVariables().put(name, value);
        return null;
    }
}
