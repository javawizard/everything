package jw.bznetwork.client.screens;

import com.google.gwt.user.client.ui.Widget;

import jw.bznetwork.client.Screen;

public class WelcomeScreen implements Screen
{
    private Widget widget;
    private 
    
    @Override
    public void deselect()
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public String getName()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getTitle()
    {
        return "Welcome";
    }
    
    @Override
    public Widget getWidget()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void reselect()
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void select()
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void init()
    {
        widget = new Label();
    }
    
}
