package jw.bznetwork.client.screens;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.Screen;
import jw.bznetwork.client.Settings;
import jw.bznetwork.client.VerticalScreen;
import jw.bznetwork.client.x.TextScripter;
import jw.bznetwork.client.x.VXProvider;
import jw.bznetwork.client.x.VXUser;

/**
 * The welcome screen shows the default welcome message.
 * 
 * @author Alexander Boyd
 * 
 */
public class WelcomeScreen extends VerticalScreen
{
    
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
    public void reselect()
    {
        select();
    }
    
    @Override
    public void select()
    {
        widget.clear();
        widget.add(new HTML(TextScripter.run(BZNetwork.publicConfiguration
                .getString(Settings.welcome), null, new VXProvider(),
                new VXUser())));
    }
    
    @Override
    public void init()
    {
    }
    
}
