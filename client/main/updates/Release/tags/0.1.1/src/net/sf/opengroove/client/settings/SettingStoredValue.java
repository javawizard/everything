package net.sf.opengroove.client.settings;

import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;

@ProxyBean
public interface SettingStoredValue
{
    @Property
    public String getTabId();
    
    public void setTabId(String tabId);
    
    @Property
    public String getSubnavId();
    
    public void setSubnavId(String tabId);
    
    @Property
    public String getGroupId();
    
    public void setGroupId(String tabId);
    
    @Property
    public String getSettingId();
    
    public void setSettingId(String tabId);
    
    @Property
    public String getStringValue();
    
    public void setStringValue(String value);
    
    @Property
    public int getIntValue();
    
    public void setIntValue(int value);
    
    @Property
    public long getLongValue();
    
    public void setLongValue(long value);
    
    @Property
    public double getDoubleValue();
    
    public void setDoubleValue(double value);
    
    @Property
    public boolean getBooleanValue();
    
    public void setBooleanValue(boolean value);
}
