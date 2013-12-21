package jw.bznetwork.client.x.lang;

import java.util.HashMap;

import jw.bznetwork.client.x.lang.i.*;

public class XInterpreter
{
    private HashMap<String, XCommand> commands = new HashMap<String, XCommand>();
    
    private XDisplayDevice display;
    
    public XInputDevice input;
    
    public HashMap<String, String> configuration = new HashMap<String, String>();
    
    public XInterpreter()
    {
        configuration.put("limit", "1000");
    }
    
    public int instructionCount;
    
    public void installDefaultCommands()
    {
        install(new CXAdd());
        install(new CXAnd());
        install(new CXBoolean());
        install(new CXCat());
        install(new CXChar());
        install(new CXConfig());
        install(new CXDecrement());
        install(new CXDefined());
        install(new CXDivide());
        install(new CXDouble());
        install(new CXEach());
        install(new CXElse());
        install(new CXEquals());
        install(new CXFalse());
        install(new CXFor());
        install(new CXFunction());
        install(new CXGreater());
        install(new CXIdentity());
        install(new CXIf());
        install(new CXIncrement());
        install(new CXItem());
        install(new CXLess());
        install(new CXList());
        install(new CXMultiply());
        install(new CXNot());
        install(new CXNull());
        install(new CXNumber());
        install(new CXNumeric());
        install(new CXOr());
        install(new CXOverwrite());
        install(new CXPrint());
        install(new CXPrompt());
        install(new CXResize());
        install(new CXReturn());
        install(new CXSet());
        install(new CXSize());
        install(new CXSpace());
        install(new CXString());
        install(new CXSubtract());
        install(new CXTrue());
        install(new CXVar());
        install(new CXWhile());
    }
    
    /**
     * Installs the specified command.
     * 
     * @param command
     *            The command to install
     */
    public void install(XCommand command)
    {
        if (command.getName() == null)
        {
            if (display != null)
            {
                display.print("XInterpreter warning: null command for class "
                        + command.getClass().getName()
                        + ". The interpreter will continue, but "
                        + "without support for this command.", true);
            }
            return;
        }
        if (commands.get(command.getName().toLowerCase()) != null)
            throw new XException(
                    "The BZNetwork version of XInterpreter does not allow "
                            + "command re-assignment for security reasons. Re-assignment of "
                            + command.getName());
        commands.put(command.getName().toLowerCase(), command);
    }
    
    public void remove(String commandName)
    {
        commands.remove(commandName);
    }
    
    public XData execute(XElement element, XInterpreterContext context)
    {
        if (element == null)
            throw new XException("Trying to run a null element. This usually "
                    + "means a command was expecting "
                    + "more tags as input than you gave to it.");
        instructionCount += 1;
        if ((instructionCount % 10) == 0)
            validateInstructionCount();
        XCommand command = commands.get(element.getTag().toLowerCase());
        if (command == null)
            throw new XException("Nonexistent command: " + element.getTag());
        try
        {
            try
            {
                return command.invoke(context, element);
            }
            catch (ClassCastException e)
            {
                throw new XException(
                        "Cast error. This usually means you tried to pass "
                                + "some data of the wrong type to a function. "
                                + "Java exception message: " + e.getMessage(),e);
            }
        }
        catch (XException e)
        {
            e.getProgramStack().add(new XStackFrame(element.getTag()));
            throw e;
        }
    }
    
    private void validateInstructionCount()
    {
        int allowed = Integer.parseInt(configuration.get("limit"));
        if (instructionCount > allowed)
            throw new XLimitExceededException("Instruction limit exceeded.");
    }
    
    public void executeChildren(XElement element, XInterpreterContext context,
            int startIndex)
    {
        if (context == null)
            context = new XInterpreterContext(this, true);
        int skipped = 0;
        for (XNode node : element.getChildren())
        {
            if (node instanceof XText)
                continue;
            if (skipped < startIndex)
            {
                skipped++;
                continue;
            }
            execute((XElement) node, context);
        }
    }
    
    public void executeChildren(XElement element, XInterpreterContext context)
    {
        executeChildren(element, context, 0);
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
    
    public XInputDevice getInput()
    {
        return input;
    }
    
    public void setInput(XInputDevice input)
    {
        this.input = input;
    }
    
    public static XData parseNumeric(String value)
    {
        if (value.contains("."))
            return new XDouble(Double.parseDouble(value));
        else
            return new XNumber(Long.parseLong(value));
    }
    
}
