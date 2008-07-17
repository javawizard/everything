package tests;

import java.sql.*;

public class Test05
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        // This class connects to our h2database and makes a query using the IN
        // keyword agains an array instead of an SQL select. The idea is to see
        // if columns can be checked for matches against any item of an array
        // somehow.
        Class.forName("org.h2.Driver");
        Connection con = DriverManager.getConnection(
            "jdbc:h2:appdata/dbp/persistant", "sa", "");
        PreparedStatement st = con
            .prepareStatement("select * from opengroove_usersettings where name in (select ?)");
        st.setObject(1, new String[] { "name", "name2" },
            Types.ARRAY);
        ResultSet rs = st.executeQuery();
        while (rs.next())
        {
            System.out.println(rs.getString("username")
                + " | " + rs.getString("name"));
        }
    }
    
}
