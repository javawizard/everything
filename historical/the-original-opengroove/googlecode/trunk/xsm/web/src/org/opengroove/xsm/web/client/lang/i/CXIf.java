package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XAttributeMerger;
import org.opengroove.xsm.web.client.lang.XBoolean;
import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;

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
