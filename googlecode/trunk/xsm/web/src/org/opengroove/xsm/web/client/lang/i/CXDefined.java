package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XAttributeMerger;
import org.opengroove.xsm.web.client.lang.XBoolean;
import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XException;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;
import org.opengroove.xsm.web.client.lang.XString;

public class CXDefined implements XCommand
{
    
    public String getName()
    {
        return "defined";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        String name = element.getAttribute("name");
        if (name == null)
            name = element.getAttribute("var");
        if (name == null)
            name = ((XString) context.execute(element.getSingleElement())).getValue();
        if (name == null)
            throw new XException("Defined was not given a variable name");
        return new XBoolean(context.getVariables().get(name) != null);
    }
    
}
