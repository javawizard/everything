package net.sf.opengroove.realmserver.gwt.core;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AdminInterface implements EntryPoint
{
    
    public void onModuleLoad()
    {
        Window.setTitle("Log in - OpenGroove Realm Server Administration");
        RootPanel root = RootPanel.get();
        HTMLPanel loginPanel = new HTMLPanel(
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
                Window.setTitle("Logging in as "
                    + usernameField.getText());
            }
        });
        loginPanel.add(loginButton, "loginButton");
        root.add(loginPanel);
    }
    
}
