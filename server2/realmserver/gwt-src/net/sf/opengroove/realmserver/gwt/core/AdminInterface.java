package net.sf.opengroove.realmserver.gwt.core;

import net.sf.opengroove.realmserver.gwt.core.rcp.AnonLink;
import net.sf.opengroove.realmserver.gwt.core.rcp.AnonLinkAsync;
import net.sf.opengroove.realmserver.gwt.core.rcp.AuthLink;
import net.sf.opengroove.realmserver.gwt.core.rcp.AuthLinkAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

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
    
    public void onModuleLoad()
    {
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
        Window
            .setTitle("Log in - OpenGroove Realm Server Administration");
        final RootPanel root = RootPanel.get();
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
        usernameField = new TextBox();
        usernameField.setVisibleLength(20);
        loginPanel.add(usernameField, "usernameField");
        passwordField = new PasswordTextBox();
        passwordField.setVisibleLength(20);
        loginPanel.add(passwordField, "passwordField");
        loginStatusLabel = new HTML("");
        loginPanel.add(loginStatusLabel, "statusLabel");
        loginButton = new Button("Log in");
        loginButton.addClickListener(new ClickListener()
        {
            
            public void onClick(Widget sender)
            {
                attemptLogin();
            }
        });
        loginPanel.add(loginButton, "loginButton");
        root.add(loginPanel);
    }
    
    protected void attemptLogin()
    {
        loginButton.setEnabled(false);
        usernameField.setEnabled(false);
        passwordField.setEnabled(false);
        loginStatusLabel
            .setHTML("<b>Please wait while OGRSAdmin logs you in...</b>");
    }
    
}
