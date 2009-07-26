package jw.bznetwork.client.screens;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import jw.bznetwork.client.Screen;

/**
 * The welcome screen shows the default welcome message.
 * 
 * @author Alexander Boyd
 * 
 */
public class WelcomeScreen implements Screen
{
    private Label widget = new Label("_LOADING_");
    
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
        if (widget.getText().equals("_LOADING_"))
        {
            
        }
    }
    
    @Override
    public void init()
    {
    }
    
}
