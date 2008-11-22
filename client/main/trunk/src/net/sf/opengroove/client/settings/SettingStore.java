package net.sf.opengroove.client.settings;

import net.sf.opengroove.common.proxystorage.Constructor;
import net.sf.opengroove.common.proxystorage.ListType;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;
import net.sf.opengroove.common.proxystorage.StoredList;

@ProxyBean
public interface SettingStore
{
    @Property
    @ListType(SettingValue.class)
    public StoredList<SettingValue> getSettings();
    @Constructor
    public SettingValue createSettingValue();
    
}
