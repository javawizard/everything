package jw.bznetwork.server;

import javax.servlet.http.HttpServletRequest;

/**
 * Provides methods related to the BZNetwork system as a whole.
 * 
 * @author Alexander Boyd
 * 
 */
public class BZNetwork
{
    /**
     * Sticks information on to the request indicating that the user has just logged in.
     * 
     * @param request
     * @param provider
     * @param username
     * @param roles
     */
    public static void login(HttpServletRequest request, String provider,
        String username, String[] roles)
    {
        
    }
}
