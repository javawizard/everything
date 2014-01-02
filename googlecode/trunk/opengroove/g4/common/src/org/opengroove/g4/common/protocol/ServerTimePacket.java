package org.opengroove.g4.common.protocol;

import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.protocol.doc.ClientToServer;
import org.opengroove.g4.common.protocol.doc.ServerToClient;

/**
 * Indicates the server's time, or indicates a request for the server's time.
 * 
 * @author Alexander Boyd
 * 
 */
@ClientToServer
@ServerToClient
public class ServerTimePacket extends Packet
{
    private long date;
}
