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
    private Class targetClass;
    private int id;
    
    StoredList(Class targetClass, int id)
    {
        this.targetClass = targetClass;
        this.id = id;
    }
    
    @Override
    public T get(int index)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void add(int index, T element)
    {
        // TODO Auto-generated method stub
        super.add(index, element);
    }
    
    @Override
    public T remove(int index)
    {
        // TODO Auto-generated method stub
        return super.remove(index);
    }
    
    @Override
    public T set(int index, T element)
    {
        // TODO Auto-generated method stub
        return super.set(index, element);
    }
    
    @Override
    public int size()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
}
