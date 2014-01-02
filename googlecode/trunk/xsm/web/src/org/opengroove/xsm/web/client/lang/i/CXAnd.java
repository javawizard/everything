package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XBoolean;
import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;
import org.opengroove.xsm.web.client.lang.XNode;

public class CXAnd implements XCommand
{
    
    public String getName()
    {
        return "and";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        for (XNode child : element.getChildren())
        {
            XElement e = (XElement) child;
            XBoolean result = (XBoolean) context.execute(e);
            if (!result.isValue())
                return new XBoolean(false);
        }
        return new XBoolean(true);
    }
    
}
