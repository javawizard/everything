package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XBoolean;
import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;

public class CXFalse implements XCommand
{
    
    public String getName()
    {
        return "false";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        return new XBoolean(false);
    }
    
}
