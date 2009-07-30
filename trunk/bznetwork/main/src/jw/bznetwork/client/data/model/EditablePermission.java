package jw.bznetwork.client.data.model;

import java.io.Serializable;

public class EditablePermission extends Permission implements Serializable
{
    public EditablePermission()
    {
    }
    
    private String groupName;
    private String serverName;
    
    public String getGroupName()
    {
        return groupName;
    }
    
    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }
    
    public String getServerName()
    {
        return serverName;
    }
    
    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }
}
