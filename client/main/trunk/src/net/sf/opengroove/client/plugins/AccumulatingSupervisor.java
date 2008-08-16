package net.sf.opengroove.client.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * A simple supervisor that can have any number of AccumulatingExtensionPoints
 * associated with it. All extensions registered are stored in a hash map that
 * maps extension points to the extensions registered with them.
 * 
 * @author Alexander Boyd
 * 
 */
public class AccumulatingSupervisor implements Supervisor
{

    private Hashtable<String, ArrayList<AccumulatingExtensionWrapper>> extensions = new Hashtable<String, ArrayList<AccumulatingExtensionWrapper>>();
    
    private PluginContext context;
    
    @Override
    public void init(PluginContext context)
    {
        this.context = context;
    }
    
    public PluginContext getContext()
    {
        return context;
    }
    
    public AccumulatingExtension[] getExtensions(
        String point)
    {
    }
    
    void addExtension(String point, Extension extension);
    
    @Override
    public void ready()
    {
    }
    
    @Override
    public void registerExtensionPoint(
        ExtensionPoint<?> point)
    {
    }
    
    @Override
    public void registerLocalExtension(Extension extension)
    {
    }
    
}
