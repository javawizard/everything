package net.sf.opengroove.client.oldplugins;

import java.io.File;

/**
 * A PluginContext is handed to all plugins when they are initialized. The
 * plugin context allows the plugin to get information about itself and about
 * the resources provided to it by OpenGroove.
 * 
 * @author Alexander Boyd
 * 
 */
public class PluginContext
{
    private Plugin plugin;
    private PluginManager manager;
    
    PluginContext(Plugin plugin, PluginManager manager)
    {
        this.plugin = plugin;
        this.manager = manager;
    }
    
    /**
     * Gets the id of this plugin, declared in the plugin's file.
     * 
     * @return
     */
    public String getId()
    {
        return plugin.getModel().getId();
    }
    
    public LanguageContext getLanguageContext()
    {
        return null;
    }
    
    public File getPersistantStorage()
    {
        File file = new File(manager.getDataFolder(),
            getId());
        if (!file.exists())
            file.mkdirs();
        return file;
    }
    
    public PluginInfo getPluginInfo()
    {
        return null;
    }
}
