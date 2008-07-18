package net.sf.opengroove.realmserver;

import java.sql.SQLException;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;

import net.sf.opengroove.realmserver.data.model.Computer;
import net.sf.opengroove.realmserver.data.model.SearchUsers;
import net.sf.opengroove.realmserver.data.model.User;
import net.sf.opengroove.realmserver.data.model.UserSetting;

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
        // in the future, this should be stored in the database, and be
        // configurable via the web interface on a per-user basis.
        if (quotaName.equalsIgnoreCase("computers"))
            return 8;
        else if (quotaName
            .equalsIgnoreCase("usersettingsize"))
            return 1024 * 128;
        return -1;
    }
    
    public static User[] searchUsers(String string,
        int offset, int limit, String[] keysToSearch)
        throws SQLException
    {
        string = string.replace("*", "%");
        SearchUsers search = new SearchUsers();
        search.setKeys(keysToSearch);
        search.setLimit(limit);
        search.setOffset(offset);
        search.setSearch(string);
        search.setSearchkeys(keysToSearch.length > 0);
        return (User[]) getPdbClient().queryForList(
            "searchUsers", search).toArray(new User[0]);
    }
    
    public static int searchUsersCount(String string,
        int parseInt, int parseInft2, String[] keysToSearch)
        throws SQLException
    {
        string = string.replace("*", "%");
        SearchUsers search = new SearchUsers();
        search.setKeys(keysToSearch);
        search.setSearch(string);
        search.setSearchkeys(keysToSearch.length > 0);
        return (Integer) getPdbClient().queryForObject(
            "searchUsersCount", search);
    }
    
    public static UserSetting getUserSetting(
        String username, String name) throws SQLException
    {
        UserSetting setting = new UserSetting();
        setting.setUsername(username);
        setting.setName(name);
        return (UserSetting) getPdbClient().queryForObject(
            "getUserSetting", setting);
    }
    
    public static void setUserSetting(String username,
        String name, String value) throws SQLException
    {
        if (value != null && value.equals(""))
            value = null;
        UserSetting setting = new UserSetting();
        setting.setUsername(username);
        setting.setName(name);
        setting.setValue(value);
        if (value == null)// delete the setting
        {
            getPdbClient().delete("deleteUserSetting",
                setting);
        }
        else if (getUserSetting(username, name) != null)// update the setting
        {
            getPdbClient().update("updateUserSetting",
                setting);
        }
        else
        // create the setting
        {
            getPdbClient().insert("insertUserSetting",
                setting);
        }
        
    }
    
    public static int getUserSettingSize(String username)
        throws SQLException
    {
        Integer i = (Integer) getPdbClient()
            .queryForObject("getUserSettingSize", username);
        if (i == null)
            i = 0;
        return i;
    }
    
    public static UserSetting[] listUserSettings(
        String username) throws SQLException
    {
        return (UserSetting[]) getPdbClient().queryForList(
            "listUserSettings", username).toArray(
            new UserSetting[0]);
    }
    
    public static UserSetting[] listPublicUserSettings(
        String username) throws SQLException
    {
        return (UserSetting[]) getPdbClient().queryForList(
            "listPublicUserSettings", username).toArray(
            new UserSetting[0]);
    }
    // !ADDTOSQL
}
