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
    private ListBox inField;
    // events group
    private ListBox eventsField;
    // interval group
    private Button startSelectionButton;
    private TextField startHourField;
    private TextField startMinuteField;
    private Button startPeriodButton;
    private Button endSelectionButton;
    private TextField endHourField;
    private TextField endMinuteField;
    private Button endPeriodButton;
    // servers group
    private ListBox serversField;
    // search group
    private Button searchButton;
    
    public LogsFilterWidget(LogsFilterSettings settings, LogSearchModel model)
    {
        table.setCellPadding(2);
        initWidget(table);
        // search group
        VerticalPanel searchPanel = wh100(new VerticalPanel());
        table.setWidget(0, 0, searchPanel);
        format.setWidth(0, 0, "20%");
        searchField = w100(new TextBox());
        searchPanel.add(new HTML("<b>Search for:</b>"));
        searchPanel.add(searchField);
        ignoreCaseField = new CheckBox("Ignore case");
        searchPanel.add(ignoreCaseField);
        searchPanel.add(new HTML("<b>In:</b>"));
        inField = w100(new ListBox(true));
        populateSearchIn(inField);
        searchPanel.add(inField);
        inField.setVisibleItemCount(4);
        //events group
        VerticalPanel eventsPanel = wh100(new VerticalPanel());
        table.setWidget(0,1,eventsPanel);
        format.setWidth(0,1,"20%");
        
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
