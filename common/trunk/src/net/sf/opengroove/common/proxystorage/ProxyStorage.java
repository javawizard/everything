package net.sf.opengroove.common.proxystorage;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.opengroove.client.OpenGroove;
import net.sf.opengroove.common.utils.Progress;

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
    /**
     * The connection to the database. This must be package-private instead of
     * just private since StoredList uses it.
     */
    Connection connection;
    
    private DatabaseMetaData dbInfo;
    
    private Class<E> rootClass;
    private ArrayList<Class> allClasses = new ArrayList<Class>();
    /**
     * Internal classes that execute sequences of queries to the database
     * synchronize on this object first to avoid getting corrupt data.<br/><br/>
     * 
     * This class is package-private (instead of private or protected) because
     * StoredList acesses it.
     */
    final Object lock = new Object();
    
    public ProxyStorage(Class<E> rootClass, File location)
        throws SQLException
    {
        this.rootClass = rootClass;
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
            + location.getPath(), "sa", "");
        dbInfo = connection.getMetaData();
        checkSystemTables();
        checkTables(rootClass, allClasses);
        vacuum(new Progress());
    }
    
    /**
     * Checks to make sure that the proxy storage system tables (such as
     * proxystorage_statics and proxystorage_collections) are present. If they
     * are not, they are created. Additionally, if the proxystorage_statics
     * table does not contain a "sequencer" static, one is created with an
     * initial value of 1.
     * 
     * @throws SQLException
     */
    private void checkSystemTables() throws SQLException
    {
        ArrayList<String> tables = getTableNames();
        if (!tables.contains("proxystorage_statics"))
        {
            createEmptyTable("proxystorage_statics");
        }
        setTableColumns(
            "proxystorage_statics",
            new TableColumn[] {
                new TableColumn("name", Types.VARCHAR, 256),
                new TableColumn("value", Types.BIGINT, 0) });
        if (!tables.contains("proxystorage_collections"))
        {
            createEmptyTable("proxystorage_collections");
        }
        setTableColumns("proxystorage_collections",
            new TableColumn[] {
                new TableColumn("id", Types.BIGINT, 0),
                new TableColumn("index", Types.INTEGER, 0),
                new TableColumn("value", Types.BIGINT, 0) });
        PreparedStatement st = connection
            .prepareStatement("select name from proxystorage_statics where name = 'sequencer'");
        ResultSet rs = st.executeQuery();
        if (!rs.next())
        {
            execute("insert into proxystorage_statics values (\'sequencer\', 1)");
        }
        rs.close();
        st.close();
    }
    
    /**
     * Creates a table with the name specified, and no columns.
     * 
     * @param name
     *            The name of the table to create
     */
    private void createEmptyTable(String name)
        throws SQLException
    {
        PreparedStatement statement = connection
            .prepareStatement("create table " + name
                + " ()");
        statement.execute();
        statement.close();
    }
    
    /**
     * Executes the string specified as an SQL statement, discarding it's result
     * set if one is made available.
     * 
     * @param sql
     * @return
     * @throws SQLException
     */
    void execute(String sql) throws SQLException
    {
        PreparedStatement st = connection
            .prepareStatement(sql);
        try
        {
            st.execute();
        }
        finally
        {
            st.close();
        }
    }
    
    /**
     * Ensures that the table specified has exactly the columns specified.
     * Currently, the type of an existing column is not checked. If a column is
     * not present on the table specified but present in this list, then it will
     * be added via an "alter table add column" statement; if a column exists on
     * the table but not in this list, it will be removed via an "alter table
     * drop column" statement.
     * 
     * @throws SQLException
     */
    private void setTableColumns(String tableName,
        TableColumn[] lc) throws SQLException
    {
        ArrayList<TableColumn> existing = getTableColumns(tableName);
        List<TableColumn> columns = Arrays.asList(lc);
        /*
         * First, get rid of existing ones that aren't here now
         */
        for (TableColumn column : existing)
        {
            if (!columns.contains(column))
            {
                /*
                 * We need to remove the column
                 */
                PreparedStatement st = connection
                    .prepareStatement("alter table "
                        + tableName + " drop column "
                        + column.getName());
                st.execute();
                st.close();
            }
        }
        for (TableColumn column : columns)
        {
            if (!existing.contains(column))
            {
                /*
                 * We need to add the column
                 */
                PreparedStatement st = connection
                    .prepareStatement("alter table "
                        + tableName
                        + " add column "
                        + column.getName()
                        + " "
                        + getStringDataType(column
                            .getType(), column.getSize()));
                st.execute();
                st.close();
            }
        }
    }
    
    /**
     * Returns a string value that can be used to represent this type within an
     * SQL statement. For example, if type is {@link Types#BIGINT}, then the
     * returned string would be "bigint" (in this case size is not used), and if
     * the type was {@link Types#VARCHAR} and the size was 1234, then the
     * returned string would be "varchar(1234)".
     * 
     * @param type
     *            The type, as defined in {@link Types}
     * @param size
     *            The size of the data type, if the type is char or varchar
     * @return A string representing the data type
     * @throws SQLException
     *             if an sql exception occurs while accessing the database
     * @throws IllegalArgumentException
     *             if the type specified is not supported by the database
     */
    private String getStringDataType(int type, int size)
        throws SQLException
    {
        ResultSet rs = dbInfo.getTypeInfo();
        try
        {
            while (rs.next())
            {
                if (rs.getInt("DATA_TYPE") == type)
                {
                    String typeName = rs
                        .getString("TYPE_NAME");
                    if (type == Types.VARCHAR)
                        typeName += "(" + size + ")";
                    return typeName;
                }
            }
            throw new IllegalArgumentException(
                "That type is not supported by the db");
        }
        finally
        {
            rs.close();
        }
    }
    
    private ArrayList<TableColumn> getTableColumns(
        String tableName) throws SQLException
    {
        ResultSet rs = dbInfo.getColumns(null, null,
            tableName.toUpperCase(), null);
        ArrayList<TableColumn> results = new ArrayList<TableColumn>();
        while (rs.next())
        {
            results.add(new TableColumn(rs
                .getString("COLUMN_NAME"), rs
                .getInt("DATA_TYPE"), rs
                .getInt("COLUMN_SIZE")));
        }
        return results;
    }
    
    private ArrayList<String> getTableNames()
        throws SQLException
    {
        ResultSet rs = dbInfo.getTables(null, null, null,
            null);
        ArrayList<String> results = new CaseInsensitiveCheckList();
        while (rs.next())
        {
            results.add(rs.getString("TABLE_NAME")
                .toUpperCase());
        }
        rs.close();
        return results;
    }
    
    /**
     * Removes all objects that do not have the root as an ancestor. If there is
     * no current root, then this does nothing.
     * 
     * This method uses a mark-and-sweep algorithm to remove objects. It should
     * therefore not be called while the ProxyStorage object is in use to avoid
     * losing data.
     * 
     * @param progress
     *            A progress object that will be updated as this vacuum
     *            operation runs
     */
    private void vacuum(Progress progress)
    {
        /*
         * We need to get all objects out of the db and scan for them. Objects
         * reside in all of the tables for allClasses (a field), and in
         * proxystorage_collections.
         * 
         * The fastest way to do this (and the way we do it here) is to find the
         * root node and trace down through all proxy bean references and all
         * stored list references, and the lists' corresponding proxy bean
         * references. We use a recursive method to do that. We then have a map
         * of object types to lists, where each of those lists contains a list
         * of referenced objects. Then, we do another hierarchy pass, this time
         * checking each object to see if it is on the list that we built. If it
         * is not, we remove it. This algorithm results in only two passes being
         * needed, one for determining whether objects are referenced, and the
         * other for removing unreferenced objects.
         * 
         * In the future, we may want to consider building the list in a
         * temporary database table in case the data is too large to be held in
         * memory, and to speed up reference searches.
         * 
         * During the iteration, we'll use a HashSet instead of an ArrayList,
         * due to the fact that testing to see if a HashSet contains a
         * particular id is orders of magnitude faster than testing to see if an
         * ArrayList contains a particular id.
         */
        ProxyObject root = (ProxyObject) getRoot(false);
        if (root == null)
        {
            progress.set(1);
            return;
        }
        HashMap<Class, HashSet<Long>> refs = new HashMap<Class, HashSet<Long>>();
        try
        {
            buildReferenceList(refs, root
                .getProxyStorageId(), rootClass, null);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        /*
         * We now have all of the referenced objects. Now we iterate over the
         * list of classes returned, and check each object in the corresponding
         * table to see if it's referenced in the set. If it's not, we'll remove
         * it. For the class StoredList, we'll iterate over
         * proxystorage_collections and use the "id" column instead of the
         * "proxystorage_id" column.
         */
        ArrayList<Class> toIterate = new ArrayList<Class>();
        toIterate.addAll(allClasses);
        toIterate.add(StoredList.class);
        for (Class c : toIterate)
        {
            Set<Long> set = refs.get(c);
            if (set == null)
                /*
                 * None of the objects of this class are referenced.
                 */
                set = new HashSet<Long>();
            String tableName;
            if (c == StoredList.class)
                tableName = "proxystorage_collections";
            else
                tableName = getTargetTableName(c);
            String idColumn = (c == StoredList.class) ? "id"
                : "proxystorage_id";
            try
            {
                PreparedStatement lst = connection
                    .prepareStatement("select "
                        + idColumn
                        + " from "
                        + tableName
                        + " where not "
                        + idColumn
                        + " in ( "
                        + delimited(set
                            .toArray(new Long[0]),
                            new ToString<Long>()
                            {
                                
                                @Override
                                public String toString(
                                    Long object)
                                {
                                    return ""
                                        + object
                                            .longValue();
                                }
                            }, ",") + ")");
                ResultSet rst = lst.executeQuery();
                ArrayList<Long> toRemove = new ArrayList<Long>();
                while (rst.next())
                {
                    toRemove.add(rst.getLong(0));
                }
                rst.close();
                lst.close();
                for (long removeId : toRemove)
                {
                    PreparedStatement st = connection
                        .prepareStatement("delete from "
                            + tableName + " where "
                            + idColumn + " = ?");
                    st.setLong(1, removeId);
                    st.execute();
                    st.close();
                }
            }
            catch (SQLException e)
            {
                RuntimeException exception = new RuntimeException(
                    "An error occured while scanning class table "
                        + tableName);
            }
        }
    }
    
    /**
     * Builds a list of all referenced entities, beginning with the id
     * specified. This method is recursive. For list types, the class should be
     * StoredList.
     * 
     * @param list
     * @param id
     * @param c
     * @param subtype
     *            The target class of the storedlist. If <code>c</code> is not
     *            StoredList.class, then this is unused.
     */
    private void buildReferenceList(
        HashMap<Class, HashSet<Long>> list, long id,
        Class c, Class subtype) throws SQLException
    {
        Object object;
        if (c != StoredList.class)
            object = getById(id, c);
        else
            object = new StoredList(ProxyStorage.this,
                subtype, id);
        HashSet<Long> set = list.get(c);
        if (set == null)
        {
            set = new HashSet();
            list.put(c, set);
        }
        if (set.contains(id))
            return;
        set.add(id);
        /*
         * We've added the id to the set. Now we need to recurse on this
         * element's children. If the element is a stored list, we recurse on
         * each element in it. If the element is a proxy bean, we recurse on all
         * methods that are annotated with Property and who's return type is
         * annotated with ProxyBean.
         */
        if (c == StoredList.class)
        {
            StoredList storedList = (StoredList) object;
            int size = storedList.size();
            for (int i = 0; i < size; i++)
            {
                ProxyObject result = (ProxyObject) storedList
                    .get(i);
                buildReferenceList(list, result
                    .getProxyStorageId(), subtype, null);
            }
        }
        else
        {
            /*
             * This is a regular old proxy bean. We'll scan through all methods
             * as discussed above.
             */
            for (Method method : getGetterMethods(c))
            {
                if (method.getReturnType()
                    .isAnnotationPresent(ProxyBean.class))
                {
                    /*
                     * The method is a proxy bean. Now we'll run it and get the
                     * id back.
                     */
                    try
                    {
                        ProxyObject result = (ProxyObject) method
                            .invoke(object, new Object[0]);
                        /*
                         * result is now the subobject that we want to scan.
                         */
                        buildReferenceList(list, result
                            .getProxyStorageId(), method
                            .getReturnType(), null);
                    }
                    catch (Exception e)
                    {
                        /*
                         * shoudn't happen
                         */
                        throw new RuntimeException(e);
                    }
                }
                else
                {
                    /*
                     * This method is a stored list.
                     */
                    try
                    {
                        StoredList result = (StoredList) method
                            .invoke(object, new Object[0]);
                        /*
                         * result is now the subobject that we want to scan.
                         */
                        buildReferenceList(list, result
                            .getProxyStorageId(),
                            StoredList.class, method
                                .getAnnotation(
                                    ListType.class).value());
                    }
                    catch (Exception e)
                    {
                        /*
                         * shoudn't happen
                         */
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
    
    /**
     * Checks to see if a table for the class specified is present, and checks
     * to see if tables are present for the classes's properties. If the tables
     * aren't present, they are created. If they are missing a column, the
     * column is added. If they have extra columns, the extra columns are
     * removed.
     * 
     * @throws SQLException
     */
    private void checkTables(Class checkClass,
        ArrayList<Class> alreadyChecked)
        throws SQLException
    {
        if (alreadyChecked.contains(checkClass))
            return;
        alreadyChecked.add(checkClass);
        /*
         * We use the alreadyChecked list here to make sure that a class won't
         * get checked twice (and result in an infinite loop) if there is a
         * circular loop in property class referencing (IE if, for example, a
         * contact references a contact computer and the contact computer
         * references it's owning contact)
         */
        String tableName = getTargetTableName(checkClass);
        checkTableExists(tableName);
        ArrayList<TableColumn> columns = getTargetColumns(checkClass);
        setTableColumns(tableName, columns
            .toArray(new TableColumn[0]));
        for (Method method : getGetterMethods(checkClass))
        {
            if (method.getReturnType().isAnnotationPresent(
                ProxyBean.class))
                checkTables(method.getReturnType(),
                    alreadyChecked);
            else if (method.getReturnType() == StoredList.class)
            {
                if (!method
                    .isAnnotationPresent(ListType.class))
                {
                    throw new IllegalArgumentException(
                        "The property with the getter "
                            + method.getName()
                            + " on the class "
                            + checkClass.getName()
                            + " is a StoredList, but it's parameter type "
                            + "is not specified with a ListType annotation.");
                }
                ListType listTypeAnnotation = method
                    .getAnnotation(ListType.class);
                if (!listTypeAnnotation.value()
                    .isAnnotationPresent(ProxyBean.class))
                    throw new IllegalArgumentException(
                        "The property with the getter "
                            + method.getName()
                            + " on the class "
                            + checkClass.getName()
                            + " is a StoredList, but it's parameter type ("
                            + listTypeAnnotation.value()
                                .getName()
                            + ") does "
                            + "not carry the ProxyBean annotation. If you were trying "
                            + "to create a list of a primitive type wrapper or "
                            + "a list of String, consider wrapping them in ProxyBean-annotated "
                            + "objects that have only one property.");
                checkTables(listTypeAnnotation.value(),
                    alreadyChecked);
            }
        }
    }
    
    private Method[] getGetterMethods(Class checkClass)
    {
        ArrayList<Method> results = new ArrayList<Method>();
        Method[] methods = checkClass.getMethods();
        for (Method method : methods)
        {
            if (!method.isAnnotationPresent(Property.class))
                continue;
            if (!(method.getName().startsWith("get") || method
                .getName().startsWith("is")))
                continue;
            results.add(method);
        }
        return results.toArray(new Method[0]);
    }
    
    /**
     * Returns a list of columns that should be in the table for the class
     * specified. There is a column called proxystorage_id that will be
     * included, and then one column per property annotated with Property.
     * 
     * @param checkClass
     *            The class to check
     * @return A list of columns that should exist for the class
     */
    private ArrayList<TableColumn> getTargetColumns(
        Class checkClass)
    {
        ArrayList<TableColumn> columns = new ArrayList<TableColumn>();
        columns.add(new TableColumn("proxystorage_id",
            Types.BIGINT, 0));
        for (Method method : getGetterMethods(checkClass))
        {
            String methodName = method.getName();
            String propertyName = propertyNameFromAccessor(methodName);
            Class propertyClass = method.getReturnType();
            int type;
            int size = 0;
            if (propertyClass == Long.TYPE
                || propertyClass == Long.class)
                /*
                 * Identity-equals checking is ok here, since there will never
                 * be more than one class object at a time that represents the
                 * same class
                 */
                type = Types.BIGINT;
            else if (propertyClass == Integer.TYPE
                || propertyClass == Integer.class)
                type = Types.INTEGER;
            else if (propertyClass == Boolean.TYPE
                || propertyClass == Boolean.class)
                type = Types.BOOLEAN;
            else if (propertyClass == String.class
                || propertyClass == BigInteger.class)
            {
                type = Types.VARCHAR;
                if (method
                    .isAnnotationPresent(Length.class))
                {
                    size = ((Length) method
                        .getAnnotation(Length.class))
                        .value();
                }
                else
                {
                    size = 1024;
                }
            }
            else if (propertyClass == StoredList.class)
            {
                /*
                 * The value should be a bigint or a long that holds the id of
                 * the list
                 */
                type = Types.BIGINT;
            }
            else if (Collection.class
                .isAssignableFrom(propertyClass))
            {
                /*
                 * Collections aren't allowed (the user should use StoredList
                 * instead). The main reason to have this here is so that the
                 * user gets informed that they can use StoredList in place of
                 * collections, instead of the user not knowing how they are
                 * supposed to use a type of list.
                 */
                throw new IllegalArgumentException(
                    "The class "
                        + propertyClass.getName()
                        + " contains a property ("
                        + propertyName
                        + ") which "
                        + "is a Java Collection. Java Collection implementations "
                        + "themselves aren't supported. You can, however, "
                        + "use a "
                        + StoredList.class.getName());
            }
            else if (propertyClass
                .isAnnotationPresent(ProxyBean.class))
            {
                /*
                 * The property is another proxy bean. The type of the column,
                 * then, should be a long, or a bigint, which will hold the id
                 * of the referenced bean.
                 */
                type = Types.BIGINT;
            }
            else
                throw new RuntimeException("The class "
                    + propertyClass.getName()
                    + " contains a property ("
                    + propertyName
                    + ") which is not of a valid type.");
            columns.add(new TableColumn(propertyName, type,
                size));
        }
        return columns;
    }
    
    private String propertyNameFromAccessor(
        String methodName)
    {
        String propertyName = methodName.startsWith("is") ? methodName
            .substring("is".length())
            : methodName.substring("get".length());
        propertyName = Introspector
            .decapitalize(propertyName);
        return propertyName;
    }
    
    /**
     * If the table specified does not exist, creates it as a table with no
     * columns. If the table specified does exist, it is not modified.
     * 
     * @param tableName
     *            The name of the table to check for
     * @throws SQLException
     */
    private void checkTableExists(String tableName)
        throws SQLException
    {
        if (!getTableNames().contains(tableName))
            createEmptyTable(tableName);
    }
    
    /**
     * Gets the name of the table that the class specified should store it's
     * information in. This is usually the non-qualified name of the class,
     * unless it has a Table annotation, in which case the table is the value of
     * that annotation.
     * 
     * @param checkClass
     * @return
     */
    private String getTargetTableName(Class checkClass)
    {
        if (checkClass.getAnnotation(Table.class) != null)
            return ((Table) checkClass
                .getAnnotation(Table.class)).getValue();
        return checkClass.getSimpleName().toLowerCase();
    }
    
    /**
     * Creates a new instance of the proxy bean interface specified. The
     * interface must be annotated with {@link ProxyBean}, and must be a type
     * that is part of the ownership tree of which <code>E</code> must be the
     * root.<br/><br/>
     * 
     * The new instance will be persisted in the database, but will not be owned
     * by anything, so a call to vacuum() would remove it until it is assigned
     * to another object that is in the tree.
     * 
     * @param c
     *            The class of the interface to create a new instance of.
     * @return The new instance.
     */
    public <T> T create(Class<T> c)
    {
        if (!allClasses.contains(c))
            throw new IllegalArgumentException(
                "The class specified ("
                    + c.getName()
                    + ") is not a class in the root "
                    + "tree of this proxy storage instance. "
                    + "(the root class is "
                    + rootClass.getName() + ")");
        try
        {
            ArrayList<TableColumn> columns = getTableColumns(getTargetTableName(c));
            String statement = "insert into "
                + getTargetTableName(c)
                + "( "
                + delimited(columns
                    .toArray(new TableColumn[0]),
                    new ToString<TableColumn>()
                    {
                        
                        @Override
                        public String toString(
                            TableColumn object)
                        {
                            return object.getName();
                        }
                    }, ",")
                + " ) values ( "
                + delimited(columns
                    .toArray(new TableColumn[0]),
                    new ToString<TableColumn>()
                    {
                        
                        @Override
                        public String toString(
                            TableColumn object)
                        {
                            return "?";
                        }
                    }, ",") + ")";
            PreparedStatement st = connection
                .prepareStatement(statement);
            int index = 0;
            long newId = nextId();
            for (TableColumn col : columns)
            {
                index += 1;
                if (col.getName().equalsIgnoreCase(
                    "proxystorage_id"))
                    st.setLong(index, newId);
                else
                {
                    st.setNull(index, col.getType());
                }
            }
            st.execute();
            st.close();
            return (T) getById(newId, c);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static <T> String delimited(T[] items,
        ToString<T> generator, String delimiter)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.length; i++)
        {
            if (i != 0)
                sb.append(delimiter);
            sb.append(generator.toString(items[i]));
        }
        return sb.toString();
    }
    
    public interface ToString<S>
    {
        public String toString(S object);
    }
    
    /**
     * Gets an object that has the specified id and is of the specified type.
     * This method doesn't handle StoredLists; it only handles proxy beans.
     * 
     * @param id
     *            The id of the object
     * @param c
     *            The class of the object
     * @return A new object that represents the id specified. If the object does
     *         not exist, null will be returned. The object returned implements
     *         the interface defined by <code>c</code>, as well as
     *         {@link ProxyObject}.
     * @throws SQLException
     */
    Object getById(long id, Class c) throws SQLException
    {
        if (!isTargetIdPresent(id, c))
            return null;
        return Proxy.newProxyInstance(getClass()
            .getClassLoader(), new Class[] { c,
            ProxyObject.class }, new ObjectHandler(c, id));
    }
    
    /**
     * Checks to see if the id specified refers to a valid row in the table for
     * the target specified.
     * 
     * @param id
     * @param c
     * @return
     * @throws SQLException
     */
    private boolean isTargetIdPresent(long id, Class c)
        throws SQLException
    {
        synchronized (lock)
        {
            PreparedStatement st = connection
                .prepareStatement("select count(*) from "
                    + getTargetTableName(c)
                    + " where proxystorage_id = ?");
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            rs.next();
            boolean exists = rs.getInt(1) > 0;
            rs.close();
            st.close();
            return exists;
        }
    }
    
    /**
     * Gets the root of this ProxyStorage instance. When this is called for the
     * first time in the life of a proxy storage location, a new instance will
     * be created via {@link #create(Class)}. All of the other invocations will
     * return an object that represents the root.
     * 
     * @return
     */
    
    public E getRoot()
    {
        return getRoot(true);
    }
    
    /**
     * Gets the root object. If createIfNonexistant is false, then null is
     * returned if this doesn't yet have a root. If it's true, then a new root
     * is created and returned.
     * 
     * @param createIfNonexistant
     * @return
     */
    public E getRoot(boolean createIfNonexistant)
    {
        try
        {
            PreparedStatement rst = connection
                .prepareStatement("select value from proxystorage_statics where name = ?");
            rst.setString(1, "root");
            ResultSet rs = rst.executeQuery();
            boolean hasNext = rs.next();
            long existingId = 0;
            if (hasNext)
                existingId = rs.getLong("value");
            rs.close();
            rst.close();
            if (hasNext)
            {
                return (E) getById(existingId, rootClass);
            }
            if (!createIfNonexistant)
                return null;
            /*
             * If we're here then the root doesn't exist yet. We'll create a new
             * root with create(), stick it's id into the statics table, and
             * return it.
             */
            Object newObject = create(rootClass);
            long newId = ((ProxyObject) newObject)
                .getProxyStorageId();
            PreparedStatement st = connection
                .prepareStatement("insert into proxystorage_statics values (?,?)");
            st.setString(1, "root");
            st.setLong(2, newId);
            st.execute();
            st.close();
            return (E) newObject;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(
                "An error occured while performing the requested operation.",
                e);
        }
    }
    
    /**
     * The class that actually handles calls to proxy bean methods. Instances of
     * proxy beans that are created use an instance of this as their invocation
     * handler.
     * 
     * @author Alexander Boyd
     * 
     */
    private class ObjectHandler implements
        InvocationHandler
    {
        private Class targetClass;
        private long targetId;
        
        public ObjectHandler(Class targetClass,
            long targetId)
        {
            super();
            this.targetClass = targetClass;
            this.targetId = targetId;
        }
        
        @Override
        public Object invoke(Object proxy, Method method,
            Object[] args) throws Throwable
        {
            synchronized (lock)
            {
                if (method.getName().equalsIgnoreCase(
                    "getProxyStorageId")
                    && method.getReturnType() == Long.TYPE)
                    return targetId;
                if (method.getName().equalsIgnoreCase(
                    "getProxyStorageClass")
                    && method.getReturnType() == Class.class)
                    return targetClass;
                if (method.getName().equalsIgnoreCase(
                    "isProxyStoragePresent")
                    && method.getReturnType() == Boolean.TYPE)
                    return isTargetIdPresent(targetId,
                        targetClass);
                if (method.getName().equalsIgnoreCase(
                    "equals")
                    && args.length == 1)
                {
                    System.out.println("equals called");
                    Object compare = args[0];
                    if (!(compare instanceof ProxyObject))
                        return false;
                    ProxyObject object = (ProxyObject) compare;
                    System.out.println("local id "
                        + targetId + ", remote id "
                        + object.getProxyStorageId());
                    long objectId = object
                        .getProxyStorageId();
                    return objectId == targetId;
                }
                if (method.getName().equalsIgnoreCase(
                    "hashCode")
                    && args.length == 0)
                {
                    return (int) targetId * 31;
                }
                if (isPropertyMethod(method))
                {
                    if (method.getName().startsWith("get")
                        || method.getName()
                            .startsWith("is"))
                    {
                        /*
                         * This method is a getter. We'll create a query to get
                         * the resulting column out of the database, and then
                         * convert the value into an object that can be returned
                         * from this method.
                         */
                        String propertyName = propertyNameFromAccessor(method
                            .getName());
                        PreparedStatement st = connection
                            .prepareStatement("select "
                                + propertyName
                                + " from "
                                + getTargetTableName(targetClass)
                                + " where proxystorage_id = ?");
                        st.setLong(1, targetId);
                        ResultSet rs = st.executeQuery();
                        boolean isPresent = rs.next();
                        if (!isPresent)
                        {
                            rs.close();
                            st.close();
                            throw new IllegalStateException(
                                "The object that was queried has been deleted "
                                    + "from the database.");
                        }
                        Object result = rs
                            .getObject(propertyName);
                        rs.close();
                        st.close();
                        if (method.getReturnType() == Integer.TYPE
                            || method.getReturnType() == Integer.class
                            || method.getReturnType() == Long.TYPE
                            || method.getReturnType() == Long.class
                            || method.getReturnType() == Boolean.TYPE
                            || method.getReturnType() == Boolean.class
                            || method.getReturnType() == String.class)
                        {
                            if (result == null)
                            {
                                if (method.getReturnType() == Integer.TYPE)
                                    result = (int) 0;
                                if (method.getReturnType() == Long.TYPE)
                                    result = (long) 0;
                                if (method.getReturnType() == Boolean.TYPE)
                                    result = false;
                            }
                            return result;
                        }
                        if (method.getReturnType() == BigInteger.class)
                        {
                            if (result == null)
                                return null;
                            return new BigInteger(
                                ((String) result), 16);
                        }
                        if (method.getReturnType() == StoredList.class)
                        {
                            /*
                             * If a stored list is null, then a new one should
                             * be created. We don't actually have to modify the
                             * proxystorage_collections table to do this; we
                             * just need to generate a new id, backstore the id
                             * to the this object, and return a new stored list
                             * for it.
                             * 
                             * The object will be of type Long, so if it's not
                             * present, we create a new one and set it on this
                             * object. Then we create a new list off of the long
                             * and return it.
                             */
                            if (result == null)
                            {
                                /*
                                 * The result is null. We'll create the new list
                                 * now.
                                 */
                                result = new Long(nextId());
                                PreparedStatement ist = connection
                                    .prepareStatement("update "
                                        + getTargetTableName(targetClass)
                                        + " set "
                                        + propertyName
                                        + " = ? where proxystorage_id = ?");
                                ist.setLong(1,
                                    (Long) result);
                                ist.setLong(2, targetId);
                                ist.execute();
                                ist.close();
                            }
                            return new StoredList(
                                ProxyStorage.this,
                                ((ListType) method
                                    .getAnnotation(ListType.class))
                                    .value(), (Long) result);
                        }
                        if (method.getReturnType()
                            .isAnnotationPresent(
                                ProxyBean.class))
                        {
                            if (result == null)
                                return null;
                            return getById((Long) result,
                                method.getReturnType());
                        }
                        throw new IllegalArgumentException(
                            "The method is a getter, but it's return "
                                + "type is not a proper type.");
                    }
                    else
                    {
                        /*
                         * The property is a setter.
                         */
                        String propertyName = propertyNameFromAccessor(method
                            .getName());
                        PreparedStatement st = connection
                            .prepareStatement("update "
                                + getTargetTableName(targetClass)
                                + " set "
                                + propertyName
                                + " = ? where proxystorage_id = ?");
                        st.setLong(2, targetId);
                        Object inputObject = args[0];
                        if (inputObject.getClass() == StoredList.class)
                        {
                            throw new IllegalArgumentException(
                                "Setters for stored lists are not allowed.");
                        }
                        if (inputObject.getClass() == BigInteger.class)
                        {
                            inputObject = ((BigInteger) inputObject)
                                .toString(16);
                        }
                        if (inputObject instanceof ProxyObject)
                        {
                            inputObject = new Long(
                                ((ProxyObject) inputObject)
                                    .getProxyStorageId());
                        }
                        st.setObject(1, inputObject);
                        st.execute();
                        st.close();
                    }
                }
                /*
                 * TODO: The method isn't a property method. What we want to do
                 * in the future is allow the creator of this proxy storage
                 * instance to specify an invocation handler that is delegated
                 * to if a particular method doesn't exist. We also want to
                 * allow additional methods, such as search methods, to be
                 * added. A search method would take (via annotations) the name
                 * of the property that is a stored list on the object that the
                 * search method is declared on, and a property within the type
                 * of the stored list's children to search for, and, if the
                 * property is a string, if like is to be used instead of =. The
                 * return type of the search method could either be an array of
                 * the object that the stored list contains (in which case all
                 * matches will be returned), or a single instance of that
                 * object, in which case the first match will be returned or
                 * null if there wasn't a match.
                 */
                return null;
            }
        }
    }
    
    /**
     * Gets the next id to use. The sequencer row in proxystorage_statics is
     * incremented, and the new value returned.
     * 
     * @return A new id to use
     * @throws SQLException
     */
    long nextId() throws SQLException
    {
        synchronized (lock)
        {
            PreparedStatement ist = connection
                .prepareStatement("update proxystorage_statics set value = value + 1 where name = ?");
            ist.setString(1, "sequencer");
            ist.execute();
            ist.close();
            PreparedStatement rst = connection
                .prepareStatement("select value from proxystorage_statics where name = ?");
            rst.setString(1, "sequencer");
            ResultSet rs = rst.executeQuery();
            rs.next();
            long newId = rs.getLong(1);
            rs.close();
            rst.close();
            return newId;
        }
    }
    
    /**
     * Returns true if the method is annotated with Property, or if the method
     * starts with "set" and the corresponding "get" method is annotated with
     * Property.
     * 
     * @param method
     * @return
     */
    private boolean isPropertyMethod(Method method)
    {
        if (method.isAnnotationPresent(Property.class))
            return true;
        if (!(method.getName().startsWith("is")
            || method.getName().startsWith("get") || method
            .getName().startsWith("set")))
            return false;
        String propertyName = propertyNameFromAccessor(method
            .getName());
        String capitalized = propertyName.substring(0, 1)
            .toUpperCase()
            + propertyName.substring(1);
        Method getter;
        try
        {
            getter = method.getDeclaringClass().getMethod(
                "get" + capitalized, new Class[0]);
        }
        catch (NoSuchMethodException e)
        {
            try
            {
                getter = method.getDeclaringClass()
                    .getMethod("is" + capitalized,
                        new Class[0]);
            }
            catch (NoSuchMethodException e2)
            {
                return false;
            }
        }
        if (!getter.isAnnotationPresent(Property.class))
            return false;
        return true;
    }
}
