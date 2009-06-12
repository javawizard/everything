package org.opengroove.g4.server.commands;

import java.io.File;

import net.sf.opengroove.common.utils.StringUtils;

import org.opengroove.g4.common.protocol.RealNamePacket;
import org.opengroove.g4.server.Command;
import org.opengroove.g4.server.G4Server;
import org.opengroove.g4.server.ServerConnection;
import org.opengroove.g4.server.commands.types.ComputerCommand;
import org.opengroove.g4.server.commands.types.UserCommand;

/**
 * Receives RealNamePackets and processes them. This also causes all users that
 * have this user as a contact to receive updated rosters (reflecting this
 * user's new real name).
 * 
 * @author Alexander Boyd
 * 
 */
@UserCommand
@ComputerCommand
public class RealNameCommand implements Command<RealNamePacket>
{
    
    public void process(RealNamePacket packet)
    {
        ServerConnection con = ServerConnection.getConnection();
        String newRealName = packet.getName();
        File realNameFile = new File(con.userFolder, "realname");
        if (newRealName == null)
            realNameFile.delete();
        else
            StringUtils.writeFile(newRealName, realNameFile);
        G4Server.updateContainingRosters(con.userid);
        G4Server.sendToAnyOnlineComputers(con.userid, packet);
        /*
         * This packet has no response.
         */
    }
    
}
