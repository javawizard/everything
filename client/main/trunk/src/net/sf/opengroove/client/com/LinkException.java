package net.sf.opengroove.client.com;

/**
 * An exception that is thrown from any communicator method that causes it to
 * run a server-side command and wait for a response when the server-side
 * command is one that requires the server to connect to another server and for
 * some reason it could not do so.<br/>
 * <br/>
 * 
 * This is not currently used, as server-to-server communication isn't currently
 * supported. This will be supported in the future.
 * 
 * @author Alexander Boyd
 * 
 */
public class LinkException extends RuntimeException
{
    
}
