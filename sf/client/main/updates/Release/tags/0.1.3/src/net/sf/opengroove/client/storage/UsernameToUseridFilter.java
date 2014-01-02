package net.sf.opengroove.client.storage;

import net.sf.opengroove.common.proxystorage.ParameterFilter;
import net.sf.opengroove.common.utils.Userids;

/**
 * This filter can be applied to methods on a LocalUser object, with only one
 * argument that is a string. If the argument is a userid, then it allows it
 * through. If the argument is a username, then it adds the realm from the
 * LocalUser object in question, and allows it through.
 * 
 * @author Alexander Boyd
 * 
 */
public class UsernameToUseridFilter implements
    ParameterFilter
{
    
    @Override
    public Object[] filter(Object on, Object[] parameters)
    {
        String original = (String) parameters[0];
        if (Userids.isUserid(original))
            return new Object[] { original };
        return new Object[] { Userids.resolveTo(original,
            ((LocalUser) on).getUserid()) };
    }
    
}
