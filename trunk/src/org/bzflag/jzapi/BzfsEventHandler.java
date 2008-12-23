package org.bzflag.jzapi;

/**
 * An event handler that can listen to events.<br/><br/>
 * 
 * Event handlers are abstract classes instead of interfaces so that they can
 * store a pointer to the C++ wrapper object.
 * 
 * @author Alexander Boyd
 * 
 */
public abstract class BzfsEventHandler
{
    private long pointer;
    
    private long getPointer()
    {
        return pointer;
    }
    
    private void setPointer(long pointer)
    {
        this.pointer = pointer;
    }
    
    public abstract void process(BzfsEvent event);
}
