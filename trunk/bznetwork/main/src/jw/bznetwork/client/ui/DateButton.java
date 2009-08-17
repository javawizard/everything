package jw.bznetwork.client.ui;

import java.util.Date;

import jw.bznetwork.client.BZNetwork;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * A button that shows a date as the button's text. Clicking on the button pops
 * up a calendar that the user can use to change the button's date. When they
 * select a date, the button's text updates to reflect that date.
 * 
 * @author Alexander Boyd
 * 
 */
public class DateButton extends Button implements ClickHandler
{
    private DatePicker picker;
    private PopupPanel box;
    
    public DateButton(Date date)
    {
        picker = new DatePicker();
        picker.setValue(date);
        box = new PopupPanel(true, false);
        box.setWidget(picker);
        setText(BZNetwork.formatDate(date));
        addStyleName("bznetwork-DateButton");
        addClickHandler(this);
        picker.addValueChangeHandler(new ValueChangeHandler<Date>()
        {
            
            @Override
            public void onValueChange(ValueChangeEvent<Date> event)
            {
                box.hide();
                setText(BZNetwork.formatDate(picker.getValue()));
            }
        });
    }
    
    @Override
    public void onClick(ClickEvent event)
    {
        box.setPopupPosition(getAbsoluteLeft(), getAbsoluteTop()
                + getOffsetHeight());
        box.show();
    }
    
    public Date getValue()
    {
        return BZNetwork.parseDate(getText());
    }
}
