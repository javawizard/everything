/**
 * 
 */
package jw.bznetwork.client;

public enum Settings
{
    welcome("Congratulations! You've successfully installed BZNetwork onto "
            + "your server. Head on over to the Configuration page to change "
            + "this message. Then check out the Getting Started link on the "
            + "Help page to get started."), sitename("MySiteName"), contact(
            "mybznetworksite@example.com"), executable("bzfs"), menuleft(
            "" + true), currentname("" + false);
    private String def;
    private SettingsManagerAdapter adapter;
    
    private Settings(String def)
    {
        this.def = def;
    }
    
    public void setAdapter(SettingsManagerAdapter adapter)
    {
        this.adapter = adapter;
    }
    
    public String getDef()
    {
        return def;
    }
    
    public String getString()
    {
        return adapter.getString(this, def);
    }
    
    public int getInteger()
    {
        return adapter.getInteger(this, Integer.parseInt(def));
    }
    
    public boolean getBoolean()
    {
        return adapter.getBoolean(this, Boolean.parseBoolean(def));
    }
    
    public void setString(String value)
    {
        adapter.setString(this, value);
    }
    
    public void setInteger(int value)
    {
        adapter.setInteger(this, value);
    }
    
    public void setBoolean(boolean value)
    {
        adapter.setBoolean(this, value);
    }
}