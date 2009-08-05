package jw.bznetwork.server.live;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;

import jw.bznetwork.client.data.model.LogEvent;
import jw.bznetwork.client.live.LivePlayer;
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
                    processData(data);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            // FIXME: implement this to shut down the server and log an error
            // message about the exception
        }
        /*
         * The server has shut down when we get to here. We'll notify the
         * LiveServer, which can take care of removing itself from the live
         * server list.
         */
        if(server.getLoadListenerQueue() != null)
            server.getLoadListenerQueue().offer("bznfail dead");
        server.completedShutdown();
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
    }
    
    private void processBznUnload()
    {
        /*
         * TODO: forcibly terminate the server's process here, since we can't
         * really keep running if the bznetwork plugin unloads.
         */
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
        String message = tokens[3];
        LogEvent event = new LogEvent();
        event.setServerid(server.getId());
        event.setEvent("chat");
        event.setSourceid(fromId);
        if (fromId == LiveServer.SERVER)
            event.setSource("+server");
        else if (fromPlayer != null)
            event.setSource(fromPlayer.getCallsign());
        else
            event.setSource("+unknown");
        if (toId == LiveServer.ALL)
            event.setTarget("+all");
        else if (toId == LiveServer.NONE)
            event.setTarget("+" + toTeam);
        else if (toPlayer != null)
            event.setTarget(toPlayer.getCallsign());
        event.setTargetid(toId);
        if (toId == LiveServer.NONE)
            event.setTargetteam(toTeam);
        event.setData(message);
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
        player.setBzid(tokens[6]);
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
        DataStore.addLogEvent(event);
    }
    
    private void processPlayerPart(String substring)
    {
        String[] tokens = substring.split("\\|", 8);
        int id = Integer.parseInt(tokens[0]);
        String reason = tokens[7];
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
