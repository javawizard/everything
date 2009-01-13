package net.sf.opengroove.client.com.model;

public class UserStatus
{
    private long lastOnline;
    private boolean isOnline;
    public long getLastOnline()
    {
        return lastOnline;
    }
    public boolean isOnline()
    {
        return isOnline;
    }
    public void setLastOnline(long lastOnline)
    {
        this.lastOnline = lastOnline;
    }
    public void setOnline(boolean isOnline)
    {
        this.isOnline = isOnline;
    }
}
