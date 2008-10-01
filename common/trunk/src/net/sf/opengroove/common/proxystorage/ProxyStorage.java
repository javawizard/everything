package net.sf.opengroove.common.proxystorage;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The ProxyStorage class is a class used for storing simple java beans to disk,
 * in such a way that updating one instance of a particular persistant bean
 * causes all other instances within the JVM to immediately reflect the new
 * state. ProxyStorage is intended to be a replacement for Java Persistence API,
 * when JPA is just too heavyweight.
 * 
 * Each ProxyStorage instance has a root object. The root object is the single
 * object to be persisted. It should have various fields that other objects can
 * be placed on to make them persistant.
 * 
 * When an object is created, it is entered into the proxy storage system with
 * no parent. It can then be assigned to fields of other persistent objects as
 * necessary. Objects that are in the database but which do not have the storage
 * root as an ancestor are removed upon calling the purge method of
 * ProxyStorage. The purge method generally should only be called right after
 * the proxy storage is created but before it goes into use, as it will remove
 * any objects that are not currently in the tree of objects, which could
 * include a newly-created object that hasn't been assigned to the tree yet.
 * 
 * @author Alexander Boyd
 * 
 * @param <E>
 *            The class of the root of the storage.
 */
public class ProxyStorage<E>
{
    private Connection connection;
    
    private Class<E> root;
    /**
     * Internal classes that execute sequences of queries to the database
     * synchronize on this object first to avoid getting corrupt data.
     */
    private final Object lock = new Object();
    
    public ProxyStorage(Class<E> root, File location)
        throws SQLException
    {
        try
        {
            Class.forName("org.h2.Driver");
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalStateException(
                "The H2Database jar is not on your classpath. H2 is "
                    + "used as the backend for ProxyStorage, "
                    + "and so is required.", e);
        }
        connection = DriverManager.getConnection("jdbc:h2:"
            + location.getPath());
    }
}
