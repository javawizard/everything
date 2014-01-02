package org.opengroove.g4.client.dynamics;

/**
 * A reader that can be used to read data from an engine.
 * 
 * @author Alexander Boyd
 * 
 */
public interface EngineReader
{
    /**
     * Obtains a read lock. This does not exclusively lock this particular
     * engine reader; it just ensures that no writes to the engine (IE
     * invocations of {@link Engine#applyCommand(Command)}) will occur while
     * this reader is locked. Reads can take place even when the engine is
     * unlocked, but this can cause major problems if the engine is written to
     * by one thread while another thread is reading from it.
     */
    public void lock();
    
    /**
     * Releases a read lock held on this engine. If lock() has been called
     * multiple times, then unlock() must be called the same number of times to
     * release the lock.<br/>
     * <br/>
     * 
     * This should always appear in a finally block right after locking the
     * reader. If, for some reason, a lock were obtained but then not released
     * with this method, the engine would never be writable again. This would
     * cause major problems within G4.
     */
    public void unlock();
}
