package jw.bznetwork.client;

/**
 * An exception thrown when the client attempts to perform an action which it is
 * not authorized to do.
 * 
 * @author Alexander Boyd
 * 
 */
public class PermissionDeniedException extends RuntimeException
{
    
    public PermissionDeniedException()
    {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public PermissionDeniedException(String arg0, Throwable arg1)
    {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }
    
    public PermissionDeniedException(String arg0)
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
    
    public PermissionDeniedException(Throwable arg0)
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
    
}
