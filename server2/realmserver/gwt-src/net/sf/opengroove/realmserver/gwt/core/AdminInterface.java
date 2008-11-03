package net.sf.opengroove.realmserver.gwt.core;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AdminInterface implements EntryPoint
{
    
    public void onModuleLoad()
    {
        Window
            .setTitle("Log in - OpenGroove Realm Server Administration");
        final RootPanel root = RootPanel.get();
        final HTMLPanel loginPanel = new HTMLPanel(
            "<table border='0' cellpadding='2'>"
                + "<tr><td colspan='2'>Please enter your username and "
                + "password to log in."
                + "</td></tr>"
                + "<tr><td><b>Username: </b></td><td>"
                + "<div id='usernameField'/></td></tr>"
                + "<tr><td><b>Password: </b></td><td>"
                + "<div id='passwordField'/></td></tr>"
                + "<tr><td>&nbsp;</td><td>"
                + "<div id='loginButton'/></td></tr></table>");
        final TextBox usernameField = new TextBox();
        usernameField.setVisibleLength(20);
        loginPanel.add(usernameField, "usernameField");
        PasswordTextBox passwordField = new PasswordTextBox();
        passwordField.setVisibleLength(20);
        loginPanel.add(passwordField, "passwordField");
        Button loginButton = new Button("Log in");
        loginButton.addClickListener(new ClickListener()
        {
            
            public void onClick(Widget sender)
            {
                root.remove(loginPanel);
                Label loggedInLabel = new Label(
                    "You have successfully logged in as "
                        + usernameField.getText());
                FlowPanel flow = new FlowPanel();
                flow.add(loggedInLabel);
                root.add(flow);
            }
        });
        loginPanel.add(loginButton, "loginButton");
        root.add(loginPanel);
    }
    
}
