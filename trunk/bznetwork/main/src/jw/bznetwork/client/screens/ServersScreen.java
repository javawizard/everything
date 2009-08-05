package jw.bznetwork.client.screens;

import com.google.gwt.user.client.ui.FlexTable;

import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.BoxCallback;
import jw.bznetwork.client.VerticalScreen;
import jw.bznetwork.client.data.ServerListModel;
import jw.bznetwork.client.data.model.Group;

public class ServersScreen extends VerticalScreen
{
    
    @Override
    public void deselect()
    {
    }
    
    @Override
    public String getName()
    {
        return "servers";
    }
    
    @Override
    public String getTitle()
    {
        return "Servers";
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
        BZNetwork.authLink
                .getServerListModel(new BoxCallback<ServerListModel>()
                {
                    
                    @Override
                    public void run(ServerListModel result)
                    {
                        select1(result);
                    }
                });
    }
    
    protected void select1(ServerListModel result)
    {
        widget.clear();
        FlexTable table = new FlexTable();
        table.addStyleName("bznetwork-ServerListTable");
        table.setBorderWidth(0);
        table.setCellSpacing(1);
        table.setCellPadding(0);
        table.setWidth("100%");
        widget.add(table);
        widget.setWidth("100%");
        table.setHTML(0, 0, "<b>Port</b>");
        table.getFlexCellFormatter().setWidth(0, 0, "55px");
        table.setHTML(0, 1, "<b>Name</b>");
        table.setHTML(0, 2, "<b>Banfile</b>");
        table.setHTML(0, 3, "<b>Chat</b>");
        table.setHTML(0, 4, "<b>Links</b>");
        table.setHTML(0, 5, "<b>Details</b>");
        table.setHTML(1, 0, "<hr width='100%'/>");
        table.getFlexCellFormatter().setColSpan(1, 0, 6);
        int row = 1;
        for (Group group : result.getGroups())
        {
            
        }
    }
    
}
