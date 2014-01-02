/**
 * 
 */
package net.sf.opengroove.client.g3com;

/**
 * A class used in conjunction with ListenerManager. An instance of this class
 * is passed to the listener manager when the listener manager is to notify it's
 * listeners of events, and the {@link #notify()} method will be called once for
 * each listener.
 * 
 * @author Alexander Boyd
 * 
 * @param <T>
 */
public interface Notifier<T>
{
    /**
     * Notifies the listener specified.
     * 
     * @param listener
     *            The listener to notify.
     */
    public void notify(T listener);
    
}
