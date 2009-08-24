package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;
import jw.bznetwork.client.x.lang.XReturnException;

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
