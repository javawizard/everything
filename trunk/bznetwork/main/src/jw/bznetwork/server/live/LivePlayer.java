package jw.bznetwork.server.live;

public class LivePlayer
{
    private String ip;
    private String name;
    private String email;
    private boolean admin;
    private boolean verified;
    
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
    
    public String getIp()
    {
        return ip;
    }
    
    public void setIp(String ip)
    {
        this.ip = ip;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
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
}
