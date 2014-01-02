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
     * 
     */
    private static final long serialVersionUID = -5485383215040242861L;
    /**
     * True if this is an initial roster packet (IE it was sent to the user
     * because they just established a connection to the server), and false if
     * this is a roster packet being sent because another of the user's
     * computers just updated the roster.
     */
    private boolean isInitial;
    /**
     * If isInitial is false, then this is the userid that caused the roster
     * packet to be sent. This can occur for two reasons: another of this user's
     * computers updated the roster, so all computers should get the new roster
     * updated; and a contact on the roster just added or removed a computer, so
     * any users that have that contact on their roster should get the new
     * update containing the new computer. In the former case, the userid will
     * be a computer userid that reflects the computer that edited the roster;
     * in the latter case, the userid will be a computer userid that reflects
     * the user that caused the change, not the new computer.<br/>
     * <br/>
     * 
     * There is also a third reason that this can occur, and that is when
     * another user changes their real name. This will cause a roster update to
     * all users that have that user as a contact. In that case, the source
     * userid will be the user that changed their real name.
     */
    private Userid source;
    /**
     * The list of contacts in the roster, including their computers.
     */
    private Contact[] contacts;
    
    public boolean isInitial()
    {
        return isInitial;
    }
    
    public void setInitial(boolean isInitial)
    {
        this.isInitial = isInitial;
    }
    
    public Userid getSource()
    {
        return source;
    }
    
    public void setSource(Userid source)
    {
        this.source = source;
    }
    
    public Contact[] getContacts()
    {
        return contacts;
    }
    
    public void setContacts(Contact[] contacts)
    {
        this.contacts = contacts;
    }
}
