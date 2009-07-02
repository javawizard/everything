package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XBoolean;
import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;

public class CXLess implements XCommand
{
    
    public String getName()
    {
        return "less";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        XData data1 = context.execute((XElement) element.getChild(0));
        XData data2 = context.execute((XElement) element.getChild(1));
        return new XBoolean(data1.getAsDouble() < data2.getAsDouble());
    }
    
}
