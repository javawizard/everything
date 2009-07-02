package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XAttributeMerger;
import org.opengroove.xsm.web.client.lang.XBoolean;
import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;

public class CXWhile implements XCommand
{
    
    public String getName()
    {
        return "while";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        while (true)
        {
            XAttributeMerger attributeSet =
                new XAttributeMerger(element, new String[] { "condition" }, null, null,
                    context, new boolean[] { true });
            XBoolean result = (XBoolean) attributeSet.getResult(0);
            if (!result.isValue())
                break;
            /*
             * Increment the instruction count to prevent an infinite loop when
             * the loop is empty
             */
            context.getInterpreter().instructionCount += 1;
            context.getInterpreter().executeChildren(element, context,
                attributeSet.getTagCount());
        }
        return null;
    }
    
}
