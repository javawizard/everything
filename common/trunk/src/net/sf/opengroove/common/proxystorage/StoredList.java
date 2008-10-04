package net.sf.opengroove.common.proxystorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        synchronized (storage.lock)
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
                            + "list. The list's id is "
                            + id);
                }
                long ref = rs.getLong("value");
                rs.close();
                st.close();
                Object result = storage.getById(ref,
                    targetClass);
                if (result == null)
                {
                    throw new IllegalStateException(
                        "The object at index "
                            + index
                            + " with id "
                            + ref
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
    }
    
    @Override
    public void add(int index, T element)
    {
        synchronized (storage.lock)
        {
            
        }
    }
    
    @Override
    public T remove(int index)
    {
        synchronized (storage.lock)
        {
            /*
             * Remove is a somewhat more complex operation than set, size, and
             * get, because it has to execute a statement for each object after
             * this one to decrement it's id by one.
             */
            try
            {
                T previous = get(index);
                /*
                 * the call to get(index) will take care of throwing an
                 * IndexOutOfBoundsException if the index specified does not
                 * reference a valid element of this list
                 */
                PreparedStatement rst = storage.connection
                    .prepareStatement("delete from proxystorage_collections "
                        + "where id = ? and index = ?");
                rst.execute();
                rst.close();
                /*
                 * The element has been removed. Now we need to shift all
                 * elements with an index greater than the one removed down one
                 * position.
                 */
                PreparedStatement dst = storage.connection
                    .prepareStatement("update proxystorage_collections"
                        + " set index = index - 1 "
                        + "where id = ? and index > ?");
                dst.setLong(1, id);
                dst.setInt(2, index);
                dst.execute();
                dst.close();
                return previous;
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
    
    @Override
    public T set(int index, T element)
    {
        synchronized (storage.lock)
        {
            if (!(element instanceof ProxyObject))
                throw new ClassCastException(
                    "The element specified (an instance of "
                        + element.getClass().getName()
                        + " is not a ProxyObject.");
            ProxyObject object = (ProxyObject) element;
            try
            {
                PreparedStatement cst = storage.connection
                    .prepareStatement("select count(*) from proxystorage_collections where id = ? and index = ?");
                cst.setLong(1, id);
                cst.setInt(2, index);
                ResultSet crs = cst.executeQuery();
                int existingCount = 0;
                if (crs.next())
                    existingCount = crs.getInt(1);
                crs.close();
                cst.close();
                if (existingCount == 0)
                    throw new IndexOutOfBoundsException(
                        "There is no element at index "
                            + index
                            + " in the list with id " + id);
                T previous = get(index);
                PreparedStatement st = storage.connection
                    .prepareStatement("update proxystorage_collections set value = ? where id = ? and index = ?");
                long insertId = object.getProxyStorageId();
                st.setLong(1, insertId);
                st.setLong(2, id);
                st.setInt(3, index);
                st.execute();
                st.close();
                return previous;
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
    
    @Override
    public int size()
    {
        synchronized (storage.lock)
        {
            try
            {
                PreparedStatement st = storage.connection
                    .prepareStatement("select count(*) from proxystorage_collections where id = ?");
                ResultSet rs = st.executeQuery();
                boolean hasNext = rs.next();
                int count = 0;
                if (hasNext)
                    count = rs.getInt(1);
                rs.close();
                st.close();
                return count;
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
    
}
