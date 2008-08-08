package net.sf.opengroove.client.plugins;

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
    /**
     * Gets the id of this plugin, declared in the plugin's file.
     * 
     * @return
     */
    public String getId()
    {
        
    }
    
    public LanguageContext getLanguageContext()
    {
        
    }
    
    public File getPersistantStorage()
    {
        
    }
    
    public PluginInfo getPluginInfo()
    {
        
    }
}
