package net.sf.opengroove.realmserver.gwt.core;

import net.sf.opengroove.realmserver.gwt.core.chart.GCanvas;
import net.sf.opengroove.realmserver.gwt.core.rcp.AnonLink;
import net.sf.opengroove.realmserver.gwt.core.rcp.AnonLinkAsync;
import net.sf.opengroove.realmserver.gwt.core.rcp.AuthException;
import net.sf.opengroove.realmserver.gwt.core.rcp.AuthLink;
import net.sf.opengroove.realmserver.gwt.core.rcp.AuthLinkAsync;
import net.sf.opengroove.realmserver.gwt.core.rcp.UserException;
import net.sf.opengroove.realmserver.gwt.core.rcp.model.GUser;
import net.sf.opengroove.realmserver.gwt.core.rcp.model.PKIGeneralInfo;

import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.InfoConfig;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.DockPanel.DockLayoutConstant;

public class AdminInterface implements EntryPoint
{
    public static AnonLinkAsync anonLink;
    
    public static AuthLinkAsync authLink;
    /**
     * The base server url, without a trailing slash. For example,
     * "http://localhost:34567".
     */
    private String baseServerUrl;
    
    private boolean isUrlSecure;
    
    private TextBox usernameField;
    
    private PasswordTextBox passwordField;
    
    private Button loginButton;
    
    private HTML loginStatusLabel;
    /**
     * The root container that objects should be added to. This container is
     * typically surrounded by some empty space and a header and a footer, none
     * of which need to be added to this panel.
     */
    private FlowPanel rootContainer;
    
    private TabPanel tabs;
    
    private Grid userListTable;
    
    public void onModuleLoad()
    {
        loadRootContainer();
        String moduleUrl = GWT.getModuleBaseURL();
        if (moduleUrl.startsWith("https"))
            isUrlSecure = true;
        String moduleUrlWithoutProtocol = moduleUrl
            .substring(isUrlSecure ? "https://".length()
                : "http://".length());
        int moduleUrlFirstSlash = moduleUrlWithoutProtocol
            .indexOf("/");
        String moduleServerSpec = moduleUrlWithoutProtocol
            .substring(0, moduleUrlFirstSlash);
        baseServerUrl = (isUrlSecure ? "https://"
            : "http://")
            + moduleServerSpec;
        anonLink = GWT.create(AnonLink.class);
        authLink = GWT.create(AuthLink.class);
        ((ServiceDefTarget) anonLink)
            .setServiceEntryPoint(baseServerUrl
                + "/bypass/anonlink");
        ((ServiceDefTarget) authLink)
            .setServiceEntryPoint(baseServerUrl
                + "/authlink");
        Window.setTitle("Log in - OGRSAdmin");
        final HTMLPanel loginPanel = new HTMLPanel(
            "<table border='0' cellpadding='2'>"
                + "<tr><td colspan='2'>Please enter your username and "
                + "password to log in."
                + "</td></tr>"
                + "<tr><td colspan='2'><div id='statusLabel'/></td></tr>"
                + "<tr><td><b>Username: </b></td><td>"
                + "<div id='usernameField'/></td></tr>"
                + "<tr><td><b>Password: </b></td><td>"
                + "<div id='passwordField'/></td></tr>"
                + "<tr><td>&nbsp;</td><td>"
                + "<div id='loginButton'/></td></tr></table>");
        final ClickListener loginClickListener = new ClickListener()
        {
            
            public void onClick(Widget sender)
            {
                attemptLogin();
            }
        };
        KeyboardListenerAdapter loginKeyListener = new KeyboardListenerAdapter()
        {
            
            public void onKeyDown(Widget sender,
                char keyCode, int modifiers)
            {
                if (keyCode == KeyboardListener.KEY_ENTER)
                    loginClickListener.onClick(sender);
            }
        };
        usernameField = new TextBox();
        usernameField.setVisibleLength(20);
        loginPanel.add(usernameField, "usernameField");
        passwordField = new PasswordTextBox();
        passwordField.setVisibleLength(20);
        loginPanel.add(passwordField, "passwordField");
        loginStatusLabel = new HTML("");
        loginPanel.add(loginStatusLabel, "statusLabel");
        loginButton = new Button("Log in");
        loginButton.addClickListener(loginClickListener);
        loginPanel.add(loginButton, "loginButton");
        rootContainer.add(loginPanel);
        anonLink.isLoggedIn(new AsyncCallback<Boolean>()
        {
            
            public void onFailure(Throwable caught)
            {
            }
            
            public void onSuccess(Boolean isLoggedIn)
            {
                if (isLoggedIn)
                    loadUI();
            }
        });
    }
    
