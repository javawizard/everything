package net.sf.opengroove.client.g3com;

import javax.swing.JApplet;

/**
 * Implementations of this class can be added to a CommandCommunicator, and will
 * be notified when an immediate message is received.
 * 
 * @author Alexander Boyd
 * 
 */
public interface ImmediateMessageListener
{
    /**
     * Indicates to this listener that an immediate message has been received.
     * This is usually called in a new thread, so there is no need for this
     * method to complete processing quickly.
     * 
     * @param id
     *            the id of the message received, as specified by the sender of
     *            the message.
     * @param username
     *            The username or userid of the user that sent the message.
     * @param computer
     *            The name of the computer that sent the message, or the empty
     *            string if the computer is not known
     * @param message
     *            The actual message itself. The array is generally shared
     *            between all <code>ImmediateMessageListener</code>s notified
     *            of the message, so the contents of this array should not be
     *            modified. If modification is necessary, the array should be
     *            copied, using a method such as
     *            {@link System#arraycopy(Object, int, Object, int, int)}.
     */
    public void receive(String id, String username,
        String computer, byte[] message);
}
