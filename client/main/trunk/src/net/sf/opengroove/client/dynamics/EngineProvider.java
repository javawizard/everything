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
    public EngineReader createReader();
    public EngineWriter createWriter();
    public 
}
