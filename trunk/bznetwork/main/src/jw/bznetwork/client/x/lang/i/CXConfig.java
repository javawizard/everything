package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;

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
