package jw.bznetwork.client.screens;

import java.util.Date;

import com.google.gwt.user.client.ui.Label;

import jw.bznetwork.client.VerticalScreen;
import jw.bznetwork.client.data.LogsFilterSettings;

public class LogsScreen extends VerticalScreen
{
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
    
    @Override
    public void deselect()
    {
        widget.clear();
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
