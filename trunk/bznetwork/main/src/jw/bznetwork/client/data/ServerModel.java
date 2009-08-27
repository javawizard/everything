package jw.bznetwork.client.data;

import java.io.Serializable;
import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

import jw.bznetwork.client.data.model.Server;
import jw.bznetwork.client.live.LivePlayer;

public class ServerModel extends Server implements Serializable, IsSerializable
{
    
    private String detailString = "";
    private boolean autoExpand;
    
    public boolean isAutoExpand()
    {
        return autoExpand;
    }
    
    public void setAutoExpand(boolean autoExpand)
    {
        this.autoExpand = autoExpand;
    }
    
    /**
     * The text that should show up in the detail column for a server. This is
     * generated on the server-side instead of the client-side to improve
     * performance.
     * 
     * @return
     */
    public String getDetailString()
    {
        return detailString;
    }
    
    public void setDetailString(String detailString)
    {
        this.detailString = detailString;
    }
    
    public static enum LiveState
    {
        LIVE, STARTING, STOPPING, STOPPED
    }
    
    private LiveState state;
    
    public LiveState getState()
    {
        return state;
    }
    
    public void setState(LiveState state)
    {
        this.state = state;
    }
    
    private LivePlayer[] players;
    
    public LivePlayer[] getPlayers()
    {
        return players;
    }
    
    public void setPlayers(LivePlayer[] players)
    {
        this.players = players;
    }
    
    public ServerModel()
    {
    }
    
}
