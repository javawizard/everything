package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;
import org.opengroove.xsm.web.client.lang.XReturnException;

public class CXReturn implements XCommand
{
    
    public String getName()
    {
        return "return";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        /*
         * Execute the single child of the return, and then throw a new
         * XReturnException containing the value to return.
         */
        if (element.getChildren().size() > 0)
        {
            XData returnData = context.execute(element.getSingleElement());
            throw new XReturnException(returnData);
        }
        else
        {
            /*
             * The custom function doesn't return a value.
             */
            throw new XReturnException(null);
        }
    }
    
}
