package jw.bznetwork.client.data.model;

import java.io.Serializable;
import java.util.HashMap;

import jw.bznetwork.client.Settings;

public class Configuration implements Serializable
{
    public Configuration()
    {
    }
    
    private HashMap<Settings, String> settings = new HashMap<Settings, String>();
    
    public static Configuration loadFromDatabase()
    {
        Configuration config = new Configuration();
        for (Settings s : Settings.values())
        {
            config.getSettings().put(s, s.getString());
        }
        return config;
    }
    
    public HashMap<Settings, String> getSettings()
    {
        return settings;
    }
    
    public String getString(Settings name)
    {
        return (String) settings.get(name);
    }
    
    public Integer getInteger(Settings name)
    {
        return Integer.parseInt(settings.get(name));
    }
    
    public Boolean getBoolean(Settings name)
    {
        return Boolean.parseBoolean(settings.get(name));
    }
    
    public void setString(Settings name, String value)
    {
        settings.put(name, value);
    }
    
    public void setInteger(Settings name, Integer value)
    {
        settings.put(name, "" + value);
    }
    
    public void setBoolean(Settings name, Boolean value)
    {
        settings.put(name, "" + value);
    }
}
