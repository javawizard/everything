package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;

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
