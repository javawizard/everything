package jw.bznetwork.client.screens;

import jw.bznetwork.client.VerticalScreen;

public class IRCScreen extends VerticalScreen
{
    
    @Override
    public void deselect()
    {
    }
    
    @Override
    public String getName()
    {
        return "irc";
    }
    
    @Override
    public String getTitle()
    {
        return "IRC";
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
    }
    
}
