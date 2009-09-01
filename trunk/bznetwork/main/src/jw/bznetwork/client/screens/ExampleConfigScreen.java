package jw.bznetwork.client.screens;

import jw.bznetwork.client.VerticalScreen;
import jw.bznetwork.client.ui.Header2;

public class ExampleConfigScreen extends VerticalScreen
{
    
    @Override
    public void deselect()
    {
    }
    
    @Override
    public String getName()
    {
        return "exampleconfig";
    }
    
    @Override
    public String getTitle()
    {
        return "Example Config";
    }
    
    @Override
    public void init()
    {
        widget.add(new Header2("Example configuration screen"));
        
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
    
}
