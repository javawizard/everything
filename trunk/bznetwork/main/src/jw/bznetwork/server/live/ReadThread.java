package jw.bznetwork.server.live;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;

import jw.bznetwork.client.data.model.LogEvent;
import jw.bznetwork.client.data.model.Server;
import jw.bznetwork.client.live.LivePlayer;
import jw.bznetwork.client.live.LivePlayer.TeamType;
import jw.bznetwork.server.data.DataStore;

/**
 * A thread that reads from the bzfs instance and processes the read data.
 * Currently, this includes updating its parent LiveServer to reflect the list
 * of players and such, sending log events to the database
 * 
 * @author Alexander Boyd
 * 
 */
public class ReadThread extends Thread
{
    private static final int BUFFER_SIZE = 1024;
    
    private LiveServer server;
    
    private InputStream serverUnbufferedIn;
    
    private BufferedInputStream bufferedIn;
    
    private DataInputStream in;
    
    private StringBuffer stdoutBuffer;
    
    public ReadThread(LiveServer server)
    {
        this.server = server;
        this.serverUnbufferedIn = server.getProcess().getInputStream();
        this.bufferedIn = new BufferedInputStream(serverUnbufferedIn,
                BUFFER_SIZE);
        this.in = new DataInputStream(bufferedIn);
    }
    
