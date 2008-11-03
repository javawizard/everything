package net.sf.opengroove.realmserver.gwt.core.rcp;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * This class provides RPC calls that can be used when the user is not logged in
 * to the realm server administration interface. Methods can be called on this
 * to authenticate the user.
 * 
 * @author Alexander Boyd
 * 
 */
public interface AnonLink extends RemoteService
{
    public void authenticate(String username,
        String password)throws AuthException;
    
    /*
     * This method is on AnonLink instead of AuthLink since it can be called
     * when the user is not logged in; it just won't have any effect
     */
    public void logout();
    
    public boolean isLoggedIn();
}
