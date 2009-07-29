package jw.bznetwork.client.screens;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;

import jw.bznetwork.client.VerticalScreen;
import jw.bznetwork.client.ui.HorizontalRule;
import jw.bznetwork.client.ui.Spacer;

public class HelpScreen extends VerticalScreen
{
    
    @Override
    public void deselect()
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public String getName()
    {
        return "help";
    }
    
    @Override
    public String getTitle()
    {
        return "Help";
    }
    
    @Override
    public void init()
    {
        widget.setWidth("100%");
        widget.setHorizontalAlignment(widget.ALIGN_CENTER);
        widget.add(new Spacer("5px", "60px"));
        HTML gettingStartedLink = new HTML(
                "<span class='bznetwork-GettingStartedLink-out'><a href='http://code.google.com/p/bzsound/wiki/BZNetworkGettingStarted' target='_blank'>"
                        + "<span class='bznetwork-GettingStartedLink'>"
                        + "Getting Started" + "</span></a></span>");
        widget.add(gettingStartedLink);
        widget.add(new Spacer("5px", "8px"));
        widget
                .add(new HTML(
                        "More help pages can be found <a href='http://code.google.com/p/bzsound/w/list?can=2&q=label%3Abznetwork' target='_blank'>here</a>."));
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
