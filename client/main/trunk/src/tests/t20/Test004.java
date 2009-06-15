package tests.t20;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import net.sf.opengroove.common.utils.DataUtils;

/**
 * A class to test the approximate storage overhead for storing several hundred
 * thousand 50-bit strings in an H2 relational database and to test the
 * approximate CPU overhead to check for the presence of a value and remove it.
 * This also stores the date that the value was entered.
 * 
 * @author Alexander Boyd
 * 
 */
public class Test004
{
    private static Connection con;
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        DataUtils.recursiveDelete(new File("test-storage/test004"));
        
        new File("test-storage/test004").mkdirs();
        Class.forName("org.h2.Driver");
        con = DriverManager.getConnection("jdbc:h2:test-storage/test004/db", "sa", "");
        System.out.println("creating table");
        execute("create table t (d timestamp, t varchar(64))");
        int newRowCount = 100 * 1000;
        System.out.println("Inserting " + newRowCount + " rows");
        for (int i = 0; i < newRowCount; i++)
        {
            execute("insert into t values ('2009-05-05 12:34:56', "
                + "'1234567890abcdef1234567890abcdef1234567890abcdef')");
        }
        System.out.println("Insert complete");
    }
    
    private static void execute(String statement) throws Throwable
    {
        PreparedStatement st = con.prepareStatement(statement);
        st.executeUpdate();
        st.close();
    }
}
