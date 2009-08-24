package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XBoolean;
import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XException;
import jw.bznetwork.client.x.lang.XInterpreterContext;

public class CXEquals implements XCommand
{
    
    public String getName()
    {
        return "equals";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        element.checkExactChildCount(2);
        XData data1 = context.execute((XElement) element.getChild(0));
        XData data2 = context.execute((XElement) element.getChild(1));
        context.validateNotNull(data1);
        context.validateNotNull(data2);
        return new XBoolean(data1.equals(data2));
    }
}
