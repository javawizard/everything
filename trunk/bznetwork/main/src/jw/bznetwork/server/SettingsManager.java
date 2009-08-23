package jw.bznetwork.server;

import jw.bznetwork.client.data.model.ConfigSetting;
import jw.bznetwork.server.data.DataStore;

public class SettingsManager
{
    public static enum Settings
    {
        welcome(""), sitename("MySiteName"), contact(
                "mybznetworksite@example.com"), executable("bzfs"), menuleft(
                "" + true), currentname("" + false);
        private String def;
        
        private Settings(String def)
        {
            this.def = def;
        }
        
        public String getDef()
        {
            return def;
        }
        
        public String getString()
        {
            return SettingsManager.getString(this, def);
        }
        
        public int getInteger()
        {
            return SettingsManager.getInteger(this, Integer.parseInt(def));
        }
        
        public boolean getBoolean()
        {
            return SettingsManager.getBoolean(this, Boolean.parseBoolean(def));
        }
        
        public void setString(String value)
        {
            SettingsManager.setString(this, value);
        }
        
        public void setInteger(int value)
        {
            SettingsManager.setInteger(this, value);
        }
        
        public void setBoolean(boolean value)
        {
            SettingsManager.setBoolean(this, value);
        }
    }
    
    public static synchronized String getString(Settings name, String def)
    {
        String value = DataStore.getConfigSettingValue(name.name());
        if (value == null)
            return def;
        return value;
    }
    
    public static synchronized boolean getBoolean(Settings name, boolean def)
    {
        return getString(name, "" + def).equalsIgnoreCase("" + true);
    }
    
    public static synchronized int getInteger(Settings name, int def)
    {
        return Integer.parseInt(getString(name, "" + def));
    }
    
    public static synchronized void setString(Settings name, String value)
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
    
    public static synchronized void setBoolean(Settings name, boolean value)
    {
        setString(name, "" + value);
    }
    
    public static synchronized void setInteger(Settings name, int value)
    {
        setString(name, "" + value);
    }
}
