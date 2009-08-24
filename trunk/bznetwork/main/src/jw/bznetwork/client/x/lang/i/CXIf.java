package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XAttributeMerger;
import jw.bznetwork.client.x.lang.XBoolean;
import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;

public class CXIf implements XCommand
{
    
    public String getName()
    {
        return "if";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        XAttributeMerger attributeSet =
            new XAttributeMerger(element, new String[] { "condition" }, null, null,
                context, new boolean[] { true });
        XBoolean result = (XBoolean) attributeSet.getResult(0);
        context.setLastIfResult(result);
        if (result.isValue())
        {
            context.getInterpreter().executeChildren(element, context,
                attributeSet.getTagCount());
        }
        return null;
    }
    
}
