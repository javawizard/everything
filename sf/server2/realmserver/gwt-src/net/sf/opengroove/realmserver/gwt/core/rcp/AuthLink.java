package net.sf.opengroove.realmserver.gwt.core.rcp;

import net.sf.opengroove.realmserver.gwt.core.rcp.model.GUser;
import net.sf.opengroove.realmserver.gwt.core.rcp.model.PKIGeneralInfo;

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
        int dismissMinutes) throws NotificationException;
    
    public void createUser(String username,
        String password, String passwordagain)
        throws UserException;
    
    public GUser[] getUsers();
    
    public PKIGeneralInfo getPKIGeneralInfo();
}
