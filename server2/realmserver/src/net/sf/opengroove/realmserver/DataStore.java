package net.sf.opengroove.realmserver;

import java.sql.SQLException;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;

import net.sf.opengroove.realmserver.data.model.Computer;
import net.sf.opengroove.realmserver.data.model.ComputerSetting;
import net.sf.opengroove.realmserver.data.model.SearchUsers;
import net.sf.opengroove.realmserver.data.model.SoftDelete;
import net.sf.opengroove.realmserver.data.model.StoredMessage;
import net.sf.opengroove.realmserver.data.model.StoredMessageData;
import net.sf.opengroove.realmserver.data.model.Subscription;
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
        else if (quotaName
            .equalsIgnoreCase("computersettingsize"))
            return 1024 * 16;
        else if (quotaName
            .equalsIgnoreCase("subscriptions"))
            return 300;
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
    
    public static ComputerSetting getComputerSetting(
        String username, String computerName, String name)
        throws SQLException
    {
        ComputerSetting setting = new ComputerSetting();
        setting.setUsername(username);
        setting.setName(name);
        setting.setComputername(computerName);
        return (ComputerSetting) getPdbClient()
            .queryForObject("getComputerSetting", setting);
    }
    
    public static void setComputerSetting(String username,
        String computerName, String name, String value)
        throws SQLException
    {
        if (value != null && value.equals(""))
            value = null;
        ComputerSetting setting = new ComputerSetting();
        setting.setUsername(username);
        setting.setComputername(computerName);
        setting.setName(name);
        setting.setValue(value);
        if (value == null)// delete the setting
        {
            getPdbClient().delete("deleteComputerSetting",
                setting);
        }
        else if (getComputerSetting(username, computerName,
            name) != null)// update the setting
        {
            getPdbClient().update("updateComputerSetting",
                setting);
        }
        else
        // create the setting
        {
            getPdbClient().insert("insertComputerSetting",
                setting);
        }
        
    }
    
    public static int getComputerSettingSize(
        String username, String computerName)
        throws SQLException
    {
        ComputerSetting setting = new ComputerSetting();
        setting.setUsername(username);
        setting.setComputername(computerName);
        Integer i = (Integer) getPdbClient()
            .queryForObject("getComputerSettingSize",
                setting);
        if (i == null)
            i = 0;
        return i;
    }
    
    public static ComputerSetting[] listComputerSettings(
        String username, String computerName)
        throws SQLException
    {
        ComputerSetting setting = new ComputerSetting();
        setting.setUsername(username);
        setting.setComputername(computerName);
        return (ComputerSetting[]) getPdbClient()
            .queryForList("listComputerSettings", setting)
            .toArray(new ComputerSetting[0]);
    }
    
    public static ComputerSetting[] listPublicComputerSettings(
        String username, String computerName)
        throws SQLException
    {
        ComputerSetting setting = new ComputerSetting();
        setting.setUsername(username);
        setting.setComputername(computerName);
        return (ComputerSetting[]) getPdbClient()
            .queryForList("listPublicComputerSettings",
                setting).toArray(new ComputerSetting[0]);
    }
    
    // !ADDTOSQL
    
    public static void updateStoredMessageInfo(
        StoredMessage v) throws SQLException
    {
        getLdbClient().update("updateStoredMessageInfo", v);
    }
    
    public static Integer getStoredMessageDataSize(String v)
        throws SQLException
    {
        return (Integer) getLdbClient().queryForObject(
            "getStoredMessageDataSize", v);
    }
    
    public static Integer getStoredMessageChunkCount(
        String v) throws SQLException
    {
        return (Integer) getLdbClient().queryForObject(
            "getStoredMessageChunkCount", v);
    }
    
    public static long getStoredMessageTotalSize(String v)
        throws SQLException
    {
        return (Long) getLdbClient().queryForObject(
            "getStoredMessageTotalSize", v);
    }
    
    public static StoredMessage getStoredMessageInfo(
        String v) throws SQLException
    {
        return (StoredMessage) getLdbClient()
            .queryForObject("getStoredMessageInfo", v);
    }
    
    public static int getMatchingSubscriptionCount(
        Subscription v) throws SQLException
    {
        return (Integer) getPdbClient().queryForObject(
            "getMatchingSubscriptionCount", v);
    }
    
    public static void deleteSubscription(Subscription v)
        throws SQLException
    {
        getPdbClient().delete("deleteSubscription", v);
    }
    
    public static int getSubscriptionCount(String v)
        throws SQLException
    {
        return (Integer) getPdbClient().queryForObject(
            "getSubscriptionCount", v);
    }
    
    public static Subscription[] listSubscriptionsByTypedTargetUser(
        Subscription v) throws SQLException
    {
        return (Subscription[]) getPdbClient()
            .queryForList(
                "listSubscriptionsByTypedTargetUser", v)
            .toArray(new Subscription[0]);
    }
    
    public static Subscription[] listSubscriptionsByTargetSetting(
        Subscription v) throws SQLException
    {
        return (Subscription[]) getPdbClient()
            .queryForList(
                "listSubscriptionsByTargetSetting", v)
            .toArray(new Subscription[0]);
    }
    
    public static Subscription[] listSubscriptionsByTargetUser(
        String v) throws SQLException
    {
        return (Subscription[]) getPdbClient()
            .queryForList("listSubscriptionsByTargetUser",
                v).toArray(new Subscription[0]);
    }
    
    public static Subscription[] listSubscriptionsByUser(
        String v) throws SQLException
    {
        return (Subscription[]) getPdbClient()
            .queryForList("listSubscriptionsByUser", v)
            .toArray(new Subscription[0]);
    }
    
    public static void insertSubscription(Subscription v)
        throws SQLException
    {
        getPdbClient().insert("insertSubscription", v);
    }
    
    public static Computer[] listComputersByUser(String v)
        throws SQLException
    {
        return (Computer[]) getPdbClient().queryForList(
            "listComputersByUser", v).toArray(
            new Computer[0]);
    }
    
    public static StoredMessage[] listOutboundMessageInfo(
        String v) throws SQLException
    {
        return (StoredMessage[]) getLdbClient()
            .queryForList("listOutboundMessageInfo", v)
            .toArray(new StoredMessage[0]);
    }
    
    public static StoredMessage[] listUnapprovedMessageInfo(
        String v) throws SQLException
    {
        return (StoredMessage[]) getLdbClient()
            .queryForList("listUnapprovedMessageInfo", v)
            .toArray(new StoredMessage[0]);
    }
    
    public static StoredMessage[] listApprovedMessageInfo(
        String v) throws SQLException
    {
        return (StoredMessage[]) getLdbClient()
            .queryForList("listApprovedMessageInfo", v)
            .toArray(new StoredMessage[0]);
    }
    
    public static String getMessageMetadata(String v)
        throws SQLException
    {
        return (String) getLdbClient().queryForObject(
            "getMessageMetadata", v);
    }
    
    public static SoftDelete[] listSoftDeletes(String v)
        throws SQLException
    {
        return (SoftDelete[]) getLdbClient().queryForList(
            "listSoftDeletes", v)
            .toArray(new SoftDelete[0]);
    }
    
    public static void deleteSoftDeletesForMessage(String v)
        throws SQLException
    {
        getLdbClient().delete(
            "deleteSoftDeletesForMessage", v);
    }
    
    public static void deleteSoftDelete(SoftDelete v)
        throws SQLException
    {
        getLdbClient().delete("deleteSoftDelete", v);
    }
    
    public static void insertSoftDelete(SoftDelete v)
        throws SQLException
    {
        getLdbClient().insert("insertSoftDelete", v);
    }
    
    public static StoredMessageData[] listStoredMessageDataForMessage(
        String v) throws SQLException
    {
        return (StoredMessageData[]) getLdbClient()
            .queryForList(
                "listStoredMessageDataForMessage", v)
            .toArray(new StoredMessageData[0]);
    }
    
    public static StoredMessageData getStoredMessageData(
        StoredMessageData v) throws SQLException
    {
        return (StoredMessageData) getLdbClient()
            .queryForObject("getStoredMessageData", v);
    }
    
    public static StoredMessageData getStoredMessageDataInfo(
        StoredMessageData v) throws SQLException
    {
        return (StoredMessageData) getLdbClient()
            .queryForObject("getStoredMessageDataInfo", v);
    }
    
    public static void updateStoredMessageData(
        StoredMessageData v) throws SQLException
    {
        getLdbClient().update("updateStoredMessageData", v);
    }
    
    public static void updateStoredMessageDataInfo(
        StoredMessageData v) throws SQLException
    {
        getLdbClient().update(
            "updateStoredMessageDataInfo", v);
    }
    
    public static void insertStoredMessageData(
        StoredMessageData v) throws SQLException
    {
        getLdbClient().insert("insertStoredMessageData", v);
    }
    
    public static void deleteStoredMessage(String v)
        throws SQLException
    {
        getLdbClient().delete("deleteStoredMessage", v);
    }
    
    public static void updateStoredMessage(StoredMessage v)
        throws SQLException
    {
        getLdbClient().update("updateStoredMessage", v);
    }
    
    public static void insertStoredMessage(StoredMessage v)
        throws SQLException
    {
        getLdbClient().insert("insertStoredMessage", v);
    }
    
}
