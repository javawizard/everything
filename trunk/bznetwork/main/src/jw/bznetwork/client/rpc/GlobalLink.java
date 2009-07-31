package jw.bznetwork.client.rpc;

import java.util.HashMap;
import java.util.Properties;

import jw.bznetwork.client.ShowMessageException;
import jw.bznetwork.client.data.EditAuthenticationModel;
import jw.bznetwork.client.data.EditAuthgroupsModel;
import jw.bznetwork.client.data.EditPermissionsModel;
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
    
    public void renameRole(int id, String newName);
    
    public EditPermissionsModel getPermissionsForRole(int roleid);
    
    public void addPermission(int roleid, String permission, int target)
            throws ShowMessageException;
    
    public void deletePermission(int roleid, String permission, int target);
    
    public EditAuthgroupsModel getEditAuthgroupsModel();
    
    public void addAuthgroup(String name, int roleid)
            throws ShowMessageException;
    
    public void deleteAuthgroup(String name);
    
    public EditAuthenticationModel getEditAuthenticationModel();
    
    public void updateAuthentication(HashMap<String, String> enabledProps);
}
