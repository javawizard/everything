package net.sf.opengroove.client.messaging;

import net.sf.opengroove.client.storage.InboundMessage;

/**
 * A class for allowing hierarchical organization of message sending and
 * receiving. It extends the listener concept to provide hierarchical listening
 * and message sending.
 * 
 * 
 * 
 * @author Alexander Boyd
 * 
 */
public abstract class MessageHierarchy
{
    /**
     * Called as a message propegates down through the hierarchy, before the
     * message propegates. This will be called before the target listener's
     * handleMessage method is called. This will also be called before
     * handleInvalidLowerMessage is called.
     * 
     * @param message
     */
    public void beforeHandleLowerMessage(
        InboundMessage message)
    {
        
    }
    
    /**
     * Called after a message has been handled by it's target, as the message
     * propegates back up. This will also be called after the closest target's
     * handleInvalidLowerMessage if the message is invalid.
     * 
     * @param message
     */
    public void afterHandleLowerMessage(
        InboundMessage message)
    {
        
    }
    
    /**
     * Handles a message. If this is called, it means that the message targets
     * this hierarchy element specifically, not an element lower down in the
     * hierarchy.
     * 
     * @param message
     */
    public abstract void handleMessage(
        InboundMessage message);
    
    /**
     * Called if a message cannot propegate further down in the hierarchy
     * because the element that it targets does not exist. This will only be
     * called on the hierarchy element closest to where the message was supposed
     * to go; Hierarchy elements higher up will only receive calls to
     * afterHandleLowerMessage and beforeHandleLowerMessage.
     * 
     * @param message
     */
    public void handleInvalidLowerMessage(
        InboundMessage message)
    {
        
    }
    
    /**
     * Injects a message into this hierarchy. Generally, implementors of this
     * class don't need to call this. This is called when some other mechanism
     * receives a message and wishes this hierarchy to process it. This class
     * will then take care of figuring out where the message is supposed to go.
     * 
     * @param message
     */
    public void injectMessage(InboundMessage message)
    {
        
    }
    
    /**
     * Sets the message deliverer for this hierarchy. It takes care of actually
     * sending a message. It also takes care of creating new message objects
     * when a message is to be created (should this be the case or should a
     * message to send just have recipient info and such, probably, so that it's
     * target can be built up as it propegates up the hierarchy, or should a new
     * message object be created by the MessageDeliverer and propegated down or
     * something...)
     * 
     * @param sender
     */
    public void setMessageSender(MessageDeliverer sender)
    {
        
    }
}
