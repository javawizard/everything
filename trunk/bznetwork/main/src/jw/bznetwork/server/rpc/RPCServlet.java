package jw.bznetwork.server.rpc;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import jw.bznetwork.server.BZNetworkServer;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * A class that extends RemoteServiceServlet and adds and overrides methods
 * necessary to get remote services to work without being added to web.xml. All
 * layout module services should extend this class instead of
 * RemoteServiceServlet.<br/>
 * <br/>
 * 
 * This class also provides utility methods for getting information about the
 * currently-authenticated user.
 * 
 * @author Alexander Boyd
 * 
 */
public class RPCServlet extends RemoteServiceServlet
{
    
    protected void doUnexpectedFailure(Throwable e)
    {
        e.printStackTrace();
        throw new RuntimeException(e);
    }
    
    public ServletContext getServletContext()
    {
        return BZNetworkServer.getServletContext();
    }
    
}
