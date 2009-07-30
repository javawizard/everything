package jw.bznetwork.client.data;

import java.io.Serializable;

import jw.bznetwork.client.data.model.Group;
import jw.bznetwork.client.data.model.Server;

public class GroupedServer extends Server implements Serializable
{
    public GroupedServer()
    {
        
    }
    
    private Group parent;
    
    public Group getParent()
    {
        return parent;
    }
    
    public void setParent(Group parent)
    {
        this.parent = parent;
    }
}
