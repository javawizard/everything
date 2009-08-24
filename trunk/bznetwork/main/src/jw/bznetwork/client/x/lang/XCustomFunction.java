package jw.bznetwork.client.x.lang;

import jw.bznetwork.client.x.lang.i.CXFunction;

/**
 * A function defined with the {@link CXFunction} command.
 * 
 * @author Alexander Boyd
 * 
 */
public class XCustomFunction implements XCommand
{
    private XElement definedElement;
    private String name;
    
    public XCustomFunction(XElement element)
    {
        this.definedElement = element;
        this.name = element.getAttribute("name");
    }
    
    public String getName()
    {
        return name;
    }
    
    public XData invoke(XInterpreterContext context, XElement callerElement)
    {
        /*
         * First, we create a new context for the function to run in.
         */
        XInterpreterContext newContext =
            new XInterpreterContext(context.getInterpreter(), false);
        /*
         * Now we set all of the attributes as "param.X" variables in the
         * function.
         */
        for (String attributeName : callerElement.getAttributes().keySet())
        {
            /*
             * We'll convert names to lowercase to make them case-insensitive.
             */
            newContext.setVariable("param." + attributeName.toLowerCase(), new XString(
                callerElement.getAttribute(attributeName)));
        }
        /*
         * Now we set the result of executing all of the elements as arg.X
         * variables.
         */
        for (int i = 0; i < callerElement.getChildren().size(); i++)
        {
            XElement ce = (XElement) callerElement.getChild(i);
            XData ceResult = context.execute(ce);
            newContext.setVariable("arg." + (i + 1), ceResult);
        }
        /*
         * We've set the variables. Now we execute the function's defined
         * element in the new context.
         */
        try
        {
            context.getInterpreter().executeChildren(definedElement, newContext);
        }
        catch (XReturnException e)
        {
            /*
             * Something in the function returned a value. So we return it.
             */
            return e.getValue();
        }
        /*
         * The function didn't return, so we return null;
         */
        return null;
    }
    
}
