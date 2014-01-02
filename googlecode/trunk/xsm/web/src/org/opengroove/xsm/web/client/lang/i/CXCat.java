package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;
import org.opengroove.xsm.web.client.lang.XNode;
import org.opengroove.xsm.web.client.lang.XString;

public class CXCat implements XCommand
{
    
    public String getName()
    {
        return "cat";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        StringBuffer buffer = new StringBuffer();
        for (XNode node : element.getChildren())
        {
            XElement ce = (XElement) node;
            XData data = context.execute(ce);
            buffer.append(data.toString());
        }
        return new XString(buffer.toString());
    }
    
}
