package jw.bznetwork.client.ui;

import java.awt.TextField;

import jw.bznetwork.client.data.LogSearchModel;
import jw.bznetwork.client.data.LogsFilterSettings;
import jw.bznetwork.client.screens.LogsScreen;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

public class LogsFilterWidget extends Composite
{
    private FlexTable table = new FlexTable();
    private FlexCellFormatter format = table.getFlexCellFormatter();
    // search group
    private TextBox searchField;
    private CheckBox ignoreCaseField;
    // search in group
    private ListBox inField;
    // events group
    private ListBox eventsField;
    // interval group
    private DateButton startSelectionButton;
    private TextField startHourField;
    private TextField startMinuteField;
    private PeriodButton startPeriodButton;
    private DateButton endSelectionButton;
    private TextField endHourField;
    private TextField endMinuteField;
    private PeriodButton endPeriodButton;
    // servers group
    private ListBox serversField;
    // search button group
    private Button searchButton;
    
    public LogsFilterWidget(LogsFilterSettings settings, LogSearchModel model)
    {
        table.setCellPadding(2);
        initWidget(table);
        // search group
        VerticalPanel searchPanel = w100(new VerticalPanel());
        table.setWidget(0, 0, searchPanel);
        format.setWidth(0, 0, "16%");
        searchField = w100(new TextBox());
        searchPanel.add(new HTML("<b>Search for:</b>"));
        searchPanel.add(searchField);
        ignoreCaseField = new CheckBox("Ignore case");
        searchPanel.add(ignoreCaseField);
        // search in group
        VerticalPanel searchInPanel = w100(new VerticalPanel());
        table.setWidget(0, 1, searchInPanel);
        format.setWidth(0, 1, "16%");
        searchInPanel.add(new HTML("<b>Search in:</b>"));
        inField = w100(new ListBox(true));
        populateSearchIn(inField);
        searchInPanel.add(inField);
        inField.setVisibleItemCount(10);
        // events group
        VerticalPanel eventsPanel = w100(new VerticalPanel());
        table.setWidget(0, 2, eventsPanel);
        format.setWidth(0, 2, "16%");
        eventsPanel.add(new HTML("<b>Events:</b>"));
        eventsField = w100(new ListBox(true));
        populateEvents(eventsField, model);
        eventsPanel.add(eventsField);
        eventsField.setVisibleItemCount(10);
        // interval group
        FlexTable intervalTable = w100(new FlexTable());
        FlexCellFormatter intervalFormat = intervalTable.getFlexCellFormatter();
        table.setWidget(0,3,intervalTable);
        format.setWidth(0,3,"16%");
        //start interval
        startSelectionButton = new DateButton();
        //end interval
        //servers group
        //search button group
    }
    

    private void populateEvents(ListBox eventsField2, LogSearchModel model)
    {
        // TODO Auto-generated method stub
        
    }
    
    private void populateSearchIn(ListBox box)
    {
        for (String s : LogsScreen.SEARCH_IN)
        {
            box.addItem(s);
        }
    }
    
    private static <T extends Widget> T w100(T widget)
    {
        widget.setWidth("100%");
        return widget;
    }
    
    private static <T extends Widget> T h100(T widget)
    {
        widget.setHeight("100%");
        return widget;
    }
    
    private static <T extends Widget> T wh100(T widget)
    {
        return w100(h100(widget));
    }
    
}
