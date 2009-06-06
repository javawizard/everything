package org.opengroove.g4.server.commands;

import java.util.ArrayList;

import org.opengroove.g4.common.protocol.OutboundMessagePacket;
import org.opengroove.g4.common.user.Userid;
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
        if (!id.startsWith(connection.getUserid().withoutComputer().toString() + "$"))
            throw new RuntimeException(
                "The message's id does not start with the user's"
                    + " userid followed by a $ sign. "
                    + "This server requires message ids to conform to this.");
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
                throw new RuntimeException("Sending messages to users on other "
                    + "servers is not allowed right now. This feature will "
                    + "be added in the near future.");
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
                recipientList.add(normalRecipient);
            }
            else
            {
                /*
                 * The recipient does not specify a computer. We'll add all of
                 * their computers to the list.
                 */
                G4Server.listComputers(normalRecipient);
            }
        }
    }
}