    private void loadRootContainer()
    {
        RootPanel root = RootPanel.get();
        HTMLPanel wrapper = new HTMLPanel(
            "<table width='100%' border='0' cellspacing='12' cellpadding=''>"
                + "<tr><td>"
                + "<table border='0' cellspacing='0' cellpadding='3'>"
                + "<tr><td colspan='2'><span style='font-size: 30'>"
                + "<b>OpenGroove Realm Server Administration</b></span>"
                + "</td></tr>"
                + "<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                + "</td><td width='100%'>"
                + "<div id='content'/></td></tr>"
                + "<tr><td>&nbsp;</td><td>&nbsp;</td></tr>"
                + "<tr<td>&nbsp;</td><td><small>"
                + "<a href='http://www.opengroove.org' target='_blank'>"
                + "OpenGroove</a>" + "</small></td></tr>");
        rootContainer = new FlowPanel();
        rootContainer.setWidth("100%");
        wrapper.add(rootContainer, "content");
        root.add(wrapper);
    }
    
    protected void attemptLogin()
    {
        loginButton.setEnabled(false);
        usernameField.setEnabled(false);
        passwordField.setEnabled(false);
        loginStatusLabel
            .setHTML("<b>Please wait while OGRSAdmin logs you in...</b>");
        anonLink.authenticate(usernameField.getText(),
            passwordField.getText(),
            new AsyncCallback<Void>()
            {
                
                public void onFailure(Throwable caught)
                {
                    /*
                     * onFailure occurs when the connection failed or the wrong
                     * username/password was entered. If it was a wrong
                     * username/password, then the connection will be of type
                     * AuthException.
                     */
                    if (caught instanceof AuthException)
                    {
                        /*
                         * Authentication error
                         */
                        loginStatusLabel
                            .setHTML("<span style='color: red'>Incorrect username and/or password.</span>");
                    }
                    else
                    {
                        /*
                         * Connection error
                         */
                        loginStatusLabel
                            .setHTML("<span style='color: red'>An error occured while connecting to the server.</span>The message is: "
                                + caught.getClass()
                                    .getName()
                                + " - "
                                + caught.getMessage());
                    }
                    loginButton.setEnabled(true);
                    usernameField.setEnabled(true);
                    passwordField.setEnabled(true);
                }
                
                public void onSuccess(Void result)
                {
                    /*
                     * The user was successfully authenticated. Now we load the
                     * OGRSAdmin UI.
                     */
                    loadUI();
                }
            });
    }
    
    protected void loadUI()
    {
        Window.setTitle("OGRealmServerAdmin Loading");
        removeAll(rootContainer);
        tabs = new TabPanel();
        tabs.setAnimationEnabled(false);
        loadWelcomeTab();
        loadUsersTab();
        loadNotificationTab();
        loadPKITab();
        loadExperimentalTab();
        tabs.selectTab(0);
        rootContainer.add(tabs);
        tabs.setWidth("100%");
        Window.setTitle("OGRealmServerAdmin");
    }
    
