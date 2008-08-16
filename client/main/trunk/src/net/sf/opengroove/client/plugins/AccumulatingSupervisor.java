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
    
    private Hashtable<String, ArrayList<Wrapper>> extensions = new Hashtable<String, ArrayList<Wrapper>>();
    
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
    
    public Wrapper[] getExtensions(String point)
    {
        return extensions.get(point)
            .toArray(new Wrapper[0]);
    }
    
    synchronized void addExtension(String point,
        Extension extension, ExtensionInfo info)
    {
        Wrapper wrapper = new Wrapper();
        wrapper.setExtension(extension);
        wrapper.setInfo(info);
        ArrayList<Wrapper> list = extensions.get(point);
        if (list == null)
        {
            list = new ArrayList<Wrapper>();
            extensions.put(point, list);
        }
        list.add(wrapper);
    }
    
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
