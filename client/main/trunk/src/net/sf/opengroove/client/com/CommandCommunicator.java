package net.sf.opengroove.client.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;

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
                                        parseDateString(dateIssuedString),
                                        parseDateString(dateExpiresString),
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
    
    /**
     * Pings the server. This command sends a ping command and waits until it
     * receives a response before returning.
     * 
     * @throws IOException
     */
    public void ping() throws IOException
    {
        // This must be communicator.query, not communicator.send!
        communicator.query(new Packet(null, "ping",
            new byte[0]), defaultTimeout);
    }
    
    /**
     * Queries the server for it's current time. The result is the server's time
     * in milliseconds since January 1, 1970 UTC.
     * 
     * @return The server's current time, in milliseconds since January 1, 1970
     *         UTC
     * @throws IOException
     */
    public long getTime() throws IOException
    {
        Packet response = communicator.query(new Packet(
            null, "gettime", new byte[0]), defaultTimeout);
        return parseDateString(new String(response
            .getContents()));
    }
    
    /**
     * Searches for users.
     * 
     * @param searchText
     *            The text to search for. If this text is present in either the
     *            user's username or one of the user settings specified, and the
     *            user is {@link #setVisibility(boolean) visible}, then the
     *            user will be included in this list.
     * @param offset
     *            The offset to return results from. This can be used to only
     *            download a page of search results at a time from the server.
     * @param limit
     *            The maximum number of results that will be returned at a time.
     * @param searchOtherRealms
     *            true to search other realm servers (in which case the search
     *            may be slow), false to only search this realm server.
     * @param userSettings
     *            A list of user settings to search in addition to the user's
     *            username. If this is the empty array, only the user's username
     *            will be searched.
     * @param timeout
     *            The timeout to wait for the results to come back. If it's -1,
     *            then the {@link #setDefaultTimeout(int) default timeout} is
     *            used. Generally, the timeout should be higher than that if
     *            other realm servers are searched.
     * @return
     * @throws IOException
     */
    public UserSearch searchUsers(String searchText,
        int offset, int limit, boolean searchOtherRealms,
        String[] userSettings, int timeout)
        throws IOException
    {
        String contents = searchText + "\n" + offset + "\n"
            + limit + "\n" + searchOtherRealms + "\n";
        for (String s : userSettings)
        {
            contents += s + "\n";
        }
        Packet response = communicator.query(new Packet(
            null, "searchusers", contents.getBytes()),
            defaultTimeout);
        String[] tokens = tokenizeByLines(new String(
            response.getContents()));
        String[] results = new String[tokens.length - 1];
        int totalResults = Integer.parseInt(tokens[0]);
        System.arraycopy(tokens, 1, results, 0,
            results.length);
        UserSearch search = new UserSearch();
        search.setResults(results);
        search.setTotal(totalResults);
        return search;
    }
    
    /**
     * Sets your visibility. When your account is created, this is, by default,
     * false. If this is false, then you won't be returned as part of any
     * searchUsers requests. If this is true, then you can be returned as part
     * of a searchUsers request. Changes to this may take a few minutes to
     * propegate through all of the realm servers, although the change will
     * generally be available to other users on your realm server immediately.
     * 
     * @param visible
     *            True to make you visible to other users, so that your username
     *            can be searched for using the searchUsers command, false to
     *            hide your information
     * @throws IOException
     */
    public void setVisibility(boolean visible)
        throws IOException
    {
        communicator.query(new Packet(null,
            "setvisibility", ("" + visible).getBytes()),
            defaultTimeout);
    }
    
    /**
     * Returns whether or not this user is publicly listed.
     * 
     * @return True if this user is publicly listed, false if not. See
     *         {@link #setVisibility(boolean)} for more info.
     * @throws IOException
     */
    public boolean getVisibility() throws IOException
    {
        return new String(communicator.query(
            new Packet(null, "getvisibility", new byte[0]),
            defaultTimeout).getContents()).trim()
            .equalsIgnoreCase("true");
    }
    
    /**
     * Gets the current status of the user specified. If <code>computer</code>
     * is null or the empty string, the resulting user status will reflect all
     * of the user's computers. This means that if any of the computers are
     * online, then isOnline(), when called on the returned object, will return
     * true, and getLastOnline() will return the last online time of the
     * computer that was most recently online.
     * 
     * @param username
     *            The username of the user to check
     * @param computer
     *            The name of the computer to get status for, or the empty
     *            string to get the status of all of the user's computers
     * @return A UserStatus object that reflects the status of the user
     * @throws IOException
     *             If an I/O error occurs
     */
    public UserStatus getUserStatus(String username,
        String computer) throws IOException
    {
        if (computer == null)
            computer = "";
        Packet response = communicator.query(new Packet(
            null, "getuserstatus",
            (username + "\n" + computer).getBytes()),
            defaultTimeout);
        UserStatus status = new UserStatus();
        String[] tokens = tokenizeByLines(new String(
            response.getContents()));
        status.setOnline(tokens[0].trim().equalsIgnoreCase(
            "true"));
        status.setLastOnline(parseDateString(tokens[1]));
        return status;
    }
    
    /**
     * Gets a setting from the user specified. If the username is null or the
     * empty string, then the setting is for this user, and the key can be
     * anything. If the username is not null or the empty string, then
     * <code>key</code> can only start with public- .
     * 
     * @param username
     *            The username of the user to get the setting for, or null or
     *            the empty string for this user
     * @param key
     *            The key of the property to get, which must only start with
     *            public- unless the username specified is null or the empty
     *            string
     * @return The value of the user setting specified
     */
    public String getUserSetting(String username, String key)
    {
        
    }
    
    /**
     * Lists the settings for the user specified. If the username is null or the
     * empty string, then all properties for this user will be returned. If the
     * username is not null or the empty string, then only properties that begin
     * with public- and are for the user specified will be returned.
     * 
     * @param username
     *            The name of the user to get properties for, or null or the
     *            empty string for this user
     * @return The list of user properties for the user specified, which will
     *         all start with public- unless the username is null or the empty
     *         string
     */
    public String[] listUserSettings(String username)
    {
        
    }
    
    /**
     * Sets the user setting specified, for this user.
     * 
     * @param key
     *            The key, or name, of the user setting to set
     * @param value
     *            The value that the user setting is to have
     */
    public void setUserSetting(String key, String value)
    {
        
    }
    
    /**
     * Creates a subscription with the properties specified.
     * 
     * @param subscription
     *            The new subscription to create.
     */
    public void createSubscription(Subscription subscription)
    {
        
    }
    
    /**
     * Lists all of this user's subscriptions.
     * 
     * @return All of this user's subscriptions.
     */
    public Subscription[] listSubscriptions()
    {
        
    }
    
    /**
     * Deletes a subscription.
     * 
     * @param subscription
     *            The information of the subscription to delete.
     */
    public void deleteSubscription(Subscription subscription)
    {
        
    }
    
    /**
     * Lists all of the computers for the user specified.
     * 
     * @param username
     *            The name of the user to list computers for, or null or the
     *            empty string to list this user's computers
     * @return A list of the computer names
     */
    public String[] listComputers(String username)
    {
        
    }
    
    /**
     * Creates a new computer.
     * 
     * @param computerName
     *            The name of the computer
     * @param computerType
     *            The type of the new computer, which cannot be changed. As of
     *            the writing of this document, the standard values for the type
     *            are pc, pda, and mobile.
     */
    public void createComputer(String computerName,
        String computerType)
    {
        
    }
    
    /**
     * Gets a particular setting for a computer.
     * 
     * @param username
     *            the username of the user who owns the computer to get a
     *            setting for
     * @param computer
     *            the computer to get the setting for.
     * @param key
     *            the key, or name, of the setting to get
     * @return the setting's value
     */
    public String getComputerSetting(String username,
        String computer, String key)
    {
        
    }
    
    /**
     * lists all of the settings for the computer specified.
     * 
     * @param username
     *            the username of the user to check
     * @param computer
     *            the name of the computer
     * @return the settings for the computer specified
     */
    public String[] listComputerSettings(String username,
        String computer)
    {
        
    }
    
    /**
     * sets the specified computer setting on a computer owned by this user.
     * 
     * @param computer
     *            the name of the computer to set the settig on
     * @param key
     *            the key, or name, of the setting
     * @param value
     *            the value to set for the setting
     */
    public void setComputerSetting(String computer,
        String key, String value)
    {
        
    }
    
    /**
     * deletes the computer specified. This deletes all of the computer's data,
     * so care should be taken when running this method.<br/><br/>
     * 
     * The server usually batches up tasks related to deleting a computer, so it
     * may take anywhere from a few minutes to an hour or more for resources
     * related to the computer (such as pending workspace items or message cache
     * space) to be reclaimed. The only guarantee about this method is that
     * calls to listComputers that happen after this method is called will not
     * return this computer.<br/><br/>
     * 
     * Due to the fact that the server usually batches up tasks related to
     * deleting a computer, a new computer with the same name as an old one
     * should not be created for at least 24 hours after the old one was
     * deleted.
     * 
     * @param computer The name of the computer to delete.
     */
    public void deleteComputer(String computer)
    {
        
    }
    
    private ListenerManager<SubscriptionListener> subscriptionListeners = new ListenerManager<SubscriptionListener>();
    
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
    
    /**
     * Parses the specified date string, in the format returned by the
     * OpenGroove Realm Server, into a long representing the number of
     * milliseconds since January 1, 1970. The standard date format in the
     * server protocol is the number of milliseconds since january 1, 1970, a
     * space, and the text-readable date in the format returned by
     * {@link java.util.Date#toString()}
     * 
     * @param dateString
     *            The string representing the date to parse
     * @return a long indicating the value of the date specified
     */
    public static long parseDateString(String dateString)
    {
        return Long.parseLong(dateString.split("\\ ", 2)[0]
            .trim());
    }
    
    /**
     * Formats the specified long into a date that the server can recognize.
     * 
     * @param date
     * @return
     */
    public static String formatDateString(long date)
    {
        return "" + date + " " + new Date(date).toString();
    }
    
}
