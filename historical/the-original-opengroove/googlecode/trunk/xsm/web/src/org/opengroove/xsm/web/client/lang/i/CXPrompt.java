package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;
import org.opengroove.xsm.web.client.lang.XNull;
import org.opengroove.xsm.web.client.lang.XString;

public class CXPrompt implements XCommand
{
    
    public String getName()
    {
        return "prompt";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        String message = element.getAttribute("message");
        if (message == null)
        {
            XData data = context.execute(element.getSingleElement());
            if (data != null)
                message = ((XString) data).getValue();
        }
        String result = context.getInterpreter().input.prompt(message);
        if (result == null)
            return new XNull();
        return new XString(result);
    }
    
}
