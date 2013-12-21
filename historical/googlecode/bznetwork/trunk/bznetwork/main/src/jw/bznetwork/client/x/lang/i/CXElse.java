package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;

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