    private void loadPKITab()
    {
        final VerticalPanel tab = new VerticalPanel();
        tab.setSpacing(5);
        tabs.addTabListener(new TabListener()
        {
            
            public boolean onBeforeTabSelected(
                SourcesTabEvents sender, int tabIndex)
            {
                return true;
            }
            
            public void onTabSelected(
                SourcesTabEvents sender, int tabIndex)
            {
                if (tabs.getWidgetIndex(tab) == tabIndex)
                {
                    refreshPKITab(tab);
                }
            }
        });
        tabs.add(tab, "PKI");
    }
    
    protected void refreshPKITab(final VerticalPanel tab)
    {
        final DialogBox refreshingDialog = new DialogBox();
        refreshingDialog.setText("Loading...");
        refreshingDialog
            .setWidget(new Label(
                "Please wait while OGRSAdmin refreshes the user list..."));
        refreshingDialog.center();
        refreshingDialog.show();
        authLink
            .getPKIGeneralInfo(new AsyncCallback<PKIGeneralInfo>()
            {
                
                public void onFailure(Throwable caught)
                {
                    refreshingDialog.hide();
                    removeAll(tab);
                    tab.add(new Label(
                        "The PKI tab couldn't be loaded. Try "
                            + "refreshing this page."));
                }
                
                public void onSuccess(PKIGeneralInfo result)
                {
                    refreshingDialog.hide();
                    removeAll(tab);
                    tab
                        .add(new HTML(
                            "Your server's certificate fingerprint is "
                                + result
                                    .getCertFingerprint()
                                + ". You'll need to provide this to "
                                + "people if they're trying to use your server and your "
                                + "server's certificate isn't signed by the OpenGroove CA."));
                    if (!result.isCertValidDate())
                    {
                        tab
                            .add(new HTML(
                                "Your server's certificate has expired. "
                                    + "You'll need to generate a new certificate "
                                    + "and, if your previous certificate was signed "
                                    + "by the OpenGroove CA, you'll need to get the"
                                    + " new one signed as well."));
                    }
                    if (!result.isHasSignedCert())
                    {
                        tab
                            .add(new HTML(
                                "<b>Your server's certificate has not been "
                                    + "signed by the OpenGroove CA.</b> This means that "
                                    + "users wanting to connect to your server will always "
                                    + "get a message stating that the server's certificate is "
                                    + "invalid. In addition, users of your realm server will "
                                    + "not be able to communicate with users of other realm "
                                    + "severs. Getting a certificate signed by the OpenGroove "
                                    + "CA is free. You can click the \"Get your certificate "
                                    + "signed\" button below to get started."));
                    }
                    tab
                        .add(new Label(
                            "Your certificate will expire on "
                                + result.getCertExpiresOn()
                                + ". If you don't want to cause problems "
                                + "with users connecting to your server, you should "
                                + "generate a new certificate or get yours re-signed before then."));
                }
            });
    }
    
    private void loadWelcomeTab()
    {
        VerticalPanel tab = new VerticalPanel();
        tab
            .setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        tab.setSpacing(5);
        tab
            .add(new Label(
                "Welcome to the OpenGroove Realm Server Administration "
                    + "interface. Use the tabs above to navigate around."));
        DockPanel wrapper = new DockPanel();
        final DialogBox logoutDialog = new DialogBox(false,
            true);
        logoutDialog.setText("Logging out");
        logoutDialog.setWidget(new Label(
            "Please wait while you are logged out..."));
        Button logoutLink = new Button();
        logoutLink.setText("Log out");
        logoutLink.addClickListener(new ClickListener()
        {
            
            public void onClick(Widget sender)
            {
                logoutDialog.center();
                logoutDialog.show();
                anonLink.logout(new AsyncCallback<Void>()
                {
                    
                    public void onFailure(Throwable caught)
                    {
                        logoutDialog.hide();
                        Window
                            .alert("OGRSAdmin failed to log you out for this reason: "
                                + caught.getMessage());
                    }
                    
                    public void onSuccess(Void result)
                    {
                        Window
                            .setTitle("Logged out - OGRSAdmin");
                        logoutDialog.hide();
                        removeAll(rootContainer);
                        rootContainer
                            .add(new Label(
                                "You have been successfully logged out of "
                                    + "OGRSAdmin. Refresh the page if you want "
                                    + "to log back in."));
                    }
                });
            }
        });
        wrapper.add(logoutLink, wrapper.EAST);
        wrapper.add(tab, wrapper.CENTER);
        tabs.add(wrapper, "Welcome");
    }
    
