package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;

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
