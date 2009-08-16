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
