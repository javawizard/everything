package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XAttributeMerger;
import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XException;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;
import org.opengroove.xsm.web.client.lang.XList;
import org.opengroove.xsm.web.client.lang.XNull;
import org.opengroove.xsm.web.client.lang.XNumber;
import org.opengroove.xsm.web.client.lang.XString;

public class CXItem implements XCommand
{
    
    public String getName()
    {
        return "item";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        // index, target, index is literal, target is variable, index is number,
        // target is not
        XAttributeMerger attributeSet =
            new XAttributeMerger(element, new String[] { "index", "target" },
                new boolean[] { true, false }, null, context, new boolean[] { false,
                    true });
        XNumber indexValue = (XNumber) attributeSet.getResult(0);
        int index = (int) indexValue.getValue();
        XData targetValue = attributeSet.getResult(1);
        if (index < 1)
            throw new XException("Index must be greater than 0");
        if (targetValue instanceof XList)
        {
            XList targetList = (XList) targetValue;
            if (index > targetList.getValue().size())
                return new XNull();
            return targetList.getValue().get(index - 1);
        }
        else if (targetValue instanceof XString)
        {
            XString targetString = (XString) targetValue;
            if (index > targetString.getValue().length())
                return new XNull();
            return new XString("" + targetString.getValue().charAt(index - 1));
        }
        else if (targetValue instanceof XNull)
        {
            return new XNull();
        }
        else
        {
            throw new XException("Target to item has to be a list or a string");
        }
    }
}
