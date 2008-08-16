package net.sf.opengroove.client.plugins;

public class AccumulatingExtensionPoint implements
    ExtensionPoint
{
    private AccumulatingSupervisor supervisor;
    private String id;
    
    @Override
    public void init(ExtensionPointContext context)
    {
        this.supervisor = (AccumulatingSupervisor) context
            .getSupervisor();
        this.id = context.getModel().getId();
    }
    
    @Override
    public void registerExtension(PluginInfo pInfo,
        ExtensionInfo info, Extension extension)
    {
        supervisor.addExtension(id, extension, info);
    }
}
