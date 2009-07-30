package jw.bznetwork.server.rpc;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jw.bznetwork.client.Perms;
import jw.bznetwork.client.ShowMessageException;
import jw.bznetwork.client.Verify;
import jw.bznetwork.client.data.EditPermissionsModel;
import jw.bznetwork.client.data.GroupedServer;
import jw.bznetwork.client.data.model.EditablePermission;
import jw.bznetwork.client.data.model.Group;
import jw.bznetwork.client.data.model.Permission;
import jw.bznetwork.client.data.model.Role;
import jw.bznetwork.client.data.model.Server;
import jw.bznetwork.client.rpc.GlobalLink;
import jw.bznetwork.server.data.DataStore;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GlobalLinkImpl extends RemoteServiceServlet implements GlobalLink
{
    
    @Override
    public void service(ServletRequest req, ServletResponse res)
            throws ServletException, IOException
    {
        HttpServletRequest hReq = (HttpServletRequest) req;
        if (hReq.getSession(false) == null
                || hReq.getSession().getAttribute("user") == null)
        {
            ((HttpServletResponse) res).sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "You are not logged in.");
            return;
        }
        super.service(req, res);
    }
    
    @Override
    public void addRole(String name)
    {
        Verify.global("manage-roles");
        Role role = new Role();
        role.setRoleid(DataStore.createId());
        role.setName(name);
        DataStore.addRole(role);
    }
    
    @Override
    public void deleteRole(int id)
    {
        Verify.global("manage-roles");
        DataStore.deleteRole(id);
    }
    
    @Override
    public EditPermissionsModel getPermissionsForRole(int roleid)
    {
        Verify.global("manage-roles");
        Permission[] perms = DataStore.getPermissionsByRole(roleid);
        EditablePermission[] result = new EditablePermission[perms.length];
        for (int i = 0; i < perms.length; i++)
        {
            result[i] = new EditablePermission();
            result[i].setGroup(perms[i].getGroup());
            result[i].setRoleid(perms[i].getRoleid());
            result[i].setPermission(perms[i].getPermission());
            result[i].setTarget(perms[i].getTarget());
            if (perms[i].getTarget() == -1)
            {
                /*
                 * Global permission
                 */
                result[i].setGroupName(null);
                result[i].setServerName(null);
            }
            else if (perms[i].getGroup() == null)
            {
                /*
                 * Group permission, since the parent group is null
                 */
                Group group = DataStore.getGroupById(perms[i].getTarget());
                if (group == null)
                    result[i].setGroupName("_missing_" + perms[i].getTarget());
                else
                    result[i].setGroupName(group.getName());
                result[i].setServerName(null);
                
            }
            else
            {
                /*
                 * Server permission, since parent group is not null
                 */
                Server server = DataStore.getServerById(perms[i].getTarget());
                if (server == null)
                    result[i].setServerName("_missing_" + perms[i].getTarget());
                else
                    result[i].setServerName(server.getName());
                Group group = DataStore.getGroupById(perms[i].getGroup());
                if (group == null)
                    result[i].setGroupName("_missing_" + perms[i].getGroup());
                else
                    result[i].setGroupName(group.getName());
            }
        }
        EditPermissionsModel model = new EditPermissionsModel();
        model.setPermissions(result);
        Group[] groups = DataStore.listGroups();
        HashMap<Integer, Group> idsToGroups = new HashMap<Integer, Group>();
        for (Group group : groups)
        {
            idsToGroups.put(group.getGroupid(), group);
        }
        Server[] servers = DataStore.listServers();
        GroupedServer[] resultServers = new GroupedServer[servers.length];
        for (int i = 0; i < servers.length; i++)
        {
            GroupedServer r = new GroupedServer();
            resultServers[i] = r;
            r.setServerid(servers[i].getServerid());
            r.setName(servers[i].getName());
            r.setGroupid(servers[i].getGroupid());
            r.setParent(idsToGroups.get(r.getGroupid()));
        }
        model.setGroups(groups);
        model.setServers(resultServers);
        return model;
    }
    
    @Override
    public Role[] getRoleList()
    {
        Verify.global("manage-roles");
        return DataStore.listRoles();
    }
    
    @Override
    public void renameRole(int id, String newName)
    {
        Verify.global("manage-roles");
        Role role = DataStore.getRoleById(id);
        role.setName(newName);
        DataStore.updateRole(role);
    }
    
    @Override
    public void addPermission(int roleid, String permission, int target)
            throws ShowMessageException
    {
        Verify.global("manage-roles");
        if (!Perms.isPermissionValid(permission))
            throw new RuntimeException("Invalid permission");
        /*
         * In the future, we might want to validate that the permission is being
         * applied to the appropriate level. For now, though, it's not that big
         * of a deal, since the client performs this validation, and a client
         * modded not to perform that validation wouldn't cause any problems
         * because the server ends up acting like the permission doesn't even
         * exist anyway.
         */
        Permission p = new Permission();
        p.setPermission(permission);
        p.setRoleid(roleid);
        p.setTarget(target);
        if (DataStore.getPermission(p) == null)
            DataStore.addPermission(p);
        else
            throw new ShowMessageException(
                    "That permission/target pair already exists on this role.");
    }
    
    @Override
    public void deletePermission(int roleid, String permission, int target)
    {
        Verify.global("manage-roles");
        Permission p = new Permission();
        p.setPermission(permission);
        p.setRoleid(roleid);
        p.setTarget(target);
        DataStore.deletePermission(p);
    }
    
}
