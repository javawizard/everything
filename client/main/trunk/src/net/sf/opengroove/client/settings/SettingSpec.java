package net.sf.opengroove.client.settings;

public class SettingSpec
{
    private String tabId;
    private String subnavId;
    private String groupId;
    private String settingId;
    
    public SettingSpec()
    {
        super();
    }

    public SettingSpec(String tabId, String subnavId,
        String groupId, String settingId)
    {
        super();
        this.tabId = tabId;
        this.subnavId = subnavId;
        this.groupId = groupId;
        this.settingId = settingId;
    }

    public String getTabId()
    {
        return tabId;
    }
    
    public String getSubnavId()
    {
        return subnavId;
    }
    
    public String getGroupId()
    {
        return groupId;
    }
    
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((groupId == null) ? 0 : groupId.hashCode());
        result = prime
            * result
            + ((settingId == null) ? 0 : settingId
                .hashCode());
        result = prime
            * result
            + ((subnavId == null) ? 0 : subnavId.hashCode());
        result = prime * result
            + ((tabId == null) ? 0 : tabId.hashCode());
        return result;
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SettingSpec other = (SettingSpec) obj;
        if (groupId == null)
        {
            if (other.groupId != null)
                return false;
        }
        else if (!groupId.equals(other.groupId))
            return false;
        if (settingId == null)
        {
            if (other.settingId != null)
                return false;
        }
        else if (!settingId.equals(other.settingId))
            return false;
        if (subnavId == null)
        {
            if (other.subnavId != null)
                return false;
        }
        else if (!subnavId.equals(other.subnavId))
            return false;
        if (tabId == null)
        {
            if (other.tabId != null)
                return false;
        }
        else if (!tabId.equals(other.tabId))
            return false;
        return true;
    }

    public String getSettingId()
    {
        return settingId;
    }
    
    public void setTabId(String tabId)
    {
        this.tabId = tabId;
    }
    
    public void setSubnavId(String subnavId)
    {
        this.subnavId = subnavId;
    }
    
    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }
    
    public void setSettingId(String settingId)
    {
        this.settingId = settingId;
    }
}
