package net.sf.opengroove.client.oldplugins;

public class ExtensionPointContext
{
    private Supervisor supervisor;
    private PluginContext pluginContext;
    private ExtensionPointModel model;
    private ExtensionPoint extensionPoint;
    public Supervisor getSupervisor()
    {
        return supervisor;
    }
    public PluginContext getPluginContext()
    {
        return pluginContext;
    }
    public ExtensionPointModel getModel()
    {
        return model;
    }
    public ExtensionPoint getExtensionPoint()
    {
        return extensionPoint;
    }
    public void setSupervisor(Supervisor supervisor)
    {
        this.supervisor = supervisor;
    }
    public void setPluginContext(PluginContext pluginContext)
    {
        this.pluginContext = pluginContext;
    }
    public void setModel(ExtensionPointModel model)
    {
        this.model = model;
    }
    public void setExtensionPoint(ExtensionPoint extensionPoint)
    {
        this.extensionPoint = extensionPoint;
    }
}
