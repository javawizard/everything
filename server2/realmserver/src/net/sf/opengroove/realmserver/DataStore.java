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
}
