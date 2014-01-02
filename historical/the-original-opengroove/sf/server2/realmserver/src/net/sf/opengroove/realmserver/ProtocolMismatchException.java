package net.sf.opengroove.realmserver;

/**
 * A ProtocolMismatchException is thrown when the client sends information not
 * conforming to the OpenGroove protocol, indicating that they're using the
 * wrong type of client (for example, using a web browser) to connect to the
 * OpenGroove server, or that they have the wrong security key.
 * 
 * @author Alexander Boyd
 * 
 */
public class ProtocolMismatchException extends
    RuntimeException
{
    
    public ProtocolMismatchException()
    {
        // TODO Auto-generated constructor stub
    }
    
    public ProtocolMismatchException(String message)
    {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    public ProtocolMismatchException(Throwable cause)
    {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    public ProtocolMismatchException(String message,
        Throwable cause)
    {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
}
