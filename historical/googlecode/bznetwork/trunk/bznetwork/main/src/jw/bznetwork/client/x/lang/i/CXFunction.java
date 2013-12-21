package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XCustomFunction;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;

public class CXFunction implements XCommand
{
    
    public String getName()
    {
        return "function";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        /*
         * This class is actually fairly small, since most of the work occurs
         * when the function is called, not when it is defined.
         */
        context.getInterpreter().install(new XCustomFunction(element));
        return null;
    }
    
}
