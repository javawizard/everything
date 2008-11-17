package net.sf.opengroove.client.messaging;

/**
 * An interface that classes can implement to indicate that they know how to
 * send messages. The topmost MessageHierarchy instance in any given hierarchy
 * must have a MessageSender attached in order for messages to be sent.
 * 
 * @author Alexander Boyd
 * 
 */
public interface MessageDeliverer
{
    /**
     * TODO: this needs to have an argument, but I haven't decided what type the
     * argument should be.
     */
    public void sendMessage();
}
