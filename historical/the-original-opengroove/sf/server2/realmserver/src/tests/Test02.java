package tests;

import java.io.ByteArrayInputStream;
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
            .getConnection("jdbc:smallsql:lib/testdb");
        for (int i = 0; i < 10000; i++)
        {
            if ((i % 100) == 0)
                System.out.println("" + i);
            PreparedStatement st = con
                .prepareStatement("insert into testtable values (?, ?)");
            st.setInt(1, i);
            st.setBlob(2, new ByteArrayInputStream(
                ("This is some test text for index " + i)
                    .getBytes()));
            st.execute();
            st.close();
            con.close();
        }
        con.close();
    }
}
