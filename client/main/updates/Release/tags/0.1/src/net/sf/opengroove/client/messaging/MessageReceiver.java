package net.sf.opengroove.client.messaging;

import net.sf.opengroove.client.storage.InboundMessage;

public interface MessageReceiver
{
    /**
     * Gets a list of messages that target exactly the path specified. The
     * returned list does not include any messages that target children of the
     * element denoted by this path.
     * 
     * @param fixedPath
     * @return
     */
    public InboundMessage[] listMessages(String fixedPath);
    
    /**
     * Gets a list of messages that target any children of this path, at any
     * level (IE grandchildren of this element will be included also). Messages
     * that target exactly this element (IE not one of this element's children
     * or grandchildren) will not be returned in this list.
     * 
     * @param floatingPath
     * @return
     */
    public InboundMessage[] listChildMessages(
        String floatingPath);
}
