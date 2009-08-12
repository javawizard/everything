package jw.bznetwork.client.screens;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DisclosureEvent;
import com.google.gwt.user.client.ui.DisclosureHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
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
import jw.bznetwork.client.data.model.Action;
import jw.bznetwork.client.ui.Header2;
import jw.bznetwork.client.ui.PreWidget;
import jw.bznetwork.server.BZNetworkServer;

public class ActionsScreen extends VerticalScreen
{
    private static final int LENGTH = 20;
    private static final int MAX_DETAILS_PREVIEW_SIZE = 80;
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
        select1();
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
    
    @SuppressWarnings("deprecation")
    protected void select2(ActionLogModel result)
    {
        widget.clear();
        widget.add(new Header2("Actions"));
        FlexTable table = new FlexTable();
        table.setCellPadding(0);
        table.setCellSpacing(0);
        widget.add(table);
        FlexCellFormatter format = table.getFlexCellFormatter();
        FlexTable headerTable = new FlexTable();
        headerTable.setWidth("100%");
        FlexCellFormatter headerFormat = headerTable.getFlexCellFormatter();
        Anchor filterLink = new Anchor(getFilterLinkText());
        headerTable.setWidget(0, 0, filterLink);
        headerFormat.setHorizontalAlignment(0, 1, HorizontalPanel.ALIGN_LEFT);
        HorizontalPanel navigationPanel = new HorizontalPanel();
        headerTable.setWidget(0, 1, navigationPanel);
        headerFormat.setHorizontalAlignment(0, 1, HorizontalPanel.ALIGN_RIGHT);
        int row = 0;
        table.setWidget(row, 0, headerTable);
        format.setColSpan(row, 0, 7);
        /*
         * TODO: this really should be Math.max'd with the count received in the
         * result so that it won't appear messed up if action messages have been
         * deleted since we loaded the page
         */
        Label currentPageLabel = new Label("" + (filterOffset + 1));
        navigationPanel.add(currentPageLabel);
        navigationPanel.add(new Label("-"));
        Label nextPageLabel = new Label(""
                + (filterOffset + result.getActions().length));
        navigationPanel.add(nextPageLabel);
        navigationPanel.add(new HTML("&nbsp;of&nbsp;"));
        Label totalLabel = new Label("" + result.getCount());
        navigationPanel.add(totalLabel);
        Anchor previousLink = new Anchor("<<");
        Anchor nextLink = new Anchor(">>");
        if (filterOffset > 0)
        {
            navigationPanel.add(new HTML("&nbsp;"));
            navigationPanel.add(previousLink);
        }
        /*
         * If our current offset, plus the number of results (so the number of
         * the last result) is greater than the total number of results.
         */
        if ((filterOffset + result.getActions().length) > result.getCount())
        {
            navigationPanel.add(new HTML("&nbsp;"));
            navigationPanel.add(nextLink);
        }
        previousLink.addClickHandler(new ClickHandler()
        {
            
            @Override
            public void onClick(ClickEvent event)
            {
                filterOffset = filterOffset - LENGTH;
                if(filterOffset < 0)
                    filterOffset = 0;
                select1();
            }
        });
        nextLink.addClickHandler(new ClickHandler()
        {
            
            @Override
            public void onClick(ClickEvent event)
            {
                filterOffset = filterOffset + 20;
                select1();
            }
        });
        row += 1;
        table.setHTML(row, 0, "<b>When</b>");
        table.setHTML(row, 1, "&nbsp;&nbsp;");
        table.setHTML(row, 2, "<b>User</b>");
        table.setHTML(row, 3, "&nbsp;&nbsp;");
        table.setHTML(row, 4, "<b>Event</b>");
        table.setHTML(row, 5, "&nbsp;&nbsp;");
        table.setHTML(row, 6, "<b>Details</b>");
        for (int i = 0; i < result.getActions().length; i++)
        {
            row += 1;
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
            Action action = result.getActions()[i];
            table.setText(row, 0, BZNetwork.format(action.getWhen()));
            table.setText(row, 2, action.getProvider() + ":"
                    + action.getUsername());
            table.setText(row, 4, action.getEvent());
            String detailsSummary = action.getDetails();
            if (detailsSummary.length() > MAX_DETAILS_PREVIEW_SIZE)
            {
                detailsSummary = detailsSummary.substring(0,
                        MAX_DETAILS_PREVIEW_SIZE);
                detailsSummary += "...";
            }
            detailsSummary = detailsSummary.replace("\n", " -- ");
            DisclosurePanel detailsNamePanel = new DisclosurePanel(
                    detailsSummary, false);
            final PreWidget detailsPanel = new PreWidget(action.getDetails());
            detailsPanel.addStyleName("bznetwork-Actions-DetailsBox");
            detailsPanel.setVisible(false);
            detailsNamePanel.addEventHandler(new DisclosureHandler()
            {
                
                @Override
                public void onClose(DisclosureEvent event)
                {
                    detailsPanel.setVisible(false);
                }
                
                @Override
                public void onOpen(DisclosureEvent event)
                {
                    detailsPanel.setVisible(true);
                }
            });
            table.setWidget(row, 6, detailsNamePanel);
            table.setHTML(row, 1, "&nbsp;&nbsp;&nbsp;&nbsp;");
            table.setHTML(row, 3, "&nbsp;&nbsp;&nbsp;&nbsp;");
            table.setHTML(row, 5, "&nbsp;&nbsp;&nbsp;&nbsp;");
            row += 1;
            table.setWidget(row, 0, detailsPanel);
            format.setColSpan(row, 0, 7);
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
