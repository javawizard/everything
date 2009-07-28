package jw.bznetwork.server.data;

import jw.bznetwork.client.data.model.Configuration;
import jw.bznetwork.client.data.model.Permission;
import jw.bznetwork.client.data.model.Role;
import jw.bznetwork.client.data.model.User;
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
