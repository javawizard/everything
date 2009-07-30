package jw.bznetwork.client.data;

import java.io.Serializable;
import java.util.HashMap;

import jw.bznetwork.client.data.model.Authgroup;

public class EditAuthgroupsModel implements Serializable
{
    public EditAuthgroupsModel()
    {
        
    }
    
    private HashMap<Integer, String> roleIdsToNames = new HashMap<Integer, String>();
    private Authgroup[] authgroups;
    public Authgroup[] getAuthgroups()
    {
        return authgroups;
    }
    public void setAuthgroups(Authgroup[] authgroups)
    {
        this.authgroups = authgroups;
    }
    public HashMap<Integer, String> getRoleIdsToNames()
    {
        return roleIdsToNames;
    }
}
