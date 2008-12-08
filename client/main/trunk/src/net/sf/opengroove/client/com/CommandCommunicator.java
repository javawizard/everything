package net.sf.opengroove.client.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;

import net.sf.opengroove.client.com.model.StoredMessage;
import net.sf.opengroove.client.com.model.StoredMessageRecipient;
import net.sf.opengroove.client.com.model.Subscription;
import net.sf.opengroove.client.com.model.UserSearch;
import net.sf.opengroove.client.com.model.UserStatus;
import net.sf.opengroove.common.utils.StringUtils;
import net.sf.opengroove.common.utils.StringUtils.ToString;

/**
 * This class wraps the Communicator class, and provides methods for doing
 * common interaction tasks with the server.
 * 
 * @author Alexander Boyd
 * 
 */
public class CommandCommunicator
{
    private static final int GLOBAL_DEFAULT_TIMEOUT = 15 * 1000;
    // 5 seconds, this should be re-thought, as a value too low would prevent
    // downloading large message chunks on a slow network, and a value too high
    // could cause application freezing upon a dropped connection. Actually, a
    // UI should probably be added into OpenGroove for allowing the user to
    // configure this. Perhaps there should be two variables, one for short
    // requests, and one for long ones such as sending a message, receiving a
    // message chunk, or searching for users.
    //
    // Update: on searching for users, this will no longer be a long-time
    // request. See http://www.opengroove.org/dev/protocol/commands for more
    // info.
    
    private Communicator communicator;
    
    private int defaultTimeout = GLOBAL_DEFAULT_TIMEOUT;
    /**
     * The long timeout. This timeout is used for commands that transfer large
     * amounts of data, so that the command will not time out because of upload
     * speed restrictions. By default, this is 35 seconds.
     */
    private int longTimeout = 35 * 1000;
    
