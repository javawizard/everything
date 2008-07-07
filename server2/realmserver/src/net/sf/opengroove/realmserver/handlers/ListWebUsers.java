package net.sf.opengroove.realmserver.handlers;

import java.sql.SQLException;

import net.sf.opengroove.realmserver.OpenGrooveRealmServer;
import net.sf.opengroove.realmserver.web.Handler;
import net.sf.opengroove.realmserver.web.HandlerContext;

public class ListWebUsers implements Handler
{
    
    @Override
    public void handle(HandlerContext context)
        throws SQLException
    {
        context.getRequest().setAttribute(
            "webUserList",
            OpenGrooveRealmServer.pdbclient
                .queryForList("listWebUsers"));
    }
    
}
