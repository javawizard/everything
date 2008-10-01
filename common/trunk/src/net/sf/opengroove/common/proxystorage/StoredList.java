package net.sf.opengroove.common.proxystorage;

import java.util.AbstractList;

/**
 * A StoredList is a list that is stored in the ProxyStorage system. Read and
 * write operations all delegate to the database.
 * 
 * @author Alexander Boyd
 * 
 * @param <T>
 */
public class StoredList<T> extends AbstractList<T>
{
    StoredList()
    {
        
    }
    
    @Override
    public T get(int index)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public int size()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
}
