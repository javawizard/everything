package org.opengroove.xsm.web.client.lang;

import java.util.HashMap;

import org.opengroove.xsm.web.client.lang.i.CXAdd;
import org.opengroove.xsm.web.client.lang.i.CXConfig;
import org.opengroove.xsm.web.client.lang.i.CXDivide;
import org.opengroove.xsm.web.client.lang.i.CXEach;
import org.opengroove.xsm.web.client.lang.i.CXFor;
import org.opengroove.xsm.web.client.lang.i.CXList;
import org.opengroove.xsm.web.client.lang.i.CXMultiply;
import org.opengroove.xsm.web.client.lang.i.CXNumber;
import org.opengroove.xsm.web.client.lang.i.CXPrint;
import org.opengroove.xsm.web.client.lang.i.CXPrompt;
import org.opengroove.xsm.web.client.lang.i.CXSet;
import org.opengroove.xsm.web.client.lang.i.CXString;
import org.opengroove.xsm.web.client.lang.i.CXSubtract;
import org.opengroove.xsm.web.client.lang.i.CXVar;

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
        install(new CXConfig());
        install(new CXDivide());
        install(new CXEach());
        install(new CXFor());
        install(new CXList());
        install(new CXMultiply());
        install(new CXNumber());
        install(new CXPrint());
        install(new CXPrompt());
        install(new CXSet());
        install(new CXString());
        install(new CXSubtract());
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
        if(command.getName() == null)
            return;
        commands.put(command.getName().toLowerCase(), command);
    }
    
    public XData execute(XElement element, XInterpreterContext context)
    {
        instructionCount += 1;
        if ((instructionCount % 10) == 0)
            validateInstructionCount();
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
    
    private void validateInstructionCount()
    {
        int allowed = Integer.parseInt(configuration.get("limit"));
        if (instructionCount > allowed)
            throw new XLimitExceededException("Instruction limit exceeded.");
    }
    
    public void executeChildren(XElement element, XInterpreterContext context, int startIndex)
    {
        if (context == null)
            context = new XInterpreterContext(this, true);
        int skipped = 0;
        for (XNode node : element.getChildren())
        {
            if(skipped < startIndex)
            {
                skipped++;
                continue;
            }
            execute((XElement) node, context);
        }
    }
    
    public void executeChildren(XElement element, XInterpreterContext context)
    {
        executeChildren(element,context,0);
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
