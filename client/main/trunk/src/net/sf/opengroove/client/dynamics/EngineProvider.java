package net.sf.opengroove.client.dynamics;

/**
 * An EngineProvider is the backing implementation of a particular engine type.
 * Classes that wish to define a new engine type would extend this class. An
 * EngineProvider is provided with a storage block (format TBD, I'm thinking of
 * a hierarchical data store similar to the file system but accessed through a
 * custom class) that it can use to store it's data locally. TODO: finish this
 * javadoc
 * 
 * @author Alexander Boyd
 * 
 */
public interface EngineProvider
{
    /**
     * Creates a new EngineReader. If <code>live</code> is true, then the
     * engine reader should keep it's contents up-to-date with the contents of
     * the engine itself, but the reader might block on a particular operation
     * if the engine is in the process of applying a delta. If it is false, then
     * the engine reader obtained provides a snapshot of the data at the time
     * this method was called.
     * 
     * @return
     */
    public EngineReader createReader(boolean live);
    
    public EngineWriter createWriter();
}
