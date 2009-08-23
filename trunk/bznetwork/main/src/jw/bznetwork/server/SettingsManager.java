package jw.bznetwork.server;

import jw.bznetwork.client.Settings;
import jw.bznetwork.client.SettingsManagerAdapter;
import jw.bznetwork.client.data.model.ConfigSetting;
import jw.bznetwork.server.data.DataStore;

public class SettingsManager implements SettingsManagerAdapter
{
    public static final SettingsManager singleton = new SettingsManager();
    
    public synchronized String getString(Settings name, String def)
    {
        String value = DataStore.getConfigSettingValue(name.name());
        if (value == null)
            return def;
        return value;
    }
    
    public synchronized boolean getBoolean(Settings name, boolean def)
    {
        return getString(name, "" + def).equalsIgnoreCase("" + true);
    }
    
    public synchronized int getInteger(Settings name, int def)
    {
        return Integer.parseInt(getString(name, "" + def));
    }
    
    public synchronized void setString(Settings name, String value)
    {
        ConfigSetting setting = DataStore.getConfigSetting(name.name());
        boolean exists = true;
        if (setting == null)
        {
            exists = false;
            setting = new ConfigSetting();
            setting.setName(name.name());
        }
        setting.setValue(value);
        if (exists)
            DataStore.updateConfigSetting(setting);
        else
            DataStore.addConfigSetting(setting);
    }
    
    public synchronized void setBoolean(Settings name, boolean value)
    {
        setString(name, "" + value);
    }
    
    public synchronized void setInteger(Settings name, int value)
    {
        setString(name, "" + value);
    }
}
