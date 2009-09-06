package jw.bznetwork.client.screens;

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.RequestTimeoutException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.BoxCallback;
import jw.bznetwork.client.VerticalScreen;
import jw.bznetwork.client.data.LogSearchModel;
import jw.bznetwork.client.data.LogsFilterSettings;
import jw.bznetwork.client.ui.HorizontalRule;
import jw.bznetwork.client.ui.LogsFilterWidget;
import jw.bznetwork.client.ui.ServerResponseWidget;
import jw.bznetwork.client.ui.VerticalBar;

public class LogsScreen extends VerticalScreen
{
    public class SayClickHandler implements ClickHandler
    {
        
        @Override
        public void onClick(ClickEvent event)
        {
            if (lowerSayField.getText().trim().equals(""))
            {
                Window.alert("You need to type some text to say first");
                return;
            }
            if (settings.getServers().size() != 1)
            {
                if (!Window
                        .confirm("You're searching on more than one server. The "
                                + "message will be sent to all of the servers you "
                                + "are searching on. Continue?"))
                    return;
            }
            BZNetwork.authLink.say(settings.getServers(), lowerSayField
                    .getText(), new BoxCallback<Void>()
            {
                
                @Override
                public void run(Void result)
                {
                    scrollDownOnce = true;
                    doPerformSearch();
                }
            });
        }
    }
    
    /**
     * Mirrored on BZNetworkServer.SEARCH_IN
     * 
     * FIXME: move this to a shared constants class
     */
    public static final String[] SEARCH_IN = new String[]
    {
            "event", "source", "target", "sourceteam", "targetteam",
            "ipaddress", "bzid", "email", "data"
    };
    private static final long ONE_DAY_MILLIS = 1000 * 60 * 60 * 24;
    /**
     * When the logs screen is selected, if this is true, then the filter
     * settings set in the fields will be loaded into the UI, and this will be
     * set to false. If this is false, the filter settings in the fields will be
     * reset to their defaults and loaded into the UI. This is mainly used for
     * other "logs" links that point to this page and that would want to
     * configure the settings for viewing logs for that particular element.
     */
    public boolean preserveSettingsOnce = false;
    /**
     * If this is true, then when the filter widgets load, a search will be
     * performed immediately. This is used when clicking on the logs links in
     * the servers screen.
     */
    public boolean performSearchOnce = false;
    /**
     * If this is true, then the page will be scrolled to the bottom the next
     * time the results page loads.
     */
    public boolean scrollDownOnce = false;
    
    public LogsFilterSettings settings;
    
    private FlexTable lowerPanelWrapper;
    private HorizontalPanel lowerPanel;
    private LogsFilterWidget filterWidget;
    /*
     * Start of lower panel widgets
     */
    private Button lowerSearchButton;
    private TextBox lowerSayField;
    private Button lowerSayButton;
    private ListBox lowerAutoRefreshBox;
    private CheckBox lowerScrollDownBox;
    /*
     * End of lower panel widgets
     */

    private LogSearchModel searchModel;
    private SimplePanel resultsWrapper = new SimplePanel();
    private JavaScriptObject xmlHttpRequest;
    
    @Override
    public void deselect()
    {
        widget.clear();
        resultsWrapper.clear();
    }
    
    @Override
    public String getName()
    {
        return "logs";
    }
    
    @Override
    public String getTitle()
    {
        return "Logs";
    }
    
