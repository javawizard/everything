package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XInterpreterContext;
import jw.bznetwork.client.x.lang.XNode;
import jw.bznetwork.client.x.lang.XString;
import jw.bznetwork.client.x.lang.XText;

public class CXString implements XCommand
{
    
    public String getName()
    {
        return "string";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        /*
         * For each text element, append it to the buffer (trimming before
         * hand). For each element, execute it and append the return value. Then
         * strip the trailing space and return the result in an XString.
         */
        StringBuffer buffer = new StringBuffer();
        for (XNode node : element.getChildren())
        {
            if (node instanceof XElement)
            {
                XData result = context.execute((XElement) node);
                buffer.append(result.toString().trim());
                buffer.append(" ");
            }
            else if (node instanceof XText)
            {
                buffer.append(((XText) node).getText().trim());
                buffer.append(" ");
            }
        }
        if (buffer.length() > 0)
            buffer = new StringBuffer(buffer.substring(0, buffer.length() - 1));
        return new XString(buffer.toString());
    }
    
}
