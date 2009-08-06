package jw.bznetwork.client.screens;

import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.BoxCallback;
import jw.bznetwork.client.Perms;
import jw.bznetwork.client.VerticalScreen;
import jw.bznetwork.client.data.ServerListModel;
import jw.bznetwork.client.data.model.Banfile;
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
        FlexCellFormatter format = table.getFlexCellFormatter();
        table.addStyleName("bznetwork-ServerListTable");
        table.setBorderWidth(0);
        table.setCellSpacing(1);
        table.setCellPadding(0);
        table.setWidth("100%");
        widget.add(table);
        widget.setWidth("100%");
        table.setHTML(0, 0, "<b>Port</b>");
        format.setWidth(0, 0, "55px");
        table.setHTML(0, 1, "<b>Name</b>");
        table.setHTML(0, 2, "<b>Banfile</b>");
        table.setHTML(0, 3, "<b>Chat</b>");
        table.setHTML(0, 4, "<b>Links</b>");
        table.setHTML(0, 5, "<b>Details</b>");
        table.setHTML(1, 0, "<hr width='100%'/>");
        format.setColSpan(1, 0, 6);
        int row = 1;
        for (Group group : result.getGroups())
        {
            row += 1;
            table.setText(row, 0, group.getName());
            format.addStyleName(row, 0, "bznetwork-ServerList-GroupName");
            format.setColSpan(row, 0, 2);
            ListBox banfileBox = generateBanfileBox(result, group.getGroupid(),
                    group.getBanfile(), true);
            if (!Perms.group("edit-group-banfile", group.getGroupid()))
                banfileBox.setEnabled(false);
            table.setWidget(row, 1, banfileBox);
        }
        row += 1;
        HorizontalPanel groupAddPanel = new HorizontalPanel();
        final TextBox addGroupNameField = new TextBox();
        addGroupNameField.setVisibleLength(15);
        groupAddPanel.add(addGroupNameField);
        Button addGroupButton = new Button("Add Group");
        groupAddPanel.add(addGroupButton);
        if (Perms.global("add-group"))
        {
            table.setWidget(row, 0, groupAddPanel);
            format.setColSpan(row, 0, 2);
        }
        addGroupButton.addClickHandler(new ClickHandler()
        {
            
            @Override
            public void onClick(ClickEvent event)
            {
                if (addGroupNameField.getText().trim().equals(""))
                {
                    Window.alert("You must type the group name before"
                            + " you can create a new group.");
                    return;
                }
                BZNetwork.authLink.addGroup(addGroupNameField.getText(),
                        new BoxCallback<Void>()
                        {
                            
                            @Override
                            public void run(Void result)
                            {
                                select();
                            }
                        });
            }
        });
    }
    
    /**
     * Works for both groups and servers. Returns a list box that contains all
     * of the banfiles available, with the specified banfile the default
     * selection. If the box's entry is changed, a notification will be sent to
     * the server to save the change. The box also has a blank option at the
     * top, which can be selected. For groups, this indicates a lack of a
     * banfile. For servers, this indicates a similar lack, and the server will
     * inherit the parent group's banfile. Servers refuse to start if both their
     * own banfile and their parent's banfile are empty.<br/><br/>
     * 
     * If <tt>banfile</tt> is -1, then this empty option will be selected by
     * default.
     * 
     * @param model
     * @param itemid
     * @param banfile
     * @param group
     * @return
     */
    private ListBox generateBanfileBox(ServerListModel model, final int itemid,
            int banfileid, final boolean group)
    {
        final ListBox box = new ListBox();
        box.addItem("", "-1");
        for (Banfile banfile : model.getBanfiles())
        {
            box.addItem(banfile.getName(), "" + banfile.getBanfileid());
            if (banfile.getBanfileid() == banfileid)
                box.setSelectedIndex(box.getItemCount() - 1);
        }
        if (banfileid == -1)
            box.setSelectedIndex(0);
        box.addChangeHandler(new ChangeHandler()
        {
            
            @Override
            public void onChange(ChangeEvent event)
            {
                if (group)
                {
                    BZNetwork.authLink.setGroupBanfile(itemid, Integer
                            .parseInt(getOptionValue(box, box
                                    .getSelectedIndex())),
                            new BoxCallback<Void>()
                            {
                                
                                @Override
                                public void run(Void result)
                                {
                                }
                                
                                public void fail(Throwable caught)
                                {
                                    select();
                                }
                            });
                }
                else
                {
                    // FIXME: implement this for servers
                    throw new RuntimeException(
                            "Updating a banfile for a server is not implemented");
                }
            }
        });
        return box;
    }
    
    public static String getOptionValue(ListBox box, int index)
    {
        return ((SelectElement) box.getElement().cast()).getOptions().getItem(
                index).getValue();
    }
    
}
