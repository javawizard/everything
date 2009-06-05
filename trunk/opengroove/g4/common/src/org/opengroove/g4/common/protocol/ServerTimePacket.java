package org.opengroove.g4.common.protocol;

import org.opengroove.g4.common.Packet;

/**
 * Indicates the server's time, or indicates a request for the server's time.
 * 
 * @author Alexander Boyd
 * 
 */
public class ServerTimePacket extends Packet
{
    private long date;
}
