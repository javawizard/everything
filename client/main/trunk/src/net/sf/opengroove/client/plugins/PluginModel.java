package net.sf.opengroove.client.plugins;

import java.io.File;

public class PluginModel
{
    /**
     * The name of the plugin.
     */
    private String name;
    /**
     * Thd id of the plugin, which should usually be present in the plugin's jar
     * file path or it's config file path
     */
    private String id;
    /**
     * The description of the plugin
     */
    private String description;
    /**
     * The plugin's license agreement
     */
    private String license;
    private String supervisorClass;
    private String updateSite;
    private Version version;
    /**
     * True if this is an internal plugin, false if it's an external plugin
     */
    private boolean internal;
    /**
     * The plugin's jar file location, null for internal plugins
     */
    private File jarFile;
    /**
     * The plugin's config file location, null for external plugins
     */
    private File configFile;
    
    public String getName()
    {
        return name;
    }
    
    public String getId()
    {
        return id;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public String getLicense()
    {
        return license;
    }
    
    public String getSupervisorClass()
    {
        return supervisorClass;
    }
    
    public String getUpdateSite()
    {
        return updateSite;
    }
    
    public Version getVersion()
    {
        return version;
    }
    
    public boolean isInternal()
    {
        return internal;
    }
    
    public File getJarFile()
    {
        return jarFile;
    }
    
    public File getConfigFile()
    {
        return configFile;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public void setLicense(String license)
    {
        this.license = license;
    }
    
    public void setSupervisorClass(String supervisorClass)
    {
        this.supervisorClass = supervisorClass;
    }
    
    public void setUpdateSite(String updateSite)
    {
        this.updateSite = updateSite;
    }
    
    public void setVersion(Version version)
    {
        this.version = version;
    }
    
    public void setInternal(boolean internal)
    {
        this.internal = internal;
    }
    
    public void setJarFile(File jarFile)
    {
        this.jarFile = jarFile;
    }
    
    public void setConfigFile(File configFile)
    {
        this.configFile = configFile;
    }
}
