package org.opengroove.g4.client.dynamics;

import java.io.File;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * An engine. See
 * http://opengroove.googlecode.com/svn/trunk/opengroove/g4/client/dynamics.txt
 * for more info.<br/>
 * <br/>
 * 
 * It is guaranteed that multiple engines accessing the same file store won't
 * exist at the same time. Therefore, an engine can store its state in memory,
 * and write it to disk but not read it, and this will not have any problems.<br/>
 * <br/>
 * 
 * Engines must not modify or read data in any other code except that in the
 * applyCommand and applyRevert methods, or methods called by those methods. In
 * particular, engines must never start threads that periodically read or modify
 * data in the engine.
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
     * Locks the engine and any of its readers. applyCommand and applyRevert are
     * only called by threads that have locked this engine. The engine must
     * guarantee that if a thread has locked it, that thread is the only one
     * that can read from readers on this engine. If another thread has locked
     * this engine already, then this method will block until the other thread
     * unlocks this engine.<br/>
     * <br/>
     * 
     * Code that writes to the engine (using the apply methods) must lock the
     * engine first. Code that reads from the engine should instead lock the
     * reader of the engine. This method is therefore equivalent to obtaining a
     * write lock on a read-write lock. In fact, most Engine implementations
     * that are part of G4 use a {@link ReentrantReadWriteLock}, with this
     * method obtaining the write lock.
     */
    public void lock();
    
    /**
     * Applies the specified command, generating a command that can be itself
     * applied to revert this command. This is also called to revert commands,
     * by passing in the generated revert command.<br/>
     * <br/>
     * 
     * This method can only be called when the engine is locked by the thread
     * calling it. Engine implementations are highly recommended to enforce
     * this, although they are not required to.<br/>
     * <br/>
     * 
     * <b>This method should not save changes to disk.</b> This method should
     * simply apply the command in such a way that a failure of the computer
     * would cause the command to be reverted. Changes are only saved to disk
     * when the unlock method is called.<br/>
     * <br/>
     * 
     * Workspaces that make use of engines generally shouldn't call this method
     * directly, since it would only apply changes to their local copy.
     * 
     * @param command
     *            The command or revert to apply
     * @return A command that will revert this one. If this command is a command
     *         that can only appear as a revert (IE it will never be generated
     *         by an engine writer produced by this engine), then this can be
     *         null, but if it isn't, it must be a command equivalent to the one
     *         that generated this revert. Otherwise, this must be a command
     *         which can revert the command passed into this method.
     */
    public Command applyCommand(Command command);
    
    /**
     * Unlocks this engine. Reads may progress concurrently after the engine is
     * unlocked.<br/>
     * <br/>
     * 
     * This method should save any changes that were applied by applyCommand. In
     * effect, then, lock() starts a new transaction and unlock() commits it.
     */
    public void unlock();
}
