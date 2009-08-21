package jw.bznetwork.client.data;

import java.io.Serializable;
import java.util.EnumMap;

import com.google.gwt.user.client.rpc.IsSerializable;

import jw.bznetwork.client.data.model.Server;
import jw.bznetwork.client.live.LivePlayer;

public class ServerModel extends Server implements Serializable, IsSerializable
{
    private EnumMap<LivePlayer.TeamType, Integer> teamLimits = new EnumMap<LivePlayer.TeamType, Integer>(
            LivePlayer.TeamType.class);
    
    public EnumMap<LivePlayer.TeamType, Integer> getTeamLimits()
    {
        return teamLimits;
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
