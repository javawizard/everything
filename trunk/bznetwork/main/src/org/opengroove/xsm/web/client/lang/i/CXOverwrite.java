package org.opengroove.xsm.web.client.lang.i;

import java.util.ArrayList;

import org.opengroove.xsm.web.client.lang.XAttributeMerger;
import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;
import org.opengroove.xsm.web.client.lang.XList;
import org.opengroove.xsm.web.client.lang.XNull;
import org.opengroove.xsm.web.client.lang.XNumber;

public class CXOverwrite implements XCommand
{
    
    public String getName()
    {
        return "overwrite";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        // index, target, value
        // target, size
        XAttributeMerger attributeSet =
            new XAttributeMerger(element, new String[] { "target", "index", "value" },
                new boolean[] { false, true, false }, null, context, new boolean[] {
                    true, false, false });
        XList targetValue = (XList) attributeSet.getResult(0);
        XNumber indexValue = (XNumber) attributeSet.getResult(1);
        XData valueData = attributeSet.getResult(2);
        int index = (int) indexValue.getValue();
        ArrayList<XData> list = targetValue.getValue();
        while (list.size() < index)
        {
            list.add(new XNull());
        }
        list.set(index - 1, valueData);
        return null;
    }
}