    private void loadUsersTab()
    {
        final VerticalPanel tab = new VerticalPanel();
        tab.setSpacing(5);
        tab
            .add(new Label(
                "This tab allows you to manage your OpenGroove users. These "
                    + "are the users that can connect to your server using "
                    + "OpenGroove itself. If you're looking for how to change "
                    + "user access settings (IE whether or not a user "
                    + "can create their own account on this server), go "
                    + "to the Settings tab."));
        tab.add(new HTML("<b>Users:</b>"));
        Button createUserButton = new Button("Create user");
        createUserButton
            .addClickListener(new ClickListener()
            {
                
                public void onClick(Widget sender)
                {
                    promptToCreateUser();
                }
            });
        tab.add(createUserButton);
        userListTable = new Grid(0, 0);
        tab.add(userListTable);
        tabs.add(tab, "Users");
        tabs.addTabListener(new TabListener()
        {
            
            public void onTabSelected(
                SourcesTabEvents sender, int tabIndex)
            {
                if (tabs.getWidgetIndex(tab) == tabIndex)
                {
                    refreshUserList();
                }
            }
            
            public boolean onBeforeTabSelected(
                SourcesTabEvents sender, int tabIndex)
            {
                return true;
            }
        });
    }
    
    protected void promptToCreateUser()
    {
        final DialogBox newUserInfoBox = new DialogBox();
        newUserInfoBox.setText("Create a user");
        HTMLPanel panel = new HTMLPanel(
            "<table border='0' cellspacing='0' cellpadding='2'>"
                + "<tr><td><b>Username:</b> &nbsp; </td>"
                + "<td><div id='usernameField'/></td></tr>"
                + "<tr><td><b>Password:</b> &nbsp; </td>"
                + "<td><div id='passwordField'/></td></tr>"
                + "<tr><td><b>Password again:</b> &nbsp; </td>"
                + "<td><div id='passwordAgainField'/></td></tr>"
                + "<tr><td colspan='2' align='right'>"
                + "<div id='buttons'/></td></tr>");
        final TextBox usernameField = new TextBox();
        usernameField.setVisibleLength(20);
        panel.add(usernameField, "usernameField");
        final PasswordTextBox passwordField = new PasswordTextBox();
        passwordField.setVisibleLength(20);
        panel.add(passwordField, "passwordField");
        final PasswordTextBox passwordAgainField = new PasswordTextBox();
        passwordAgainField.setVisibleLength(20);
        panel.add(passwordAgainField, "passwordAgainField");
        DockPanel buttonPanel = new DockPanel();
        Button createButton = new Button("Create");
        Button cancelButton = new Button("Cancel");
        buttonPanel.add(createButton, buttonPanel.WEST);
        buttonPanel.add(cancelButton, buttonPanel.EAST);
        createButton.addClickListener(new ClickListener()
        {
            
            public void onClick(Widget sender)
            {
                String username = usernameField.getText();
                String password = passwordField.getText();
                String passwordAgain = passwordAgainField
                    .getText();
                newUserInfoBox.hide();
                final DialogBox creatingBox = new DialogBox();
                creatingBox.setText("Creating...");
                creatingBox.center();
                creatingBox.show();
                authLink.createUser(username, password,
                    passwordAgain,
                    new AsyncCallback<Void>()
                    {
                        
                        public void onFailure(
                            Throwable caught)
                        {
                            String message;
                            if (caught instanceof UserException)
                            {
                                message = caught
                                    .getMessage();
                            }
                            else
                            {
                                message = "An exception occured "
                                    + "while creating the user: "
                                    + caught.getClass()
                                        .getName()
                                    + " - "
                                    + caught.getMessage();
                            }
                            creatingBox.hide();
                            newUserInfoBox.show();
                            Window.alert(message);
                        }
                        
                        public void onSuccess(Void result)
                        {
                            creatingBox.hide();
                            refreshUserList();
                            showInfoBox(
                                "Successfully created user",
                                "The user has been successfully created. "
                                    + "They can now log in to OpenGroove.");
                        }
                    });
            }
        });
        cancelButton.addClickListener(new ClickListener()
        {
            
            public void onClick(Widget sender)
            {
                newUserInfoBox.hide();
            }
        });
        panel.add(buttonPanel, "buttons");
        newUserInfoBox.setWidget(panel);
        newUserInfoBox.center();
        newUserInfoBox.show();
    }
    
