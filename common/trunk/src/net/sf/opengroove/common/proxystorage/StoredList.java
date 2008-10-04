package net.sf.opengroove.common.proxystorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    /**
     * The class of the objects that are members of this list.
     * {@link Class#isAnnotationPresent(Class) targetClass.isAnnotationPresent}
     * should always return true.
     */
    private Class targetClass;
    /**
     * The id of this list, which is used to get and set it's items in the
     * proxystorage_collections table.
     */
    private int id;
    private ProxyStorage storage;
    
    StoredList(ProxyStorage storage, Class targetClass,
        int id)
    {
        this.targetClass = targetClass;
        this.id = id;
        this.storage = storage;
    }
    
    @Override
    public T get(int index)
    {
        try
        {
            PreparedStatement st = storage.connection
                .prepareStatement("select value from proxystorage_collections where id = ? and index ");
            ResultSet rs = st.executeQuery();
            if (!rs.next())
            {
                rs.close();
                st.close();
                throw new IndexOutOfBoundsException(
                    "The index "
                        + index
                        + " is not within the allowed bounds for this "
                        + "list. The list's id is " + id);
            }
            long ref = rs.getLong("value");
            rs.close();
            st.close();
            Object result = storage.getById(ref,
                targetClass);
            if (result == null)
            {
                throw new IllegalStateException(
                    "The object at index " + index
                        + " with id " + ref
                        + " has been removed from the "
                        + "database. This list's id is "
                        + id);
            }
            return (T) result;
        }
        catch (Exception e)
        {
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            throw new IllegalStateException(
                "An exception was encountered while performing the "
                    + "requested operation.", e);
        }
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
        try
        {
            PreparedStatement st = storage.connection
                .prepareStatement("select count(*) from proxystorage_collections where id = ?");
            ResultSet rs = st.executeQuery();
            boolean hasNext = rs.next();
            int count = 0;
            if(hasNext)
                count = rs.getInt(1);
            
        }
        catch (Exception e)
        {
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            throw new IllegalStateException(
                "An exception was encountered while performing the "
                    + "requested operation.", e);
        }
    }
    
}
