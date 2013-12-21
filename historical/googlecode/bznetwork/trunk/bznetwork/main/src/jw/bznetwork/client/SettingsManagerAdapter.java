package jw.bznetwork.client;

import jw.bznetwork.client.data.model.ConfigSetting;
import jw.bznetwork.server.data.DataStore;

public interface SettingsManagerAdapter
{
    public String getString(Settings name, String def);
    
    public boolean getBoolean(Settings name, boolean def);
    
    public int getInteger(Settings name, int def);
    
    public void setString(Settings name, String value);
    
    public void setBoolean(Settings name, boolean value);
    
    public void setInteger(Settings name, int value);
}
