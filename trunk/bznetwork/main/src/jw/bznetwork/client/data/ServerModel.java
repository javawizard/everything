package jw.bznetwork.client.data;

import java.io.Serializable;

import jw.bznetwork.client.data.model.Server;
import jw.bznetwork.client.live.LivePlayer;

public class ServerModel extends Server implements Serializable
{
    private LivePlayer[] players;
    public LivePlayer[] getPlayers()
    {
        return players;
    }

    public void setPlayers(LivePlayer[] players)
    {
        this.players = players;
    }

    public boolean isLive()
    {
        return isLive;
    }

    public void setLive(boolean isLive)
    {
        this.isLive = isLive;
    }

    private boolean isLive;
    
    public ServerModel()
    {
        super();
        // TODO Auto-generated constructor stub
    }
    
}
