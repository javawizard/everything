package tests.t20;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sf.opengroove.common.utils.DataUtils;

/**
 * This test deletes the database at C:\opengroove-test003-db if it exists. Then
 * it creates the database. It then inserts a blob, reads it, modifies it, and
 * reads it again. The main purpose of the test is to see of the H2 database
 * supports modifying blobs and reading segments of blobs, since H2's
 * documentation is ambiguous on this point.
 * 
 * @author Alexander Boyd
 * 
 */
public class Test003
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        DataUtils.recursiveDelete(new File("C:/opengroove-test003-db"));
        Class.forName("org.h2.Driver");
        Connection c =
            DriverManager.getConnection("jdbc:h2:C:/opengroove-test003-db/db");
        PreparedStatement st =
            c.prepareStatement("create table mytable (mycolumn blob)");
        st.execute();
        st.close();
        st = c.prepareStatement("insert into mytable values (?)");
        st.setBlob(1, new ByteArrayInputStream("Hello. This is some text.".getBytes()));
        st.execute();
        st.close();
        st = c.prepareStatement("select * from mytable");
        ResultSet rs = st.executeQuery();
        rs.next();
        Blob blob = rs.getBlob(1);
        byte[] bytes = blob.getBytes(0, 5);
        System.out.println(new String(bytes));
        blob.setBytes(0, "seeya".getBytes());
    }
}
