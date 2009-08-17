package jw.bznetwork.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

/**
 * A button that allows the user to select between AM and PM by clicking the
 * button to toggle the selection.
 * 
 * @author Alexander Boyd
 * 
 */
public class PeriodButton extends Button implements ClickHandler
{
    public PeriodButton(boolean pm)
    {
        setText(pm ? "PM" : "AM");
        addStyleName("bznetwork-PeriodButton");
        addClickHandler(this);
    }
    
    public boolean isAm()
    {
        return getText().equals("AM");
    }
    
    public boolean isPm()
    {
        return getText().equals("PM");
    }
    
    @Override
    public void onClick(ClickEvent event)
    {
        setText(isAm() ? "PM" : "AM");
    }
}
