package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XBoolean;
import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;
import jw.bznetwork.client.x.lang.XNode;

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
