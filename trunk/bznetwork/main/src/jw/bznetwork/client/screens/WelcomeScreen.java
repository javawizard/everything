package jw.bznetwork.client.screens;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.Screen;
import jw.bznetwork.client.Settings;

/**
 * The welcome screen shows the default welcome message.
 * 
 * @author Alexander Boyd
 * 
 */
public class WelcomeScreen implements Screen
{
    private VerticalPanel widget = new VerticalPanel();
    
    @Override
    public void deselect()
    {
    }
    
    @Override
    public String getName()
    {
        return "welcome";
    }
    
    @Override
    public String getTitle()
    {
        return "Welcome";
    }
    
    @Override
    public Widget getWidget()
    {
        return widget;
    }
    
    @Override
    public void reselect()
    {
        select();
    }
    
    @Override
    public void select()
    {
    }
    
    @Override
    public void init()
    {
        widget.add(new HTML(BZNetwork.publicConfiguration
                .getString(Settings.welcome)));
    }
    
}
