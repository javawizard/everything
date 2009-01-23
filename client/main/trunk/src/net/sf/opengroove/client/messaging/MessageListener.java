package net.sf.opengroove.client.messaging;

/**
 * Instances of this clas can be added to
 * {@link MessageHierarchy message hierarchies} to listen for messages. If not
 * all methods need to be overriden
 * 
 * @author Alexander Boyd
 * 
 */
public interface MessageListener
{
    public void messageReceived(Message message);
    
    public void beforeLowerMessageReceived(Message message);
    
    public void afterLowerMessageReceived(Message message);
    
    public void invalidLowerMessageReceived(Message message);
}
