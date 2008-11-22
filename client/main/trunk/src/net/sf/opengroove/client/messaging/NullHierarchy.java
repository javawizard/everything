package net.sf.opengroove.client.messaging;

import net.sf.opengroove.client.storage.InboundMessage;

/**
 * A MessageHierarchy that does nothing with messages it receives. This is
 * useful if the only purpose of the hierarchy is to dispatch messages to
 * children.
 * 
 * @author Alexander Boyd
 * 
 */
public class NullHierarchy extends MessageHierarchy
{
    public NullHierarchy(String name)
    {
        super(name);
    }
    
    public void handleMessage(InboundMessage message)
    {
        System.err
            .println("message delivered directly to NullHierarchy, this isn't allowed");
        message.setStage(InboundMessage.STAGE_READ);
        getInboundMessageFile(message.getId()).delete();
    }
    
}