    public static void showInfoBox(String title,
        String message)
    {
        InfoConfig config = new InfoConfig(title, message);
        config.display = 6000;
        config.title = title;
        config.text = message;
        Info.display(config);
    }
    
    private void refreshUserList()
    {
        final DialogBox refreshingDialog = new DialogBox();
        refreshingDialog.setText("Loading...");
        refreshingDialog
            .setWidget(new Label(
                "Please wait while OGRSAdmin refreshes the user list..."));
        refreshingDialog.center();
        refreshingDialog.show();
        authLink.getUsers(new AsyncCallback<GUser[]>()
        {
            
            public void onFailure(Throwable caught)
            {
                refreshingDialog.hide();
                Window
                    .alert("An error occured while refreshing the user list. "
                        + "Switch to another tab and then back to this "
                        + "one to try again.");
            }
            
            public void onSuccess(GUser[] result)
            {
                refreshingDialog.hide();
                userListTable.resize(result.length, 3);
                userListTable.setBorderWidth(0);
                userListTable.setCellSpacing(3);
                for (int i = 0; i < result.length; i++)
                {
                    final GUser user = result[i];
                    userListTable
                        .setWidget(i, 0, new Label(user
                            .getUsername()
                            + "  "));
                    Button notifyButton = new Button(
                        "Send notification");
                    notifyButton
                        .addClickListener(new ClickListener()
                        {
                            
                            public void onClick(
                                Widget sender)
                            {
                                NotificationSender
                                    .promptForSend("user:"
                                        + user
                                            .getUsername(),
                                        "", "", "info", -1);
                            }
                        });
                    userListTable.setWidget(i, 1,
                        notifyButton);
                    Button deleteButton = new Button(
                        "Delete");
                    deleteButton
                        .addClickListener(new ClickListener()
                        {
                            
                            public void onClick(
                                Widget sender)
                            {
                                Window
                                    .alert("We haven't added the ability to delete users yet.");
                            }
                        });
                    userListTable.setWidget(i, 2,
                        deleteButton);
                }
            }
        });
    }
    
    private void loadNotificationTab()
    {
        VerticalPanel tab = new VerticalPanel();
        tab.setSpacing(5);
        tab
            .add(new Label(
                "This tab allows you to send user notifications to "
                    + "OpenGroove users using this server, schedule notifications "
                    + "to be sent in the future, and creat notification templates "
                    + "(notifications that you expect to send often)."));
        Button sendToAll = new Button(
            "Send a notification to all connected users");
        sendToAll.addClickListener(new ClickListener()
        {
            
            public void onClick(Widget sender)
            {
                NotificationSender.promptForSend("all", "",
                    "", "", -1);
            }
        });
        tab.add(sendToAll);
        tabs.add(tab, "Notification");
    }
    
    private void loadStatusTab()
    {
        
    }
    
    private void loadExperimentalTab()
    {
    }
    
    public static void removeAll(HasWidgets container)
    {
        for (Widget widget : container)
        {
            container.remove(widget);
        }
    }
    
}
