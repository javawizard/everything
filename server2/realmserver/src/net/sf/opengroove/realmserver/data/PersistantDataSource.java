package net.sf.opengroove.realmserver.data;

import java.sql.Connection;
import java.sql.SQLException;

import net.sf.opengroove.realmserver.OpenGrooveRealmServer;

public class PersistantDataSource extends
    ConnectionDataSource
{
    
    @Override
    public Connection getConnection() throws SQLException
    {
        // TODO Auto-generated method stub
        return OpenGrooveRealmServer.pdb;
    }
}
