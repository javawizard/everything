package net.sf.opengroove.client;

/**
 * This class contains utility methods for parsing and generating user ids.
 * 
 * @author Alexander Boyd
 * 
 */
public class UserIds
{
    
    /**
     * Converts the username and realm specified to a userid. Userids take the
     * format realm:username , so a user who's username is javawizard and who's
     * realm is opengroove.org would have the userid opengroove.org:javawizard .
     * 
     * @param realm
     *            The name of the realm
     * @param username
     *            The username
     * @return The corresponding userid
     */
    public static String toUserid(String realm,
        String username)
    {
        return realm + ":" + username;
    }
    
    /**
     * Extracts the name of the user's realm from the userid.
     * 
     * @param userid
     *            The userid
     * @return The realm portion of the userid specified
     */
    public static String toRealm(String userid)
    {
        return userid.split("\\:")[0];
    }
    
    /**
     * Extracts the user's username from the userid.
     * 
     * @param userid
     *            The userid
     * @return The username portion of the userid specified
     */
    public static String toUsername(String userid)
    {
        return userid.split("\\:")[1];
    }
    
}
