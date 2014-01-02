package org.opengroove.g4.common.protocol;

import java.io.Serializable;

import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.protocol.doc.ClientToServer;
import org.opengroove.g4.common.user.Userid;

/**
 * An object that can be the payload of a message addressed to the user
 * "_profile" on the user's server. This message functions more like a command,
 * but is instead called as a message to make it easy for it to be queued for
 * later sending on the client side.<br/>
 * <br/>
 * 
 * This message can have 3 actions: add a contact, change whether a contact is
 * shown (which does not affect anything on the server; it merely instructs
 * clients to change the way they display things), and set the contact's local
 * name. Currently, there isn't a command for deleting a contact; the contact
 * should simply be hidden, or the server owner can delete a contact for a user
 * by editing their contact list file and removing the contact's entry when the
 * server is not running. This will cause all clients that connect thereafter to
 * receive the update that the contact was deleted.<br/>
 * <br/>
 * 
 * Contacts must always be user userids; computer userids or server userids are
 * not currently accepted.<br/>
 * <br/>
 * 
 * Any computers that are signed on when this change takes place will receive a
 * new RosterPacket from the server, containing the newly-updated roster.
 * 
 * @author Alexander Boyd
 * 
 */
@ClientToServer
public class RosterChangePacket extends Packet
{
    /**
     * 
     */
    private static final long serialVersionUID = 7865774539021019005L;
    
    public static enum Action
    {
        /**
         * An action for adding a new contact. {@link RosterMessage#contact}
         * should be the userid of the contact to add. This action has no effect
         * if the contact is already on this user's roster.
         */
        ADD,
        /**
         * An action for setting a contact's visibility.
         * {@link RosterMessage#contact} should be the userid of the contact to
         * edit, and {@link RosterMessage#shown} should be true or false to show
         * or hide the contact. This action has no effect if the contact is
         * already in that state of visibility (except that a new RosterPacket
         * will still be broadcast to everyone), or if that contact isn't one of
         * this user's contacts.
         */
        VISIBILITY,
        /**
         * An action for setting a contact's name. {@link RosterMessage#contact}
         * should be the userid of the contact to edit, and
         * {@link RosterMessage#name} should be the new name for the contact.
         * This action has no effect if the contact isn't one of this user's
         * contacts.
         */
        NAME
    }
    private Action action;
    private Userid contact;
    private String name;
    private boolean shown;
    
    public Userid getContact()
    {
        return contact;
    }
    
    public void setContact(Userid contact)
    {
        this.contact = contact;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public boolean isShown()
    {
        return shown;
    }
    
    public void setShown(boolean shown)
    {
        this.shown = shown;
    }

    public Action getAction()
    {
        return action;
    }

    public void setAction(Action action)
    {
        this.action = action;
    }
}
