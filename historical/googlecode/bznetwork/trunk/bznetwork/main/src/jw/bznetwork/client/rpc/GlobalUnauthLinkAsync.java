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

/**
* Returns configuration options which should be publicly available.
* Currently, this is just the site name. If the user is logged in, however,
* then this returns the contact and the welcome message.
* 
* @return
*/
public void getPublicConfiguration(AsyncCallback<Configuration> callback);

/**
* Attempts to authenticate using an internal username and password.
* 
* @param username
*            The username to log in with
* @param password
*            The password (plain-text, not hashed) to log in with
* @return Null if the login was successful, at which point GlobalLink
*         methods will work, or a message indicating why the login failed.
*/
public void login(String username, String password, AsyncCallback<String> callback);

}