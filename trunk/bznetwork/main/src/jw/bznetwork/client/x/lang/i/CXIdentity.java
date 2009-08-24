package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;

public class CXIdentity implements XCommand
{
    
    public String getName()
    {
        return "identity";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        return context.execute(element.getSingleElement());
    }
    
}
