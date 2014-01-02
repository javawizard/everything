package org.opengroove.g4.client.dynamics;

/**
 * An engine writer. This can be obtained from an engine, and its sole
 * responsibility is to convert a series of method calls to a list of commands
 * that correspond to them. 
 * 
 * @author Alexander Boyd
 * 
 */
public interface EngineWriter
{
    public void commit();
    public Command[] getCommands();
}
