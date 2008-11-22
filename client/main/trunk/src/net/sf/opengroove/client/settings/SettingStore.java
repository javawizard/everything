package net.sf.opengroove.client.settings;

import net.sf.opengroove.common.proxystorage.CompoundSearch;
import net.sf.opengroove.common.proxystorage.Constructor;
import net.sf.opengroove.common.proxystorage.ListType;
import net.sf.opengroove.common.proxystorage.Property;
import net.sf.opengroove.common.proxystorage.ProxyBean;
import net.sf.opengroove.common.proxystorage.StoredList;

@ProxyBean
public interface SettingStore
{
    @Property
    @ListType(SettingStoredValue.class)
    public StoredList<SettingStoredValue> getSettings();
    
    @Constructor
    public SettingStoredValue createSettingValue();
    
    @CompoundSearch(listProperty = "settings", searchProperties = {
        "tabId", "subnavId", "groupId", "settingId" }, exact = {
        true, true, true, true }, anywhere = { false,
        false, false, false })
    public SettingStoredValue getSettingValue(String tabId,
        String subnavId, String groupId, String settingId);
}
