package org.opengroove.g4.server.commands;

import java.io.File;

import org.opengroove.g4.common.protocol.RosterChangePacket;
import org.opengroove.g4.common.protocol.RosterChangePacket.Action;
import org.opengroove.g4.common.user.Userid;
import org.opengroove.g4.common.utils.PropUtils;
import org.opengroove.g4.server.Command;
import org.opengroove.g4.server.G4Server;
import org.opengroove.g4.server.ServerConnection;
import org.opengroove.g4.server.commands.types.ComputerCommand;
import org.opengroove.g4.server.commands.types.UserCommand;

/**
 * Receives RosterChangePackets. This also causes all computers of this user to
 * receive an updated roster.
 * 
 * @author Alexander Boyd
 * 
 */
@UserCommand
@ComputerCommand
public class RosterChangeCommand implements Command<RosterChangePacket>
{
    
    public void process(RosterChangePacket packet)
    {
        ServerConnection con = ServerConnection.getConnection();
        Action action = packet.getAction();
        File rosterFile = new File(con.userFolder, "roster");
        Userid contactUserid = packet.getContact();
        String contactUseridString = G4Server.toContactUseridString(contactUserid);
        String currentLine = PropUtils.getProperty(rosterFile, contactUseridString);
        if (action == Action.ADD)
        {
            /*
             * Add the user as visible and with a null local name if they aren't
             * already on the roster. If they are already on the roster, then
             * set the user to visible, and that's it.
             */
            if (currentLine == null)
            {
                /*
                 * This is a new contact. Set their line to visible with a null
                 * local name.
                 */
                currentLine = G4Server.createRosterLine(true, null);
            }
            else
            {
                /*
                 * This is an existing contact. Set their line to visible with
                 * their existing local name.
                 */
                currentLine =
                    G4Server.createRosterLine(true, G4Server
                        .rosterLineContactLocal(currentLine));
            }
            PropUtils.setProperty(rosterFile, contactUseridString, currentLine);
        }
        else if (action == Action.NAME)
        {
            /*
             * If the contact doesn't exist, this does nothing. If they do, this
             * changes their local name but retains their current visiblility.
             */
            if (currentLine != null)
            {
                currentLine =
                    G4Server.createRosterLine(
                        G4Server.rosterLineIsVisible(currentLine), packet.getName());
                PropUtils.setProperty(rosterFile, contactUseridString, currentLine);
            }
        }
        else if (action == Action.VISIBILITY)
        {
            /*
             * If the contact doesn't exist, this does nothing. If they do, this
             * changes their visibility but retains their current local name.
             */
            if (currentLine != null)
            {
                currentLine =
                    G4Server.createRosterLine(packet.isShown(), G4Server
                        .rosterLineContactLocal(currentLine));
                PropUtils.setProperty(rosterFile, contactUseridString, currentLine);
            }
        }
        /*
         * We've updated our roster. Now send it to all of the user's computers
         * that are signed on (which will include the computer that sent this
         * roster update).
         */
        G4Server.resendRoster(con.userid.withoutComputer(), con.userid);
        /*
         * This packet has no response.
         */
    }
}
