package net.sf.opengroove.client.plugins;

public class ExtensionModel
{
    private String id;
    private String plugin;
    private String point;
    private String extensionClass;
    public String getId()
    {
        return id;
    }
    public String getPlugin()
    {
        return plugin;
    }
    public String getPoint()
    {
        return point;
    }
    public String getExtensionClass()
    {
        return extensionClass;
    }
    public void setId(String id)
    {
        this.id = id;
    }
    public void setPlugin(String plugin)
    {
        this.plugin = plugin;
    }
    public void setPoint(String point)
    {
        this.point = point;
    }
    public void setExtensionClass(String extensionClass)
    {
        this.extensionClass = extensionClass;
    }
}
