package net.sf.opengroove.realmserver.web.rpc;

import net.sf.opengroove.realmserver.OpenGrooveRealmServer;
import net.sf.opengroove.realmserver.gwt.core.rcp.AuthLink;
import net.sf.opengroove.realmserver.gwt.core.rcp.NotificationException;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AuthLinkImpl extends RemoteServiceServlet
    implements AuthLink
{
    
    @Override
    public void sendUserNotification(String to,
        String subject, String message, String priority,
        int dismissMinutes) throws NotificationException

    {
        OpenGrooveRealmServer.sendUserNotifications(to,
            subject, message, priority, dismissMinutes);
    }
}
