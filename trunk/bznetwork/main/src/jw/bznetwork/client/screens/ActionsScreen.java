package jw.bznetwork.client.screens;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.BoxCallback;
import jw.bznetwork.client.VerticalScreen;
import jw.bznetwork.client.data.ActionLogModel;
import jw.bznetwork.client.ui.Header2;
import jw.bznetwork.server.BZNetworkServer;

public class ActionsScreen extends VerticalScreen
{
    private static final int LENGTH = 25;
    private String filterEvent;
    private String filterProvider;
    private String filterUsername;
    private int filterOffset;
    
    @Override
    public void deselect()
    {
    }
    
    @Override
    public String getName()
    {
        return "actions";
    }
    
    @Override
    public String getTitle()
    {
        return "Actions";
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
        filterEvent = null;
        filterProvider = null;
        filterUsername = null;
        filterOffset = 0;
        select1();
    }
    
    public void select1()
    {
        BZNetwork.authLink.getActionLogModel(filterEvent, filterProvider,
                filterUsername, filterOffset, LENGTH,
                new BoxCallback<ActionLogModel>()
                {
                    
                    @Override
                    public void run(ActionLogModel result)
                    {
                        select2(result);
                    }
                });
    }
    
    protected void select2(ActionLogModel result)
    {
        widget.clear();
        widget.add(new Header2("Actions"));
        FlexTable table = new FlexTable();
        widget.add(table);
        FlexCellFormatter format = table.getFlexCellFormatter();
        FlexTable headerTable = new FlexTable();
        FlexCellFormatter headerFormat = headerTable.getFlexCellFormatter();
        Anchor filterLink = new Anchor(getFilterLinkText());
        headerTable.setWidget(0, 0, filterLink);
        headerFormat.setHorizontalAlignment(0, 1, HorizontalPanel.ALIGN_LEFT);
        HorizontalPanel navigationPanel = new HorizontalPanel();
        headerTable.setWidget(0, 1, navigationPanel);
        headerFormat.setHorizontalAlignment(0, 1, HorizontalPanel.ALIGN_RIGHT);
        int row = 0;
        table.setWidget(row, 0, headerTable);
        format.setColSpan(row, 0, 4);
        /*
         * TODO: this really should be Math.max'd with the count received in the
         * result so that it won't appear messed up if action messages have been
         * deleted since we loaded the page
         */
        Label currentPageLabel = new Label("" + (filterOffset + 1));
        navigationPanel.add(currentPageLabel);
        navigationPanel.add(new Label("-"));
        Label nextPageLabel = new Label("" + filterOffset
                + result.getActions().length);
        navigationPanel.add(nextPageLabel);
        navigationPanel.add(new HTML("&nbsp;of&nbsp;"));
        Label totalLabel = new Label("" + result.getCount());
        navigationPanel.add(totalLabel);
        row += 1;
        for (int i = 0; i < result.getActions().length; i++)
        {
            /*
             * TODO: pick up here 2009.08.12, add a header above this for
             * statement that contains "when", "user", "event", and "details",
             * add a row for each event, details should be a disclosurepanel,
             * row after that has full colspan that contains the real details,
             * trim details in details column to like 64 or 18 chars or
             * something and replace newlines with spaces, details in dropdown
             * on the next row should be in a <pre> tag, filtering dialog box
             * changes ActionsScreen fields and select()s.
             */
        }
    }
    
    private String getFilterLinkText()
    {
        if (filterProvider == null && filterUsername == null
                && filterEvent == null)
            /*
             * Not filtering on anything
             */
            return "Filter";
        else if (filterEvent == null)
            /*
             * Filtering on user only
             */
            return "Filtering on user " + filterProvider + ":" + filterUsername;
        else if (filterProvider == null && filterUsername == null)
            /*
             * Filtering on event only
             */
            return "Filtering on event " + filterEvent;
        else
            /*
             * Filtering on user and event
             */
            return "Filtering on event " + filterEvent + " and user "
                    + filterProvider + ":" + filterUsername;
    }
    
}
