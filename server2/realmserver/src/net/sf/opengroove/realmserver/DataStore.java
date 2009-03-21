package net.sf.opengroove.realmserver;

import java.sql.SQLException;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;

import net.sf.opengroove.realmserver.data.model.Computer;
import net.sf.opengroove.realmserver.data.model.ComputerSetting;
import net.sf.opengroove.realmserver.data.model.Message;
import net.sf.opengroove.realmserver.data.model.MessageRecipient;
import net.sf.opengroove.realmserver.data.model.SearchUsers;
import net.sf.opengroove.realmserver.data.model.Subscription;
import net.sf.opengroove.realmserver.data.model.User;
import net.sf.opengroove.realmserver.data.model.UserSetting;

/**
 * The class that allows OpenGroove to access the database. It accesses it's
 * data from OpenGrooveRealmServer.pdbClient and
 * OpenGrooveRealmServer.ldbClient. In the future, I'll probably change this
 * into a class that has most of it's methods as instance methods (instead of
 * static methods like they are now), and then the realm server will construct a
 * new DataStore, passing in it's persistant client and large client.
 * 
 * @author Alexander Boyd
 * 
 */
public class DataStore
{
    
    public static User getUser(String username, String passwordHash)
        throws SQLException
    {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordHash);
        return (User) getPdbClient().queryForObject("authUser", user);
    }
    
    private static SqlMapClient getPdbClient()
    {
        return OpenGrooveRealmServer.pdbclient;
    }
    
    private static SqlMapClient getLdbClient()
    {
        return OpenGrooveRealmServer.ldbclient;
    }
    
    public static Computer getComputer(String username, String computerName)
        throws SQLException
    {
        Computer computer = new Computer();
        computer.setUsername(username);
        computer.setComputername(computerName);
        return (Computer) getPdbClient().queryForObject("getComputer", computer);
    }
    
    public static List listUsers() throws SQLException
    {
        return getPdbClient().queryForList("listUsers");
    }
    
    public static User getUser(String username) throws SQLException
    {
        return (User) getPdbClient().queryForObject("getUser", username);
    }
    
    public static void addUser(String username, String encPassword,
        boolean publiclyListed) throws SQLException
    {
        User user = new User();
        user.setUsername(username);
        user.setPassword(encPassword);
        user.setPubliclylisted(publiclyListed);
        getPdbClient().insert("addUser", user);
    }
    
    public static void updateUser(User user) throws SQLException
    {
        getPdbClient().update("updateUser", user);
    }
    
    public static void deleteUser(String username) throws SQLException
    {
        getPdbClient().delete("deleteUser", username);
    }
    
    public static Computer[] getComputersForUser(String username) throws SQLException
    {
        return (Computer[]) getPdbClient()
            .queryForList("getComputersForUser", username).toArray(new Computer[0]);
    }
    
    public static void addComputer(String username, String computerName, String type)
        throws SQLException
    {
        Computer computer = new Computer();
        computer.setUsername(username);
        computer.setComputername(computerName);
        computer.setType(type);
        computer.setLastonline(0);
        getPdbClient().insert("addComputer", computer);
    }
    
    public static void updateComputer(Computer computer) throws SQLException
    {
        getPdbClient().update("updateComputer", computer);
    }
    
    public static void deleteComputer(String username, String computerName)
        throws SQLException
    {
        Computer computer = new Computer();
        computer.setUsername(username);
        computer.setComputername(computerName);
        getPdbClient().delete("deleteComputer", computer);
    }
    
    public static long getUserLastOnline(String username) throws SQLException
    {
        Object obj = getPdbClient().queryForObject("getUserLastOnline", username);
        if (obj == null)
            return 0;
        return (Long) obj;
    }
    
    public static int getUserQuota(String username, String quotaName)
    {
        // in the future, this should be stored in the database, and be
        // configurable via the web interface on a per-user basis.
        if (quotaName.equalsIgnoreCase("computers"))
            return 8;
        else if (quotaName.equalsIgnoreCase("usersettingsize"))
            return 1024 * 128;
        else if (quotaName.equalsIgnoreCase("computersettingsize"))
            return 1024 * 16;
        else if (quotaName.equalsIgnoreCase("subscriptions"))
            return 1000;
        return -1;
    }
    
    public static User[] searchUsers(String string, int offset, int limit,
        String[] keysToSearch) throws SQLException
    {
        string = string.replace("*", "%");
        SearchUsers search = new SearchUsers();
        search.setKeys(keysToSearch);
        search.setLimit(limit);
        search.setOffset(offset);
        search.setSearch(string);
        search.setSearchkeys(keysToSearch.length > 0);
        return (User[]) getPdbClient().queryForList("searchUsers", search).toArray(
            new User[0]);
    }
    
    public static int searchUsersCount(String string, int parseInt, int parseInft2,
        String[] keysToSearch) throws SQLException
    {
        string = string.replace("*", "%");
        SearchUsers search = new SearchUsers();
        search.setKeys(keysToSearch);
        search.setSearch(string);
        search.setSearchkeys(keysToSearch.length > 0);
        return (Integer) getPdbClient().queryForObject("searchUsersCount", search);
    }
    
    public static UserSetting getUserSetting(String username, String name)
        throws SQLException
    {
        UserSetting setting = new UserSetting();
        setting.setUsername(username);
        setting.setName(name);
        return (UserSetting) getPdbClient().queryForObject("getUserSetting", setting);
    }
    
    public static void setUserSetting(String username, String name, String value)
        throws SQLException
    {
        if (value != null && value.equals(""))
            value = null;
        UserSetting setting = new UserSetting();
        setting.setUsername(username);
        setting.setName(name);
        setting.setValue(value);
        if (value == null)// delete the setting
        {
            getPdbClient().delete("deleteUserSetting", setting);
        }
        else if (getUserSetting(username, name) != null)// update the setting
        {
            getPdbClient().update("updateUserSetting", setting);
        }
        else
        // create the setting
        {
            getPdbClient().insert("insertUserSetting", setting);
        }
        
    }
    
    public static int getUserSettingSize(String username) throws SQLException
    {
        Integer i =
            (Integer) getPdbClient().queryForObject("getUserSettingSize", username);
        if (i == null)
            i = 0;
        return i;
    }
    
    public static UserSetting[] listUserSettings(String username) throws SQLException
    {
        return (UserSetting[]) getPdbClient()
            .queryForList("listUserSettings", username).toArray(new UserSetting[0]);
    }
    
    public static UserSetting[] listPublicUserSettings(String username)
        throws SQLException
    {
        return (UserSetting[]) getPdbClient().queryForList("listPublicUserSettings",
            username).toArray(new UserSetting[0]);
    }
    
    public static ComputerSetting getComputerSetting(String username,
        String computerName, String name) throws SQLException
    {
        ComputerSetting setting = new ComputerSetting();
        setting.setUsername(username);
        setting.setName(name);
        setting.setComputername(computerName);
        return (ComputerSetting) getPdbClient().queryForObject("getComputerSetting",
            setting);
    }
    
    public static void setComputerSetting(String username, String computerName,
        String name, String value) throws SQLException
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
            getPdbClient().delete("deleteComputerSetting", setting);
        }
        else if (getComputerSetting(username, computerName, name) != null)// update
                                                                          // the
                                                                          // setting
        {
            getPdbClient().update("updateComputerSetting", setting);
        }
        else
        // create the setting
        {
            getPdbClient().insert("insertComputerSetting", setting);
        }
        
    }
    
    public static int getComputerSettingSize(String username, String computerName)
        throws SQLException
    {
        ComputerSetting setting = new ComputerSetting();
        setting.setUsername(username);
        setting.setComputername(computerName);
        Integer i =
            (Integer) getPdbClient().queryForObject("getComputerSettingSize", setting);
        if (i == null)
            i = 0;
        return i;
    }
    
    public static ComputerSetting[] listComputerSettings(String username,
        String computerName) throws SQLException
    {
        ComputerSetting setting = new ComputerSetting();
        setting.setUsername(username);
        setting.setComputername(computerName);
        return (ComputerSetting[]) getPdbClient().queryForList("listComputerSettings",
            setting).toArray(new ComputerSetting[0]);
    }
    
    public static ComputerSetting[] listPublicComputerSettings(String username,
        String computerName) throws SQLException
    {
        ComputerSetting setting = new ComputerSetting();
        setting.setUsername(username);
        setting.setComputername(computerName);
        return (ComputerSetting[]) getPdbClient().queryForList(
            "listPublicComputerSettings", setting).toArray(new ComputerSetting[0]);
    }
    
    // !ADDTOSQL
    
    public static MessageRecipient[] listOrphanMessageRecipients() throws SQLException
    {
        return (MessageRecipient[]) getLdbClient().queryForList(
            "listOrphanMessageRecipients").toArray(new MessageRecipient[0]);
    }
    
    public static Message[] listMessagesWithoutRecipients() throws SQLException
    {
        return (Message[]) getLdbClient().queryForList("listMessagesWithoutRecipients")
            .toArray(new Message[0]);
    }
    
    public static void deleteMessageRecipients(String v) throws SQLException
    {
        getLdbClient().delete("deleteMessageRecipients", v);
    }
    
    /**
     * Lists all outbound messages for the user anc computer specified.
     * 
     * @param v
     * @return
     * @throws SQLException
     */
    public static String[] listOutboundMessages(Message v) throws SQLException
    {
        return (String[]) getLdbClient().queryForList("listOutboundMessages", v)
            .toArray(new String[0]);
    }
    
    public static String[] listInboundMessages(MessageRecipient v) throws SQLException
    {
        return (String[]) getLdbClient().queryForList("listInboundMessages", v)
            .toArray(new String[0]);
    }
    
    public static void updateMessage(Message v) throws SQLException
    {
        getLdbClient().update("updateMessage", v);
    }
    
    public static Message getMessage(String v) throws SQLException
    {
        return (Message) getLdbClient().queryForObject("getMessage", v);
    }
    
    public static void deleteMessage(String v) throws SQLException
    {
        getLdbClient().delete("deleteMessage", v);
    }
    
    public static Integer isMessageSender(Message v) throws SQLException
    {
        return (Integer) getLdbClient().queryForObject("isMessageSender", v);
    }
    
    public static Integer isMessageRecipient(MessageRecipient v) throws SQLException
    {
        return (Integer) getLdbClient().queryForObject("isMessageRecipient", v);
    }
    
    public static void deleteMessageRecipient(MessageRecipient v) throws SQLException
    {
        getLdbClient().delete("deleteMessageRecipient", v);
    }
    
    public static MessageRecipient[] listMessageRecipients(String v)
        throws SQLException
    {
        return (MessageRecipient[]) getLdbClient().queryForList(
            "listMessageRecipients", v).toArray(new MessageRecipient[0]);
    }
    
    public static void addMessageRecipient(MessageRecipient v) throws SQLException
    {
        getLdbClient().insert("addMessageRecipient", v);
    }
    
    public static void addMessage(Message v) throws SQLException
    {
        getLdbClient().insert("addMessage", v);
    }
    
    public static boolean checkMessageExists(String v) throws SQLException
    {
        return ((Integer) getLdbClient().queryForObject("checkMessageExists", v)) != 0;
    }
    
    public static int getMatchingSubscriptionCount(Subscription v) throws SQLException
    {
        return (Integer) getPdbClient().queryForObject("getMatchingSubscriptionCount",
            v);
    }
    
    public static void deleteSubscription(Subscription v) throws SQLException
    {
        getPdbClient().delete("deleteSubscription", v);
    }
    
    public static int getSubscriptionCount(String v) throws SQLException
    {
        return (Integer) getPdbClient().queryForObject("getSubscriptionCount", v);
    }
    
    public static Subscription[] listSubscriptionsByTypedTargetUser(Subscription v)
        throws SQLException
    {
        return (Subscription[]) getPdbClient().queryForList(
            "listSubscriptionsByTypedTargetUser", v).toArray(new Subscription[0]);
    }
    
    public static Subscription[] listSubscriptionsByTargetSetting(Subscription v)
        throws SQLException
    {
        return (Subscription[]) getPdbClient().queryForList(
            "listSubscriptionsByTargetSetting", v).toArray(new Subscription[0]);
    }
    
    public static Subscription[] listSubscriptionsByTargetUser(String v)
        throws SQLException
    {
        return (Subscription[]) getPdbClient().queryForList(
            "listSubscriptionsByTargetUser", v).toArray(new Subscription[0]);
    }
    
    public static Subscription[] listSubscriptionsByUser(String v) throws SQLException
    {
        return (Subscription[]) getPdbClient().queryForList("listSubscriptionsByUser",
            v).toArray(new Subscription[0]);
    }
    
    public static void insertSubscription(Subscription v) throws SQLException
    {
        getPdbClient().insert("insertSubscription", v);
    }
    
    public static Computer[] listComputersByUser(String v) throws SQLException
    {
        return (Computer[]) getPdbClient().queryForList("listComputersByUser", v)
            .toArray(new Computer[0]);
    }
    
}
