package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;

public class CXConfig implements XCommand
{
    
    public String getName()
    {
        return "config";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        String name = element.getAttribute("name");
        String value = element.getAttribute("value");
        context.getInterpreter().configuration.put(name, value);
        return null;
    }
    
}
