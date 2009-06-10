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
        /**
         * Indicates that the user just came online, or has come back from being
         * idle.
         */
        Online,
        /**
         * Indicates that the user is now offline. Clients currently cannot sent
         * this to the server (it will throw an exception if it receives this),
         * although an "invisible" mode, similar to Gmail's invisible mode where
         * the user appears offline but is really online, might be added in the
         * future, and it would be activated by sending this packet from the
         * client to the server.
         */
        Offline,
        /**
         * Indicates that the user is currently not using their computer. This
         * is typically calculated from the last time that the user's mouse was
         * moved (since java doesn't have the capability to see when the user
         * last pressed a key on their keyboard).
         */
        Idle
    }
    
    private Status status;
    /**
     * The time that the user has been idle, if the status is Idle. This is not
     * a date but rather the amount of time that has passed since the user went
     * idle. When the client is reporting their idle status, they calculate this
     * using their own time compared to their locally stored time of when they
     * went idle. Same with the server.
     */
    private long duration;
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
     * For outbound presence, irrelevant. For inbound presence, the computer
     * userid that this presence corresponds to. This will always be a computer
     * userid; it won't ever be just a username userid.
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
    
    public long getDuration()
    {
        return duration;
    }
    
    public void setDuration(long duration)
    {
        this.duration = duration;
    }
}
