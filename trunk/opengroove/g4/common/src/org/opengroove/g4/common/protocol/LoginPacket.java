package org.opengroove.g4.common.protocol;

import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.protocol.doc.ClientToServer;

@ClientToServer
public class LoginPacket extends Packet
{
    private Userid userid;
    private String password;
}
