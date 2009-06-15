package net.sf.opengroove.client.com;

/**
 * An exception that is thrown from the query() methods on Communicator when the
 * connection to the server is lost while waiting for a response.
 * 
 * @author Alexander Boyd
 * 
 */
public class DisconnectedException extends RuntimeException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -8700494137324060968L;
    
    public DisconnectedException()
    {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public DisconnectedException(String arg0, Throwable arg1)
    {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }
    
    public DisconnectedException(String arg0)
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
    
    public DisconnectedException(Throwable arg0)
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
    
}
