/**
 * 
 */
package jw.bznetwork.client;

/**
 * All of the settings that show up on the configuration page.<br/><br/>
 * 
 * Settings must appear in a specific order in order for everything to work.
 * Specifically, all settings with a type of {@link SettingType#sensitive} must
 * appear together (IE they must all be next to each other), and there must be
 * at least one setting after them.
 * 
 * @author Alexander Boyd
 * 
 */
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
                    + "argument will be the server's port. This command will not be terminated "
                    + "when a server shuts down, even if the server is killed.",
            ""), publichostname(
            SettingType.text,
            "Public hostname",
            "This is the hostname that will be used in the -publicaddr switch "
                    + "added to each server. Leave this blank if you don't want BZNetwork"
                    + " to automatically add -publicaddr to your servers.", ""), menuleft(
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
            "" + false), hiddenglobal(
            SettingType.checkbox,
            "Global auth on non-public servers",
            "If this is checked, then servers that are not public will still use the "
                    + "-public switch along with -advertise NONE so that the server can still use "
                    + "global auth. If this is not checked, servers that are not public "
                    + "will not use the -public switch at all.", "true"), welcome(
            SettingType.area,
            "Welcome message",
            "This is the text that shows up when the user initially "
                    + "logs into your site. This can contain HTML and XSM-inline.",
            "Congratulations! You've successfully installed BZNetwork onto "
                    + "your server. Head on over to the Configuration page to change "
                    + "this message. Then check out the Getting Started link on the "
                    + "Help page to get started.");
    public String getLabel()
    {
        return label;
    }
    
    public String getDesc()
    {
        return desc;
    }
    
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