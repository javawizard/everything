package net.sf.opengroove.client.oldplugins;

/**
 * An EmptySupervisor does nothing. It is useful when a particular plugin has no
 * extension points and only one extension. It can use an EmptySupervisor (or
 * not specify a supervisor at all, in which case OpenGroove will create an
 * EmptySupervisor for it), and do all of it's processing in the extension class
 * itself.
 * 
 * @author Alexander Boyd
 * 
 */
public class EmptySupervisor implements Supervisor
{
    
    @Override
    public void init(PluginContext context)
    {
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
