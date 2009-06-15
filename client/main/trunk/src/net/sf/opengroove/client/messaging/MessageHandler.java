package net.sf.opengroove.client.messaging;

import org.opengroove.g4.common.user.Userid;

/**
 * A message handler that can handle messages.
 * 
 * @author Alexander Boyd
 */
public interface MessageHandler
{
    /**
     * Passes the message to this handler for processing. Every time OpenGroove
     * starts, this will be called on a particular message until the invocation
     * returns successfully.
     * 
     * @param message
     *            The message object
     * @param sender
     *            The user that sent the message
     */
    public void handle(Object message, Userid sender);
}
