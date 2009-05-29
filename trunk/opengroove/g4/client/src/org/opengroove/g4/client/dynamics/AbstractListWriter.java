package org.opengroove.g4.client.dynamics;

import java.util.ArrayList;

/**
 * An abstract EngineWriter that can be used for engine writers with a
 * one-to-one correspondence between commands and methods on the writer. It
 * stores a list of commands that have been added to it, which subclasses add
 * upon method calls. Committing it has the effect of preventing future
 * modifications and making the commands available from getCommands.
 * 
 * @author Alexander Boyd
 * 
 */
public abstract class AbstractListWriter implements EngineWriter
{
    private ArrayList<Command> commands = new ArrayList<Command>();
    private Command[] commandArray;
    
    public void commit()
    {
        commandArray = commands.toArray(new Command[0]);
        commands = null;
    }
    
    protected void addCommand(Command command)
    {
        if (commands == null)
            throw new IllegalStateException("Already committed");
        commands.add(command);
    }
    
    public Command[] getCommands()
    {
        if (commandArray == null)
            throw new IllegalStateException("Not committed");
        return commandArray;
    }
    
}
