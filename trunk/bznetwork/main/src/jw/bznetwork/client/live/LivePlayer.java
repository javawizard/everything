package jw.bznetwork.client.live;

import java.io.Serializable;

public class LivePlayer implements Serializable
{
    public LivePlayer()
    {
    }
    
    private int id;
    private String ipaddress;
    private String callsign;
    private String email;
    private TeamType team;
    private boolean admin;
    private boolean verified;
    
    public int hashCode()
    {
        return 31 * id;
    }
    
    public boolean equals(Object obj)
    {
        if (!(obj instanceof LivePlayer))
            return false;
        return ((LivePlayer) obj).id == id;
    }
    
    private String bzid;
    
    public static enum TeamType
    {
        red, green, blue, purple, observer, rogue, rabbit, hunters, admin, noteam
    };
    
    public static TeamType colorToTeam(String name)
    {
        if (name.equals("hunter"))
            name = "hunters";
        else if (name.equals("administrator"))
            name = "admin";
        return TeamType.valueOf(name);
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public String getIpaddress()
    {
        return ipaddress;
    }
    
    public void setIpaddress(String ipaddress)
    {
        this.ipaddress = ipaddress;
    }
    
    public String getCallsign()
    {
        return callsign;
    }
    
    public void setCallsign(String callsign)
    {
        this.callsign = callsign;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public TeamType getTeam()
    {
        return team;
    }
    
    public void setTeam(TeamType team)
    {
        this.team = team;
    }
    
    public boolean isAdmin()
    {
        return admin;
    }
    
    public void setAdmin(boolean admin)
    {
        this.admin = admin;
    }
    
    public boolean isVerified()
    {
        return verified;
    }
    
    public void setVerified(boolean verified)
    {
        this.verified = verified;
    }
    
    public String getBzid()
    {
        return bzid;
    }
    
    public void setBzid(String bzid)
    {
        this.bzid = bzid;
    }
}
