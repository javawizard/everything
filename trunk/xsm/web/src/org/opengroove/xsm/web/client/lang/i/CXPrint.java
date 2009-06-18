package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;

public class CXPrint implements XCommand
{
    
    public String getName()
    {
        return "print";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        XData toPrint = context.execute(element.getSingleElement());
        boolean newline =
            !"true".equals(element.getAttributes().get("newline").toLowerCase());
        String value = toPrint.toString();
        context.getInterpreter().getDisplay().print(value, newline);
        return null;
    }
    
}
