package net.sf.opengroove.common.proxystorage;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    
    private DatabaseMetaData dbInfo;
    
    private Class<E> rootClass;
    /**
     * Internal classes that execute sequences of queries to the database
     * synchronize on this object first to avoid getting corrupt data.
     */
    private final Object lock = new Object();
    
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
        checkTables(rootClass);
        vacuum();
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
     * Ensures that the table specified has exactly the columns specified.
     * Currently, the type of an existing column is not checked. If a column is
     * not present on the table specified but present in this list, then it will
     * be added via an "alter table" statement; if a column exists on the table
     * but not in this list, it will be similarly removed.
     * 
     * @param columns
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
                        + getStringDataType(column
                            .getType(), column.getSize()));
                st.execute();
                st.close();
            }
        }
    }
    
    private String getStringDataType(int type, int size)
    {
        dbInfo.getTypeInfo();
    }
    
    private ArrayList<TableColumn> getTableColumns(
        String tableName) throws SQLException
    {
        ResultSet rs = dbInfo.getColumns(null, null,
            tableName, null);
        ArrayList<TableColumn> results = new ArrayList<TableColumn>();
        while (rs.next())
        {
            results.add(new TableColumn(rs
                .getString("COLUMN_NAME"), rs
                .getInt("DATA_TYPE"), rs
                .getInt("COLUMN_TYPE")));
        }
        return results;
    }
    
    private ArrayList<String> getTableNames()
        throws SQLException
    {
        ResultSet rs = dbInfo.getTables(null, null, null,
            null);
        ArrayList<String> results = new ArrayList<String>();
        while (rs.next())
        {
            results.add(rs.getString("TABLE_NAME"));
        }
        rs.close();
        return results;
    }
    
    /**
     * Removes all objects that do not have the root as an ancestor. If there is
     * no current root, then this does nothing. This should usually only be
     * called when creating the ProxyStorage, as it might remove an object that
     * has been created but not yet added into the hierarchy if called while the
     * proxy storage is in use.
     */
    private void vacuum()
    {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * Checks to see if a table for the class specified is present, and checks
     * to see if tables are present for the classes's properties. If the tables
     * aren't present, they are created. If they are missing a column, the
     * column is added. If they have extra columns, the extra columns are
     * removed.
     */
    private void checkTables(Class checkClass)
    {
    }
}
