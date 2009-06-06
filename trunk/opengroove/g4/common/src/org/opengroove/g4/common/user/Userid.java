package org.opengroove.g4.common.user;

import java.io.Serializable;

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
public class Userid implements Serializable
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
            // server, "server::"
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
            // computer, ":computer"
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
        this.server = screen(server);
        this.username = screen(username);
        this.computer = screen(computer);
    }
    
    public String getServer()
    {
        /*
         * The replacing is to sanitize the output in case someone tries to
         * include a file path in it.
         */
        return server.replaceAll("[\\.\\/\\:\\\\]", "");
    }
    
    public String getUsername()
    {
        return username.replaceAll("[\\.\\/\\:\\\\]", "");
    }
    
    public String getComputer()
    {
        return computer.replaceAll("[\\.\\/\\:\\\\]", "");
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
        if (this.username == null && this.computer != null)
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
    
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((computer == null) ? 0 : computer.hashCode());
        result = prime * result + ((server == null) ? 0 : server.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }
    
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Userid other = (Userid) obj;
        if (computer == null)
        {
            if (other.computer != null)
                return false;
        }
        else if (!computer.equals(other.computer))
            return false;
        if (server == null)
        {
            if (other.server != null)
                return false;
        }
        else if (!server.equals(other.server))
            return false;
        if (username == null)
        {
            if (other.username != null)
                return false;
        }
        else if (!username.equals(other.username))
            return false;
        return true;
    }
    
    public Userid withoutServer()
    {
        return new Userid(null, username, computer).checkNotEmpty();
    }
    
    public Userid withoutUsername()
    {
        if (hasServer() && hasComputer())
            throw new RuntimeException(
                "Removing the username would create an invalid userid. "
                    + "Remove the computer or the server first.");
        return new Userid(server, null, computer).checkNotEmpty();
    }
    
    public Userid withoutComputer()
    {
        return new Userid(server, username, computer).checkNotEmpty();
    }
    
    public Userid withServer(String server)
    {
        return new Userid(server, username, computer).checkNotEmpty();
    }
    
    public Userid withUsername(String username)
    {
        return new Userid(server, username, computer).checkNotEmpty();
    }
    
    public Userid withComputer(String computer)
    {
        return new Userid(server, username, computer).checkNotEmpty();
    }
    
    private Userid checkNotEmpty()
    {
        if (!(hasServer() || hasUsername() || hasComputer()))
            throw new RuntimeException("This userid is empty.");
        if (hasServer() && !hasUsername() && hasComputer())
            throw new RuntimeException("This userid doesn't have a username but has "
                + "everything else; this is not valid");
        return this;
    }
    
    /**
     * Converts this userid to a userid absolute to the one specified. If this
     * userid is absolute, then it is returned as-is. If this userid is
     * relative, then it is taken to be relative to the userid specified. If
     * this userid specifies only a computer and the absolute userid specifies
     * only a server, then an exception is thrown.
     * 
     * @param absolute
     *            An absolute userid to resolve this one against
     * @return
     */
    public Userid relativeTo(Userid absolute)
    {
        if (isAbsolute())
            return this;
        if (hasUsername())
            return new Userid(absolute.getServer(), username, computer).checkNotEmpty();
        return new Userid(absolute.getServer(), absolute.getUsername(), computer)
            .checkNotEmpty();
    }
    
    public boolean isAbsolute()
    {
        return hasServer();
    }
    
    /**
     * Returns a userid absolute to the specified server. If the userid passed
     * in is relative, it wil be made absolute to this server If it is absolute
     * and not of this server, an exception will be thrown.
     * 
     * @param server
     * @return
     */
    public Userid validateServer(String server)
    {
        if (isAbsolute() && !getServer().equals(server))
            throw new RuntimeException("Incorrect server");
        return relativeTo(new Userid(server + "::"));
    }
}
