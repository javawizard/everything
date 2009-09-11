package jw.bznetwork.client.screens;

import jw.bznetwork.client.VerticalScreen;

public class EmailScreen extends VerticalScreen
{
    
    @Override
    public void deselect()
    {
    }
    
    @Override
    public String getName()
    {
        return "email";
    }
    
    @Override
    public String getTitle()
    {
        return "Email";
    }
    
    @Override
    public void init()
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void reselect()
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void select()
    {
        addToHistory(null);
    }
    
}
