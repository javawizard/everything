package org.opengroove.xsm.web.client.lang;

import java.util.HashMap;

import org.opengroove.xsm.web.client.lang.i.CXPrint;
import org.opengroove.xsm.web.client.lang.i.CXSet;
import org.opengroove.xsm.web.client.lang.i.CXVar;

public class XInterpreter
{
    private HashMap<String, XCommand> commands = new HashMap<String, XCommand>();
    
    private XDisplayDevice display;
    
    public void installDefaultCommands()
    {
        install(new CXPrint());
        install(new CXSet());
        install(new CXVar());
    }
    
    /**
     * Installs the specified command.
     * 
     * @param command
     *            The command to install
     */
    public void install(XCommand command)
    {
        commands.put(command.getName().toLowerCase(), command);
    }
    
    public XData execute(XElement element, XInterpreterContext context)
    {
        XCommand command = commands.get(element.getTag().toLowerCase());
        if (command == null)
            throw new XException("Nonexistent command: " + element.getTag());
        try
        {
            return command.invoke(context, element);
        }
        catch (XException e)
        {
            e.getProgramStack().add(new XStackFrame(element.getTag()));
            throw e;
        }
    }
    
    public void executeChildren(XElement element, XInterpreterContext context)
    {
        if (context == null)
            context = new XInterpreterContext(this, true);
        for (XNode node : element.getChildren())
        {
            execute((XElement) node, context);
        }
    }
    
    public XDisplayDevice getDisplay()
    {
        return display;
    }
    
    /**
     * Sets the display used by this interpreter. The print command will throw
     * an exception if this has not been set and the print command is called.
     * 
     * @param display
     *            The display that should be used for printing data
     */
    public void setDisplay(XDisplayDevice display)
    {
        this.display = display;
    }
    
}
