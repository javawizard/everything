package jw.bznetwork.server.rpc;

import javax.servlet.http.HttpSession;

import jw.bznetwork.client.data.AuthUser;
import jw.bznetwork.client.rpc.GlobalUnauthLink;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GlobalUnauthLinkImpl extends RemoteServiceServlet implements
        GlobalUnauthLink
{
    
    @Override
    public AuthUser getThisUser()
    {
        HttpSession session = getThreadLocalRequest().getSession(false);
        if (session == null)
            return null;
        return (AuthUser) session.getAttribute("user");
    }
    
}
