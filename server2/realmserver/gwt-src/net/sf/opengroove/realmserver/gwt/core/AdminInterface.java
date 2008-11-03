package net.sf.opengroove.realmserver.gwt.core;

import net.sf.opengroove.realmserver.gwt.core.rcp.AnonLink;
import net.sf.opengroove.realmserver.gwt.core.rcp.AnonLinkAsync;
import net.sf.opengroove.realmserver.gwt.core.rcp.AuthException;
import net.sf.opengroove.realmserver.gwt.core.rcp.AuthLink;
import net.sf.opengroove.realmserver.gwt.core.rcp.AuthLinkAsync;

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
import com.google.gwt.user.client.ui.FlowPanel;
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
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.DockPanel.DockLayoutConstant;

public class AdminInterface implements EntryPoint
{
    private AnonLinkAsync anonLink;
    
    private AuthLinkAsync authLink;
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
            "<table width='100%' border='0' cellspacing='0' cellpadding='12'>"
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
        Window.setTitle("OGRSAdmin");
        removeAll(rootContainer);
        tabs = new TabPanel();
        tabs.setAnimationEnabled(false);
        loadWelcomeTab();
        tabs.selectTab(0);
        rootContainer.add(tabs);
        tabs.setWidth("100%");
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
        Button sendToAll = new Button("Send a notification to all connected users");
        sendToAll.addClickListener();
        tabs.add(tab, "Notification");
    }
    
    private void loadStatusTab()
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
