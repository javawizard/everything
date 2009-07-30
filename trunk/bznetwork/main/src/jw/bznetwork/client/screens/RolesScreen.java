package jw.bznetwork.client.screens;

import com.google.gwt.user.client.ui.Label;

import jw.bznetwork.client.VerticalScreen;

public class RolesScreen extends VerticalScreen
{
    
    @Override
    public void deselect()
    {
    }
    
    @Override
    public String getName()
    {
        return "roles";
    }
    
    @Override
    public String getTitle()
    {
        return "Roles";
    }
    
    @Override
    public void init()
    {
    }
    
    @Override
    public void reselect()
    {
        deselect();
        select();
    }
    
    @Override
    public void select()
    {
        widget.clear();
        widget.add(new Label("Loading..."));
        
    }
    
}
