/**
 * 
 */
package jw.bznetwork.client;

public enum Settings
{
    sitename(SettingType.text, "Site name",
            "This is the name of your site. It appears in various locations, "
                    + "such as at the top of this page.", "MySiteName"), contact(
            SettingType.text,
            "Contact",
            "This is some information that users of your site can "
                    + "use to get in touch with you. It can be an email address, "
                    + "a nickname, an IRC channel, or whatever you want it to be.",
            "mybznetworksite@example.com"), executable(
            SettingType.sensitive,
            "Executable",
            "The Executable field is the executable that "
                    + "should be run to start bzfs. Normally this is exactly that: \"bzfs\". "
                    + "This must not contain arguments to the command.", "bzfs"), startuptrigger(
            SettingType.sensitive,
            "Startup trigger",
            "This command will be run after any given server has "
                    + "started up. The first argument will be the server's id and the second "
                    + "argument will be the server's port.", ""), menuleft(
            SettingType.checkbox,
            "Show menu to the left",
            "If this is checked, the list of pages shows up to the left. If "
                    + "this is not checked, the list of pages can be accessed in a dropdown "
                    + "menu by clicking on the Menu link in the upper-right corner.",
            "" + true), currentname(
            SettingType.checkbox,
            "Show current page name in header",
            "If this is checked, the name of the current page will "
                    + "be shown at the top of the page, next to the site name.",
            "" + false), welcome(
            SettingType.area,
            "Welcome message",
            "This is the text that shows up when the user initially "
                    + "logs into your site. This can contain HTML. Right now, you can't "
                    + "have links to other pages (such as the servers page or the roles page) "
                    + "in this field, but I'm planning on adding that in the future.",
            "Congratulations! You've successfully installed BZNetwork onto "
                    + "your server. Head on over to the Configuration page to change "
                    + "this message. Then check out the Getting Started link on the "
                    + "Help page to get started.");
    private String def;
    private SettingsManagerAdapter adapter;
    private SettingType type;
    private String label;
    private String desc;
    
    private Settings(SettingType type, String label, String desc, String def)
    {
        this.type = type;
        this.label = label;
        this.desc = desc;
        this.def = def;
    }
    
    public SettingType getType()
    {
        return type;
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