package net.sf.opengroove.realmserver.web.rpc;

import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.HashMap;

import net.sf.opengroove.common.security.Hash;
import net.sf.opengroove.realmserver.OpenGrooveRealmServer;
import net.sf.opengroove.realmserver.gwt.core.rcp.AnonLink;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AnonLinkImpl extends RemoteServiceServlet
    implements AnonLink
{
    
    @Override
    public void authenticate(String username,
        String password)
    {
        HashMap map = new HashMap();
        map.put("username", username);
        String hash = Hash.hash(password);
        map.put("password", hash);
        String role;
        try
        {
            role = (String) OpenGrooveRealmServer.pdbclient
                .queryForObject("authenticateWebUser", map);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(
                "Error accessing the database", e);
        }
        if (role == null || !role.equals("admin"))// failed authentication
        {
            throw new RuntimeException(
                "Incorrect username and/or password");
        }
        getThreadLocalRequest().getSession().setAttribute(
            "username", username);
        getThreadLocalRequest().getSession().setAttribute(
            "role", role);
    }
    
    @Override
    public void logout()
    {
        getThreadLocalRequest().getSession()
            .removeAttribute("username");
        getThreadLocalRequest().getSession()
            .removeAttribute("role");
    }
    
}