    public void run()
    {
        logStatus("The server process has started up.");
        /*
         * Any output relevant to us will begin with a single pipe character
         * followed by 5 characters which represents a number. That number is
         * the number of bytes after that to read, so we then read that number
         * of bytes.
         */
        int i;
        try
        {
            while (true)
            {
                i = in.read();
                if (i == -1)
                    break;
                if (i == '|')
                {
                    byte[] lengthBytes = new byte[5];
                    in.readFully(lengthBytes);
                    int length = Integer.parseInt(new String(lengthBytes));
                    byte[] data = new byte[length];
                    in.readFully(data);
                    /*
                     * At this point we've read all the data that we need to.
                     * We'll go ahead and process it.
                     */
                    try
                    {
                        processData(data);
                    }
                    catch (Exception e)
                    {
                        logStatus("Data processing error: "
                                + e.getClass().getName() + ": "
                                + e.getMessage());
                    }
                }
                else
                {
                    System.out.write(i);
                    if (stdoutBuffer == null)
                    {
                        stdoutBuffer = new StringBuffer();
                    }
                    stdoutBuffer.append((char) i);
                    if (i == '\n')
                    {
                        String outString = stdoutBuffer.toString();
                        if (!outString.trim().equals(""))
                        {
                            logStdout(outString);
                        }
                        stdoutBuffer = null;
                    }
                    System.out.flush();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logStatus("Read thread exception: " + e.getClass().getName() + ": "
                    + e.getMessage());
            try
            {
                server.getProcess().destroy();
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
        }
        /*
         * The server has shut down when we get to here. We'll notify the
         * LiveServer, which can take care of removing itself from the live
         * server list.
         */
        String statusMessage = "The server process has terminated";
        int exitCode = Integer.MIN_VALUE;
        try
        {
            exitCode = server.getProcess().exitValue();
        }
        catch (Exception e)
        {
        }
        if (exitCode != Integer.MIN_VALUE)
            statusMessage += " with exit code " + exitCode;
        statusMessage += ".";
        logStatus(statusMessage);
        if (server.getLoadListenerQueue() != null)
            server.getLoadListenerQueue().offer("bznfail dead");
        server.completedShutdown();
    }
    
    public synchronized void logStdout(String outString)
    {
        LogEvent event = new LogEvent();
        event.setServerid(server.getId());
        event.setEvent("stdout");
        event.setData(outString);
        DataStore.addLogEvent(event);
    }
    
    public synchronized void logStatus(String outString)
    {
        LogEvent event = new LogEvent();
        event.setServerid(server.getId());
        event.setEvent("status");
        event.setData(outString);
        DataStore.addLogEvent(event);
    }
    
    private void processData(byte[] dataBytes)
    {
        /*
         * Currently, all data output from the server will contain only visible
         * characters, so we'll just put it into a string.
         */
        String data = new String(dataBytes);
        if (data.startsWith("playerjoin "))
            processPlayerJoin(data.substring("playerjoin ".length()));
        else if (data.startsWith("playerpart "))
            processPlayerPart(data.substring("playerpart ".length()));
        else if (data.startsWith("chatmessage "))
            processChatMessage(data.substring("chatmessage ".length()));
        else if (data.equals("bznload"))
            processBznLoad();
        else if (data.startsWith("bznfail "))
            processBznFail(data.substring("bznfail ".length()));
        else if (data.equals("bznunload"))
            processBznUnload();
        else if (data.startsWith("slashcommand "))
            processSlashCommand(data.substring("slashcommand ".length()));
        else if (data.startsWith("messagefiltered"))
            processMessageFiltered(data.substring("messagefiltered ".length()));
        else
        {
            Server serverObject = DataStore.getServerById(server.getId());
            int port = -1;
            if (serverObject != null)
                port = serverObject.getPort();
            System.out.println("Unknown data received from server "
                    + server.getId() + " which runs on port " + port + ": "
                    + data);
        }
    }
    
    private void processMessageFiltered(String substring)
    {
        String[] tokens = substring.split("\\|", 2);
        int playerId = Integer.parseInt(tokens[0]);
        String message = "";
        if (tokens.length > 1)
            message = tokens[1];
        LogEvent event = new LogEvent();
        event.setServerid(server.getId());
        event.setEvent("filtered");
        event.setSourceid(playerId);
        LivePlayer player = server.getIdsToPlayers().get(playerId);
        if (playerId == LiveServer.SERVER)
            event.setSource("+server");
        else if (player != null)
            event.setSource(player.getCallsign());
        else
            event.setSource("+unknown");
        if (player != null && player.getTeam() != null)
            event.setSourceteam(player.getTeam().name());
        event.setData(message);
        DataStore.addLogEvent(event);
    }
    
    private void processSlashCommand(String substring)
    {
        String[] tokens = substring.split("\\|", 2);
        int playerId = Integer.parseInt(tokens[0]);
        String message = "";
        if (tokens.length > 1)
            message = tokens[1];
        LogEvent event = new LogEvent();
        event.setServerid(server.getId());
        if (message.toLowerCase().startsWith("/report "))
            event.setEvent("report");
        else
            event.setEvent("slashcommand");
        event.setSourceid(playerId);
        LivePlayer player = server.getIdsToPlayers().get(playerId);
        if (playerId == LiveServer.SERVER)
            event.setSource("+server");
        else if (player != null)
            event.setSource(player.getCallsign());
        else
            event.setSource("+unknown");
        if (player != null && player.getTeam() != null)
            event.setSourceteam(player.getTeam().name());
        if (event.getEvent().equals("report"))
            event.setData(message.substring("/report ".length()));
        else
            event.setData(message);
        DataStore.addLogEvent(event);
    }
    
    private void processBznFail(String substring)
    {
        if (server.getLoadListenerQueue() != null)
            server.getLoadListenerQueue().offer("bznfail " + substring);
    }
    
    private void processBznLoad()
    {
        if (server.getLoadListenerQueue() != null)
            server.getLoadListenerQueue().offer("bznload");
        server.setChangingState(false);
        server.setStarting(false);
    }
    
    private void processBznUnload()
    {
        /*
         * TODO: forcibly terminate the server's process here, since we can't
         * really keep running if the bznetwork plugin unloads.
         */
        logStatus("The bznetwork plugin was unloaded. No events will be "
                + "logged until it is reloaded or the server is restarted.");
    }
    
    private void processChatMessage(String substring)
    {
        String[] tokens = substring.split("\\|", 4);
        int fromId = Integer.parseInt(tokens[0]);
        LivePlayer fromPlayer = server.getIdsToPlayers().get(fromId);
        int toId = Integer.parseInt(tokens[1]);
        LivePlayer toPlayer = server.getIdsToPlayers().get(toId);
        /*
         * fromPlayer and toPlayer can be null at this point if the message was
         * from the server or to all or a team
         */
        String toTeam = tokens[2];
        String message = "";
        if (tokens.length > 3)
            message = tokens[3];
        LogEvent event = new LogEvent();
        String chatType = "unknown";
        event.setServerid(server.getId());
        event.setSourceid(fromId);
        if (fromId == LiveServer.SERVER)
            event.setSource("+server");
        else if (fromPlayer != null)
            event.setSource(fromPlayer.getCallsign());
        else
            event.setSource("+unknown");
        if (fromPlayer != null && fromPlayer.getTeam() != null)
            event.setSourceteam(fromPlayer.getTeam().name());
        if (toId == LiveServer.ALL)
        {
            event.setTarget("+all");
            if ("+server".equals(event.getSource()))
                chatType = "server";
            else
                chatType = "broadcast";
        }
        else if (toId == LiveServer.NONE)
        {
            event.setTarget("+" + toTeam);
            if (toTeam.toLowerCase().startsWith("adm"))
                chatType = "admin";
            else
                chatType = "team";
        }
        else if (toPlayer != null)
        {
            event.setTarget(toPlayer.getCallsign());
            chatType = "private";
        }
        if (toPlayer != null && toPlayer.getTeam() != null)
            event.setSourceteam(toPlayer.getTeam().name());
        event.setTargetid(toId);
        if (toId == LiveServer.NONE)
            event.setTargetteam(toTeam);
        event.setData(message);
        event.setEvent("chat-" + chatType);
        DataStore.addLogEvent(event);
    }
    
    private void processPlayerJoin(String substring)
    {
        String[] tokens = substring.split("\\|");
        LivePlayer player = new LivePlayer();
        player.setId(Integer.parseInt(tokens[0]));
        player.setIpaddress(tokens[1]);
        player.setTeam(LivePlayer.colorToTeam(tokens[2]));
        player.setVerified(tokens[3].equals("verified"));
        player.setCallsign(tokens[4]);
        player.setEmail(tokens[5]);
        player.setBzid(tokens.length > 6 ? tokens[6] : "");
        server.getPlayers().add(player);
        server.getCallsignsToPlayers().put(player.getCallsign(), player);
        server.getIdsToPlayers().put(player.getId(), player);
        LogEvent event = new LogEvent();
        event.setBzid(player.getBzid());
        event.setEmail(player.getEmail());
        event.setEvent("join");
        event.setIpaddress(player.getIpaddress());
        event.setServerid(server.getId());
        event.setSource(player.getCallsign());
        event.setSourceid(player.getId());
        event.setSourceteam(player.getTeam().name());
        event.setData((player.isVerified() ? "verified" : "notverified") + " "
                + (player.isAdmin() ? "admin" : "notadmin"));
        DataStore.addLogEvent(event);
    }
    
    private void processPlayerPart(String substring)
    {
        String[] tokens = substring.split("\\|", 8);
        int id = Integer.parseInt(tokens[0]);
        String reason = tokens.length > 7 ? tokens[7] : "(no reason)";
        LivePlayer player = server.getIdsToPlayers().get(id);
        LogEvent event = new LogEvent();
        event.setBzid(player.getBzid());
        event.setEmail(player.getEmail());
        event.setEvent("part");
        event.setIpaddress(player.getIpaddress());
        event.setServerid(server.getId());
        event.setSource(player.getCallsign());
        event.setSourceid(player.getId());
        event.setSourceteam(player.getTeam().name());
        event.setData(reason);
        DataStore.addLogEvent(event);
        server.getPlayers().remove(player);
        server.getCallsignsToPlayers().remove(player.getCallsign());
        server.getIdsToPlayers().remove(player.getId());
    }
}
