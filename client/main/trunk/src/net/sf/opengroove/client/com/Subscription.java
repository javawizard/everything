package net.sf.opengroove.client.com;

public class Subscription
{
    private String type;
    private String onUser;
    private String onComputer;
    private String onSetting;
    private boolean deleteWithTarget;
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
}
