package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XAttributeMerger;
import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;
import org.opengroove.xsm.web.client.lang.XList;
import org.opengroove.xsm.web.client.lang.XNull;
import org.opengroove.xsm.web.client.lang.XNumber;

public class CXResize implements XCommand
{
    
    public String getName()
    {
        return "resize";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        // target, size
        XAttributeMerger attributeSet =
            new XAttributeMerger(element, new String[] { "target", "size" },
                new boolean[] { false, true }, null, context, new boolean[] { true,
                    false });
        XList targetValue = (XList) attributeSet.getResult(0);
        XNumber sizeValue = (XNumber) attributeSet.getResult(1);
        int size = (int) sizeValue.getValue();
        while (size < targetValue.getValue().size())
        {
            targetValue.getValue().remove(targetValue.getValue().size() - 1);
        }
        while (size > targetValue.getValue().size())
        {
            targetValue.getValue().add(new XNull());
        }
        return null;
    }
    
}
