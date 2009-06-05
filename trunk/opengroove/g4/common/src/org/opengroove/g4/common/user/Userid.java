package org.opengroove.g4.common.user;

/**
 * A G4 userid. A userid can represent a server, a user, or a computer. A userid
 * can also be relative to a server or to a user.
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
            // server
            server = spec.substring(0, spec.length() - 2);
        }
        else if (spec.matches("^[^\\:]*\\:\\:[^\\:]*$"))
        {
            // server and username
            String[] tokens = spec.split("\\:\\:");
            server = tokens[0];
            username = tokens[1];
        }
    }
}
