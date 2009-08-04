package jw.bznetwork.client.screens;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.BoxCallback;
import jw.bznetwork.client.VerticalScreen;
import jw.bznetwork.client.data.AuthUser;
import jw.bznetwork.client.data.UserSession;

@SuppressWarnings("deprecation")
public class SessionsScreen extends VerticalScreen
{
    
    public class ForceLogoutListener implements ClickListener
    {
        private String id;
        
        public ForceLogoutListener(String id)
        {
            super();
            this.id = id;
        }
        
        @Override
        public void onClick(Widget sender)
        {
            BZNetwork.authLink.invalidateUserSession(id,
                    new BoxCallback<Void>()
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
        return "sessions";
    }
    
    @Override
    public String getTitle()
    {
        return "Sessions";
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
        BZNetwork.authLink.getUserSessions(new BoxCallback<UserSession[]>()
        {
            
            @Override
            public void run(UserSession[] result)
            {
                select1(result);
            }
        });
    }
    
    protected void select1(UserSession[] result)
    {
        Window.alert("session count: " + result.length);
        widget.clear();
        FlexTable table = new FlexTable();
        table.setHTML(0, 0, "<b>IP Address</b>");
        table.setHTML(0, 1, "<b>Auth Provider</b>");
        table.setHTML(0, 2, "<b>Username</b>");
        table.setHTML(0, 3, "<b>Role(s)</b>");
        for (int i = 0; i < result.length; i++)
        {
            UserSession us = result[i];
            int row = i + 1;
            table.setText(row, 0, us.getIp());
            if (us.getUser() != null)
            {
                AuthUser user = us.getUser();
                table.setText(row, 1, user.getProvider());
                table.setText(row, 2, user.getUsername());
                StringBuffer rolesBuffer = new StringBuffer();
                for (String role : user.getRoleNames())
                {
                    if (rolesBuffer.length() > 0)
                        rolesBuffer.append(", ");
                    rolesBuffer.append(role);
                }
                table.setText(row, 3, rolesBuffer.toString());
                Button forceLogoutButton = new Button("Force logout");
                forceLogoutButton.addClickListener(new ForceLogoutListener(us
                        .getId()));
                table.setWidget(row, 4, forceLogoutButton);
            }
        }
        widget.add(table);
    }
    
}
