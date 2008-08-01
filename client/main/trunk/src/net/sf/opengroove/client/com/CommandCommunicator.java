package net.sf.opengroove.client.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

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
        communicator.addPacketListener(new PacketListener()
        {
            
            @Override
            public void receive(Packet packet)
            {
                if (packet.getCommand().equalsIgnoreCase(
                    "receiveimessage"))
                {
                    String firstSubsection = new String(
                        packet.getContents(), 0, Math.min(
                            128,
                            packet.getContents().length));
                    String[] tokens = firstSubsection
                        .split(" ", 4);
                    final String messageId = tokens[0];
                    final String sendingUsername = tokens[1];
                    final String sendingComputer = tokens[2];
                    int dataIndex = messageId.length()
                        + sendingUsername.length()
                        + sendingComputer.length() + 3;
                    final byte[] messageContents = new byte[packet
                        .getContents().length
                        - dataIndex];
                    System.arraycopy(packet.getContents(),
                        dataIndex, messageContents, 0,
                        messageContents.length);
                    // Ok, we've put the message together. Now we notify all
                    // imessage listeners of the message.
                    imessageListeners
                        .notify(new Notifier<ImmediateMessageListener>()
                        {
                            
                            @Override
                            public void notify(
                                ImmediateMessageListener listener)
                            {
                                listener.receive(messageId,
                                    sendingUsername,
                                    sendingComputer,
                                    messageContents);
                            }
                        });
                }
                else if (packet.getCommand()
                    .equalsIgnoreCase("usernotification"))
                {
                    String packetContents = new String(
                        packet.getContents());
                    String[] tokens = tokenizeByLines(packetContents);
                    final String dateIssuedString = tokens[0];
                    final String dateExpiresString = tokens[1];
                    final String priorityString = tokens[2];
                    final String subject = tokens[3];
                    String message = tokens[4];
                    // Now append the remaining lines of the notification
                    for (int i = 5; i < tokens.length; i++)
                    {
                        message += "\n" + tokens[i];
                    }
                    final String fMessage = message;
                    userNotificationListeners
                        .notify(new Notifier<UserNotificationListener>()
                        {
                            
                            @Override
                            public void notify(
                                UserNotificationListener listener)
                            {
                                listener
                                    .receive(
                                        Long
                                            .parseLong(dateIssuedString),
                                        Long
                                            .parseLong(dateExpiresString),
                                        UserNotificationListener.Priority
                                            .valueOf(priorityString
                                                .trim()
                                                .toUpperCase()),
                                        subject, fMessage);
                            }
                        });
                }
            }
        });
    }
    
    public static String[] tokenizeByLines(String data)
    {
        BufferedReader reader = new BufferedReader(
            new StringReader(data));
        ArrayList<String> tokens = new ArrayList<String>();
        String s;
        try
        {
            while ((s = reader.readLine()) != null)
                tokens.add(s);
            reader.close();
        }
        catch (IOException e)
        {
            // shouldn't happen
            throw new RuntimeException(e);
        }
        return tokens.toArray(new String[0]);
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
    
    public void addImmediateMessageListener(
        ImmediateMessageListener listener)
    {
        imessageListeners.addListener(listener);
    }
    
    public void removeImmediateMessageListener(
        ImmediateMessageListener listener)
    {
        imessageListeners.removeListener(listener);
    }
    
    private ListenerManager<UserNotificationListener> userNotificationListeners = new ListenerManager<UserNotificationListener>();
    
    public void addUserNotificationListener(
        UserNotificationListener listener)
    {
        userNotificationListeners.addListener(listener);
    }
    
    public void removeUserNotificationListener(
        UserNotificationListener listener)
    {
        userNotificationListeners.removeListener(listener);
    }
    
}
