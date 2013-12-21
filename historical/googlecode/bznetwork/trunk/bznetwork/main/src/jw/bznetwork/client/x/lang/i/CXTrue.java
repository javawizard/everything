package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XBoolean;
import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;

public class CXTrue implements XCommand
{
    
    public String getName()
    {
        return "true";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        return new XBoolean(true);
    }
    
}
