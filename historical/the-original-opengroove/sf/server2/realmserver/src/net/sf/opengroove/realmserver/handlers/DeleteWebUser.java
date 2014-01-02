package net.sf.opengroove.realmserver.handlers;

import java.sql.SQLException;

import net.sf.opengroove.realmserver.OpenGrooveRealmServer;
import net.sf.opengroove.realmserver.web.Handler;
import net.sf.opengroove.realmserver.web.HandlerContext;

public class DeleteWebUser implements Handler
{
    
    @Override
    public void handle(HandlerContext context) throws SQLException
    {
        if (context.getRequest().getParameter("finalized") == null)
            return;
        OpenGrooveRealmServer.pdbclient.delete(
            "deleteWebUser", context.getRequest()
                .getParameter("username"));
        context.setRedirect("listwebusers");
    }
    
}
