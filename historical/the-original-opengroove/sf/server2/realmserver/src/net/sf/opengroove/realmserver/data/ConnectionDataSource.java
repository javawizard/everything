package net.sf.opengroove.realmserver.data;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public abstract class ConnectionDataSource implements DataSource
{
    
    @Override
    public Connection getConnection(String username,
        String password) throws SQLException
    {
        return getConnection();
    }
    
    @Override
    public PrintWriter getLogWriter() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public int getLoginTimeout() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public void setLogWriter(PrintWriter out)
        throws SQLException
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void setLoginTimeout(int seconds)
        throws SQLException
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public boolean isWrapperFor(Class<?> iface)
        throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
