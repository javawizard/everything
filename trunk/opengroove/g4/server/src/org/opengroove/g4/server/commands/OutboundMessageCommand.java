package org.opengroove.g4.server.commands;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

import org.opengroove.g4.common.messaging.Message;
import org.opengroove.g4.common.protocol.InboundMessagePacket;
import org.opengroove.g4.common.protocol.MessageResponse;
import org.opengroove.g4.common.protocol.OutboundMessagePacket;
import org.opengroove.g4.common.user.Userid;
import org.opengroove.g4.common.utils.ObjectUtils;
import org.opengroove.g4.server.Command;
import org.opengroove.g4.server.G4Server;
import org.opengroove.g4.server.ServerConnection;
import org.opengroove.g4.server.commands.types.ComputerCommand;

@ComputerCommand
public class OutboundMessageCommand implements Command<OutboundMessagePacket>
{
    
    public void process(OutboundMessagePacket packet)
    {
        ServerConnection connection = ServerConnection.getConnection();
        /*
         * First, we serialize the message to disk. If it already exists as a
         * recipient's message, then we'll assume that it has already been
         * uploaded for that recipient. Then we queue it for sending to the
         * recipient. Then we respond saying that we successfully sent the
         * message.
         */
        String id = packet.getMessageId();
        MessageResponse response = new MessageResponse();
        response.setPacketThread(packet.getPacketThread());
        response.setMessageId(id);
        if (!id.startsWith(connection.getUserid().withoutComputer().toString() + "$"))
        {
            /*
             * Whenever we get a message-related exception, we'll want to tell
             * the client to delete the message, so they don't keep trying to
             * resend it.
             */
            connection.send(response.respondTo(packet));
            throw new RuntimeException(
                "The message's id does not start with the user's"
                    + " userid followed by a $ sign. "
                    + "This server requires message ids to conform to this.");
        }
        ArrayList<Userid> recipientList = new ArrayList<Userid>();
        Userid sender = connection.userid;
        for (Userid normalRecipient : packet.getRecipients())
        {
            /*
             * Relativize the userid to the sender. This makes it so that
             * messages targeted to only a computer will be sent to the sender's
             * computer of that name, and messages targeted to a user will be
             * sent to that user on this server.
             */
            normalRecipient = normalRecipient.relativeTo(sender.withoutComputer());
            if (!normalRecipient.getServer().equals(G4Server.serverName))
            {
                connection.send(response.respondTo(packet));
                throw new RuntimeException("Sending messages to users on other "
                    + "servers is not allowed right now. This feature will "
                    + "be added in the near future.");
            }
            /*
             * The recipient is on this server. Now we see if they specify a
             * computer.
             */
            if (normalRecipient.hasComputer())
            {
                /*
                 * The recipient specifies a computer. We'll add them directly
                 * to the list.
                 */
                G4Server.verifyComputerExists(normalRecipient);
                recipientList.add(normalRecipient);
            }
            else
            {
                /*
                 * The recipient does not specify a computer. We'll add all of
                 * their computers to the list.
                 */
                G4Server.verifyUserExists(normalRecipient);
                recipientList.addAll(Arrays.asList(G4Server
                    .listComputers(normalRecipient)));
            }
        }
        /*
         * We now have the actual list of computers to send the message to, and
         * all of them exist. We'll go and write them to disk now in the
         * computer's recipient message folder, and then send the message to the
         * recipient if they are online. But first we'll dispatch the message if
         * it is a profile message.
         */
        if (recipientList.size() == 1
            && recipientList.get(0).getUsername().equals("_profile"))
        {
            connection.dispatchProfileMessage(packet);
            /*
             * Notify the client that we received the profile message
             */
            connection.send(response);
            return;
        }
        InboundMessagePacket inboundMessage = new InboundMessagePacket();
        inboundMessage.setMessageId(id);
        inboundMessage.setSender(connection.getUserid());
        inboundMessage.setMessage(packet.getMessage());
        for (Userid user : recipientList)
        {
            try
            {
                File messageFolder = G4Server.getMessageFolder(user);
                File messageFile = new File(messageFolder, URLEncoder.encode(id));
                if (messageFile.exists())
                    continue;
                if (!messageFolder.getAbsoluteFile().getParentFile().exists())
                {
                    /*
                     * Message to a nonexistant recipient, since the message
                     * folder's parent folder (the user's folder) doesn't exist;
                     * We'll discard the message. If the message itself is a
                     * user message, we'll send a reply user message.
                     */
                    if (inboundMessage.getMessage() instanceof Message)
                    {
                        Message messageObject = (Message) inboundMessage.getMessage();
                        G4Server.sendUserMessage(connection, G4Server.serverUserid,
                            new Userid[] { connection.getUserid() },
                            "Nonexistent recipient",
                            "You just sent a message. One of the recipients was "
                                + user
                                + ". This user does not exist, so the message was "
                                + "not sent to them. The message has been "
                                + "sent to all other recipients.", messageObject
                                .getHeader().getMessageId(), messageObject.getHeader()
                                .getSubject());
                    }
                    continue;
                }
                messageFolder.mkdirs();
                ObjectUtils.writeObject(inboundMessage, messageFile);
                /*
                 * We've written the message to disk. Now we'll queue it for
                 * sending to the target user.
                 */
                ServerConnection recipientConnection = G4Server.connections.get(user);
                if (recipientConnection != null)
                    recipientConnection.send(inboundMessage);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
        /*
         * The message has been sent. We'll reply to the user now, stating that
         * the message has been sent.
         */
        connection.send(response.respondTo(packet));
    }
}
