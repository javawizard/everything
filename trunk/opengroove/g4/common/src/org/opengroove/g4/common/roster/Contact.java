package org.opengroove.g4.common.roster;

import java.io.Serializable;

import org.opengroove.g4.common.user.Userid;

public class Contact implements Serializable
{
    private Userid userid;
    private String name;
    private boolean visible;
    
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
}
