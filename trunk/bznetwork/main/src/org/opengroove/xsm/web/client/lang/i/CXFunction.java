package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XCustomFunction;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;

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
