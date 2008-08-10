/**
 * 
 */
package net.sf.opengroove.client.com;

public interface Notifier<T>
{
    
    public void notify(T listener);
    
}