package net.sf.opengroove.realmserver.data;

import java.sql.Connection;
import java.sql.SQLException;

import net.sf.opengroove.realmserver.OpenGrooveRealmServer;

public class LargeDataSource extends ConnectionDataSource
{
    
    @Override
    public Connection getConnection() throws SQLException
    {
        // TODO Auto-generated method stub
        return OpenGrooveRealmServer.ldb;
    }
}
