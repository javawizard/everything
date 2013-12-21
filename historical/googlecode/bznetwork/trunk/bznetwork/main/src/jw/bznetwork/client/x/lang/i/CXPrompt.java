package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;
import jw.bznetwork.client.x.lang.XNull;
import jw.bznetwork.client.x.lang.XString;

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
