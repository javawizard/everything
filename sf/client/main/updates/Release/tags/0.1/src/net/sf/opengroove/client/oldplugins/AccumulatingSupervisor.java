package net.sf.opengroove.client.oldplugins;

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
    
    private Hashtable<String, AccumulatingExtensionPoint> extensions = new Hashtable<String, AccumulatingExtensionPoint>();
    
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
    
    public ExtensionWrapper[] getExtensions(String point)
    {
        return extensions.get(point).getExtensions();
    }
    
    @Override
    public void ready()
    {
    }
    
    @Override
    public void registerExtensionPoint(
        ExtensionPoint<?> point)
    {
        if (point instanceof AccumulatingExtensionPoint)
        {
            extensions.put(
                ((AccumulatingExtensionPoint) point)
                    .getId(),
                (AccumulatingExtensionPoint) point);
        }
    }
    
    @Override
    public void registerLocalExtension(Extension extension)
    {
    }
    
}
