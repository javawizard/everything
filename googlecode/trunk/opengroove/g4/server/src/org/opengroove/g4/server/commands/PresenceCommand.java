package org.opengroove.g4.server.commands;

import org.opengroove.g4.common.protocol.PresencePacket;
import org.opengroove.g4.common.protocol.PresencePacket.Status;
import org.opengroove.g4.server.Command;
import org.opengroove.g4.server.G4Server;
import org.opengroove.g4.server.ServerConnection;
import org.opengroove.g4.server.commands.types.ComputerCommand;

@ComputerCommand
public class PresenceCommand implements Command<PresencePacket>
{
    
    public void process(PresencePacket packet)
    {
        /*
         * Broadcasting offline presence packets isn't allowed for now, so if we
         * see an offline presence packet, we'll ignore it.
         */
        if (packet.getStatus() == Status.Offline)
        {
            System.out.println("Warning: offline status packet received, ignoring");
            return;
        }
        ServerConnection con = ServerConnection.getConnection();
        /*
         * Prevent the user from spoofing the stauts of other users
         */
        packet.setUserid(con.userid);
        /*
         * If the presence is online, then remove this computer's entry from the
         * idle table. If the presence is idle, set the entry.
         */
        if (packet.getStatus() == Status.Idle)
        {
            G4Server.idleTimes.put(con.userid, System.currentTimeMillis()
                - packet.getDuration());
        }
        else
        {
            G4Server.idleTimes.remove(con.userid);
        }
        /*
         * Now actually broadcast the presence packet
         */
        G4Server.updateContainingPresence(con.userid, packet);
        /*
         * That's it! Presence packets don't have a response packet, so there's
         * no need to send one.
         */
    }
}
