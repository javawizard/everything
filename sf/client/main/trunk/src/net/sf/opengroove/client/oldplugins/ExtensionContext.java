package net.sf.opengroove.client.oldplugins;

public class ExtensionContext
{
    private Supervisor supervisor;
    private Extension extension;
    private PluginContext pluginContext;
    private ExtensionModel model;
    public Supervisor getSupervisor()
    {
        return supervisor;
    }
    public Extension getExtension()
    {
        return extension;
    }
    public PluginContext getPluginContext()
    {
        return pluginContext;
    }
    public ExtensionModel getModel()
    {
        return model;
    }
    public void setSupervisor(Supervisor supervisor)
    {
        this.supervisor = supervisor;
    }
    public void setExtension(Extension extension)
    {
        this.extension = extension;
    }
    public void setPluginContext(PluginContext pluginContext)
    {
        this.pluginContext = pluginContext;
    }
    public void setModel(ExtensionModel model)
    {
        this.model = model;
    }
}
