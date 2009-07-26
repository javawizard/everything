package jw.bznetwork.client.rpc;

import jw.bznetwork.client.data.AuthUser;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("bypass/unauth-link")
public interface GlobalUnauthLink extends RemoteService
{
    /**
     * Returns the user that is currently logged in, or null if the user is not
     * logged in.
     * 
     * @return
     */
    public AuthUser getThisUser();
}
