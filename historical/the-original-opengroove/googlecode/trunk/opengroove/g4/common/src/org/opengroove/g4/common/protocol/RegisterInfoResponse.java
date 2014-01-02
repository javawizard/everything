package org.opengroove.g4.common.protocol;

import javax.swing.JPanel;

import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.protocol.doc.ServerToClient;

@ServerToClient
public class RegisterInfoResponse extends Packet
{
    /**
     * The possible status responses that a registration information response
     * can contain.
     * 
     * @author Alexander Boyd
     * 
     */
    public static enum Status
    {
        /**
         * Indicates that registration is open on this server. This means that
         * the user just needs to provide a username and a password, and the
         * server will be happy to provide them with an account.
         */
        Open,
        /**
         * Indicates that registration requires additional information beyond
         * the user's username and password. The server may choose to limit
         * registration based on this additional info (for example, a server
         * could require employees to provide a valid employee number to
         * register for an account). {@link RegisterInfoResponse#component}
         * should have a swing component that can accept this additional info.
         */
        Info,
        /**
         * Indicates that registration for this server is closed, and that the
         * user must use some other means to register.
         * {@link RegisterInfoResponse#component} can still be non-null, but it
         * should simply be a component that informs the user of why they can't
         * register, and where they should go to register.
         * {@link RegisterInfoResponse#url} can also contain a link to
         * information on how to register.
         */
        Closed
    }
    
    private Status status;
    private JPanel component;
    private String url;
    
    /**
     * The status of registering on the server. See {@link Status} for more
     * info.
     * 
     * @return
     */
    public Status getStatus()
    {
        return status;
    }
    
    /**
     * See Status.INFO and Status.CLOSED for information on what this is.
     * 
     * @return
     */
    public JPanel getComponent()
    {
        return component;
    }
    
    public String getUrl()
    {
        return url;
    }
    
    public void setStatus(Status status)
    {
        this.status = status;
    }
    
    public void setComponent(JPanel component)
    {
        this.component = component;
    }
    
    public void setUrl(String url)
    {
        this.url = url;
    }
}
