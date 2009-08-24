package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XAttributeMerger;
import jw.bznetwork.client.x.lang.XBoolean;
import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;

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
