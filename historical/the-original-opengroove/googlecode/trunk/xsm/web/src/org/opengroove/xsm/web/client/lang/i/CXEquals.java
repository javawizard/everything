package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XBoolean;
import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XException;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;

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
