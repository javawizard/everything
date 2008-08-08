package net.sf.opengroove.client.plugins;

public interface Supervisor
{
    public void init(PluginContext context);
    
    public void registerExtensionPoint(
        ExtensionPoint<?> point);
    
    public void registerLocalExtension(Extension extension);
}
