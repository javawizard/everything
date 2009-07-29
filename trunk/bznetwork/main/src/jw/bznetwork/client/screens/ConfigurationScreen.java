package jw.bznetwork.client.screens;

import jw.bznetwork.client.Screen;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ConfigurationScreen implements Screen
{
    private VerticalPanel widget = new VerticalPanel();
    
    @Override
    public void deselect()
    {
    }
    
    @Override
    public String getName()
    {
        return "configuration";
    }
    
    @Override
    public String getTitle()
    {
        return "Configuration";
    }
    
    @Override
    public Widget getWidget()
    {
        return widget;
    }
    
    @Override
    public void init()
    {
        widget.add(new Label("Configuration screen coming soon!"));
    }
    
    @Override
    public void reselect()
    {
    }
    
    @Override
    public void select()
    {
    }
}
