package net.sf.opengroove.client.messaging;

/**
 * This class can be overriden, instead of implementing {@link MessageListener},
 * if only a method or two from MessageListener needs to actually be
 * implemented. None of the methods in this class do anything.
 * 
 * @author Alexander Boyd
 * 
 */
public class MessageAdapter implements MessageListener
{
    
    public void afterLowerMessageReceived(Message message)
    {
    }
    
    public void beforeLowerMessageReceived(Message message)
    {
    }
    
    public void invalidLowerMessageReceived(Message message)
    {
    }
    
    public void messageReceived(Message message)
    {
    }
    
}
