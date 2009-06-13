package org.opengroove.g4.common.protocol;

import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.protocol.doc.ClientToServer;
import org.opengroove.g4.common.protocol.doc.ServerToClient;

/**
 * A packet that does nothing. This is used to allow the client and the server
 * to detect when they have lost connectivity with each other, by detecting an
 * absense of any data coming across the wire.
 * 
 * @author Alexander Boyd
 * 
 */
@ClientToServer
@ServerToClient
public class NopPacket extends Packet
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 3780813587118093179L;
    
}
