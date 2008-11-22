package net.sf.opengroove.client.settings;

public class SettingSpec
{
    private String tabId;
    private String subnavId;
    private String groupId;
    private String settingId;
    
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
