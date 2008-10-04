package net.sf.opengroove.common.proxystorage;

import javax.swing.event.ChangeListener;

/**
 * This interface is implemented by all proxy bean objects. Interfaces annotated
 * with {@link ProxyBean} don't need to implement this interface; instances of
 * the proxy bean interface can just be cast to this one. Proxy beans can,
 * however, implement it if they wish their users to be able to use the methods
 * in this interface without having to cast.
 * 
 * @author Alexander Boyd
 * 
 */
public interface ProxyObject
{
    /**
     * Adds a listener that will be notified whenever any of this object's
     * properties are changed. The listener will also be notified when any of
     * this object's StoredLists are changed.
     * 
     * @param listener
     *            The listener to add
     */
    public void addChangeListener(ChangeListener listener);
    
    /**
     * Removes a change listener previously added with
     * {@link #addChangeListener(ChangeListener)}.
     * 
     * @param listener
     */
    public void removeChangeListener(ChangeListener listener);
}
