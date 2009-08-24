package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XAttributeMerger;
import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;
import jw.bznetwork.client.x.lang.XList;
import jw.bznetwork.client.x.lang.XNull;
import jw.bznetwork.client.x.lang.XNumber;

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
