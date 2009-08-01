package jw.bznetwork.server;

public class LivePlayer
{
    private String ip;
    private String name;
    private String email;
    private boolean admin;
    private boolean verified;
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
