package jw.bznetwork.client.x.lang.i;

import jw.bznetwork.client.x.lang.XBoolean;
import jw.bznetwork.client.x.lang.XCommand;
import jw.bznetwork.client.x.lang.XData;
import jw.bznetwork.client.x.lang.XElement;
import jw.bznetwork.client.x.lang.XException;
import jw.bznetwork.client.x.lang.XInterpreter;
import jw.bznetwork.client.x.lang.XInterpreterContext;
import jw.bznetwork.client.x.lang.XNode;
import jw.bznetwork.client.x.lang.XText;

public class CXBoolean implements XCommand
{
    
    public String getName()
    {
        return "boolean";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        /*
         * Exactly like <string>, except that at the end we remove all
         * whitespace and parse the value into a number.
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
        if (buffer.length() == 0)
            buffer.append("0");
        String result = buffer.toString().replaceAll("\\s", "");
        if (result.equalsIgnoreCase("true"))
            return new XBoolean(true);
        else if (result.equalsIgnoreCase("false"))
            return new XBoolean(false);
        else
            throw new XException("Input to <boolean> wasn't true or false, was "
                + buffer.toString());
    }
    
}
