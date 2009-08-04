package jw.bznetwork.server.live;

public class LivePlayer
{
    private int id;
    private String ipaddress;
    private String callsign;
    private String email;
    private TeamType team;
    private boolean admin;
    private boolean verified;
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (admin ? 1231 : 1237);
        result = prime * result + ((bzid == null) ? 0 : bzid.hashCode());
        result = prime * result
                + ((callsign == null) ? 0 : callsign.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + id;
        result = prime * result
                + ((ipaddress == null) ? 0 : ipaddress.hashCode());
        result = prime * result + ((team == null) ? 0 : team.hashCode());
        result = prime * result + (verified ? 1231 : 1237);
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LivePlayer other = (LivePlayer) obj;
        if (admin != other.admin)
            return false;
        if (bzid == null)
        {
            if (other.bzid != null)
                return false;
        }
        else if (!bzid.equals(other.bzid))
            return false;
        if (callsign == null)
        {
            if (other.callsign != null)
                return false;
        }
        else if (!callsign.equals(other.callsign))
            return false;
        if (email == null)
        {
            if (other.email != null)
                return false;
        }
        else if (!email.equals(other.email))
            return false;
        if (id != other.id)
            return false;
        if (ipaddress == null)
        {
            if (other.ipaddress != null)
                return false;
        }
        else if (!ipaddress.equals(other.ipaddress))
            return false;
        if (team == null)
        {
            if (other.team != null)
                return false;
        }
        else if (!team.equals(other.team))
            return false;
        if (verified != other.verified)
            return false;
        return true;
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
