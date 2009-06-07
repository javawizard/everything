package org.opengroove.g4.server.commands;

import java.io.File;
import java.io.IOException;

import org.opengroove.g4.common.protocol.CreateComputerPacket;
import org.opengroove.g4.common.protocol.CreateComputerResponse;
import org.opengroove.g4.server.Command;
import org.opengroove.g4.server.ServerConnection;
import org.opengroove.g4.server.commands.types.ComputerCommand;
import org.opengroove.g4.server.commands.types.UserCommand;

@UserCommand
@ComputerCommand
public class CreateComputerCommand implements Command<CreateComputerPacket>
{
    
    public void process(CreateComputerPacket packet)
    {
        ServerConnection connection = ServerConnection.getConnection();
        File computersFolder = new File(connection.userFolder, "computers");
        File computerFile =
            new File(computersFolder, packet.getName().replaceAll("[\\.\\/\\\\", ""));
        if (computerFile.exists())
        {
            connection.respond(new CreateComputerResponse(
                CreateComputerResponse.Status.AlreadyExists, null));
            return;
        }
        try
        {
            computerFile.createNewFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getClass().getName() + ": " + e.getMessage(),
                e);
        }
    }
    
}
