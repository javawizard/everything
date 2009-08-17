package jw.bznetwork.client.screens;

import com.google.gwt.user.client.ui.Label;

import jw.bznetwork.client.VerticalScreen;

public class LogsScreen extends VerticalScreen
{
    public static final String[] SEARCH_IN = new String[]
    {
            "event", "source", "target", "sourceteam", "targetteam",
            "ipaddress", "bzid", "email", "metadata", "data"
    };
    /**
     * When the logs screen is selected, if this is true, then the filter
     * settings set in the fields will be loaded into the UI, and this will be
     * set to false. If this is false, the filter settings in the fields will be
     * reset to their defaults and loaded into the UI.
     */
    public boolean preserveSettingsOnce = false;
    
    @Override
    public void deselect()
    {
    }
    
    @Override
    public String getName()
    {
        return "logs";
    }
    
    @Override
    public String getTitle()
    {
        return "Logs";
    }
    
    @Override
    public void init()
    {
    }
    
    @Override
    public void reselect()
    {
        select();
    }
    
    @Override
    public void select()
    {
        widget.add(new Label("The logs screen is coming soon!"));
    }
    
}
