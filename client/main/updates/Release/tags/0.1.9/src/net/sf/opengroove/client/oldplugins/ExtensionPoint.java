package net.sf.opengroove.client.oldplugins;

public interface ExtensionPoint<E extends Extension>
{
    /**
     * Called to initialize an ExtensionPoint. this is called just before the
     * extension point is registered to it's supervisor.
     * 
     * @param context
     */
    public void init(ExtensionPointContext context);
    
    public void registerExtension(PluginInfo pInfo,
        ExtensionInfo eInfo, E extension);
}
