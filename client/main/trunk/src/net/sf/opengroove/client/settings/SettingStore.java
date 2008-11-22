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
    @ListType(SettingValue.class)
    public StoredList<SettingValue> getSettings();
    
    @Constructor
    public SettingValue createSettingValue();
    
    @CompoundSearch(listProperty = "settings", searchProperties = {
        "tabId", "subnavId", "groupId", "settingId" }, exact = {
        true, true, true, true }, anywhere = { false,
        false, false, false })
    public SettingValue getSettingValue(String tabId,
        String subnavId, String groupId, String settingId);
}
