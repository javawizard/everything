package net.sf.opengroove.realmserver;

import java.sql.SQLException;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;

import net.sf.opengroove.realmserver.data.model.Computer;
import net.sf.opengroove.realmserver.data.model.User;

public class DataStore
{
    
    public static User getUser(String username,
        String passwordHash) throws SQLException
    {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordHash);
        return (User) getPdbClient().queryForObject(
            "authUser", user);
    }
    
    private static SqlMapClient getPdbClient()
    {
        return OpenGrooveRealmServer.pdbclient;
    }
    
    private static SqlMapClient getLdbClient()
    {
        return OpenGrooveRealmServer.ldbclient;
    }
    
    public static Computer getComputer(String username,
        String computerName) throws SQLException
    {
        Computer computer = new Computer();
        computer.setUsername(username);
        computer.setComputername(computerName);
        return (Computer) getPdbClient().queryForObject(
            "getComputer", computer);
    }
    
    public static List listUsers() throws SQLException
    {
        return getPdbClient().queryForList("listUsers");
    }
    
    public static User getUser(String username)
        throws SQLException
    {
        return (User) getPdbClient().queryForObject(
            "getUser", username);
    }
    
    public static void addUser(String username,
        String encPassword, boolean publiclyListed)
        throws SQLException
    {
        User user = new User();
        user.setUsername(username);
        user.setPassword(encPassword);
        user.setPubliclylisted(publiclyListed);
        getPdbClient().insert("addUser", user);
    }
    
    public static void updateUser(User user)
        throws SQLException
    {
        getPdbClient().update("updateUser", user);
    }
    
    public static void deleteUser(String username)
        throws SQLException
    {
        getPdbClient().delete("deleteUser", username);
    }
    
    public static Computer[] getComputersForUser(
        String username) throws SQLException
    {
        return (Computer[]) getPdbClient().queryForList(
            "getComputersForUser", username).toArray(
            new Computer[0]);
    }
    
    public static void addComputer(String username,
        String computerName, String type)
        throws SQLException
    {
        Computer computer = new Computer();
        computer.setUsername(username);
        computer.setComputername(computerName);
        computer.setType(type);
        computer.setLastonline(0);
        getPdbClient().insert("addComputer", computer);
    }
    
    public static void updateComputer(Computer computer)
        throws SQLException
    {
        getPdbClient().update("updateComputer", computer);
    }
    
    public static void deleteComputer(String username,
        String computerName) throws SQLException
    {
        Computer computer = new Computer();
        computer.setUsername(username);
        computer.setComputername(computerName);
        getPdbClient().delete("deleteComputer", computer);
    }
    
    public static long getUserLastOnline(String username)
        throws SQLException
    {
        return (Long) getPdbClient().queryForObject(
            "getUserLastOnline", username);
    }
    
    public static int getUserQuota(String username,
        String quotaName)
    {
        // in the future, this should contact the database
        if (quotaName.equalsIgnoreCase("computers"))
            return 8;
        return -1;
    }
}
