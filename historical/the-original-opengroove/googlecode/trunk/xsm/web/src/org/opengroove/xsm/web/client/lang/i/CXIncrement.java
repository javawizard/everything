package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XDouble;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XException;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;
import org.opengroove.xsm.web.client.lang.XNumber;

public class CXIncrement implements XCommand
{
    
    public String getName()
    {
        return "increment";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        String var = element.getAttribute("name");
        if (var == null)
            var = element.getAttribute("var");
        if (var == null)
            throw new XException("No var to increment specified");
        XData data = context.getVariable(var);
        if (data instanceof XNumber)
        {
            data = new XNumber(((XNumber) data).getValue() + 1);
        }
        else if (data instanceof XDouble)
        {
            data = new XDouble(((XDouble) data).getValue() + 1);
        }
        else
        {
            throw new XException(
                "Incompatible type to increment, only" +
                " number and double are allowed");
        }
        context.setVariable(var, data);
        return null;
    }
}
