package jw.bznetwork.client.data;

import java.io.Serializable;
import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

import jw.bznetwork.client.data.model.Banfile;

/**
 * 
 * @author Alexander Boyd
 * 
 */
public class ServerListModel implements Serializable, IsSerializable
{
    public GroupModel[] getGroups()
    {
        return groups;
    }
    
    public void setGroups(GroupModel[] groups)
    {
        this.groups = groups;
    }
    
    private GroupModel[] groups;
    
    public ServerListModel()
    {
        super();
        // TODO Auto-generated constructor stub
    }
    
    /**
     * This will be filled in if the user can edit the banfile of at least one
     * group or server in the list.
     */
    private Banfile[] banfiles;
    
    private HashMap<Integer, Banfile> banfileMap = new HashMap<Integer, Banfile>();
    
    public HashMap<Integer, Banfile> getBanfileMap()
    {
        return banfileMap;
    }
    
    public Banfile[] getBanfiles()
    {
        return banfiles;
    }
    
    public void setBanfiles(Banfile[] banfiles)
    {
        this.banfiles = banfiles;
    }
}
