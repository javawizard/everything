package jw.bznetwork.client.screens;

import com.google.gwt.user.client.ui.Label;

import jw.bznetwork.client.VerticalScreen;

public class LogsScreen extends VerticalScreen
{
    
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
