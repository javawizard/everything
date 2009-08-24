package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XDouble;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;
import org.opengroove.xsm.web.client.lang.XNumber;

public class CXSubtract implements XCommand
{
    
    public String getName()
    {
        return "subtract";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        /*
         * Execute all elements, if any of them are a double then the result is
         * a double, subtract them all from the first
         */
        XData[] values = new XData[element.getChildren().size()];
        boolean isDoubles = false;
        for (int i = 0; i < values.length; i++)
        {
            values[i] = context.execute((XElement) element.getChild(i));
            if (values[i] instanceof XDouble)
                isDoubles = true;
        }
        if (values.length == 0)
            values = new XData[] { new XNumber(0) };
        XData output;
        if (isDoubles)
            output = new XDouble(values[0].getAsDouble());
        else
            output = (XNumber) values[0];
        for (int i = 1; i < values.length; i++)
        {
            if (isDoubles)
                output = new XDouble(output.getAsDouble() - values[i].getAsDouble());
            else
                output = new XNumber(output.getAsLong() - values[i].getAsLong());
        }
        return output;
    }
    
}
