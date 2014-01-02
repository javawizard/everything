package org.opengroove.g4.server.commands;

import java.io.File;
import java.net.URLEncoder;

import org.opengroove.g4.common.protocol.MessageResponse;
import org.opengroove.g4.common.user.Userid;
import org.opengroove.g4.server.Command;
import org.opengroove.g4.server.G4Server;
import org.opengroove.g4.server.ServerConnection;
import org.opengroove.g4.server.commands.types.ComputerCommand;

@ComputerCommand
public class MessageResponseCommand implements Command<MessageResponse>
{
    
    public void process(MessageResponse packet)
    {
        ServerConnection connection = ServerConnection.getConnection();
        Userid user = connection.getUserid();
        File messageFolder = G4Server.getMessageFolder(user);
        File messageFile =
            new File(messageFolder, URLEncoder.encode(packet.getMessageId()));
        messageFile.delete();
    }
    
}
