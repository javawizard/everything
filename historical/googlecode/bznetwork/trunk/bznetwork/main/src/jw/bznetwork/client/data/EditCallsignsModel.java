package jw.bznetwork.client.data;

import java.io.Serializable;
import java.util.HashMap;

import jw.bznetwork.client.data.model.Callsign;

public class EditCallsignsModel implements Serializable
{
    public EditCallsignsModel()
    {
        
    }
    
    private HashMap<Integer, String> roleIdsToNames = new HashMap<Integer, String>();
    private Callsign[] callsigns;
    
    public Callsign[] getCallsigns()
    {
        return callsigns;
    }
    
    public void setCallsigns(Callsign[] authgroups)
    {
        this.callsigns = authgroups;
    }
    
    public HashMap<Integer, String> getRoleIdsToNames()
    {
        return roleIdsToNames;
    }
}
