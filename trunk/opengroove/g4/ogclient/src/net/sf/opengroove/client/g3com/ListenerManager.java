package net.sf.opengroove.client.g3com;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class for managing event listeners. The object that uses this class can
 * create addAbcListener() and removeAbcListener() to this class, and then call
 * notify(Notifier) to notify the listeners
 * 
 * @author Alexander Boyd
 * 
 */
public class ListenerManager<T>
{
    private List<T> listeners = Collections
        .synchronizedList(new ArrayList<T>());
    
    public void addListener(T listener)
    {
        listeners.add(listener);
    }
    
    public void removeListener(T listener)
    {
        listeners.remove(listener);
    }
    
    public void add(T listener)
    {
        listeners.add(listener);
    }
    
    public void remove(T listener)
    {
        listeners.remove(listener);
    }
    
    public void notify(Notifier<T> notifier)
    {
        for (T listener : listeners)
        {
            notifier.notify(listener);
        }
    }
}
