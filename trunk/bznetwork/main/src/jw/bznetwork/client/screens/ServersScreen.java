package jw.bznetwork.client.screens;

import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DisclosureEvent;
import com.google.gwt.user.client.ui.DisclosureHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.BoxCallback;
import jw.bznetwork.client.Perms;
import jw.bznetwork.client.VerticalScreen;
import jw.bznetwork.client.data.GroupModel;
import jw.bznetwork.client.data.ServerListModel;
import jw.bznetwork.client.data.ServerModel;
import jw.bznetwork.client.data.ServerModel.LiveState;
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
    
    @SuppressWarnings("deprecation")
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
        for (final GroupModel group : result.getGroups())
        {
            row += 1;
            table.setText(row, 0, group.getName());
            format.addStyleName(row, 0, "bznetwork-ServerList-GroupName");
            format.setColSpan(row, 0, 2);
            ListBox banfileBox = generateBanfileBox(result, group.getGroupid(),
                    group.getBanfile(), true);
            if (Perms.group("edit-group-banfile", group.getGroupid()))
            {
                table.setWidget(row, 1, banfileBox);
            }
            else
            {
                table.setWidget(row, 1, new Label(banfileBox
                        .getItemText(banfileBox.getSelectedIndex())));
            }
            /*
             * We've added the group's widgets. Now we'll iterate over the
             * group's servers and add their details.
             */
            for (ServerModel server : group.getServers())
            {
                row += 1;
                table.setText(row, 0, "" + server.getPort());
                format.setHorizontalAlignment(row, 0,
                        HorizontalPanel.ALIGN_RIGHT);
                DisclosurePanel serverDropdown = new DisclosurePanel(server
                        .getName());
                final VerticalPanel serverInfoPanel = new VerticalPanel();
                serverInfoPanel.setVisible(false);
                // TODO: perhaps have the panel expanded if there are any
                // non-observer players at the server
                serverDropdown.addEventHandler(new DisclosureHandler()
                {
                    
                    @Override
                    public void onClose(DisclosureEvent event)
                    {
                        serverInfoPanel.setVisible(false);
                    }
                    
                    @Override
                    public void onOpen(DisclosureEvent event)
                    {
                        serverInfoPanel.setVisible(true);
                    }
                });
                table.setWidget(row, 1, serverDropdown);
                serverDropdown.addStyleName("bznetwork-ServerState-"
                        + server.getState().name());
                ListBox serverBanfileBox = generateBanfileBox(result, server
                        .getServerid(), server.getBanfile(), false);
                if (Perms.server("edit-server-banfile", server.getServerid(),
                        group.getGroupid()))
                {
                    table.setWidget(row, 2, serverBanfileBox);
                }
                else
                {
                    table.setWidget(row, 2, new Label(serverBanfileBox
                            .getItemText(serverBanfileBox.getSelectedIndex())));
                }
                FlowPanel linksPanel = new FlowPanel();
                table.setWidget(row, 4, linksPanel);
                /*
                 * Now we'll add some links for this server. Links right now are
                 * pretty much rename, settings, groupdb, map, upload, conf, and
                 * start/stop/kill.
                 */
                createServerLinks(group, server, linksPanel);
                /*
                 * We've added the actual server's row. Now we'll add a row to
                 * hold the server info widget. This widget is shown when the
                 * user clicks on the server's name, and contains stuff like the
                 * users that are at the server.
                 */
                row += 1;
                serverInfoPanel.add(new HTML("<b>Users:</b>"));
                table.setWidget(row, 1, serverInfoPanel);
                format.setColSpan(row, 1, 5);
            }
            /*
             * All of the servers under this group have now been added to the
             * table. Now we'll add controls to allow the user to add a new
             * server to the group.
             */
            row += 1;
            HorizontalPanel serverAddPanel = new HorizontalPanel();
            final TextBox addServerNameField = new TextBox();
            addServerNameField.setVisibleLength(15);
            serverAddPanel.add(addServerNameField);
            Button addServerButton = new Button("Add Server");
            serverAddPanel.add(addServerButton);
            if (Perms.group("create-server", group.getGroupid()))
            {
                table.setWidget(row, 1, serverAddPanel);
            }
            /*
             * Now for the listener that actually adds servers to the group.
             */
            addServerButton.addClickHandler(new ClickHandler()
            {
                
                @Override
                public void onClick(ClickEvent event)
                {
                    if (addServerNameField.getText().trim().equals(""))
                    {
                        Window.alert("You must type the server name before"
                                + " you can create a new server.");
                        return;
                    }
                    BZNetwork.authLink.addServer(addServerNameField.getText(),
                            group.getGroupid(), new BoxCallback<Void>()
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
        row += 1;
        HorizontalPanel groupAddPanel = new HorizontalPanel();
        final TextBox addGroupNameField = new TextBox();
        addGroupNameField.setVisibleLength(15);
        groupAddPanel.add(addGroupNameField);
        Button addGroupButton = new Button("Add Group");
        groupAddPanel.add(addGroupButton);
        if (Perms.global("create-group"))
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
    
    @SuppressWarnings("deprecation")
    private void createServerLinks(final GroupModel group,
            final ServerModel server, FlowPanel linksPanel)
    {
        Anchor renameLink = new Anchor("rename");
        if (Perms.server("edit-server-settings", server))
        {
            linksPanel.add(renameLink);
            linksPanel.add(new HTML("&nbsp;&nbps;"));
        }
        renameLink.addClickListener(new ClickListener()
        {
            
            @Override
            public void onClick(Widget sender)
            {
                String newName = Window.prompt(
                        "Type a new name for this server.", server.getName());
                if (newName == null)
                    return;
                BZNetwork.authLink.renameServer(server.getServerid(), newName,
                        new BoxCallback<Void>()
                        {
                            
                            @Override
                            public void run(Void result)
                            {
                                /*
                                 * TODO: change this to simply update the
                                 * server's label instead of causing the entire
                                 * page to reload.
                                 */
                                select();
                            }
                        });
            }
        });
        Anchor settingsLink = new Anchor("settings");
        if (Perms.server("edit-server-settings", server))
        {
            linksPanel.add(settingsLink);
            linksPanel.add(new HTML("&nbsp;&nbps;"));
        }
        settingsLink.addClickListener(new ClickListener()
        {
            
            @Override
            public void onClick(Widget sender)
            {
                showSettingsBox(group, server);
            }
        });
        Anchor groupdbLink = new Anchor("groupdb");
        if (Perms.server("edit-groupdb", server))
        {
            linksPanel.add(groupdbLink);
            linksPanel.add(new HTML("&nbsp;&nbps;"));
        }
        Anchor mapLink = new Anchor("map");
        /*
         * The only permission the map link is dependent on is
         * view-in-server-list, so we don't have to perform any checks here.
         */
        linksPanel.add(mapLink);
        linksPanel.add(new HTML("&nbsp;&nbps;"));
        Anchor uploadLink = new Anchor("upload");
        uploadLink.setTitle("Allows you to upload a new map for this "
                + "server. The new map will take effect when the "
                + "server is restarted.");
        if (Perms.server("edit-map", server))
        {
            linksPanel.add(uploadLink);
            linksPanel.add(new HTML("&nbsp;&nbps;"));
        }
        Anchor confLink = new Anchor("conf");
        confLink
                .setTitle("Allows you to edit this server's BZFlag configuration file.");
        if (Perms.server("edit-server-settings", server))
        {
            linksPanel.add(confLink);
            linksPanel.add(new HTML("&nbsp;&nbps;"));
        }
        if (Perms.server("start-stop-server", server))
        {
            /*
             * Now we'll add the start/stop/kill link. Which of these it is
             * depends on the server's state. If the server is starting up or
             * shutting down, then this should be kill. If the server is
             * running, then this should be stop. If the server is not rnning,
             * then this should be start.
             */
            if (server.getState() == LiveState.LIVE)
            {
                /*
                 * We need a stop link.
                 */
                Anchor stopLink = new Anchor("stop");
                linksPanel.add(stopLink);
            }
            else if (server.getState() == LiveState.STOPPED)
            {
                /*
                 * We need a start link.
                 */
                Anchor startLink = new Anchor("start");
                linksPanel.add(startLink);
            }
            else
            {
                /*
                 * We need a kill link.
                 */
                Anchor killLink = new Anchor("kill");
                linksPanel.add(killLink);
            }
            linksPanel.add(new HTML("&nbsp;&nbps;"));
        }
    }
    
    @SuppressWarnings("deprecation")
    protected void showSettingsBox(GroupModel group, ServerModel server)
    {
        final PopupPanel box = new PopupPanel(false, true);
        FlexTable table = new FlexTable();
        box.setWidget(table);
        table.setText(0, 0, "Support for settings editing is coming soon.");
        Button closeButton = new Button("Close");
        table.setWidget(1, 0, closeButton);
        closeButton.addClickListener(new ClickListener()
        {
            
            @Override
            public void onClick(Widget sender)
            {
                box.hide();
            }
        });
        box.center();
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
                BoxCallback<Void> callback = new BoxCallback<Void>()
                {
                    
                    @Override
                    public void run(Void result)
                    {
                    }
                    
                    public void fail(Throwable caught)
                    {
                        select();
                    }
                };
                if (group)
                {
                    BZNetwork.authLink.setGroupBanfile(itemid, Integer
                            .parseInt(getOptionValue(box, box
                                    .getSelectedIndex())), callback);
                }
                else
                {
                    BZNetwork.authLink.setServerBanfile(itemid, Integer
                            .parseInt(getOptionValue(box, box
                                    .getSelectedIndex())), callback);
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
