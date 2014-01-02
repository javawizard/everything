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
     * Creates a new EngineReader. The engine reader is "live", in the sense
     * that any changes that occur to the engine will be immediately accessible
     * from the reader. If a new delta is to be applied to the engine, the
     * reader's methods will block during that time.
     * 
     * @return A newly-created EngineReader that can be used to read from this
     *         engine
     */
    public EngineReader createReader();
    
    /**
     * Creates a new EngineWriter. An EngineWriter is used to make changes to
     * the engine.
     * 
     * @return a newly-created EngineWriter
     */
    public EngineWriter createWriter();
}
