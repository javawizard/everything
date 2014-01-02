package net.sf.opengroove.client.dynamics;

/**
 * This class is the core of the dynamics subsystem. It provides the means to
 * allow an application to utilize the dynamics subsystem. An engine is created
 * from an EngineProviderFactory, which the dynamics system uses to create an
 * engine provider, and an EngineContext, which the client using the class must
 * define.
 * 
 * @author Alexander Boyd
 * 
 */
public class Engine
{
    /**
     * Creates a new Engine, using the provider factory and context provided.
     * 
     * @param providerClass
     * @param context
     */
    public Engine(EngineProviderFactory providerFactory,
        EngineContext context)
    {
        
    }
}
