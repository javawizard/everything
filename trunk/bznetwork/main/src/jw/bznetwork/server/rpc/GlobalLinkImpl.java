package jw.bznetwork.server.rpc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jw.bznetwork.client.Perms;
import jw.bznetwork.client.Verify;
import jw.bznetwork.client.data.model.Permission;
import jw.bznetwork.client.data.model.Role;
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
        
    }
    
    @Override
    public void deleteRole(int id)
    {
        Verify.global("manage-roles");
        DataStore.deleteRole(id);
    }
    
    @Override
    public Permission[] getPermissionsForRole(int roleid)
    {
        Verify.global("manage-roles");
        return DataStore.getPermissionsByRole(roleid);
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
    
}
