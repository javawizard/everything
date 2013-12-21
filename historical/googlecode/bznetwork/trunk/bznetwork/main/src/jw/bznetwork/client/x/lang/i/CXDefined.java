package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XAttributeMerger;
import jw.bznetwork.client.x.lang.XBoolean;
import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XException;
import jw.bznetwork.client.x.lang.XInterpreterContext;
import jw.bznetwork.client.x.lang.XString;

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
