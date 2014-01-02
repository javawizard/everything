package org.opengroove.g4.common.roster;

import java.io.Serializable;

import org.opengroove.g4.common.user.Userid;

public class Contact implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -7570764169258873686L;
    /**
     * The username userid of this contact
     */
    private Userid userid;
    /**
     * The computers that this contact has
     */
    private Userid[] computers;
    /**
     * The local name that this user has set for the contact, or null if the
     * user has not set a local name for this contact
     */
    private String name;
    /**
     * The real name as set by the contact themselves, not the user that has
     * added the contact. Clients typically ignore this if a local name has been
     * set by this user.
     */
    private String realName;
    /**
     * True if this contact has been set as visible, false if this contact has
     * been hidden. In the old G3 terminology, a hidden contact was referred to
     * as a "known user" and a shown contact was referred to as a "contact".
     */
    private boolean visible;
    /**
     * True if this contact actually exists, false if they do not. Any userid
     * can be added to a user's contact list without problem, but the contact
     * will come back in the roster as non-existent. This is checked whenever
     * the roster is to be sent to the user, so if a nonexistent contact is
     * created but then that userid is later registered, then the next time any
     * users with that user as a contact connect they will get the new
     * information that the user does exist.
     */
    private boolean exists;
    
    public Userid getUserid()
    {
        return userid;
    }
    
    public void setUserid(Userid userid)
    {
        this.userid = userid;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public boolean isVisible()
    {
        return visible;
    }
    
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }
    
    public boolean isExists()
    {
        return exists;
    }
    
    public void setExists(boolean exists)
    {
        this.exists = exists;
    }
    
    public Userid[] getComputers()
    {
        return computers;
    }
    
    public void setComputers(Userid[] computers)
    {
        this.computers = computers;
    }
    
    public String getRealName()
    {
        return realName;
    }
    
    public void setRealName(String realName)
    {
        this.realName = realName;
    }
}
