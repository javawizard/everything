package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XAttributeMerger;
import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;
import org.opengroove.xsm.web.client.lang.XList;
import org.opengroove.xsm.web.client.lang.XNumber;
import org.opengroove.xsm.web.client.lang.XString;

public class CXEach implements XCommand
{
    
    public String getName()
    {
        return "each";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        XAttributeMerger attributeSet =
            new XAttributeMerger(element, new String[] { "var", "over", "count" },
                null, new boolean[] { false, false, true }, context, new boolean[] {
                    false, true, false });
        XList overValue = (XList) attributeSet.getResult(1);
        String varName = ((XString) attributeSet.getResult(0)).getValue();
        String countName = null;
        if (attributeSet.getResult(2) != null)
        {
            countName = ((XString) attributeSet.getResult(2)).getValue();
        }
        XData previous = context.getVariables().get(varName);
        XData previousCount = null;
        if (countName != null)
            previousCount = context.getVariables().get(countName);
        int countIndex = 0;
        for (XData currentData : overValue.getValue())
        {
            countIndex += 1;
            if (countName != null)
                context.setVariable(countName, new XNumber(countIndex));
            context.setVariable(varName, currentData);
            context.getInterpreter().executeChildren(element, context,
                attributeSet.getTagCount());
        }
        context.setVariable(varName, previous);
        if (countName != null)
            context.setVariable(countName, previousCount);
        return null;
    }
}
