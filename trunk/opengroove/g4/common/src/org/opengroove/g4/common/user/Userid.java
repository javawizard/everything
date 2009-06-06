package org.opengroove.g4.common.user;

/**
 * A G4 userid. A userid can represent a server, a user, or a computer. A userid
 * can also be relative to a server or to a user.<br/>
 * <br/>
 * 
 * Userid objects are immutable.
 * 
 * @author Alexander Boyd
 * 
 */
public class Userid
{
    private String server;
    private String username;
    private String computer;
    
    /**
     * Creates a userid based on the spec specified. An IllegalArgumentException
     * will be thrown if the spec is not valid.
     * 
     * @param spec
     */
    public Userid(String spec)
    {
        if (spec.matches("^[^\\:]*\\:\\:$"))
        {
            // server, "server"
            server = spec.substring(0, spec.length() - 2);
        }
        else if (spec.matches("^[^\\:]*\\:\\:[^\\:]*$"))
        {
            // server and username, "server::username"
            String[] tokens = spec.split("\\:\\:");
            server = tokens[0];
            username = tokens[1];
        }
        else if (spec.matches("^[^\\:]*\\:\\:[^\\:]*\\:[^\\:]*$"))
        {
            // server, username, and computer, "server::username:computer"
            String[] tokens = spec.split("\\:\\:");
            server = tokens[0];
            String[] tokens2 = tokens[1].split("\\:");
            username = tokens2[0];
            computer = tokens2[1];
        }
        else if (spec.matches("^[^\\:]*$"))
        {
            // username, "username"
            username = spec;
        }
        else if (spec.matches("^\\:[^\\:]*$"))
        {
            // computer, "computer"
            computer = spec;
        }
        else if (spec.matches("^[^\\:]*\\:[^\\:]*$"))
        {
            // username and computer, "username:computer"
            String[] tokens = spec.split("\\:\\:");
            username = tokens[0];
            computer = tokens[1];
        }
        else
            throw new IllegalArgumentException("Invalid userid spec: " + spec);
    }
    
    public Userid(String server, String username, String computer)
    {
        super();
        if ((server == null && username == null && computer == null)
            || (server != null && username == null && computer != null))
            throw new IllegalArgumentException("Malformed userid, s:" + server + ",u:"
                + username + ",c:" + computer);
        this.server = server;
        this.username = username;
        this.computer = computer;
    }
    
    public String getServer()
    {
        return server;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public String getComputer()
    {
        return computer;
    }
    
    public void setServer(String server)
    {
        this.server = server;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public void setComputer(String computer)
    {
        this.computer = computer;
    }
    
    /**
     * Checks to see if this username either has the server specified or doesn't
     * have a server at all.
     * 
     * @param server
     * @return
     */
    public boolean isServerOrRelative(String server)
    {
        return this.server == null || server.equals(this.server);
    }
    
    /**
     * If this userid is relative to a server only, then returns an absolute
     * userid on the server specified. Otherwise, returns this userid
     * unmodified. If this userid is relative to a user as well as a server,
     * then this method throws an IllegalArgumentException.
     * 
     * @param serverName
     * @return
     */
    public Userid relativeToServer(String serverName)
    {
        if (this.username == null)
            throw new IllegalArgumentException(
                "Trying to relativize a username-relative userid against a server");
        if (this.server == null)
            return new Userid(serverName, this.username, this.computer);
        return this;
    }
    
    /**
     * Screens the value specified. If it contains a : character, an exception
     * will be thrown.
     * 
     * @param value
     * @return
     */
    public static String screen(String value)
    {
        if (value.contains(":"))
            throw new IllegalArgumentException("The value " + value
                + " has a : character, which is not allowed");
        return value;
    }
    
    public String toString()
    {
        boolean s = server != null;
        boolean u = username != null;
        boolean c = computer != null;
        if (s && !u && !c)
            return server + "::";
        else if (s && u && !c)
            return server + "::" + username;
        else if (s && u && c)
            return server + "::" + username + ":" + computer;
        else if (!s && u && c)
            return username + ":" + computer;
        else if (!s && !u && c)
            return ":" + computer;
        else if (!s && u && !c)
            return username;
        else
            throw new IllegalArgumentException("Malformed userid, s:" + server + ",u:"
                + username + ",c:" + computer);
    }
    
    public boolean hasServer()
    {
        return server != null;
    }
    
    public boolean hasUsername()
    {
        return username != null;
    }
    
    public boolean hasComputer()
    {
        return computer != null;
    }
    
    public boolean isRelative()
    {
        return !hasServer();
    }
}
