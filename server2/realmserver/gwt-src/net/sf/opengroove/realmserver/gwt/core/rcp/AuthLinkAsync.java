package net.sf.opengroove.realmserver.gwt.core.rcp;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AuthLinkAsync
{
    public void sendUserNotification(String to,
        String subject, String message, String priority,
        int dismissMinutes, AsyncCallback<Void> callback);
}
