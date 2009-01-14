package net.sf.opengroove.client.oldplugins;

import java.io.File;
import java.util.jar.JarFile;

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
    private File jarSource;
    private DependencyModel[] dependencies;
    private IconModel[] icons;
    private PermissionModel[] permissions;
    private ExtensionModel[] extensions;
    private ExtensionPointModel[] extensionPoints;
    private String failureReason;
    /**
     * True if this is an internal plugin, false if it's an external plugin
     */
    private boolean internal;
    /**
     * The plugin's jar file location, null for internal plugins
     */
    private JarFile jarFile;
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
    
    public boolean isInternal()
    {
        return internal;
    }
    
    public JarFile getJarFile()
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
    
    public void setInternal(boolean internal)
    {
        this.internal = internal;
    }
    
    public void setJarFile(JarFile jarFile)
    {
        this.jarFile = jarFile;
    }
    
    public void setConfigFile(File configFile)
    {
        this.configFile = configFile;
    }
    
    public IconModel[] getIcons()
    {
        return icons;
    }
    
    public PermissionModel[] getPermissions()
    {
        return permissions;
    }
    
    public ExtensionModel[] getExtensions()
    {
        return extensions;
    }
    
    public ExtensionPointModel[] getExtensionPoints()
    {
        return extensionPoints;
    }
    
    public void setIcons(IconModel[] icons)
    {
        this.icons = icons;
    }
    
    public void setPermissions(PermissionModel[] permissions)
    {
        this.permissions = permissions;
    }
    
    public void setExtensions(ExtensionModel[] extensions)
    {
        this.extensions = extensions;
    }
    
    public void setExtensionPoints(
        ExtensionPointModel[] extensionPoints)
    {
        this.extensionPoints = extensionPoints;
    }
    
    public DependencyModel[] getDependencies()
    {
        return dependencies;
    }
    
    public void setDependencies(
        DependencyModel[] dependencies)
    {
        this.dependencies = dependencies;
    }
    
    public File getJarSource()
    {
        return jarSource;
    }
    
    public void setJarSource(File jarSource)
    {
        this.jarSource = jarSource;
    }
    
    public String getFailureReason()
    {
        return failureReason;
    }
    
    public void setFailureReason(String failureReason)
    {
        this.failureReason = failureReason;
    }
}
