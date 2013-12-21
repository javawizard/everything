package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;
import jw.bznetwork.client.x.lang.XNode;
import jw.bznetwork.client.x.lang.XString;

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
