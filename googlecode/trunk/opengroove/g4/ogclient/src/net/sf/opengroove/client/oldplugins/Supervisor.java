package net.sf.opengroove.client.oldplugins;

public interface Supervisor
{
    /**
     * Called to initialize the supervisor, before anything else is called.
     * 
     * @param context
     */
    public void init(PluginContext context);
    
    /**
     * Called after all extension points and extensions have been registered.
     */
    public void ready();
    
    public void registerExtensionPoint(
        ExtensionPoint<?> point);
    
    public void registerLocalExtension(Extension extension);
}
