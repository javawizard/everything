package net.sf.opengroove.client;

import javax.swing.JTabbedPane;

import net.sf.opengroove.client.com.CommandCommunicator;
import net.sf.opengroove.client.help.HelpViewer;

/**
 * A user context object is created for each user that logs in, and is passed
 * down through the hierarchy of objects created so that they can get a
 * reference to which user the objects are for. User objects also allow various
 * listeners, which can listen for events such as that the user is logging out.<br/><br/>
 * 
 * This class consists mostly of fields that used to be static fields on the
 * OpenGroove class, and that were used for management of the current user.
 * 
 * @author Alexander Boyd
 * 
 */
public class UserContext
{
    private String userid;
    
    private CommandCommunicator com;
    
    private String password;
    
    private JTabbedPane launchbarTabbedPane;
    private HelpViewer helpViewer;
    
    public String getUserid()
    {
        return userid;
    }
    
    public void setUserid(String userid)
    {
        this.userid = userid;
    }
    
    public String getUsername()
    {
        return UserIds.toUsername(userid);
    }
    
    public String getRealm()
    {
        return UserIds.toRealm(userid);
    }
}
