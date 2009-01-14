package net.sf.opengroove.client.oldplugins;

public class DependencyModel
{
    private String plugin;
    private boolean required;
    private String details;
    private String updateSite;
    public String getPlugin()
    {
        return plugin;
    }
    public boolean isRequired()
    {
        return required;
    }
    public String getDetails()
    {
        return details;
    }
    public String getUpdateSite()
    {
        return updateSite;
    }
    public void setPlugin(String plugin)
    {
        this.plugin = plugin;
    }
    public void setRequired(boolean required)
    {
        this.required = required;
    }
    public void setDetails(String details)
    {
        this.details = details;
    }
    public void setUpdateSite(String updateSite)
    {
        this.updateSite = updateSite;
    }
}
