package net.sf.opengroove.realmserver.gwt.core.rcp;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * This class provides RPC calls that can only be used when the user has been
 * authenticated.
 * 
 * @author Alexander Boyd
 * 
 */
public interface AuthLink extends RemoteService
{
    public void sendUserNotification(String to,
        String subject, String message, String priority,
        int dismissMinutes)throws NotificationException;
}
