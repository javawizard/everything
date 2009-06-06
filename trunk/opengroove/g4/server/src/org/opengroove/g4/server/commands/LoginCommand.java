package org.opengroove.g4.server.commands;

import java.io.File;

import net.sf.opengroove.common.security.Hash;
import net.sf.opengroove.common.utils.StringUtils;

import org.opengroove.g4.common.protocol.LoginPacket;
import org.opengroove.g4.common.protocol.LoginResponse;
import org.opengroove.g4.common.user.Userid;
import org.opengroove.g4.server.Command;
import org.opengroove.g4.server.G4Server;
import org.opengroove.g4.server.ServerConnection;
import org.opengroove.g4.server.commands.types.UnauthCommand;

@UnauthCommand
public class LoginCommand implements Command<LoginPacket>
{
    /**
     * This must be synchronized in order to avoid issues with the same computer
     * logging in twice
     */
    public void process(LoginPacket packet)
    {
        ServerConnection connection = ServerConnection.getConnection();
        Userid userid = packet.getUserid();
        userid = userid.relativeTo(G4Server.serverUserid);
        synchronized (G4Server.authLock)
        {
            if (!userid.getServer().equals(G4Server.serverName))
            {
                connection.send(new LoginResponse(LoginResponse.Status.WrongServer,
                    "You tried to use the server " + userid.getServer()
                        + " as the server, but this server is " + G4Server.serverName));
                return;
            }
            String username = userid.getUsername();
            String computer = userid.getComputer();
            String password = packet.getPassword();
            File userFolder = new File(G4Server.authFolder, username);
            if (!userFolder.exists())
            {
                connection.send(new LoginResponse(LoginResponse.Status.BadAuth, null));
                return;
            }
            String realEncPassword =
                StringUtils.readFile(new File(userFolder, "password"));
            String encPassword = Hash.hash(password);
            if (!encPassword.equals(realEncPassword))
            {
                connection.send(new LoginResponse(LoginResponse.Status.BadAuth, null));
                return;
            }
            /*
             * PICK UP HERE, validate computer or make sure they didn't specify
             * one, then if there is a computer add to the connection map, if
             * there isn't just mark them as logged in. Make the userid
             * absolute, and add an equals and hashCode method to Userid.
             */
            if (computer != null)
            {
                File computerFile = new File(userFolder, "computers/" + computer);
                if (!computerFile.exists())
                {
                    /*
                     * The computer that the user is trying to sign on as
                     * doesn't currently exist
                     */
                    connection.send(new LoginResponse(LoginResponse.Status.BadComputer,
                        null));
                    return;
                }
                /*
                 * The computer does exist. Now validate that we're not already
                 * connected to the server.
                 */
                if (G4Server.connections.get(userid) != null)
                {
                    /*
                     * We're already connected as this computer
                     */
                    connection.send(new LoginResponse(
                        LoginResponse.Status.AlreadyConnected, null));
                    return;
                }
                /*
                 * Successful. We'll now go and add us to the connection map.
                 */
                G4Server.connections.put(userid, connection);
            }
            connection.userid = userid;
            connection.userFolder = userFolder;
            connection.send(new LoginResponse(LoginResponse.Status.Successful, null));
        }
        /*
         * Ok, we've successfully logged in. Now we'll send initial state.
         */
        connection.sendInitialLoginState(userid.hasComputer());
    }
}
