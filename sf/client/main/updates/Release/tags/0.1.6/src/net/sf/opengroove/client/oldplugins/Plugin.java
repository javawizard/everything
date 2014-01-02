package net.sf.opengroove.client.oldplugins;

import java.awt.Image;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This class represents a plugin.
 * 
 * @author Alexander Boyd
 * 
 */
public class Plugin
{
    private PluginModel model;
    /**
     * The plugin's class loader, or null if this plugin is an internal plugin
     */
    private PluginClassLoader classLoader;
    private PluginContext context;
    private Supervisor supervisor;
    private ExtensionPointContext[] extensionPoints;
    private ExtensionContext[] extensions;
    private Map<String, ArrayList<Image>> icons = new HashMap<String, ArrayList<Image>>();
    
    public PluginModel getModel()
    {
        return model;
    }
    
    public Supervisor getSupervisor()
    {
        return supervisor;
    }
    
    public Map<String, ArrayList<Image>> getIcons()
    {
        return icons;
    }
    
    public void setModel(PluginModel model)
    {
        this.model = model;
    }
    
    public void setSupervisor(Supervisor supervisor)
    {
        this.supervisor = supervisor;
    }
    
    public PluginContext getContext()
    {
        return context;
    }
    
    public void setContext(PluginContext context)
    {
        this.context = context;
    }
    
    public PluginClassLoader getClassLoader()
    {
        return classLoader;
    }
    
    public void setClassLoader(PluginClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    public ExtensionPointContext[] getExtensionPoints()
    {
        return extensionPoints;
    }

    public ExtensionContext[] getExtensions()
    {
        return extensions;
    }

    public void setExtensionPoints(
        ExtensionPointContext[] extensionPoints)
    {
        this.extensionPoints = extensionPoints;
    }

    public void setExtensions(ExtensionContext[] extensions)
    {
        this.extensions = extensions;
    }
}
