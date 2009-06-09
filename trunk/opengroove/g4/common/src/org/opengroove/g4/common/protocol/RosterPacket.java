package org.opengroove.g4.common.protocol;

import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.protocol.doc.ServerToClient;
import org.opengroove.g4.common.roster.Contact;
import org.opengroove.g4.common.user.Userid;

/**
 * This packet is sent once upon connection and once every time this user or
 * another of the user's computers updates the roster.<br/>
 * <br/>
 * 
 * If the client wants to update its roster on the server, then it uses an
 * OutboundMessagePacket wrapping a RosterMessage instead of using this class.
 * See the javadoc on RosterMessage for why it is wrapped in an
 * OutboundMessagePacket and sent as a message instead of being sent as an
 * actual protocol packet.
 * 
 * @author Alexander Boyd
 * 
 */
@ServerToClient
public class RosterPacket extends Packet
{
    /**
     * True if this is an initial roster packet (IE it was sent to the user
     * because they just established a connection to the server), and false if
     * this is a roster packet being sent because another of the user's
     * computers just updated the roster.
     */
    private boolean isInitial;
    private Userid source;
    private Contact[] contacts;
}
