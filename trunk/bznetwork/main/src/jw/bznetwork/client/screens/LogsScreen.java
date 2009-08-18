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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.BoxCallback;
import jw.bznetwork.client.VerticalScreen;
import jw.bznetwork.client.data.LogSearchModel;
import jw.bznetwork.client.data.LogsFilterSettings;
import jw.bznetwork.client.ui.HorizontalRule;
import jw.bznetwork.client.ui.LogsFilterWidget;
import jw.bznetwork.client.ui.ServerResponseWidget;

public class LogsScreen extends VerticalScreen
{
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
    
    public LogsFilterSettings settings;
    
    private LogsFilterWidget filterWidget;
    
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
    }
    
    @Override
    public void reselect()
    {
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
        /*
         * TODO: pick up here, create the filter components and load the
         * settings into them, figure out where to add a search button
         */
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
        widget.add(resultsWrapper);
        filterWidget.addSearchButtonListener(new ClickHandler()
        {
            
            @Override
            public void onClick(ClickEvent event)
            {
                doPerformSearch();
            }
        });
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
        // Window.alert("Constructed url: " + url);
        /*
         * We have our query. Now we'll execute it.
         */
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        builder.setTimeoutMillis(30 * 1000);
        final PopupPanel box = BZNetwork.showLoadingBox();
        xmlHttpRequest = null;
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
                }
                finally
                {
                    box.hide();
                }
            }
        });
        try
        {
            Request request = builder.send();
            // xmlHttpRequest = BZNetwork.getXMLHttpRequest(request);
        }
        catch (RequestException e)
        {
            box.hide();
            BZNetwork.fail(e);
        }
    }
    
    private LogsFilterSettings createDefaultSettings()
    {
        LogsFilterSettings theSettings = new LogsFilterSettings();
        theSettings.setStart(getStartOfDay(new Date()));
        theSettings.setEnd(getEndOfDay(new Date()));
        theSettings.setSearch("");
        theSettings.setIgnoreCase(false);
        for (String s : SEARCH_IN)
            theSettings.getSearchIn().add(s);
        // leave the server list empty for all servers
        // leave the event list empty for all events
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
    
}
