package jw.bznetwork.server.data;

import jw.bznetwork.client.data.model.Authgroup;
import jw.bznetwork.client.data.model.Banfile;
import jw.bznetwork.client.data.model.Configuration;
import jw.bznetwork.client.data.model.Group;
import jw.bznetwork.client.data.model.LogEvent;
import jw.bznetwork.client.data.model.Permission;
import jw.bznetwork.client.data.model.Role;
import jw.bznetwork.client.data.model.Server;
import jw.bznetwork.client.data.model.User;
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

public static synchronized Banfile getBanfileById(Integer v){try{return (Banfile) getGdbClient().queryForObject("getBanfileById",v);}catch(Exception e){throw new RuntimeException("Exception in database statement getBanfileById",e);}}
    
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
            executeSql("         delete from users where role = " + v
                    + ";        delete from authgroups where role = " + v
                    + ";        delete from callsigns where role = " + v
                    + ";        delete from permissions where roleid = " + v
                    + ";        delete from roles where roleid = " + v);
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
