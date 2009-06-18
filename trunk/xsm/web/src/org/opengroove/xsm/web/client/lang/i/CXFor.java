package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XDouble;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XException;
import org.opengroove.xsm.web.client.lang.XInterpreter;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;
import org.opengroove.xsm.web.client.lang.XNode;
import org.opengroove.xsm.web.client.lang.XNumber;

public class CXFor implements XCommand
{
    
    public String getName()
    {
        return "for";
    }
    
    public XData invoke(XInterpreterContext context, XElement element)
    {
        /*
         * First, get the elements that represent our initial, final, and step
         * variables. If they're attributes instead of arguments, we'll create
         * pseudo-commands for them. Somewhat more computationally-expensive,
         * but easier to code.
         * 
         * Initial, final, and step will only be evaluated once at the
         * beginning, not over and over again.
         * 
         * If initial is a double, then everything else can be a double, too.
         * Otherwise, they have to be a number.
         */
        String var = element.getAttribute("var");
        XData initialValue = null;
        XData finalValue = null;
        XData stepValue = null;
        String initialAttribute = element.getAttribute("initial");
        String finalAttribute = element.getAttribute("final");
        String stepAttribute = element.getAttribute("step");
        int argumentIndex = 0;
        if (initialAttribute != null)
            initialValue = XInterpreter.parseNumeric(initialAttribute);
        if (finalAttribute != null)
            finalValue = XInterpreter.parseNumeric(finalAttribute);
        if (stepAttribute != null)
            stepValue = XInterpreter.parseNumeric(stepAttribute);
        for (XNode child : element.getChildren())
        {
            XElement ce = (XElement) child;
            String tag = ce.getTag();
            if (!(tag.equals("initial") || tag.equals("final") || tag.equals("step")))
                break;
            argumentIndex += 1;
            if (tag.equals("initial"))
                initialValue = context.execute(ce.getSingleElement());
            if (tag.equals("final"))
                finalValue = context.execute(ce.getSingleElement());
            if (tag.equals("step"))
                stepValue = context.execute(ce.getSingleElement());
        }
        if (initialValue == null || finalValue == null || stepValue == null)
            throw new XException("Missing initial, final, or step");
        if (var == null)
            throw new XException("Missing var");
        boolean isDoubles = initialValue instanceof XDouble;
        /*
         * We should have our values now, and we know where the actual code
         * elements are in our for loop. Now we loop.
         */
        XData currentValue = initialValue;
        boolean isUp = stepValue.getAsDouble() > 0;
        XData previousValue = context.getVariable(var);
        /*
         * While (we're going up and we're not more than the final) or (we're
         * going down and we're not less than the final)
         */
        while ((isUp && currentValue.getAsDouble() <= finalValue.getAsDouble())
            || ((!isUp) && currentValue.getAsDouble() >= finalValue.getAsDouble()))
        {
            context.getVariables().put(var, currentValue);
            context.getInterpreter().executeChildren(element, context, argumentIndex);
            /*
             * Now increment the current value
             */
            if (isDoubles)
            {
                currentValue =
                    new XDouble(currentValue.getAsDouble() + stepValue.getAsDouble());
            }
            else
            {
                currentValue =
                    new XNumber(((XNumber) currentValue).getValue()
                        + ((XNumber) stepValue).getValue());
            }
        }
        /*
         * We're done!
         */
        return null;
    }
}
