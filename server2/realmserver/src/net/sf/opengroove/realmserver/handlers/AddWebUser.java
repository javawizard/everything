package net.sf.opengroove.realmserver.handlers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.opengroove.realmserver.OpenGrooveRealmServer;
import net.sf.opengroove.realmserver.web.Handler;
import net.sf.opengroove.realmserver.web.HandlerContext;
import net.sf.opengroove.common.security.Hash;

public class AddWebUser implements Handler
{
    
    @Override
    public void handle(HandlerContext context)
        throws SQLException
    {
        HttpServletRequest request = context.getRequest();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String passwordAgain = request
            .getParameter("passwordagain");
        if (username == null || password == null
            || passwordAgain == null)
            return;
        if (!password.equals(passwordAgain))
        {
            context
                .setMessage("The passwords don't match.");
            return;
        }
        if (password.length() < 5)
        {
            context
                .setMessage("The password isn't long enough. Passwords must be at least 5 characters.");
            return;
        }
        if (OpenGrooveRealmServer.pdbclient.queryForList(
            "listWebUsers").contains(username))
        {
            context
                .setMessage("That username is already in use.");
            return;
        }
        Map map = new HashMap();
        map.put("username", username);
        map.put("password", Hash.hash(password));
        map.put("role", "admin");
        OpenGrooveRealmServer.pdbclient.insert(
            "addWebUser", map);
        context.setRedirect("listwebusers");
    }
    
}
