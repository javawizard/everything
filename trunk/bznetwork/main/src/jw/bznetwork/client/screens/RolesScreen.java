package jw.bznetwork.client.screens;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.BoxCallback;
import jw.bznetwork.client.VerticalScreen;
import jw.bznetwork.client.data.model.Role;
import jw.bznetwork.client.ui.Header2;

public class RolesScreen extends VerticalScreen
{
    
    public class RenameListener implements ClickListener
    {
        private int roleid;
        private String oldName;
        
        public RenameListener(int roleid, String oldName)
        {
            super();
            this.roleid = roleid;
            this.oldName = oldName;
        }
        
        @Override
        public void onClick(Widget sender)
        {
            String newName = Window.prompt("Type a new name for the role.",
                    oldName);
            if (newName == null)
                return;
            BZNetwork.authLink.renameRole(roleid, newName,
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
    
    public class PermissionsListener implements ClickListener
    {
        private int roleid;
        
        public PermissionsListener(int roleid)
        {
            super();
            this.roleid = roleid;
        }
        
        @Override
        public void onClick(Widget sender)
        {
            Window.alert("Permissions editing is not supported yet.");
        }
        
    }
    
    public class DeleteRoleListener implements ClickListener
    {
        private int roleid;
        
        public DeleteRoleListener(int roleid)
        {
            super();
            this.roleid = roleid;
        }
        
        @Override
        public void onClick(Widget sender)
        {
            if (!Window
                    .confirm("Deleting this role will also delete any users and authgroups "
                            + "that use it. Are you sure you want to continue?"))
                return;
            BZNetwork.authLink.deleteRole(roleid, new BoxCallback<Void>()
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
        return "roles";
    }
    
    @Override
    public String getTitle()
    {
        return "Roles";
    }
    
    @Override
    public void init()
    {
    }
    
    @Override
    public void reselect()
    {
        deselect();
        select();
    }
    
    @Override
    public void select()
    {
        widget.clear();
        final PopupPanel box = BZNetwork.showLoadingBox();
        BZNetwork.authLink.getRoleList(new AsyncCallback<Role[]>()
        {
            
            @Override
            public void onFailure(Throwable caught)
            {
                box.hide();
                BZNetwork.fail(caught);
            }
            
            @Override
            public void onSuccess(Role[] result)
            {
                box.hide();
                select1(result);
            }
        });
    }
    
    @SuppressWarnings("deprecation")
    protected void select1(Role[] result)
    {
        widget.clear();
        widget.add(new Header2("Roles"));
        FlexTable table = new FlexTable();
        for (int i = 0; i < result.length; i++)
        {
            table.setText(i, 0, result[i].getName());
            // Anchor viewUsersLink = new Anchor("View users");
            // table.setWidget(i,1,viewUsersLink);
            Anchor renameLink = new Anchor("rename");
            table.setWidget(i, 1, renameLink);
            Anchor permissionsLink = new Anchor("permissions");
            table.setWidget(i, 2, permissionsLink);
            Anchor deleteLink = new Anchor("delete");
            table.setWidget(i, 3, deleteLink);
            deleteLink.addClickListener(new DeleteRoleListener(result[i]
                    .getRoleid()));
            permissionsLink.addClickListener(new PermissionsListener(result[i]
                    .getRoleid()));
            renameLink.addClickListener(new RenameListener(result[i]
                    .getRoleid(), result[i].getName()));
        }
        final TextBox addNameBox = new TextBox();
        addNameBox.setVisibleLength(15);
        table.setWidget(result.length, 0, addNameBox);
        Button addButton = new Button("Add");
        addButton.addClickListener(new ClickListener()
        {
            
            @Override
            public void onClick(Widget sender)
            {
                if (addNameBox.getText().trim().equals(""))
                {
                    Window.alert("You didn't type the name of the new role.");
                    return;
                }
                final PopupPanel box = BZNetwork.showLoadingBox();
                BZNetwork.authLink.addRole(addNameBox.getText(),
                        new AsyncCallback<Void>()
                        {
                            
                            @Override
                            public void onFailure(Throwable caught)
                            {
                                box.hide();
                                BZNetwork.fail(caught);
                            }
                            
                            @Override
                            public void onSuccess(Void result)
                            {
                                box.hide();
                                select();
                            }
                        });
            }
        });
        table.setWidget(result.length, 1, addButton);
        widget.add(table);
    }
}
