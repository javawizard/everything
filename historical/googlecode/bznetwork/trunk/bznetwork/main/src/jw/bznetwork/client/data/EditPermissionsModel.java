package jw.bznetwork.client.data;

import java.io.Serializable;

import jw.bznetwork.client.data.model.Banfile;
import jw.bznetwork.client.data.model.EditablePermission;
import jw.bznetwork.client.data.model.Group;
import jw.bznetwork.client.data.model.Server;

public class EditPermissionsModel implements Serializable
{
    private EditablePermission[] permissions;
    private Group[] groups;
    private Banfile[] banfiles;
    
    public EditablePermission[] getPermissions()
    {
        return permissions;
    }
    
    public Banfile[] getBanfiles()
    {
        return banfiles;
    }
    
    public void setBanfiles(Banfile[] banfiles)
    {
        this.banfiles = banfiles;
    }
    
    public void setPermissions(EditablePermission[] permissions)
    {
        this.permissions = permissions;
    }
    
    public Group[] getGroups()
    {
        return groups;
    }
    
    public void setGroups(Group[] groups)
    {
        this.groups = groups;
    }
    
    public GroupedServer[] getServers()
    {
        return servers;
    }
    
    public void setServers(GroupedServer[] servers)
    {
        this.servers = servers;
    }
    
    /**
     * Servers in here only have their serverid, their name, and their parent
     * group id.
     */
    private GroupedServer[] servers;
    
    public EditPermissionsModel()
    {
        
    }
}
