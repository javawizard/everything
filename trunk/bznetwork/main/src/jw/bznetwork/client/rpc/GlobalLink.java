package jw.bznetwork.client.rpc;

import java.util.HashMap;
import java.util.Properties;

import jw.bznetwork.client.ShowMessageException;
import jw.bznetwork.client.data.EditAuthenticationModel;
import jw.bznetwork.client.data.EditAuthgroupsModel;
import jw.bznetwork.client.data.EditConfigurationModel;
import jw.bznetwork.client.data.EditPermissionsModel;
import jw.bznetwork.client.data.ServerListModel;
import jw.bznetwork.client.data.UserSession;
import jw.bznetwork.client.data.model.Banfile;
import jw.bznetwork.client.data.model.Configuration;
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
    
    public EditConfigurationModel getEditConfigurationModel();
    
    public void updateConfiguration(Configuration config);
    
    public void disableEc();
    
    public UserSession[] getUserSessions();
    
    public void invalidateUserSession(String id);
    
    public Banfile[] listBanfiles();
    
    public void addBanfile(String name);
    
    public void deleteBanfile(int id) throws ShowMessageException;
    
    /**
     * Gets the list of groups available to this user, and the list of servers
     * within each of those groups available to the user. This also includes
     * information about what users are connected to those servers, and other
     * information. It's essentially enough information to show the servers
     * page, complete with expanding each server to see the list of users and
     * other server info.
     * 
     * @return
     */
    public ServerListModel getServerListModel();
    
    public void addGroup(String name);
    
    public void setGroupBanfile(int group, int banfile);
}
