package net.sf.opengroove.client.dynamics;

/**
 * An interface that classes extend to indicate that they have the ability to
 * create {@link EngineProvider EngineProviders}.
 * 
 * @author Alexander Boyd
 * 
 */
public interface EngineProviderFactory
{
    /**
     * Creates a new EngineProvider.
     * 
     * @return a new EngineProvider.
     */
    public EngineProvider create();
}
