package jw.bznetwork.client.data.model;

import java.io.Serializable;

public class Callsign implements Serializable
{
    private String callsign;
    private int role;
    
    public String getCallsign()
    {
        return callsign;
    }
    
    public void setCallsign(String callsign)
    {
        this.callsign = callsign;
    }
    
    public int getRole()
    {
        return role;
    }
    
    public void setRole(int role)
    {
        this.role = role;
    }
}
