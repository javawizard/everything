package jw.bznetwork.client.screens;

import gwtupload.client.BasicModalProgress;
import gwtupload.client.IUploader;
import gwtupload.client.SingleUploader;
import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.BoxCallback;
import jw.bznetwork.client.Perms;
import jw.bznetwork.client.VerticalScreen;
import jw.bznetwork.client.data.GroupModel;
import jw.bznetwork.client.data.LogsFilterSettings;
import jw.bznetwork.client.data.ServerListModel;
import jw.bznetwork.client.data.ServerModel;
import jw.bznetwork.client.data.ServerModel.LiveState;
import jw.bznetwork.client.data.model.Banfile;
import jw.bznetwork.client.live.LivePlayer;
import jw.bznetwork.client.ui.Header3;

import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DisclosureEvent;
import com.google.gwt.user.client.ui.DisclosureHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

public class ServersScreen extends VerticalScreen
{
    
    public class StopServerClickHandler implements ClickHandler
    {
        private int serverid;
        
        @Override
        public void onClick(ClickEvent event)
        {
            BZNetwork.authLink.stopServer(serverid, new BoxCallback<Void>()
            {
                
                @Override
                public void run(Void result)
                {
                    select();
                }
            });
        }
        
        public StopServerClickHandler(int serverid)
        {
            super();
            this.serverid = serverid;
        }
        
    }
    
    public class StartServerClickHandler implements ClickHandler
    {
        private int serverid;
        
        @Override
        public void onClick(ClickEvent event)
        {
            BZNetwork.authLink.startServer(serverid, new BoxCallback<String>()
            {
                
                @Override
                public void run(String result)
                {
                    if (result != null && !result.startsWith("bznload"))
                        Window.alert("Failed to start the server: " + result);
                    select();
                }
            });
        }
        
        public StartServerClickHandler(int serverid)
        {
            super();
            this.serverid = serverid;
        }
        
    }
    
    public class KillServerClickHandler implements ClickHandler
    {
        private int serverid;
        
        public KillServerClickHandler(int serverid)
        {
            super();
            this.serverid = serverid;
        }
        
