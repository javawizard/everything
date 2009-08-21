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
    
    public static enum GameType
    {
        /**
         * Indicates that this game is a free-for-all. A free-for-all is a game
         * where there are no team bases and no team flags.
         */
        FreeForAll,
        /**
         * Indicates that this game is a capture-the-flag game. A
         * capture-the-flag game is one where there are team flags and bases to
         * capture those flags on.
         */
        CaptureTheFlag,
        /**
         * Indicates that this game is a rabbit-hunt game. A rabbit-hunt game is
         * where one player is the rabbit, and every other player attempts to
         * kill the rabbit. When a player kills the rabbit, that player becomes
         * the new rabbit.
         */
        RabbitHunt,
        /**
         * This should never occur in practice. It is used when bzfs returns a
         * game type from the bz_getGameType() method that is not one of the
         * above types. The only feasible reason that this could happen is if
         * this plugin were used with a newer version of bzfs that supported
         * some additional game types.
         */
        UnknownGameType
    }
    
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