    public CommandCommunicator(
        final Communicator communicator)
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
                    .equalsIgnoreCase("subscriptionevent"))
                {
                    Subscription parsed;
                    try
                    {
                        parsed = stringToSubscription(
                            new String(packet.getContents()),
                            "\n");
                    }
                    catch (IllegalArgumentException e)
                    {
                        communicator.reconnect();
                        throw new RuntimeException(e);
                    }
                    final Subscription subscription = parsed;
                    subscriptionListeners
                        .notify(new Notifier<SubscriptionListener>()
                        {
                            
                            @Override
                            public void notify(
                                SubscriptionListener listener)
                            {
                                listener
                                    .event(subscription);
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
                else if (packet.getCommand()
                    .equalsIgnoreCase("messageavailable"))
                {
                    final String messageId = new String(packet
                        .getContents());
                    messageAvailableListeners
                        .notify(new Notifier<MessageAvailableListener>()
                        {

                            public void notify(
                                MessageAvailableListener listener)
                            {
                                listener
                                    .messageAvailable(messageId);
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
    
    public String authenticate(String connectionType,
        String username, String computer, String password)
        throws IOException
    {
        Packet packet = new Packet(null, "authenticate",
            (connectionType + "\n" + username + "\n"
                + computer + "\n" + password).getBytes());
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
        // This must be communicator.query, not communicator.send
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
     * TODO: this needs to be redone to match the new user search spec
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
     *            may be slow), false to only search this realm server. Exactly
     *            which other realm servers are searched is up to this user's
     *            realm server, but it is typically all servers listed in the
     *            public server directory, as well as any additional servers
     *            that this user's realm server is configured to search.
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
     * of a searchUsers request. Changes to this may take a few minutes (or even
     * longer, the OpenGroove specs specify a maximum prepegation time of 24
     * hours, although it is generally much shorter than that) to propegate
     * through all of the realm servers, although the change will generally be
     * available to other users on your realm server immediately.
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
     * @throws IOException
     */
    public String getUserSetting(String username, String key)
        throws IOException
    {
        Packet response = communicator.query(new Packet(
            null, "getusersetting", (username + "\n" + key)
                .getBytes()), defaultTimeout);
        if (new String(response.getContents()).trim()
            .equals(""))
            return null;
        return new String(response.getContents());
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
     * @throws IOException
     */
    public String[] listUserSettings(String username)
        throws IOException
    {
        Packet response = communicator.query(new Packet(
            null, "listusersettings", username.getBytes()),
            defaultTimeout);
        return tokenizeByLines(new String(response
            .getContents()));
    }
    
    /**
     * Sets the user setting specified, for this user.
     * 
     * @param key
     *            The key, or name, of the user setting to set
     * @param value
     *            The value that the user setting is to have
     * @throws IOException
     */
    public void setUserSetting(String key, String value)
        throws IOException
    {
        communicator.query(new Packet(null,
            "setusersetting", (key + "\n" + value)
                .getBytes()), defaultTimeout);
    }
    
    /**
     * Creates a subscription with the properties specified.
     * 
     * @param subscription
     *            The new subscription to create.
     * @throws IOException
     */
    public void createSubscription(Subscription subscription)
        throws IOException
    {
        communicator.query(new Packet(null,
            "createsubscription", subscriptionToString(
                subscription, "\n").getBytes()),
            defaultTimeout);
    }
    
    /**
     * Converts the subscription specified into a string, with each field
     * delimited by the delimiter specified.
     * 
     * @param delimiter
     * @return
     */
    private String subscriptionToString(
        Subscription subscription, String delimiter)
    {
        return subscription.getType() + delimiter
            + subscription.getOnUser() + delimiter
            + subscription.getOnComputer() + delimiter
            + subscription.getOnSetting() + delimiter
            + subscription.isDeleteWithTarget();
    }
    
    /**
     * Converts the string specified into a subscription object, assuming the
     * delimiter specified is used to delimit fields.
     * 
     * @param string
     * @param delimiter
     * @return
     */
    private Subscription stringToSubscription(
        String string, String delimiter)
    {
        try
        {
            String[] tokens = string.trim().split(
                "\\" + delimiter);
            Subscription subscription = new Subscription();
            subscription.setType(tokens[0]);
            subscription.setOnUser(tokens[1]);
            subscription.setOnComputer(tokens[2]);
            subscription.setOnSetting(tokens[3]);
            subscription.setDeleteWithTarget(tokens[4]
                .trim().equalsIgnoreCase("true"));
            return subscription;
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new IllegalArgumentException(
                "The subscription denoted by the string \""
                    + string
                    + "\" using delimiter \""
                    + delimiter
                    + "\" could not be parsed, because it did not contain "
                    + "enough tokens to parse into a subscription.");
        }
    }
    
    /**
     * Lists all of this user's subscriptions.
     * 
     * @return All of this user's subscriptions.
     * @throws IOException
     */
    public Subscription[] listSubscriptions()
        throws IOException
    {
        Packet response;
        try
        {
            response = communicator.query(new Packet(null,
                "listsubscriptions", new byte[0]),
                defaultTimeout);
        }
        catch (FailedResponseException e)
        {
            /*
             * For now we'll assume it was because of a NORESULTS error. In the
             * future, we should actually check the error that caused it
             * (perhaps by moving OpenGrooveRealmServer.Status into it's own
             * enum in OpenGroove Commons and adding a field to this exception,
             * or sharing this exception with it's counterpart in OpenGroove
             * Realm Server).
             */
            return new Subscription[0];
        }
        String[] responseTokens = tokenizeByLines(new String(
            response.getContents()));
        ArrayList<Subscription> subscriptions = new ArrayList<Subscription>();
        for (String responseToken : responseTokens)
        {
            if (!responseToken.trim().equals(""))
            {
                try
                {
                    subscriptions.add(stringToSubscription(
                        responseToken.trim(), " "));
                }
                catch (IllegalArgumentException e)
                {
                    /*
                     * Some weird error keeps cropping up where info coming from
                     * the server gets corrupted. It only seems to happen every
                     * once in a while, and when it happens, it persists
                     * throughout the life of a connection. This leads me to
                     * believe that it's something to do with a certain choice
                     * of random security key for the conversation. Reconnecting
                     * the communicator forces it to get a new key, and seems to
                     * solve the problem.
                     * 
                     * TODO: Figure out a real fix for this problem. I spend a
                     * couple of days on this problem alone but didn't get
                     * anywhere.
                     */
                    communicator.reconnect();
                    throw e;
                }
            }
        }
        return subscriptions.toArray(new Subscription[0]);
    }
    
    /**
     * Returns true if the user indicated by the userid or username specified
     * exists, false if the user does not. The server may limit the rate at
     * which this can be called to prevent searching for users who are not
     * publicly visible by dictionary attacking their userids using this method.
     * 
     * @param userid
     *            The userid or username to check
     * @return True if the user specified exists; false if not
     * @throws IOException
     *             if an I/O error occurs
     */
    public boolean userExists(String userid)
        throws IOException
    {
        System.out
            .println("sending server message to see if user "
                + userid + " exists");
        try
        {
            getUserStatus(userid, "");
        }
        catch (TimeoutException e)
        {
            throw e;
        }
        catch (FailedResponseException e)
        {
            return false;
        }
        return true;
    }
    
    /**
     * Deletes a subscription.
     * 
     * @param subscription
     *            The information of the subscription to delete.
     * @throws IOException
     */
    public void deleteSubscription(Subscription subscription)
        throws IOException
    {
        communicator.query(new Packet(null,
            "deletesubscription", subscriptionToString(
                subscription, "\n").getBytes()),
            defaultTimeout);
    }
    
    /**
     * Lists all of the computers for the user specified.
     * 
     * @param username
     *            The name of the user to list computers for, or null or the
     *            empty string to list this user's computers
     * @return A list of the computer names
     * @throws IOException
     */
    public String[] listComputers(String username)
        throws IOException
    {
        if (username == "")
            username = "\n";
        try
        {
            return tokenizeByLines(new String(communicator
                .query(
                    new Packet(null, "listcomputers",
                        username.getBytes()),
                    defaultTimeout).getContents()));
        }
        catch (FailedResponseException e)
        {
            if (e.getResponseCode().equalsIgnoreCase(
                "NORESULTS"))
                return new String[0];
            throw e;
        }
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
     * @throws IOException
     */
    public void createComputer(String computerName,
        String computerType) throws IOException
    {
        communicator.query(
            new Packet(null, "createcomputer",
                (computerName + "\n" + computerType)
                    .getBytes()), defaultTimeout);
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
     * @throws IOException
     */
    public String getComputerSetting(String username,
        String computer, String key) throws IOException
    {
        try
        {
            Packet response = communicator
                .query(
                    new Packet(
                        null,
                        "getcomputersetting",
                        (username + "\n" + computer + "\n" + key)
                            .getBytes()), defaultTimeout);
            if (new String(response.getContents()).trim()
                .equals(""))
                return null;
            return new String(response.getContents());
        }
        catch (FailedResponseException e)
        {
            /*
             * We really should check to see if it's a response type that
             * indicates a nonexistant property, computer, or user, and return
             * null, and throw the exception back out if it's not one of those
             * types.
             */
            return null;
        }
    }
    
    /**
     * lists all of the settings for the computer specified.
     * 
     * @param username
     *            the username of the user to check
     * @param computer
     *            the name of the computer
     * @return the settings for the computer specified
     * @throws IOException
     */
    public String[] listComputerSettings(String username,
        String computer) throws IOException
    {
        return tokenizeByLines(new String(
            communicator
                .query(
                    new Packet(null,
                        "listcomputersettings", (username
                            + "\n" + computer).getBytes()),
                    defaultTimeout).getContents()));
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
     * @throws IOException
     */
    public void setComputerSetting(String computer,
        String key, String value) throws IOException
    {
        communicator
            .query(new Packet(null, "setcomputersetting",
                (computer + "\n" + key + "\n" + value)
                    .getBytes()), defaultTimeout);
    }
    
    public void createMessage(String messageId,
        StoredMessageRecipient[] recipients)
        throws IOException
    {
        communicator.query(new Packet(null,
            "createmessage",
            (messageId + "\n" + StringUtils.delimited(
                recipients,
                new ToString<StoredMessageRecipient>()
                {
                    
                    public String toString(
                        StoredMessageRecipient object)
                    {
                        return object.getUserid()
                            + (object.getComputer() == null ? ""
                                : " "
                                    + object.getComputer());
                    }
                }, "\n")).getBytes()), defaultTimeout);
    }
    
    public int getMessageSize(String messageId)
        throws IOException
    {
        Packet response = communicator.query(new Packet(
            null, "getmessagesize", messageId.getBytes()),
            defaultTimeout);
        String responseString = new String(response
            .getContents());
        return Integer.parseInt(responseString.trim());
    }
    
    public byte[] readMessageData(String messageId,
        int offset, int length) throws IOException
    {
        return communicator
            .query(
                new Packet(
                    null,
                    "readmessagedata",
                    (messageId + "\n" + offset + "\n" + length)
                        .getBytes()), longTimeout)
            .getContents();
    }
    
    public void writeMessageData(String messageId,
        int offset, int length, byte[] bytes)
        throws IOException
    {
        communicator.query(new Packet(null,
            "writemessagedata", Communicator
                .concat((messageId + " " + offset + " "
                    + length + " ").getBytes(), bytes)),
            longTimeout);
    }
    
    public void deleteMessage(String messageId)
        throws IOException
    {
        communicator.query(new Packet(null,
            "deletemessage", messageId.getBytes()),
            defaultTimeout);
    }
    
    public void sendMessage(String messageId)
        throws IOException
    {
        communicator.query(new Packet(null, "sendmessage",
            messageId.getBytes()), defaultTimeout);
    }
    
    public String[] listInboundMessages()
        throws IOException
    {
        try
        {
            Packet response = communicator.query(
                new Packet(null, "listinboundmessages",
                    new byte[0]), defaultTimeout);
            return tokenizeByLines(new String(response
                .getContents()));
        }
        catch (FailedResponseException e)
        {
            /*
             * We'll assume it's a NORESULTS code
             */
            return new String[0];
        }
    }
    
    public String[] listOutboundMessages()
        throws IOException
    {
        try
        {
            Packet response = communicator.query(
                new Packet(null, "listoutboundmessages",
                    new byte[0]), defaultTimeout);
            return tokenizeByLines(new String(response
                .getContents()));
        }
        catch (FailedResponseException e)
        {
            /*
             * We'll assume it's a NORESULTS code
             */
            return new String[0];
        }
    }
    
    public StoredMessage getMessageInfo(String messageId)
        throws IOException
    {
        try
        {
            Packet response = communicator.query(
                new Packet(null, "getmessageinfo",
                    messageId.getBytes()), defaultTimeout);
            String[] tokens = tokenizeByLines(new String(
                response.getContents()));
            StoredMessage message = new StoredMessage();
            message.setMessageId(messageId);
            message.setSender(tokens[0]);
            message.setComputer(tokens[1]);
            message.setSent(tokens[2]
                .equalsIgnoreCase("true"));
            return message;
        }
        catch (FailedResponseException e)
        {
            if (e.getResponseCode().equalsIgnoreCase(
                "nosuchmessage")
                || e.getResponseCode().equalsIgnoreCase(
                    "unauthorized"))
                return null;
            throw e;
        }
    }
    
    /**
     * deletes the computer specified. This deletes all of the computer's data,
     * so care should be taken when running this method.<br/><br/>
     * 
     * The server usually batches up tasks related to deleting a computer, so it
     * may take anywhere from a few minutes to an hour or more for resources
     * related to the computer (such as pending workspace items or message cache
     * space) to be reclaimed. The only guarantee about this method is that
     * calls to listComputers by this user that happen after this method is
     * called will not return this computer.<br/><br/>
     * 
     * Due to the fact that the server usually batches up tasks related to
     * deleting a computer, a new computer with the same name as an old one
     * should not be created for at least 24 hours after the old one was
     * deleted. Otherwise, the new computer, or some of it's data, could be
     * sporadically delete.
     * 
     * @param computer
     *            The name of the computer to delete.
     * @throws IOException
     */
    public void deleteComputer(String computer)
        throws IOException
    {
        communicator.query(new Packet(null,
            "deletecomputer", computer.getBytes()),
            defaultTimeout);
    }
    
    private ListenerManager<SubscriptionListener> subscriptionListeners = new ListenerManager<SubscriptionListener>();
    
    public void addSubscriptionListener(
        SubscriptionListener listener)
    {
        subscriptionListeners.addListener(listener);
    }
    
    public void removeSubscriptionListener(
        SubscriptionListener listener)
    {
        subscriptionListeners.removeListener(listener);
    }
    
    private ListenerManager<MessageAvailableListener> messageAvailableListeners = new ListenerManager<MessageAvailableListener>();
    
    public void addMessageAvailableListener(
        MessageAvailableListener listener)
    {
        messageAvailableListeners.addListener(listener);
    }
    
    public void removeMessageAvailableListener(
        MessageAvailableListener listener)
    {
        messageAvailableListeners.removeListener(listener);
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
