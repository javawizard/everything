package net.sf.opengroove.common.utils;

/**
 * This class contains utility methods for parsing and generating user ids.
 * 
 * @author Alexander Boyd
 * 
 */
public class Userids
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
     * Extracts the name of the user's realm from the userid. If the userid does
     * not have a realm component, or if the userid is a username, the empty
     * string is returned.
     * 
     * @param userid
     *            The userid
     * @return The realm portion of the userid specified
     */
    public static String toRealm(String userid)
    {
        if (userid == null)
            return "";
        if (!userid.contains(":"))
            return "";
        return userid.split("\\:")[0];
    }
    
    /**
     * Extracts the user's username from the userid. If the userid specified
     * does not have a username component (e.g. "example.com:"), the empty
     * string is returned. If the userid specified is a username, then it is
     * returned without modification.
     * 
     * @param userid
     *            The userid
     * @return The username portion of the userid specified
     */
    public static String toUsername(String userid)
    {
        if (userid.indexOf(":") == -1)
            return userid;
        return userid.split("\\:")[1];
    }
    
    /**
     * Returns a new userid that is equal to the one specified, but with the
     * realm set to the realm specified.
     * 
     * @param userid
     * @param realm
     * @return
     */
    public static String setRealm(String userid,
        String realm)
    {
        return toUserid(realm, toUsername(userid));
    }
    
    /**
     * Returns a new userid that is equal to the one specified, but with the
     * username set to the username specified.
     */
    
    public static String setUsername(String userid,
        String username)
    {
        return toUserid(toRealm(userid), username);
    }
    
    /**
     * Resolves a username to a userid, relative to another username. More
     * precisely, if <code>toResolve</code> is already a userid, then it is
     * returned without modification. If it is a username, then it is made into
     * a userid by extracting the realm from <code>relativeTo</code> and
     * adding it to the username. For example:<br/><br/>
     * 
     * resolveTo("realm:username","differentrealm:differentusername")<br/><br/>
     * 
     * would evaluate to "realm:username", but:<br/><br/>
     * 
     * resolveTo("username","differentrealm:differentusername")<br/><br/>
     * 
     * would evaluate to "differentrealm:username".
     * 
     * @param toResolve
     *            The userid or username to resolve
     * @param relativeTo
     *            The userid that <code>toResolve</code> should be interpreted
     *            relative to if <code>toResolve</code> is a username
     * @return The relativized userid.
     */
    public static String resolveTo(String toResolve,
        String relativeTo)
    {
        checkUseridOrUsername(toResolve);
        checkUserid(relativeTo);
    }
    
    public static void checkUserid(String userid)
    {
        if (!isUserid(userid))
            throw new IllegalArgumentException("The value "
                + userid
                + " is not a userid, as was expected");
    }
    
    public static void checkUseridOrUsername(
        String useridOrUsername)
    {
        if (!(isUserid(useridOrUsername) || isUsername(useridOrUsername)))
            throw new IllegalArgumentException(
                "The value "
                    + useridOrUsername
                    + " is not a userid or a username, as was expected");
    }
    
    public static boolean isUserid(String userid)
    {
        return userid.contains(":")
            && userid.indexOf(":") == userid
                .lastIndexOf(":");
    }
    
    public static boolean isUsername(String username)
    {
        return !username.contains(":");
    }
    
}
