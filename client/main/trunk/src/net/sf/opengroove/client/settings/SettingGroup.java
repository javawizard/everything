package net.sf.opengroove.client.settings;


public class SettingGroup
{
    private String name;
    private String description;
    private Setting[] settings;
    
    public SettingGroup(String name, String description,
        Setting[] settings)
    {
        super();
        this.name = name;
        this.description = description;
        this.settings = settings;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public Setting[] getSettings()
    {
        return settings;
    }
}
