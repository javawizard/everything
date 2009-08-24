package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;
import jw.bznetwork.client.x.lang.XString;

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
