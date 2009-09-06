package jw.bznetwork.server.data;

import java.util.Date;

import jw.bznetwork.client.data.model.Action;
import jw.bznetwork.client.data.model.ActionRequest;
import jw.bznetwork.client.data.model.Authgroup;
import jw.bznetwork.client.data.model.Banfile;
import jw.bznetwork.client.data.model.Callsign;
import jw.bznetwork.client.data.model.ConfigSetting;
import jw.bznetwork.client.data.model.Configuration;
import jw.bznetwork.client.data.model.EmailGroup;
import jw.bznetwork.client.data.model.Group;
import jw.bznetwork.client.data.model.IrcBot;
import jw.bznetwork.client.data.model.LogEvent;
import jw.bznetwork.client.data.model.LogRequest;
import jw.bznetwork.client.data.model.Permission;
import jw.bznetwork.client.data.model.Role;
import jw.bznetwork.client.data.model.Server;
import jw.bznetwork.client.data.model.TargetEventPair;
import jw.bznetwork.client.data.model.Trigger;
import jw.bznetwork.client.data.model.User;
import jw.bznetwork.client.data.model.UserPair;
import jw.bznetwork.client.data.model.ValueFive;
import jw.bznetwork.server.BZNetworkServer;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * Conventions (there are some contradictions in this file; these need to be
 * fixed as soon as possible):<br/>
 * 
 * <ul>
 * <li>Add: methods that add a single row to a table</li>
 * <li>Delete: methods that delete a row or a set of rows from a table</li>
 * <li>Get: methods that get a single object from a table</li>
 * <li>List: methods that get a list of objects from a table</li>
 * <li>Update: methods that update a single object in a table</li>
 * <li>Don't include the word "all" in a list statemet that has no arguments.
 * For example, do "listPrograms" instead of "listAllPrograms".</li>
 * <li>Listing methods that return items restricted on input use the "by" word.
 * For example, "listRolesByPrototype". Multiple filters use multiple "by"
 * words.</li>
 * <li>Listing methods that return items restricted by some fixed aspect of the
 * method do not use the "by" word, but instead use the restriction right after
 * the "list" word. For example, "listPublicLookAndFeels", which returns look
 * and feels where the public column is true.</li>
 * <li>Use primitive types and strings for arguments and return types where
 * possible. For example, "deleteSurvey" should accept a long as a parameter
 * (the survey's id), not a survey object.</li>
 * </ul>
 * 
 * @author Alexander Boyd
 * 
 */
public class DataStore
{
    
    private static SqlMapClient getGdbClient()
    {
        return BZNetworkServer.getGeneralDataClient();
    }
    
    // !ADDTOSQL
    
    public static synchronized Trigger[] listTriggersByRecipient(Integer v)
    {
        try
        {
            return (Trigger[]) getGdbClient().queryForList(
                    "listTriggersByRecipient", v).toArray(new Trigger[0]);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement listTriggersByRecipient",
                    e);
        }
    }
    
    public static synchronized EmailGroup getEmailGroupById(Integer v)
    {
        try
        {
            return (EmailGroup) getGdbClient().queryForObject(
                    "getEmailGroupById", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement getEmailGroupById", e);
        }
    }
    
    public static synchronized IrcBot getIrcBotById(Integer v)
    {
        try
        {
            return (IrcBot) getGdbClient().queryForObject("getIrcBotById", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement getIrcBotById", e);
        }
    }
    
    public static synchronized Trigger getTriggerById(Integer v)
    {
        try
        {
            return (Trigger) getGdbClient().queryForObject("getTriggerById", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement getTriggerById", e);
        }
    }
    
    public static synchronized Trigger[] listTriggersByTargetAndEvent(
            TargetEventPair v)
    {
        try
        {
            return (Trigger[]) getGdbClient().queryForList(
                    "listTriggersByTargetAndEvent", v).toArray(new Trigger[0]);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement listTriggersByTargetAndEvent",
                    e);
        }
    }
    
    public static synchronized IrcBot[] listIrcBots()
    {
        try
        {
            return (IrcBot[]) getGdbClient().queryForList("listIrcBots")
                    .toArray(new IrcBot[0]);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement listIrcBots", e);
        }
    }
    
    public static synchronized EmailGroup[] listEmailGroups()
    {
        try
        {
            return (EmailGroup[]) getGdbClient()
                    .queryForList("listEmailGroups").toArray(new EmailGroup[0]);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement listEmailGroups", e);
        }
    }
    
    public static synchronized Trigger[] listTriggers()
    {
        try
        {
            return (Trigger[]) getGdbClient().queryForList("listTriggers")
                    .toArray(new Trigger[0]);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement listTriggers", e);
        }
    }
    
    public static synchronized void updateTrigger(Trigger v)
    {
        try
        {
            getGdbClient().update("updateTrigger", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement updateTrigger", e);
        }
    }
    
    public static synchronized void deleteTrigger(Integer v)
    {
        try
        {
            getGdbClient().delete("deleteTrigger", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement deleteTrigger", e);
        }
    }
    
    public static synchronized void addTrigger(Trigger v)
    {
        try
        {
            getGdbClient().insert("addTrigger", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement addTrigger", e);
        }
    }
    
    public static synchronized void updateIrcBot(IrcBot v)
    {
        try
        {
            getGdbClient().update("updateIrcBot", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement updateIrcBot", e);
        }
    }
    
    public static synchronized void updateEmailGroup(EmailGroup v)
    {
        try
        {
            getGdbClient().update("updateEmailGroup", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement updateEmailGroup", e);
        }
    }
    
    public static synchronized void deleteEmailGroup(Integer v)
    {
        try
        {
            getGdbClient().delete("deleteEmailGroup", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement deleteEmailGroup", e);
        }
    }
    
    public static synchronized void addEmailGroup(EmailGroup v)
    {
        try
        {
            getGdbClient().insert("addEmailGroup", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement addEmailGroup", e);
        }
    }
    
    public static synchronized void deleteIrcBot(Integer v)
    {
        try
        {
            getGdbClient().delete("deleteIrcBot", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement deleteIrcBot", e);
        }
    }
    
    public static synchronized void addIrcBot(IrcBot v)
    {
        try
        {
            getGdbClient().insert("addIrcBot", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement addIrcBot", e);
        }
    }
    
    public static synchronized ConfigSetting getConfigSetting(String v)
    {
        try
        {
            return (ConfigSetting) getGdbClient().queryForObject(
                    "getConfigSetting", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement getConfigSetting", e);
        }
    }
    
    public static synchronized void updateConfigSetting(ConfigSetting v)
    {
        try
        {
            getGdbClient().update("updateConfigSetting", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement updateConfigSetting", e);
        }
    }
    
    public static synchronized void addConfigSetting(ConfigSetting v)
    {
        try
        {
            getGdbClient().insert("addConfigSetting", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement addConfigSetting", e);
        }
    }
    
    public static synchronized void deleteConfigSetting(ConfigSetting v)
    {
        try
        {
            getGdbClient().delete("deleteConfigSetting", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement deleteConfigSetting", e);
        }
    }
    
    public static synchronized String[] listConfigSettingNames()
    {
        try
        {
            return (String[]) getGdbClient().queryForList(
                    "listConfigSettingNames").toArray(new String[0]);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement listConfigSettingNames", e);
        }
    }
    
    public static synchronized ConfigSetting[] listConfigSettings()
    {
        try
        {
            return (ConfigSetting[]) getGdbClient().queryForList(
                    "listConfigSettings").toArray(new ConfigSetting[0]);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement listConfigSettings", e);
        }
    }
    
    public static synchronized String getConfigSettingValue(String v)
    {
        try
        {
            return (String) getGdbClient().queryForObject(
                    "getConfigSettingValue", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement getConfigSettingValue", e);
        }
    }
    
    public static synchronized LogEvent[] searchLogs(LogRequest v)
    {
        try
        {
            return (LogEvent[]) getGdbClient().queryForList("searchLogs", v)
                    .toArray(new LogEvent[0]);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement searchLogs", e);
        }
    }
    
    public static synchronized void clearActionLog(UserPair v)
    {
        try
        {
            getGdbClient().delete("clearActionLog", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement clearActionLog", e);
        }
    }
    
    public static synchronized UserPair[] getActionUserList()
    {
        try
        {
            return (UserPair[]) getGdbClient()
                    .queryForList("getActionUserList").toArray(new UserPair[0]);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement getActionUserList", e);
        }
    }
    
    public static synchronized String[] getActionEventNames()
    {
        try
        {
            return (String[]) getGdbClient()
                    .queryForList("getActionEventNames").toArray(new String[0]);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement getActionEventNames", e);
        }
    }
    
    public static synchronized Integer getActionCountForSearch(ActionRequest v)
    {
        try
        {
            return (Integer) getGdbClient().queryForObject(
                    "getActionCountForSearch", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement getActionCountForSearch",
                    e);
        }
    }
    
    public static synchronized Action[] listActionsForSearch(ActionRequest v)
    {
        try
        {
            return (Action[]) getGdbClient().queryForList(
                    "listActionsForSearch", v).toArray(new Action[0]);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement listActionsForSearch", e);
        }
    }
    
    public static synchronized Callsign getCallsignByName(String v)
    {
        try
        {
            return (Callsign) getGdbClient().queryForObject(
                    "getCallsignByName", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement getCallsignByName", e);
        }
    }
    
    public static synchronized void deleteCallsign(String v)
    {
        try
        {
            getGdbClient().delete("deleteCallsign", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement deleteCallsign", e);
        }
    }
    
    public static synchronized Callsign[] listCallsigns()
    {
        try
        {
            return (Callsign[]) getGdbClient().queryForList("listCallsigns")
                    .toArray(new Callsign[0]);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement listCallsigns", e);
        }
    }
    
    public static synchronized void addCallsign(Callsign v)
    {
        try
        {
            getGdbClient().insert("addCallsign", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement addCallsign", e);
        }
    }
    
    public static synchronized void addActionEvent(Action v)
    {
        try
        {
            getGdbClient().insert("addActionEvent", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement addActionEvent", e);
        }
    }
    
    public static synchronized void updateServer(Server v)
    {
        try
        {
            getGdbClient().update("updateServer", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement updateServer", e);
        }
    }
    
    public static synchronized void addServer(Server v)
    {
        try
        {
            getGdbClient().insert("addServer", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement addServer", e);
        }
    }
    
    public static synchronized void updateGroup(Group v)
    {
        try
        {
            getGdbClient().update("updateGroup", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement updateGroup", e);
        }
    }
    
    public static synchronized void addGroup(Group v)
    {
        try
        {
            getGdbClient().insert("addGroup", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement addGroup", e);
        }
    }
    
    public static synchronized Server[] listServersByGroup(Integer v)
    {
        try
        {
            return (Server[]) getGdbClient().queryForList("listServersByGroup",
                    v).toArray(new Server[0]);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement listServersByGroup", e);
        }
    }
    
    public static synchronized void addBanfile(Banfile v)
    {
        try
        {
            getGdbClient().insert("addBanfile", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement addBanfile", e);
        }
    }
    
    public static synchronized Banfile getBanfileById(Integer v)
    {
        try
        {
            return (Banfile) getGdbClient().queryForObject("getBanfileById", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement getBanfileById", e);
        }
    }
    
    public static synchronized Banfile[] listBanfiles()
    {
        try
        {
            return (Banfile[]) getGdbClient().queryForList("listBanfiles")
                    .toArray(new Banfile[0]);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement listBanfiles", e);
        }
    }
    
    public static synchronized void addLogEvent(LogEvent v)
    {
        if (v.getWhen() == null)
            v.setWhen(new Date());
        try
        {
            getGdbClient().insert("addLogEvent", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement addLogEvent", e);
        }
    }
    
    public static synchronized Permission getPermission(Permission v)
    {
        try
        {
            return (Permission) getGdbClient().queryForObject("getPermission",
                    v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement getPermission", e);
        }
    }
    
    public static synchronized Server[] listServers()
    {
        try
        {
            return (Server[]) getGdbClient().queryForList("listServers")
                    .toArray(new Server[0]);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement listServers", e);
        }
    }
    
    public static synchronized Group[] listGroups()
    {
        try
        {
            return (Group[]) getGdbClient().queryForList("listGroups").toArray(
                    new Group[0]);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement listGroups", e);
        }
    }
    
    public static synchronized Server getServerById(Integer v)
    {
        try
        {
            return (Server) getGdbClient().queryForObject("getServerById", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement getServerById", e);
        }
    }
    
    public static synchronized Group getGroupById(Integer v)
    {
        try
        {
            return (Group) getGdbClient().queryForObject("getGroupById", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement getGroupById", e);
        }
    }
    
    public static synchronized void executeSql(String v)
    {
        try
        {
            getGdbClient().update("executeSql", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement executeSql", e);
        }
    }
    
    private static synchronized void setNextId(Integer v)
    {
        try
        {
            getGdbClient().update("setNextId", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement setNextId", e);
        }
    }
    
    private static synchronized Integer getNextIdDb()
    {
        try
        {
            return (Integer) getGdbClient().queryForObject("getNextId");
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement getNextId", e);
        }
    }
    
    public static synchronized int createId()
    {
        int nextId = getNextIdDb();
        setNextId(nextId + 1);
        return nextId;
    }
    
    public static synchronized void updateConfiguration(Configuration v)
    {
        try
        {
            getGdbClient().update("updateConfiguration", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement updateConfiguration", e);
        }
    }
    
    public static synchronized void addAuthgroup(Authgroup v)
    {
        try
        {
            getGdbClient().insert("addAuthgroup", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement addAuthgroup", e);
        }
    }
    
    public static synchronized void deleteAuthgroup(String v)
    {
        try
        {
            getGdbClient().delete("deleteAuthgroup", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement deleteAuthgroup", e);
        }
    }
    
    public static synchronized void deletePermissionsByTarget(Integer v)
    {
        try
        {
            getGdbClient().delete("deletePermissionsByTarget", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement deletePermissionsByTarget",
                    e);
        }
    }
    
    public static synchronized void deletePermissionsByRole(Integer v)
    {
        try
        {
            getGdbClient().delete("deletePermissionsByRole", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement deletePermissionsByRole",
                    e);
        }
    }
    
    public static synchronized void deletePermission(Permission v)
    {
        try
        {
            getGdbClient().update("deletePermission", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement deletePermission", e);
        }
    }
    
    public static synchronized void addPermission(Permission v)
    {
        try
        {
            getGdbClient().insert("addPermission", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement addPermission", e);
        }
    }
    
    public static synchronized void deleteRole(Integer v)
    {
        try
        {
            executeSql("        delete from users where role = " + v);
            executeSql("        delete from authgroups where role = " + v);
            executeSql("        delete from callsigns where role = " + v);
            executeSql("        delete from permissions where roleid = " + v);
            executeSql("        delete from roles where roleid = " + v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement deleteRole", e);
        }
    }
    
    public static synchronized void updateRole(Role v)
    {
        try
        {
            getGdbClient().update("updateRole", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement updateRole", e);
        }
    }
    
    public static synchronized void addRole(Role v)
    {
        try
        {
            getGdbClient().insert("addRole", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement addRole", e);
        }
    }
    
    public static synchronized Role[] listRoles()
    {
        try
        {
            return (Role[]) getGdbClient().queryForList("listRoles").toArray(
                    new Role[0]);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement listRoles", e);
        }
    }
    
    public static synchronized Authgroup getAuthgroupByName(String v)
    {
        try
        {
            return (Authgroup) getGdbClient().queryForObject(
                    "getAuthgroupByName", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement getAuthgroupByName", e);
        }
    }
    
    public static synchronized Authgroup[] listAuthgroups()
    {
        try
        {
            return (Authgroup[]) getGdbClient().queryForList("listAuthgroups")
                    .toArray(new Authgroup[0]);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement listAuthgroups", e);
        }
    }
    
    public static synchronized Permission[] getPermissionsByRole(Integer v)
    {
        try
        {
            return (Permission[]) getGdbClient().queryForList(
                    "getPermissionsByRole", v).toArray(new Permission[0]);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement getPermissionsByRole", e);
        }
    }
    
    public static synchronized Role getRoleById(Integer v)
    {
        try
        {
            return (Role) getGdbClient().queryForObject("getRoleById", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement getRoleById", e);
        }
    }
    
    public static synchronized User getUserByUsername(String v)
    {
        try
        {
            return (User) getGdbClient().queryForObject("getUserByUsername", v);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement getUserByUsername", e);
        }
    }
    
    public static synchronized Configuration getConfiguration()
    {
        try
        {
            return (Configuration) getGdbClient().queryForObject(
                    "getConfiguration");
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Exception in database statement getConfiguration", e);
        }
    }
    
}
