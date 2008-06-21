package tests;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class Test02
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        Class.forName("smallsql.database.SSDriver");
        Connection con = DriverManager
            .getConnection("jdbc:smallsql:appdata/testdb?create=true");
        PreparedStatement st = con
            .prepareStatement("insert into testtable values (12345, 'Hello, world!')");
        st.execute();
        st.close();
        con.close();
    }
    
}
