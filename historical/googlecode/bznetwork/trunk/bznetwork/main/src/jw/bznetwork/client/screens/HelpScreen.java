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
        // http://code.google.com/p/bznetwork/issues/entry?template=Feature+
        // Request
        widget.setWidth("100%");
        widget.setHorizontalAlignment(widget.ALIGN_CENTER);
        widget.add(new Spacer("5px", "40px"));
        HTML gettingStartedLink = new HTML(
                "<span class='bznetwork-GettingStartedLink-out'><a href='http://code.google.com/p/bznetwork/wiki/GettingStarted' target='_blank'>"
                        + "<span class='bznetwork-GettingStartedLink'>"
                        + "Getting Started" + "</span></a></span>");
        widget.add(gettingStartedLink);
        widget.add(new Spacer("5px", "3px"));
        HTML faqLink = new HTML(
                "<span class='bznetwork-GettingStartedLink-out'><a href='http://code.google.com/p/bznetwork/wiki/FrequentlyAskedQuestions' target='_blank'>"
                        + "<span class='bznetwork-GettingStartedLink'>"
                        + "Frequently Asked Questions" + "</span></a></span>");
        widget.add(faqLink);
        widget.add(new Spacer("5px", "3px"));
        widget
                .add(new HTML(
                        "<span class='bznetwork-GettingStartedLink-out'><a href='http://code.google.com/p/bznetwork/issues/entry?template=Feature+Request' target='_blank'>"
                                + "<span class='bznetwork-GettingStartedLink'>"
                                + "Request a Feature" + "</span></a></span>"));
        widget.add(new Spacer("5px", "3px"));
        widget
                .add(new HTML(
                        "<span class='bznetwork-GettingStartedLink-out'><a href='http://java.sun.com/javase/6/docs/api/java/util/Formatter.html#syntax' target='_blank'>"
                                + "<span class='bznetwork-GettingStartedLink'>"
                                + "Printf Syntax" + "</span></a></span>"));
        widget.add(new Spacer("5px", "8px"));
        widget
                .add(new HTML(
                        "More help pages can be found <a href='http://code.google.com/p/bznetwork/w/list?can=2&q=label%3Ahelp' target='_blank'>here</a>."));
        widget.add(new Spacer("5px", "2px"));
        widget.add(new HTML("Request a Feature requires a Google account."));
    }// http://java.sun.com/javase/6/docs/api/java/util/Formatter.html#syntax
    
    @Override
    public void reselect()
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void select()
    {
    }
    
}