    @Override
    public void init()
    {
        /*
         * Load the lower panel
         */
        lowerPanelWrapper = new FlexTable();
        lowerPanelWrapper.setWidth("100%");
        lowerPanel = new HorizontalPanel();
        lowerPanel.setVerticalAlignment(lowerPanel.ALIGN_MIDDLE);
        lowerPanelWrapper.setWidget(0, 0, lowerPanel);
        lowerPanelWrapper.getFlexCellFormatter().setHorizontalAlignment(0, 0,
                HorizontalPanel.ALIGN_RIGHT);
        /*
         * Say box
         */
        lowerSayField = new TextBox();
        lowerSayField.setVisibleLength(30);
        lowerPanel.add(lowerSayField);
        /*
         * Say button
         */
        lowerSayButton = new Button("Say");
        lowerSayButton.addClickHandler(new SayClickHandler());
        lowerPanel.add(lowerSayButton);
        lowerPanel.add(new VerticalBar());
        
        /*
         * Auto refresh box
         */
        lowerAutoRefreshBox = buildAutoRefreshBox();
        lowerPanel.add(lowerAutoRefreshBox);
        lowerScrollDownBox = new CheckBox("Scroll down");
        lowerScrollDownBox.addStyleName("bznetwork-vertical-center");
        lowerPanel.add(lowerScrollDownBox);
        lowerPanel.add(new VerticalBar());
        /*
         * Lower search button
         */
        lowerSearchButton = new Button("Search");
        lowerSearchButton.addClickHandler(new ClickHandler()
        {
            
            @Override
            public void onClick(ClickEvent event)
            {
                scrollDownOnce = lowerScrollDownBox.isChecked();
                doPerformSearch();
            }
        });
        lowerPanel.add(lowerSearchButton);
    }
    
    private ListBox buildAutoRefreshBox()
    {
        ListBox box = new ListBox();
        box.setTitle("Select an interval to refresh that often. For example, "
                + "selecting \"1 minute\" will cause the logs to be "
                + "automatically reloaded every minute.");
        box.addItem("No refresh", "");
        box.addItem("20 seconds", "2");
        box.addItem("40 seconds", "4");
        box.addItem("1 minute", "6");
        box.addItem("2 minutes", "12");
        box.addItem("5 minutes", "30");
        return box;
    }
    
    @Override
    public void reselect()
    {
        deselect();
        select();
    }
    
    @Override
    public void select()
    {
        if (preserveSettingsOnce)
        {
            preserveSettingsOnce = false;
        }
        else
        {
            settings = createDefaultSettings();
        }
        BZNetwork.authLink.getLogSearchModel(new BoxCallback<LogSearchModel>()
        {
            
            @Override
            public void run(LogSearchModel result)
            {
                select1(result);
            }
        });
    }
    
    protected void select1(LogSearchModel model)
    {
        this.searchModel = model;
        widget.clear();
        filterWidget = new LogsFilterWidget(settings, model);
        filterWidget.setWidth("100%");
        widget.add(filterWidget);
        widget.setWidth("100%");
        widget.add(new HorizontalRule("100%"));
        // widget.add(new Label("Local machine time is "
        // + BZNetwork.format(new Date()) + " -- " + new Date().getTime()
        // + " -- " + new Date().getTimezoneOffset()));
        widget.add(resultsWrapper);
        widget.add(lowerPanelWrapper);
        lowerPanelWrapper.setVisible(false);
        filterWidget.addSearchButtonListener(new ClickHandler()
        {
            
            @Override
            public void onClick(ClickEvent event)
            {
                doPerformSearch();
            }
        });
        if (performSearchOnce)
        {
            performSearchOnce = false;
            doPerformSearch();
        }
    }
    
