package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XBoolean;
import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;

public class CXGreater implements XCommand
{
    
    public String getName()
    {
        return "greater";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        XData data1 = context.execute((XElement) element.getChild(0));
        XData data2 = context.execute((XElement) element.getChild(1));
        return new XBoolean(data1.getAsDouble() > data2.getAsDouble());
    }
    
}
