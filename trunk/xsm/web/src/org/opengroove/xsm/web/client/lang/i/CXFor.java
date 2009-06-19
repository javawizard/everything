package org.opengroove.xsm.web.client.lang.i;

import org.opengroove.xsm.web.client.lang.XAttributeMerger;
import org.opengroove.xsm.web.client.lang.XCommand;
import org.opengroove.xsm.web.client.lang.XData;
import org.opengroove.xsm.web.client.lang.XDouble;
import org.opengroove.xsm.web.client.lang.XElement;
import org.opengroove.xsm.web.client.lang.XException;
import org.opengroove.xsm.web.client.lang.XInterpreter;
import org.opengroove.xsm.web.client.lang.XInterpreterContext;
import org.opengroove.xsm.web.client.lang.XNode;
import org.opengroove.xsm.web.client.lang.XNumber;
import org.opengroove.xsm.web.client.lang.XString;

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
        XAttributeMerger attributeSet =
            new XAttributeMerger(element, new String[] { "var", "initial", "final",
                "step" }, new boolean[] { false, true, true, true }, new boolean[] {
                false, false, false, false }, context, null);
        String var = ((XString) attributeSet.getResult(0)).getValue();
        XData initialValue = attributeSet.getResult(1);
        XData finalValue = attributeSet.getResult(2);
        XData stepValue = attributeSet.getResult(3);
        boolean isDoubles = initialValue instanceof XDouble;
        /*
         * We should have our values now, and we know where the actual code
         * elements are in our for loop. Now we loop.
         */
        XData currentValue = initialValue;
        boolean isUp = stepValue.getAsDouble() > 0;
        XData previousValue = context.getVariables().get(var);
        /*
         * While (we're going up and we're not more than the final) or (we're
         * going down and we're not less than the final)
         */
        while ((isUp && currentValue.getAsDouble() <= finalValue.getAsDouble())
            || ((!isUp) && currentValue.getAsDouble() >= finalValue.getAsDouble()))
        {
            context.getVariables().put(var, currentValue);
            context.getInterpreter().executeChildren(element, context,
                attributeSet.getTagCount());
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
        context.setVariable(var, previousValue);
        return null;
    }
}
