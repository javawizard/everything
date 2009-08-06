package jw.bznetwork.client.data;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

import jw.bznetwork.client.data.model.Group;

public class GroupModel extends Group implements Serializable, IsSerializable
{
    
    private ServerModel[] servers;
    
    public GroupModel()
    {
    }
    
    public ServerModel[] getServers()
    {
        return servers;
    }
    
    public void setServers(ServerModel[] servers)
    {
        this.servers = servers;
    }
    
}
