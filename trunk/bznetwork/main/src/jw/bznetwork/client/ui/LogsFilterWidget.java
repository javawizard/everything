package jw.bznetwork.client.ui;

import java.util.Date;

import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.data.LogSearchModel;
import jw.bznetwork.client.data.LogsFilterSettings;
import jw.bznetwork.client.data.model.Server;
import jw.bznetwork.client.screens.LogsScreen;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
    private TextBox startHourField;
    private TextBox startMinuteField;
    private PeriodButton startPeriodButton;
    private DateButton endSelectionButton;
    private TextBox endHourField;
    private TextBox endMinuteField;
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
        format.setVerticalAlignment(0, 0, VerticalPanel.ALIGN_TOP);
        searchField = w100(new TextBox());
        searchPanel.add(new HTML("<b>Search for:</b>"));
        searchPanel.add(searchField);
        searchField.setText(settings.getSearch());
        ignoreCaseField = new CheckBox("Ignore case");
        searchPanel.add(ignoreCaseField);
        ignoreCaseField.setValue(settings.isIgnoreCase());
        // search in group
        VerticalPanel searchInPanel = w100(new VerticalPanel());
        table.setWidget(0, 1, searchInPanel);
        format.setWidth(0, 1, "16%");
        format.setVerticalAlignment(0, 1, VerticalPanel.ALIGN_TOP);
        searchInPanel.add(new HTML("<b>Search in:</b>"));
        inField = w100(new ListBox(true));
        populateSearchIn(inField, settings);
        searchInPanel.add(inField);
        inField.setVisibleItemCount(14);
        // events group
        VerticalPanel eventsPanel = w100(new VerticalPanel());
        table.setWidget(0, 2, eventsPanel);
        format.setWidth(0, 2, "16%");
        format.setVerticalAlignment(0, 2, VerticalPanel.ALIGN_TOP);
        eventsPanel.add(new HTML("<b>Events:</b>"));
        eventsField = w100(new ListBox(true));
        populateEvents(eventsField, model, settings);
        eventsPanel.add(eventsField);
        eventsField.setVisibleItemCount(14);
        // interval group
        FlexTable intervalTable = w100(new FlexTable());
        FlexCellFormatter intervalFormat = intervalTable.getFlexCellFormatter();
        table.setWidget(0, 3, intervalTable);
        format.setWidth(0, 3, "16%");
        format.setVerticalAlignment(0, 3, VerticalPanel.ALIGN_TOP);
        intervalTable.setHTML(0, 0, "<b>Interval:</b>");
        intervalFormat.setColSpan(0, 0, 3);
        // start interval
        startSelectionButton = new DateButton(settings.getStart());
        startSelectionButton.setWidth("100%");
        intervalTable.setWidget(1, 0, startSelectionButton);
        intervalFormat.setColSpan(1, 0, 3);
        startHourField = new TextBox();
        startHourField.addStyleName("bznetwork-TimeBox");
        intervalTable.setWidget(2, 0, startHourField);
        intervalTable.setText(2, 1, ":");
        intervalFormat.setWidth(2, 1, "8px");
        intervalFormat.setHorizontalAlignment(2, 1,
                HorizontalPanel.ALIGN_CENTER);
        startMinuteField = new TextBox();
        startMinuteField.addStyleName("bznetwork-TimeBox");
        intervalTable.setWidget(2, 2, startMinuteField);
        populateTimeWidgets(startHourField, startMinuteField, settings
                .getStart());
        startPeriodButton = new PeriodButton(isPm(settings.getStart()));
        startPeriodButton.setWidth("100%");
        intervalTable.setWidget(3, 0, startPeriodButton);
        intervalFormat.setColSpan(3, 0, 3);
        // mid interval marker
        intervalTable.setText(4, 0, "to");
        intervalFormat.setColSpan(4, 0, 3);
        intervalFormat.setHorizontalAlignment(4, 0,
                HorizontalPanel.ALIGN_CENTER);
        // end interval
        endSelectionButton = new DateButton(settings.getEnd());
        endSelectionButton.setWidth("100%");
        intervalTable.setWidget(5, 0, endSelectionButton);
        intervalFormat.setColSpan(5, 0, 3);
        endHourField = new TextBox();
        endHourField.addStyleName("bznetwork-TimeBox");
        intervalTable.setWidget(6, 0, endHourField);
        intervalTable.setText(6, 1, ":");
        intervalFormat.setWidth(6, 1, "8px");
        intervalFormat.setHorizontalAlignment(6, 1,
                HorizontalPanel.ALIGN_CENTER);
        endMinuteField = new TextBox();
        endMinuteField.addStyleName("bznetwork-TimeBox");
        intervalTable.setWidget(6, 2, endMinuteField);
        populateTimeWidgets(endHourField, endMinuteField, settings.getEnd());
        endPeriodButton = new PeriodButton(isPm(settings.getEnd()));
        endPeriodButton.setWidth("100%");
        intervalTable.setWidget(7, 0, endPeriodButton);
        intervalFormat.setColSpan(7, 0, 3);
        // servers group
        VerticalPanel serversPanel = w100(new VerticalPanel());
        table.setWidget(0, 4, serversPanel);
        format.setWidth(0, 4, "16%");
        format.setVerticalAlignment(0, 4, VerticalPanel.ALIGN_TOP);
        serversPanel.add(new HTML("<b>Servers:</b>"));
        serversField = w100(new ListBox(true));
        populateServers(serversField, model, settings);
        serversPanel.add(serversField);
        serversField.setVisibleItemCount(14);
        // search button group
        searchButton = new Button("Search");
        searchButton.addStyleName("bznetwork-LogFilterWidget-SearchButton");
        searchButton.setWidth("100%");
        searchButton.setHeight("100%");
        table.setWidget(0, 5, searchButton);
        format.setWidth(0, 5, "16%");
        format.setHeight(0, 5, "100%");
        format.setVerticalAlignment(0, 5, VerticalPanel.ALIGN_TOP);
        // We're done!
    }
    
    public void addSearchButtonListener(ClickHandler listener)
    {
        searchButton.addClickHandler(listener);
    }
    
    /**
     * Creates a settings object that holds the settings reflected by the
     * current widget selections and returns it.
     * 
     * @return
     */
    public LogsFilterSettings getCurrentSettings()
    {
        LogsFilterSettings settings = new LogsFilterSettings();
        settings.setStart(getSelectedStart());
        settings.setEnd(getSelectedEnd());
        settings.setSearch(searchField.getText());
        settings.setIgnoreCase(ignoreCaseField.isChecked());
        for (int i = 0; i < eventsField.getItemCount(); i++)
        {
            if (eventsField.isItemSelected(i))
                settings.getEvents().add(eventsField.getItemText(i));
        }
        for (int i = 0; i < inField.getItemCount(); i++)
        {
            if (inField.isItemSelected(i))
                settings.getSearchIn().add(inField.getItemText(i));
        }
        for (int i = 0; i < serversField.getItemCount(); i++)
        {
            if (serversField.isItemSelected(i))
                settings.getServers().add(
                        Integer.parseInt(BZNetwork.getSelectionValue(
                                serversField, i)));
        }
        return settings;
    }
    
    public Date getSelectedStart()
    {
        return getSelectedDate(startSelectionButton, startHourField,
                startMinuteField, startPeriodButton);
    }
    
    private Date getSelectedDate(DateButton dateButton, TextBox hourBox,
            TextBox minuteBox, PeriodButton periodButton)
    {
        Date date = dateButton.getValue();
        int hours = Integer.parseInt(hourBox.getText());
        if (hours == 0)
            throw new NumberFormatException(
                    "Hours is not between 1 and 12, inclusive");
        if (hours == 12)
            hours = 0;
        if (hours < 0 || hours > 11)
            throw new NumberFormatException(
                    "Hours is not between 1 and 12, inclusive");
        int minutes = Integer.parseInt(minuteBox.getText());
        boolean pm = periodButton.isPm();
        if (pm)
            hours = hours + 12;
        if (minutes < 0 || minutes > 59)
            throw new NumberFormatException(
                    "Minutes is not between 0 and 59, inclusive");
        date.setHours(hours);
        date.setMinutes(minutes);
        return date;
    }
    
    public Date getSelectedEnd()
    {
        return getSelectedDate(endSelectionButton, endHourField,
                endMinuteField, endPeriodButton);
    }
    
    private void populateServers(ListBox box, LogSearchModel model,
            LogsFilterSettings settings)
    {
        for (Server s : model.getServers())
        {
            box.addItem(s.getName(), "" + s.getServerid());
            if (settings.getServers().contains(s.getServerid()))
                box.setItemSelected(box.getItemCount() - 1, true);
        }
    }
    
    private void populateEvents(ListBox box, LogSearchModel model,
            LogsFilterSettings settings)
    {
        for (String s : model.getEvents())
        {
            box.addItem(s);
            if (settings.getEvents().contains(s))
                box.setItemSelected(box.getItemCount() - 1, true);
        }
    }
    
    private void populateSearchIn(ListBox box, LogsFilterSettings settings)
    {
        for (String s : LogsScreen.SEARCH_IN)
        {
            box.addItem(s);
            if (settings.getSearchIn().contains(s))
                box.setItemSelected(box.getItemCount() - 1, true);
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
    
    public static boolean isPm(Date date)
    {
        return date.getHours() >= 12;
    }
    
    public static void populateTimeWidgets(TextBox hour, TextBox minute,
            Date date)
    {
        int hourNumber = date.getHours();
        hourNumber = hourNumber % 12;
        if (hourNumber == 0)
            hourNumber = 12;
        hour.setText(padZeros(2, "" + hourNumber));
        minute.setText(padZeros(2, "" + date.getMinutes()));
    }
    
    public static String padZeros(int length, String s)
    {
        while (s.length() < length)
            s = "0" + s;
        return s;
    }
    
}