        @Override
        public void onClick(ClickEvent event)
        {
            BZNetwork.authLink.killServer(serverid, new BoxCallback<Void>()
            {
                
                @Override
                public void run(Void result)
                {
                    select();
                }
            });
        }
        
    }
    
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
        /*
         * The header is now in place. Now we add all of the groups, and their
         * child servers.
         */
        for (final GroupModel group : result.getGroups())
        {
            row += 1;
            table.setText(row, 0, group.getName());
            BZNetwork.setCellTitle(table, row, 0, "Id: " + group.getGroupid());
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
             * Now the group links
             */
            HorizontalPanel groupLinksPanel = new HorizontalPanel();
            groupLinksPanel.setSpacing(4);
            table.setWidget(row, 3, groupLinksPanel);
            /*
             * Now we'll add some links for this group.
             */
            createGroupLinks(group, groupLinksPanel);
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
                serverDropdown.setTitle("Id: " + server.getServerid());
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
                HorizontalPanel linksPanel = new HorizontalPanel();
                linksPanel.setSpacing(4);
                table.setWidget(row, 4, linksPanel);
                /*
                 * Now we'll add some links for this server. Links right now are
                 * pretty much rename, settings, groupdb, map, upload, conf, and
                 * start/stop/kill.
                 */
                createServerLinks(group, server, linksPanel);
                /*
                 * Last is the detail for the server. We'll let the server
                 * generate the details to improve performance.
                 */
                table.setHTML(row, 5, server.getDetailString());
                /*
                 * We've added the actual server's row. Now we'll add a row to
                 * hold the server info widget. This widget is shown when the
                 * user clicks on the server's name, and contains stuff like the
                 * users that are at the server.
                 */
                row += 1;
                /*
                 * TODO: consider having this lazily loaded (IE it loads the
                 * first time a server's player list is expanded) to make the
                 * page load faster. It would still be downloaded from the
                 * server as-is, but the grid wouldn't be built until the user
                 * expands the disclosure panel for the first time.
                 */
                loadServerInfoPanel(server, serverInfoPanel);
                /*
                 * If the server is supposed to auto-expand, as determined by
                 * the BZNetwork server, then we'll expand it.
                 */
                if (server.isAutoExpand())
                    serverDropdown.setOpen(true);
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
    
    private void loadServerInfoPanel(ServerModel server, VerticalPanel panel)
    {
        if (server.getPlayers() == null || server.getPlayers().length == 0)
        {
            Label noPlayersLabel = new Label("No players");
            noPlayersLabel.addStyleName("bznetwork-ServerList-DetailsNoUsers");
            panel.add(noPlayersLabel);
            return;
        }
        // panel.add(new HTML("<b>Users:</b>"));
        Grid usersTable = new Grid(server.getPlayers().length, 5);
        panel.add(usersTable);
        int row = 0;
        for (LivePlayer player : server.getPlayers())
        {
            usersTable.setHTML(row, 0, "&nbsp;&nbsp;");
            if (player.isAdmin())
            {
                usersTable.setText(row, 1, "@");
                usersTable.getCellFormatter().addStyleName(row, 1,
                        "bznetwork-ServerPlayerList-Admin");
            }
            else if (player.isVerified())
            {
                usersTable.setText(row, 2, "+");
                usersTable.getCellFormatter().addStyleName(row, 1,
                        "bznetwork-ServerPlayerList-Verified");
            }
            else if (player.getBzid() != null
                    && !player.getBzid().trim().equals(""))
            {
                usersTable.setText(row, 2, "-");
                usersTable.getCellFormatter().addStyleName(row, 1,
                        "bznetwork-ServerPlayerList-VerifiedWrong");
            }
            usersTable.setText(row, 2, player.getCallsign());
            usersTable.getCellFormatter().addStyleName(row, 2,
                    "bznetwork-ServerPlayerList-" + player.getTeam().name());
            usersTable.setText(row, 3, player.getEmail());
            row += 1;
        }
    }
    
    private void createGroupLinks(final GroupModel group,
            HorizontalPanel linksPanel)
    {
        Anchor logsLink = new Anchor("logs");
        linksPanel.add(logsLink);
        logsLink
                .setTitle("Shows the logs for the servers in this group that have occurred today.");
        logsLink.addClickHandler(new ClickHandler()
        {
            
            @Override
            public void onClick(ClickEvent event)
            {
                LogsScreen logsScreen = (LogsScreen) BZNetwork.mainScreen
                        .get("logs");
                LogsFilterSettings filterSettings = logsScreen
                        .createDefaultSettings();
                filterSettings.getServers().clear();
                for (ServerModel server : group.getServers())
                {
                    if (Perms.server("view-logs", server.getServerid(), group
                            .getGroupid()))
                        filterSettings.getServers().add(server.getServerid());
                }
                logsScreen.preserveSettingsOnce = true;
                logsScreen.settings = filterSettings;
                logsScreen.performSearchOnce = true;
                BZNetwork.mainScreen.selectScreen("logs", null);
            }
        });
        Anchor renameLink = new Anchor("rename");
        if (Perms.group("rename-group", group.getGroupid()))
        {
            linksPanel.add(renameLink);
        }
        Anchor groupdbLink = new Anchor("groupdb");
        if (Perms.group("edit-group-groupdb", group.getGroupid()))
        {
            linksPanel.add(groupdbLink);
        }
        groupdbLink.addClickHandler(new ClickHandler()
        {
            
            @Override
            public void onClick(ClickEvent event)
            {
                showGroupGroupdbBox(group);
            }
        });
    }
    
    @SuppressWarnings("deprecation")
    private void createServerLinks(final GroupModel group,
            final ServerModel server, Panel linksPanel)
    {
        Anchor logsLink = new Anchor("logs");
        logsLink
                .setTitle("Shows the logs for this server that have occurred today.");
        if (Perms.server("view-logs", server))
        {
            linksPanel.add(logsLink);
        }
        logsLink.addClickHandler(new ClickHandler()
        {
            
            @Override
            public void onClick(ClickEvent event)
            {
                LogsScreen logsScreen = (LogsScreen) BZNetwork.mainScreen
                        .get("logs");
                LogsFilterSettings filterSettings = logsScreen
                        .createDefaultSettings();
                filterSettings.getServers().clear();
                filterSettings.getServers().add(server.getServerid());
                logsScreen.preserveSettingsOnce = true;
                logsScreen.settings = filterSettings;
                logsScreen.performSearchOnce = true;
                BZNetwork.mainScreen.selectScreen("logs", null);
            }
        });
        Anchor renameLink = new Anchor("rename");
        if (Perms.server("edit-server-settings", server))
        {
            linksPanel.add(renameLink);
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
        }
        groupdbLink.addClickHandler(new ClickHandler()
        {
            
            @Override
            public void onClick(ClickEvent event)
            {
                showServerGroupdbBox(group, server);
            }
        });
        Anchor mapLink = new Anchor("map", BZNetwork.CONTEXT_URL
                + "/download-map/" + server.getServerid() + "/"
                + URL.encode(server.getName()) + ".bzw", "_blank");
        /*
         * / The only permission the map link is dependent on is
         * view-in-server-list, so we don't have to perform any checks here.
         */
        linksPanel.add(mapLink);
        Anchor uploadLink = new Anchor("upload");
        uploadLink.setTitle("Allows you to upload a new map for this "
                + "server. The new map will take effect when the "
                + "server is restarted.");
        if (Perms.server("edit-map", server))
        {
            linksPanel.add(uploadLink);
        }
        uploadLink.addClickListener(new ClickListener()
        {
            
            @Override
            public void onClick(Widget sender)
            {
                showUploadBox(group, server);
            }
        });
        Anchor confLink = new Anchor("conf");
        confLink
                .setTitle("Allows you to edit this server's BZFlag configuration file.");
        if (Perms.server("edit-server-settings", server))
        {
            linksPanel.add(confLink);
        }
        confLink.addClickHandler(new ClickHandler()
        {
            
            @Override
            public void onClick(ClickEvent event)
            {
                showConfigBox(group, server);
            }
        });
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
                stopLink.addClickHandler(new StopServerClickHandler(server
                        .getServerid()));
            }
            else if (server.getState() == LiveState.STOPPED)
            {
                /*
                 * We need a start link.
                 */
                Anchor startLink = new Anchor("start");
                linksPanel.add(startLink);
                startLink.addClickHandler(new StartServerClickHandler(server
                        .getServerid()));
            }
            else
            {
                /*
                 * We need a kill link.
                 */
                Anchor killLink = new Anchor("kill");
                linksPanel.add(killLink);
                killLink.addClickHandler(new KillServerClickHandler(server
                        .getServerid()));
            }
        }
    }
    
    // CONFIG
    
    protected void showConfigBox(GroupModel group, final ServerModel server)
    {
        BZNetwork.authLink.getServerConfig(server.getServerid(),
                new BoxCallback<String>()
                {
                    
                    @Override
                    public void run(String result)
                    {
                        final PopupPanel box = new PopupPanel();
                        int clientWidth = Window.getClientWidth();
                        int clientHeight = Window.getClientHeight();
                        VerticalPanel panel = new VerticalPanel();
                        box.setWidget(panel);
                        panel.add(new Header3("Configuration for "
                                + server.getName()));
                        final TextArea configField = new TextArea();
                        configField.setText(result);
                        configField.setWidth(""
                                + Math.max(clientWidth - 100, 200) + "px");
                        configField.setHeight(""
                                + Math.max(clientHeight - 150, 150) + "px");
                        panel.add(configField);
                        HorizontalPanel buttonsPanel = new HorizontalPanel();
                        Button saveButton = new Button("Save");
                        Button cancelButton = new Button("Cancel");
                        buttonsPanel.add(saveButton);
                        buttonsPanel.add(cancelButton);
                        panel.add(buttonsPanel);
                        cancelButton.addClickHandler(new ClickHandler()
                        {
                            
                            @Override
                            public void onClick(ClickEvent event)
                            {
                                if (Window
                                        .confirm("Are you sure you want to discard your changes?"))
                                    box.hide();
                            }
                        });
                        saveButton.addClickHandler(new ClickHandler()
                        {
                            
                            @Override
                            public void onClick(ClickEvent event)
                            {
                                saveConfig(configField.getText(), server, box);
                            }
                            
                        });
                        box.center();
                    }
                });
    }
    
    private void saveConfig(String text, ServerModel server,
            final PopupPanel box)
    {
        BZNetwork.authLink.saveServerConfig(server.getServerid(), text,
                new BoxCallback<Void>()
                {
                    
                    @Override
                    public void run(Void result)
                    {
                        box.hide();
                    }
                });
    }
    
    // GROUP GROUPDB
    
    protected void showGroupGroupdbBox(final GroupModel group)
    {
        BZNetwork.authLink.getGroupGroupdb(group.getGroupid(),
                new BoxCallback<String>()
                {
                    
                    @Override
                    public void run(String result)
                    {
                        final PopupPanel box = new PopupPanel();
                        int clientWidth = Window.getClientWidth();
                        int clientHeight = Window.getClientHeight();
                        VerticalPanel panel = new VerticalPanel();
                        box.setWidget(panel);
                        panel
                                .add(new Header3("Groupdb for "
                                        + group.getName()));
                        final TextArea groupdbField = new TextArea();
                        groupdbField.setText(result);
                        groupdbField.setWidth(""
                                + Math.max(clientWidth - 100, 200) + "px");
                        groupdbField.setHeight(""
                                + Math.max(clientHeight - 150, 150) + "px");
                        panel.add(groupdbField);
                        HorizontalPanel buttonsPanel = new HorizontalPanel();
                        Button saveButton = new Button("Save");
                        Button cancelButton = new Button("Cancel");
                        buttonsPanel.add(saveButton);
                        buttonsPanel.add(cancelButton);
                        panel.add(buttonsPanel);
                        cancelButton.addClickHandler(new ClickHandler()
                        {
                            
                            @Override
                            public void onClick(ClickEvent event)
                            {
                                if (Window
                                        .confirm("Are you sure you want to discard your changes?"))
                                    box.hide();
                            }
                        });
                        saveButton.addClickHandler(new ClickHandler()
                        {
                            
                            @Override
                            public void onClick(ClickEvent event)
                            {
                                saveGroupGroupdb(groupdbField.getText(), group,
                                        box);
                            }
                            
                        });
                        box.center();
                    }
                });
    }
    
    private void saveGroupGroupdb(String text, GroupModel group,
            final PopupPanel box)
    {
        BZNetwork.authLink.saveGroupGroupdb(group.getGroupid(), text,
                new BoxCallback<Void>()
                {
                    
                    @Override
                    public void run(Void result)
                    {
                        box.hide();
                    }
                });
    }
    
    // SERVER GROUPDB
    
    protected void showServerGroupdbBox(GroupModel group,
            final ServerModel server)
    {
        BZNetwork.authLink.getServerGroupdb(server.getServerid(),
                new BoxCallback<String>()
                {
                    
                    @Override
                    public void run(String result)
                    {
                        final PopupPanel box = new PopupPanel();
                        int clientWidth = Window.getClientWidth();
                        int clientHeight = Window.getClientHeight();
                        VerticalPanel panel = new VerticalPanel();
                        box.setWidget(panel);
                        panel
                                .add(new Header3("Groupdb for "
                                        + server.getName()));
                        final TextArea groupdbField = new TextArea();
                        groupdbField.setText(result);
                        groupdbField.setWidth(""
                                + Math.max(clientWidth - 100, 200) + "px");
                        groupdbField.setHeight(""
                                + Math.max(clientHeight - 150, 150) + "px");
                        panel.add(groupdbField);
                        HorizontalPanel buttonsPanel = new HorizontalPanel();
                        Button saveButton = new Button("Save");
                        Button cancelButton = new Button("Cancel");
                        buttonsPanel.add(saveButton);
                        buttonsPanel.add(cancelButton);
                        panel.add(buttonsPanel);
                        cancelButton.addClickHandler(new ClickHandler()
                        {
                            
                            @Override
                            public void onClick(ClickEvent event)
                            {
                                if (Window
                                        .confirm("Are you sure you want to discard your changes?"))
                                    box.hide();
                            }
                        });
                        saveButton.addClickHandler(new ClickHandler()
                        {
                            
                            @Override
                            public void onClick(ClickEvent event)
                            {
                                saveServerGroupdb(groupdbField.getText(),
                                        server, box);
                            }
                            
                        });
                        box.center();
                    }
                });
    }
    
    private void saveServerGroupdb(String text, ServerModel server,
            final PopupPanel box)
    {
        BZNetwork.authLink.saveServerGroupdb(server.getServerid(), text,
                new BoxCallback<Void>()
                {
                    
                    @Override
                    public void run(Void result)
                    {
                        box.hide();
                    }
                });
    }
    
    // END
    
    @SuppressWarnings("deprecation")
    protected void showUploadBox(GroupModel group, ServerModel server)
    {
        final PopupPanel box = new PopupPanel(false, true);
        FlexTable table = new FlexTable();
        // FlexCellFormatter format = table.getFlexCellFormatter();
        box.setWidget(table);
        table.setWidget(0, 0, new Header3("Upload new map for "
                + server.getName()));
        /*
         * Now we'll create the uploader. We're creating an anonymous subclass
         * of Button here so that we can override the addStyleName method to
         * prevent the "changed" style from being set by the SingleUploader. It
         * sets this style when the user selects a file, to highlight the button
         * in red to remind the user that they still have to click it to start
         * the upload. I don't, however, like this, so I'm filtering it out.
         */
        SingleUploader uploader = new SingleUploader(new BasicModalProgress(),
                new Button("Upload")
                {
                    
                    @Override
                    public void addStyleName(String style)
                    {
                        if (!style.equals("changed"))
                            super.addStyleName(style);
                    }
                });
        uploader.add(new Hidden("serverid", "" + server.getServerid()));
        uploader.setServletPath("upload.gwtupmap");
        uploader.setOnFinishHandler(new ValueChangeHandler<IUploader>()
        {
            
            @Override
            public void onValueChange(ValueChangeEvent<IUploader> event)
            {
                Window.alert("The map has been successfully uploaded.");
                box.hide();
            }
        });
        HorizontalPanel panel = new HorizontalPanel();
        panel.add(uploader);
        final Button cancelButton = new Button("Cancel");
        panel.add(cancelButton);
        uploader.setOnStartHandler(new ValueChangeHandler<IUploader>()
        {
            
            @Override
            public void onValueChange(ValueChangeEvent<IUploader> event)
            {
                cancelButton.setEnabled(false);
            }
        });
        cancelButton.addClickListener(new ClickListener()
        {
            
            @Override
            public void onClick(Widget sender)
            {
                box.hide();
            }
        });
        table.setWidget(1, 0, panel);
        box.center();
    }
    
    @SuppressWarnings("deprecation")
    protected void showSettingsBox(GroupModel group, final ServerModel server)
    {
        final PopupPanel box = new PopupPanel(false, true);
        FlexTable table = new FlexTable();
        box.setWidget(table);
        FlexCellFormatter format = table.getFlexCellFormatter();
        Header3 header = new Header3("Settings for " + server.getName(), true);
        header.setWidth("100%");
        table.setWidget(0, 0, header);
        format.setColSpan(0, 0, 2);
        table.setText(1, 0, "Port: ");
        table.setText(2, 0, "Public: ");
        table.setText(3, 0, "Inherit groupdb: ");
        table.setText(4, 0, "Log level: ");
        table.setText(5, 0, "Notes: ");
        format.setVerticalAlignment(5, 0, VerticalPanel.ALIGN_TOP);
        final TextBox portField = new TextBox();
        portField.setText("" + server.getPort());
        table.setWidget(1, 1, portField);
        final SimpleCheckBox publicCheckbox = new SimpleCheckBox();
        publicCheckbox.setChecked(server.isListed());
        table.setWidget(2, 1, publicCheckbox);
        final SimpleCheckBox inheritCheckbox = new SimpleCheckBox();
        inheritCheckbox.setChecked(server.isInheritgroupdb());
        inheritCheckbox.setEnabled(Perms.server("inherit-parent-groupdb",
                server.getServerid(), server.getGroupid()));
        table.setWidget(3, 1, inheritCheckbox);
        table.setHTML(4, 1,
                "<span style='color: #888888'>Not supported yet</span>");
        final TextArea notesField = new TextArea();
        notesField.setCharacterWidth(65);
        notesField.setVisibleLines(7);
        notesField.setText(server.getNotes());
        table.setWidget(5, 1, notesField);
        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");
        HorizontalPanel buttonsPanel = new HorizontalPanel();
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);
        table.setWidget(6, 1, buttonsPanel);
        box.center();
        cancelButton.addClickListener(new ClickListener()
        {
            
            @Override
            public void onClick(Widget sender)
            {
                if (Window
                        .confirm("Are you sure you want to discard your changes?"))
                    box.hide();
            }
        });
        saveButton.addClickListener(new ClickListener()
        {
            
            @Override
            public void onClick(Widget sender)
            {
                int portNumber;
                try
                {
                    portNumber = Integer.parseInt(portField.getText());
                }
                catch (Exception e)
                {
                    Window
                            .alert("The value you typed for the server's port isn't a number.");
                    return;
                }
                if (portNumber < 0 || portNumber > 65535)
                {
                    Window.alert("The port number must be greater or "
                            + "equal to 0 and less than 65536.");
                    return;
                }
                server.setPort(portNumber);
                server.setListed(publicCheckbox.isChecked());
                server.setInheritgroupdb(inheritCheckbox.isChecked());
                server.setNotes(notesField.getText());
                BZNetwork.authLink.updateServer(server, new BoxCallback<Void>()
                {
                    
                    @Override
                    public void run(Void result)
                    {
                        box.hide();
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
