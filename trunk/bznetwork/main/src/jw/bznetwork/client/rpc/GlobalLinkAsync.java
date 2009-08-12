package jw.bznetwork.client.rpc;

import java.util.HashMap;

import java.util.Properties;

import jw.bznetwork.client.ShowMessageException;

import jw.bznetwork.client.data.ActionLogModel;

import jw.bznetwork.client.data.EditAuthenticationModel;

import jw.bznetwork.client.data.EditAuthgroupsModel;

import jw.bznetwork.client.data.EditCallsignsModel;

import jw.bznetwork.client.data.EditConfigurationModel;

import jw.bznetwork.client.data.EditPermissionsModel;

import jw.bznetwork.client.data.ServerListModel;

import jw.bznetwork.client.data.UserSession;

import jw.bznetwork.client.data.model.Banfile;

import jw.bznetwork.client.data.model.Configuration;

import jw.bznetwork.client.data.model.Permission;

import jw.bznetwork.client.data.model.Role;

import jw.bznetwork.client.data.model.Server;

import com.google.gwt.user.client.rpc.RemoteService;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
* Asynchronous interface for GlobalLink generated by BrightPages AsyncCreator, modified by Alexander Boyd
*/
public interface GlobalLinkAsync  { 

/**
* 
* @return
*/
public void getRoleList(AsyncCallback<Role[]> callback);

public void addRole(String name, AsyncCallback<Void> callback);

public void deleteRole(int id, AsyncCallback<Void> callback);

public void renameRole(int id, String newName, AsyncCallback<Void> callback);

public void getPermissionsForRole(int roleid, AsyncCallback<EditPermissionsModel> callback);

public void addPermission(int roleid, String permission, int target, AsyncCallback<Void> callback);

public void deletePermission(int roleid, String permission, int target, AsyncCallback<Void> callback);

public void getEditAuthgroupsModel(AsyncCallback<EditAuthgroupsModel> callback);

public void addAuthgroup(String name, int roleid, AsyncCallback<Void> callback);

public void deleteAuthgroup(String name, AsyncCallback<Void> callback);

public void getEditCallsignsModel(AsyncCallback<EditCallsignsModel> callback);

public void addCallsign(String name, int roleid, AsyncCallback<Void> callback);

public void deleteCallsign(String name, AsyncCallback<Void> callback);

public void getEditAuthenticationModel(AsyncCallback<EditAuthenticationModel> callback);

public void updateAuthentication(HashMap<String, String> enabledProps, AsyncCallback<Void> callback);

public void getEditConfigurationModel(AsyncCallback<EditConfigurationModel> callback);

public void updateConfiguration(Configuration config, AsyncCallback<Void> callback);

public void disableEc(AsyncCallback<Void> callback);

public void getUserSessions(AsyncCallback<UserSession[]> callback);

public void invalidateUserSession(String id, AsyncCallback<Void> callback);

public void listBanfiles(AsyncCallback<Banfile[]> callback);

public void addBanfile(String name, AsyncCallback<Void> callback);

public void deleteBanfile(int id, AsyncCallback<Void> callback);

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
public void getServerListModel(AsyncCallback<ServerListModel> callback);

public void addGroup(String name, AsyncCallback<Void> callback);

public void setGroupBanfile(int group, int banfile, AsyncCallback<Void> callback);

public void addServer(String name, int group, AsyncCallback<Void> callback);

public void setServerBanfile(int server, int banfile, AsyncCallback<Void> callback);

public void renameServer(int server, String newName, AsyncCallback<Void> callback);

public void renameGroup(int group, String newName, AsyncCallback<Void> callback);

public void updateServer(Server server, AsyncCallback<Void> callback);

public void getServerConfig(int serverid, AsyncCallback<String> callback);

public void saveServerConfig(int serverid, String config, AsyncCallback<Void> callback);

public void getServerGroupdb(int serverid, AsyncCallback<String> callback);

public void saveServerGroupdb(int serverid, String groupdb, AsyncCallback<Void> callback);

public void getGroupGroupdb(int groupid, AsyncCallback<String> callback);

public void saveGroupGroupdb(int groupid, String groupdb, AsyncCallback<Void> callback);

public void startServer(int serverid, AsyncCallback<String> callback);

public void stopServer(int serverid, AsyncCallback<Void> callback);

public void killServer(int serverid, AsyncCallback<Void> callback);

public void getActionLogModel(String event, int user, int offset, int length, AsyncCallback<ActionLogModel> callback);

public void clearActionLog(int user, AsyncCallback<Void> callback);

}
