package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;

public class CXPrint implements XCommand
{
    private static final CXString stringCommand = new CXString();
    
    public String getName()
    {
        return "print";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        XData toPrint = stringCommand.invoke(context, element);
        boolean newline = !"false".equals(element.getAttributes().get("newline"));
        String value = toPrint.toString();
        context.getInterpreter().getDisplay().print(value, newline);
        return null;
    }
    
}
