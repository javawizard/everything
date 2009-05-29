package org.opengroove.g4.client.dynamics;

import java.io.File;

/**
 * An engine. See
 * http://opengroove.googlecode.com/svn/trunk/opengroove/g4/client/dynamics.txt
 * for more info.<br/>
 * <br/>
 * 
 * It is guaranteed that multiple engines accessing the same file store won't
 * exist at the same time. Therefore, an engine can store its state in memory,
 * and write it to disk but not read it, and this will not have any problems.
 * 
 * @author Alexander Boyd
 * 
 */
public interface Engine
{
    /**
     * Initializes this engine with the storage folder specified. This will be a
     * storage folder that this engine previously modified, or a blank folder.
     * This must, however, remain a folder; it cannot be deleted and then
     * recreated as a file.
     * 
     * @param storage
     */
    public void init(File storage);
    
    /**
     * Creates a new EngineWriter for writing to the engine. This must create a
     * new writer each time it is called.
     * 
     * @return
     */
    public EngineWriter createWriter();
    
    /**
     * Creates a new EngineReader for reading from this engine.
     * 
     * @return
     */
    public EngineReader createReader();
    
    /**
     * Applies the specified commands, optionally applying the reverts specified
     * before-hand. The reverts and the commands should be applied in the exact
     * order specified. The reverts will always be provided in the opposite
     * order that their commands were, so the order of the reverts should not be
     * reversed before they are executed.<br/>
     * <br/>
     * 
     * This method should block all EngineReaders (but not EngineWriters) that
     * might be in use by this engine. This could be done by having this engine
     * maintain a lock object, and then having this method and all EngineReader
     * methods synchronize on the lock.
     * 
     * @param reverts
     *            The reverts to execute before executing the commands. This
     *            array can be empty to not execute any reverts.
     * @param commands
     *            The commands to execute after executing the specified reverts
     * @return A list of reverts for each of the commands specified that can be
     *         used to revert those commands, in the exact same order that the
     *         commands are provided in.
     */
    public DataBlock[] applyCommands(DataBlock[] reverts, Command[] commands);
}
