package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;

public class CXElse implements XCommand
{
    
    public String getName()
    {
        return "else";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        /*
         * Execute all children as normal if the last if result was false,
         * otherwise do nothing
         */
        if (!context.getLastIfResult().isValue())
        {
            context.getInterpreter().executeChildren(element, context);
        }
        return null;
    }
    
}
