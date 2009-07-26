package jw.bznetwork.client.rpc;

import jw.bznetwork.client.AuthProvider;

import jw.bznetwork.client.data.AuthUser;

import jw.bznetwork.client.data.model.Configuration;

import com.google.gwt.user.client.rpc.RemoteService;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
* Asynchronous interface for GlobalUnauthLink generated by BrightPages AsyncCreator, modified by Alexander Boyd
*/
public interface GlobalUnauthLinkAsync  { 

/**
* Returns the user that is currently logged in, or null if the user is not
* logged in.
* 
* @return
*/
public void getThisUser(AsyncCallback<AuthUser> callback);

/**
* Gets a list of all enabled auth providers.
* 
* @return
*/
public void listEnabledAuthProviders(AsyncCallback<AuthProvider[]> callback);

public void getPublicConfiguration(AsyncCallback<Configuration> callback);

}
