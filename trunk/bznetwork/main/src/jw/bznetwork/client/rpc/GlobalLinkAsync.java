package jw.bznetwork.client.rpc;

import java.util.HashMap;

import java.util.Properties;

import jw.bznetwork.client.ShowMessageException;

import jw.bznetwork.client.data.EditAuthenticationModel;

import jw.bznetwork.client.data.EditAuthgroupsModel;

import jw.bznetwork.client.data.EditConfigurationModel;

import jw.bznetwork.client.data.EditPermissionsModel;

import jw.bznetwork.client.data.model.Configuration;

import jw.bznetwork.client.data.model.Permission;

import jw.bznetwork.client.data.model.Role;

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

public void getEditAuthenticationModel(AsyncCallback<EditAuthenticationModel> callback);

public void updateAuthentication(HashMap<String, String> enabledProps, AsyncCallback<Void> callback);

public void getEditConfigurationModel(AsyncCallback<EditConfigurationModel> callback);

public void updateConfiguration(Configuration config, AsyncCallback<Void> callback);

public void disableEc(AsyncCallback<Void> callback);

}
