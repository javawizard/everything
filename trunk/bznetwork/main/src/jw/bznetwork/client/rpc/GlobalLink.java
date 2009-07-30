package jw.bznetwork.client.rpc;

import jw.bznetwork.client.data.model.Permission;
import jw.bznetwork.client.data.model.Role;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("auth-link")
public interface GlobalLink extends RemoteService
{
    /**
     * 
     * @return
     */
    public Role[] getRoleList();
    
    public void addRole(String name);
    
    public void deleteRole(int id);
    
    public void renameRole(int id, int newName);
    
    public Permission[] getPermissionsForRole(int roleid);
}
