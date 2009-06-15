package net.sf.opengroove.client.g3com.model;

import net.sf.opengroove.common.utils.Userids;

public class Subscription
{
    private String type = "";
    private String onUser = "";
    private String onComputer = "";
    private String onSetting = "";
    private boolean deleteWithTarget;
    
    public Subscription(String type, String onUser,
        String onComputer, String onSetting,
        boolean deleteWithTarget)
    {
        super();
        this.type = type;
        this.onUser = onUser;
        this.onComputer = onComputer;
        this.onSetting = onSetting;
        this.deleteWithTarget = deleteWithTarget;
    }
    
    public Subscription()
    {
        super();
    }
    
    public String getType()
    {
        return type;
    }
    
    public String getOnUser()
    {
        return onUser;
    }
    
    public String getOnComputer()
    {
        return onComputer;
    }
    
    public String getOnSetting()
    {
        return onSetting;
    }
    
    public boolean isDeleteWithTarget()
    {
        return deleteWithTarget;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public void setOnUser(String onUser)
    {
        this.onUser = onUser;
    }
    
    public void setOnComputer(String onComputer)
    {
        this.onComputer = onComputer;
    }
    
    public void setOnSetting(String onSetting)
    {
        this.onSetting = onSetting;
    }
    
    public void setDeleteWithTarget(boolean deleteWithTarget)
    {
        this.deleteWithTarget = deleteWithTarget;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + (deleteWithTarget ? 1231 : 1237);
        result = prime
            * result
            + ((onComputer == null) ? 0 : onComputer
                .hashCode());
        result = prime
            * result
            + ((onSetting == null) ? 0 : onSetting
                .hashCode());
        result = prime * result
            + ((onUser == null) ? 0 : onUser.hashCode());
        result = prime * result
            + ((type == null) ? 0 : type.hashCode());
        return result;
    }
    
    /**
     * Returns a subscription identical to this one but with onUser converted to
     * be an absolute userid instead of a username if it is one. Usernames will
     * be resolved to userids using the realm specified.
     * 
     * @param realm
     *            The realm to resolve usernames against
     * @return a new, absolute subscription
     */
    public Subscription absolute(String realm)
    {
        return new Subscription(type, Userids.resolveTo(
            onUser, realm + ":"), onComputer, onSetting,
            deleteWithTarget);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Subscription other = (Subscription) obj;
        // System.out.println("checking subscription this: "
        // + type + "," + onUser + "," + onComputer + ","
        // + onSetting + "," + deleteWithTarget
        // + " against subscription: " + other.type + ","
        // + other.onUser + "," + other.onComputer + ","
        // + other.onSetting + "," + deleteWithTarget);
        if (deleteWithTarget != other.deleteWithTarget)
            return false;
        if (onComputer == null)
        {
            if (other.onComputer != null)
                return false;
        }
        else if (!onComputer.equals(other.onComputer))
            return false;
        if (onSetting == null)
        {
            if (other.onSetting != null)
                return false;
        }
        else if (!onSetting.equals(other.onSetting))
            return false;
        if (onUser == null)
        {
            if (other.onUser != null)
                return false;
        }
        else if (!onUser.equals(other.onUser))
            return false;
        if (type == null)
        {
            if (other.type != null)
                return false;
        }
        else if (!type.equals(other.type))
            return false;
        return true;
    }
}
