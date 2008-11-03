package net.sf.opengroove.realmserver.gwt.core.rcp;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AnonLinkAsync
{
    public void authenticate(String username,
        String password, AsyncCallback<Void> callback);
    
    public void logout(AsyncCallback<Void> callback);
    
    public void isLoggedIn(AsyncCallback<Boolean> callback);
}
