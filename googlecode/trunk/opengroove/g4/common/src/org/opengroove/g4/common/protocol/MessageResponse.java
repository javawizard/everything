package org.opengroove.g4.common.protocol;

import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.protocol.doc.ClientToServer;
import org.opengroove.g4.common.protocol.doc.ServerToClient;

/**
 * Sent in response to an OutboundMessagePacket or an InboundMessagePacket...
 * 
 * @author Alexander Boyd
 * 
 */
@ClientToServer
@ServerToClient
public class MessageResponse extends Packet
{
    private String messageId;

    public String getMessageId()
    {
        return messageId;
    }

    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }
}
