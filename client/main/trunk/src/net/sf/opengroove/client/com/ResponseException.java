package net.sf.opengroove.client.com;

/**
 * An exception thrown from the query() methods of Communicator that indicates
 * that a server-side exception was thrown while processing the command. The
 * {@link Exception#getCause() cause} of the ResponseException is the actual
 * server-side exception that occured, complete with the server-side stack
 * trace.
 * 
 * @author Alexander Boyd
 * 
 */
public class ResponseException extends RuntimeException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -5234251157623296594L;
    
}
