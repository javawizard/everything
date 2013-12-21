package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;

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
