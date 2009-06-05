package org.opengroove.g4.common.protocol;

import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.protocol.doc.ClientToServer;
import org.opengroove.g4.common.protocol.doc.ServerToClient;
import org.opengroove.g4.common.user.Userid;

/**
 * Presence updates. When the client connects to the server, one of these is
 * sent by the server for each user on the roster. The client can then send this
 * to the server, and every user with this client on their roster will receive
 * the update.
 * 
 * @author Alexander Boyd
 * 
 */
@ClientToServer
@ServerToClient
public class PresencePacket extends Packet
{
    public static enum Status
    {
        Online, Offline, Idle
    }
    
    private Status status;
    private long date;
    private Userid userid;
    
    /**
     * The status of this presence packet
     * 
     * @return
     */
    public Status getStatus()
    {
        return status;
    }
    
    public void setStatus(Status status)
    {
        this.status = status;
    }
    
    /**
     * For outbound presence, the date at which this transition occured
     * (relative to the server's time). For inbound presence, the date that the
     * remote user specified.
     * 
     * @return
     */
    public long getDate()
    {
        return date;
    }
    
    public void setDate(long date)
    {
        this.date = date;
    }
    
    /**
     * For outbound presence, irrelevant. For inbound presence, the user that
     * this presence corresponds to.
     * 
     * @return
     */
    public Userid getUserid()
    {
        return userid;
    }
    
    public void setUserid(Userid userid)
    {
        this.userid = userid;
    }
}
