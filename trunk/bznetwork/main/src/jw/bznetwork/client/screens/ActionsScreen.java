package jw.bznetwork.client.screens;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
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
        Label currentPageLabel = new Label("" + filterOffset);
        navigationPanel.add(currentPageLabel);
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
