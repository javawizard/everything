package jw.bznetwork.client.x.lang.i;

import java.util.ArrayList;

import jw.bznetwork.client.x.lang.XAttributeMerger;
import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;
import jw.bznetwork.client.x.lang.XList;
import jw.bznetwork.client.x.lang.XNull;
import jw.bznetwork.client.x.lang.XNumber;


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