    protected void doPerformSearch()
    {
        /*
         * First we need to get the new settings from the filter widget and
         * store them locally.
         */
        settings = filterWidget.getCurrentSettings();
        /*
         * Now we need to build those settings into a list of parameters to send
         * to the server.
         */
        String url = BZNetwork.CONTEXT_URL + "/logviewer.jsp?random-var="
                + Math.random() + "." + Math.random();
        url += "&start=" + settings.getStart().getTime();
        url += "&end=" + settings.getEnd().getTime();
        if (settings.getSearch() != null
                && !settings.getSearch().trim().equals(""))
        {
            url += "&search=" + URL.encodeComponent(settings.getSearch());
            for (String s : settings.getSearchIn())
            {
                url += "&searchin=" + URL.encodeComponent(s);
            }
            url += "&caseignore="
                    + (settings.isIgnoreCase() ? "true" : "false");
        }
        for (String s : settings.getEvents())
        {
            url += "&event=" + URL.encodeComponent(s);
        }
        for (int i : settings.getServers())
        {
            url += "&server=" + i;
        }
        /*
         * We'll also add our timezone offset to the query so that the server
         * can output dates in our timezone.
         */
        int reversedTimezoneOffset = (new Date().getTimezoneOffset() * -1);
        int absoluteTimezoneOffset = Math.abs(reversedTimezoneOffset);
        int hours = absoluteTimezoneOffset / 60;
        int minutes = absoluteTimezoneOffset % 60;
        boolean negative = reversedTimezoneOffset < 0;
        String timezoneSpecifier = "GMT" + (negative ? "-" : "+")
                + LogsFilterWidget.padZeros(2, "" + hours)
                + LogsFilterWidget.padZeros(2, "" + minutes);
        url += "&timezone=" + timezoneSpecifier;
        // Window.alert("Constructed url: " + url);
        /*
         * We have our query. Now we'll execute it.
         */
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        builder.setTimeoutMillis(30 * 1000);
        final PopupPanel box = BZNetwork.showLoadingBox();
        builder.setCallback(new RequestCallback()
        {
            
            @Override
            public void onError(Request request, Throwable exception)
            {
                box.hide();
                if (exception instanceof RequestTimeoutException)
                {
                    Window
                            .alert("The server didn't send the logs within 30 seconds. Try "
                                    + "searching again, and maybe select a smaller interval.");
                }
                else
                {
                    BZNetwork.fail(exception);
                }
            }
            
            @Override
            public void onResponseReceived(Request request, Response response)
            {
                try
                {
                    resultsWrapper.clear();
                    HTML html = new HTML(response.getText());
                    html.setWidth("100%");
                    resultsWrapper.setWidth("100%");
                    resultsWrapper.setWidget(html);
                    lowerPanelWrapper.setVisible(true);
                    if (scrollDownOnce)
                    {
                        scrollDownOnce = false;
                        Window.scrollTo(0, 2000000);
                    }
                }
                finally
                {
                    box.hide();
                }
            }
        });
        try
        {
            builder.send();
            // xmlHttpRequest = BZNetwork.getXMLHttpRequest(request);
        }
        catch (RequestException e)
        {
            box.hide();
            BZNetwork.fail(e);
        }
    }
    
    LogsFilterSettings createDefaultSettings()
    {
        LogsFilterSettings theSettings = new LogsFilterSettings();
        theSettings.setStart(getStartOfDay(new Date()));
        theSettings.setEnd(getEndOfDay(new Date()));
        theSettings.setSearch("");
        theSettings.setIgnoreCase(false);
        for (String s : SEARCH_IN)
            theSettings.getSearchIn().add(s);
        return theSettings;
    }
    
    private Date getEndOfDay(Date date)
    {
        Date created = new Date();
        created.setYear(date.getYear());
        created.setMonth(date.getMonth());
        created.setDate(date.getDate());
        created.setHours(23);
        created.setMinutes(59);
        created.setSeconds(59);
        return created;
    }
    
    private Date getStartOfDay(Date date)
    {
        Date created = new Date();
        created.setYear(date.getYear());
        created.setMonth(date.getMonth());
        created.setDate(date.getDate());
        created.setHours(0);
        created.setMinutes(0);
        created.setSeconds(0);
        return created;
    }
    
    public void tick(int number)
    {
        String refreshFrequency = BZNetwork.getSelectionValue(
                lowerAutoRefreshBox, lowerAutoRefreshBox.getSelectedIndex());
        if (refreshFrequency.equals(""))
            return;
        int interval = Integer.parseInt(refreshFrequency);
        if ((number % interval) == 0)
        {
            scrollDownOnce = lowerScrollDownBox.isChecked();
            doPerformSearch();
        }
    }
    
}
