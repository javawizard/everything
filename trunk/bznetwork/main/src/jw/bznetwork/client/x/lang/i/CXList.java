package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;
import jw.bznetwork.client.x.lang.XList;
import jw.bznetwork.client.x.lang.XNode;

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
