package jw.bznetwork.client.data;

import java.io.Serializable;

import jw.bznetwork.client.data.model.Action;
import jw.bznetwork.client.data.model.UserPair;

public class ActionLogModel implements Serializable
{
    public ActionLogModel()
    {
        super();
    }
    
    private Action[] actions;
    private int count;
    private UserPair[] users;
    
    public Action[] getActions()
    {
        return actions;
    }
    
    public void setActions(Action[] actions)
    {
        this.actions = actions;
    }
    
    public int getCount()
    {
        return count;
    }
    
    public void setCount(int count)
    {
        this.count = count;
    }
    
    public UserPair[] getUsers()
    {
        return users;
    }
    
    public void setUsers(UserPair[] users)
    {
        this.users = users;
    }
    
    public String[] getEventNames()
    {
        return eventNames;
    }
    
    public void setEventNames(String[] eventNames)
    {
        this.eventNames = eventNames;
    }
    
    private String[] eventNames;
}
