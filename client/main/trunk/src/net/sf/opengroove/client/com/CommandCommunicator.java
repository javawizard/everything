package net.sf.opengroove.client.com;

import java.io.IOException;

/**
 * This class wraps the Communicator class, and provides methods for doing
 * common interaction tasks with the server.
 * 
 * @author Alexander Boyd
 * 
 */
public class CommandCommunicator
{
    private static final int GLOBAL_DEFAULT_TIMEOUT = 5000;
    
    private Communicator communicator;
    
    private int defaultTimeout = GLOBAL_DEFAULT_TIMEOUT;
    
    public CommandCommunicator(Communicator communicator)
    {
        this.communicator = communicator;
    }
    
    public Communicator getCommunicator()
    {
        return communicator;
    }
    
    public void setDefaultTimeout(int timeout)
    {
        this.defaultTimeout = timeout;
    }
    
    /**
     * Sends an immediate message to the user specified.
     * 
     * @param id
     *            An id for the imessage. This can be anything that the
     *            recipient of the message would be expecting, but cannot be
     *            longer than 64 characters.
     * @param username
     *            The userid or username of the user to send the message to.
     * @param computer
     *            The name of the computer to send the message to
     * @param message
     *            The message itself, not longer than 8KB
     * @return The status of sending the message. If sending the message
     *         succeeded, then this would be the string "OK". If sending the
     *         message failed, then this is a code that represents the reason
     *         for the failure. See the source for OpenGrooveRealmServer for the
     *         list of possible error codes.
     */
    public String sendImmediateMessage(String id,
        String username, String computer, byte[] message)
        throws IOException
    {
        Packet packet = new Packet(null, "sendimessage",
            Communicator
                .concat(("" + id + " " + username + " "
                    + computer + " ").getBytes(), message));
        Packet response = communicator.query(packet,
            defaultTimeout);
        return response.getResponse();
    }
    
    private ListenerManager<ImmediateMessageListener> imessageListeners = new ListenerManager<ImmediateMessageListener>();
}
