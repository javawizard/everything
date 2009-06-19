package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;
import org.opengroove.xsm.web.client.lang.XList;
import org.opengroove.xsm.web.client.lang.XNode;

public class CXList implements XCommand
{
    
    public String getName()
    {
        return "list";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        XList list = new XList();
        for (XNode child : element.getChildren())
        {
            XElement ce = (XElement) child;
            list.add(context.execute(ce));
        }
        return list;
    }
    
}
