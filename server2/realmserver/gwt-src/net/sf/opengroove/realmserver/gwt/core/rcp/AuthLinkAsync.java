package net.sf.opengroove.realmserver.gwt.core.rcp;

import net.sf.opengroove.realmserver.gwt.core.rcp.model.GUser;
import net.sf.opengroove.realmserver.gwt.core.rcp.model.PKIGeneralInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AuthLinkAsync
{
    public void sendUserNotification(String to,
        String subject, String message, String priority,
        int dismissMinutes, AsyncCallback<Void> callback);
    
    public void createUser(String username,
        String password, String passwordagain,
        AsyncCallback<Void> callback);
    
    public void getUsers(AsyncCallback<GUser[]> callback);
    
    public void getPKIGeneralInfo(
        AsyncCallback<PKIGeneralInfo> calback);
}
