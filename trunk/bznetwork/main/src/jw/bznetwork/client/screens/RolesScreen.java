package jw.bznetwork.client.screens;

import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.BoxCallback;
import jw.bznetwork.client.Perms;
import jw.bznetwork.client.VerticalScreen;
import jw.bznetwork.client.data.EditPermissionsModel;
import jw.bznetwork.client.data.GroupedServer;
import jw.bznetwork.client.data.model.EditablePermission;
import jw.bznetwork.client.data.model.Group;
import jw.bznetwork.client.data.model.Permission;
import jw.bznetwork.client.data.model.Role;
import jw.bznetwork.client.ui.Header2;
import jw.bznetwork.client.ui.Spacer;

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
        private String name;
        
        public PermissionsListener(int roleid, String name)
        {
            super();
            this.roleid = roleid;
            this.name = name;
        }
        
        @Override
        public void onClick(Widget sender)
        {
            /*
             * We'll get the list of permissions from the server. When it's
             * returned, we'll clear the screen, build the permissions table,
             * and add it to the screen.
             */
            showPermissionEditor(roleid, name);
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
    
    public void showPermissionEditor(final int roleid, final String name)
    {
        BZNetwork.authLink.getPermissionsForRole(roleid,
                new BoxCallback<EditPermissionsModel>()
                {
                    
                    @Override
                    public void run(EditPermissionsModel result)
                    {
                        showPermissionEditor1(result, roleid, name);
                    }
                });
    }
    
    @SuppressWarnings("deprecation")
    protected void showPermissionEditor1(EditPermissionsModel model,
            final int roleid, final String roleName)
    {
        EditablePermission[] result = model.getPermissions();
        widget.clear();
        widget.add(new Header2("Permissions for " + roleName));
        FlexTable table = new FlexTable();
        for (int i = 0; i < result.length; i++)
        {
            EditablePermission permission = result[i];
            table.setText(i, 0, permission.getPermission());
            table.setHTML(i, 1, "&nbsp;on&nbsp;");
            table.getFlexCellFormatter().addStyleName(i, 1,
                    "bznetwork-PermsTableOn");
            HorizontalPanel targetPanel = new HorizontalPanel();
            if (permission.getGroupName() == null
                    && permission.getServerName() == null)
            {
                Label l = new Label("Global");
                l.addStyleName("bznetwork-PermsTableGlobal");
                targetPanel.add(l);
            }
            else if (permission.getServerName() == null)
            {
                Label l = new Label(permission.getGroupName());
                targetPanel.add(l);
            }
            else
            {
                Label l1 = new Label(permission.getGroupName());
                targetPanel.add(l1);
                Label l2 = new Label("/");
                l2.addStyleName("bznetwork-PermsTableGroupServerSlash");
                targetPanel.add(l2);
                Label l3 = new Label(permission.getServerName());
                targetPanel.add(l3);
            }
            table.setWidget(i, 2, targetPanel);
            Anchor deleteLink = new Anchor("delete");
            table.setWidget(i, 3, deleteLink);
        }
        final ListBox permissionBox = createPermissionListBox();
        table.setWidget(result.length, 0, permissionBox);
        table.setHTML(result.length, 1, "&nbsp;on&nbsp;");
        table.getFlexCellFormatter().addStyleName(result.length, 1,
                "bznetwork-PermsTableOn");
        final ListBox targetBox = createTargetListBox(model);
        table.setWidget(result.length, 2, targetBox);
        Button addPermissionButton = new Button("Add");
        table.setWidget(result.length, 3, addPermissionButton);
        widget.add(table);
        widget.add(new Spacer("8px", "8px"));
        widget.add(new HTML(
                "Changes to permissions will take effect when the user "
                        + "logs out and then logs back in again. If you "
                        + "need to revoke a permission immediately because "
                        + "a user is abusing it, revoke it here, then go "
                        + "to the <b>Sessions</b> page, find the user in "
                        + "the list, and click <b>Force logout</b>. A list of "
                        + "permissions and what they mean is available "
                        + "<a href='http://code.google.com/p/bzsound/wiki"
                        + "/BZNetworkPermissions#Available_permissions' "
                        + "target='_blank'>here</a>."));
        Anchor backLink = new Anchor("<< Back to the list of roles");
        backLink.addClickListener(new ClickListener()
        {
            
            @Override
            public void onClick(Widget sender)
            {
                select();
            }
        });
        widget.add(new Spacer("8px", "8px"));
        widget.add(backLink);
        addPermissionButton.addClickListener(new ClickListener()
        {
            
            @Override
            public void onClick(Widget sender)
            {
                String permission = permissionBox.getItemText(permissionBox
                        .getSelectedIndex());
                if (permission.equals(""))
                {
                    Window.alert("You need to select a permission to add.");
                    return;
                }
                int targetIndex = targetBox.getSelectedIndex();
                String targetValue = ((SelectElement) targetBox.getElement()
                        .cast()).getOptions().getItem(targetIndex).getValue();
                if (targetValue.equals(""))
                {
                    Window
                            .alert("You need to select a target for this permission.");
                    return;
                }
                int target = Integer.parseInt(targetValue);
                Window.alert("This would add permission " + permission
                        + " to target " + target);
            }
        });
    }
    
    private ListBox createTargetListBox(EditPermissionsModel model)
    {
        ListBox box = new ListBox();
        box.addItem("", "");
        box.addItem("Global", "-1");
        ((SelectElement) box.getElement().cast()).getOptions().getItem(1)
                .setClassName("bznetwork-PermsTableNewGlobal");
        for (Group g : model.getGroups())
        {
            box.addItem(g.getName(), "" + g.getGroupid());
        }
        for (GroupedServer s : model.getServers())
        {
            box.addItem(s.getParent().getName() + "/" + s.getName(), ""
                    + s.getServerid());
        }
        return box;
    }
    
    private ListBox createPermissionListBox()
    {
        ListBox box = new ListBox();
        box.setVisibleItemCount(1);
        box.addItem("");
        for (String s : Perms.getSortedPermissionsList())
        {
            box.addItem(s);
        }
        return box;
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
                    .getRoleid(), result[i].getName()));
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
