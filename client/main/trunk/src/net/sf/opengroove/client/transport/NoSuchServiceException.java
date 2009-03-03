package net.sf.opengroove.client.transport;

/**
 * Thrown from {@link Connector#getService(java.net.URI)} when the URI specified
 * does not denote a valid service supported by this connector.
 * 
 * @author Alexander Boyd
 * 
 */
public class NoSuchServiceException extends Exception
{
    
    public NoSuchServiceException()
    {
        // TODO Auto-generated constructor stub
    }
    
    public NoSuchServiceException(String message)
    {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    public NoSuchServiceException(Throwable cause)
    {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
    public NoSuchServiceException(String message, Throwable cause)
    {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
}
