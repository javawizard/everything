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
import jw.bznetwork.client.data.model.Banfile;
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
            final EditablePermission permission = result[i];
            table.setText(i, 0, permission.getPermission());
            table.setHTML(i, 1, "&nbsp;on&nbsp;");
            table.getFlexCellFormatter().addStyleName(i, 1,
                    "bznetwork-PermsTableOn");
            HorizontalPanel targetPanel = new HorizontalPanel();
            if (permission.getGroupName() == null
                    && permission.getServerName() == null
                    && permission.getBanfileName() == null)
            {
                Label l = new Label("Global");
                l.addStyleName("bznetwork-PermsTableGlobal");
                l.setTitle("Id: -1");
                targetPanel.add(l);
            }
            else if (permission.getBanfileName() != null)
            {
                Label l = new Label("banfile: " + permission.getBanfileName());
                l.setTitle("Id: " + permission.getTarget());
                targetPanel.add(l);
            }
            else if (permission.getServerName() == null)
            {
                Label l = new Label("group: " + permission.getGroupName());
                l.setTitle("Id: " + permission.getTarget());
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
                targetPanel.setTitle("Id: " + permission.getTarget());
            }
            table.setWidget(i, 2, targetPanel);
            Anchor deleteLink = new Anchor("delete");
            deleteLink
                    .setTitle("Use this to delete this permission. This will take "
                            + "effect when users that use this role log out "
                            + "and then log back in.");
            table.setWidget(i, 3, deleteLink);
            deleteLink.addClickListener(new ClickListener()
            {
                
                @Override
                public void onClick(Widget sender)
                {
                    BZNetwork.authLink.deletePermission(roleid, permission
                            .getPermission(), permission.getTarget(),
                            new BoxCallback<Void>()
                            {
                                
                                @Override
                                public void run(Void result)
                                {
                                    showPermissionEditor(roleid, roleName);
                                }
                            });
                }
            });
        }
        final ListBox permissionBox = createPermissionListBox();
        permissionBox.setTitle("Select a permission to add here.");
        table.setWidget(result.length, 0, permissionBox);
        table.setHTML(result.length, 1, "&nbsp;on&nbsp;");
        table.getFlexCellFormatter().addStyleName(result.length, 1,
                "bznetwork-PermsTableOn");
        final ListBox targetBox = createTargetListBox(model);
        targetBox
                .setTitle("Select what target the permission should be applied to here.");
        table.setWidget(result.length, 2, targetBox);
        Button addPermissionButton = new Button("Add");
        addPermissionButton
                .setTitle("Once you've selected a permission and a target, click "
                        + "this button. If the permission you selected can't be applied to "
                        + "the specified target, an error message will be shown.");
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
                String[] targetSplit = targetValue.split("\\/");
                int target = Integer.parseInt(targetSplit[1]);
                String level = targetSplit[0];
                if (!Perms.isPermissionLevelValid(permission, level))
                {
                    Window
                            .alert("You can't apply that permission to a target of that level.");
                    return;
                }
                BZNetwork.authLink.addPermission(roleid, permission, target,
                        new BoxCallback<Void>()
                        {
                            
                            @Override
                            public void run(Void result)
                            {
                                showPermissionEditor(roleid, roleName);
                            }
                        });
            }
        });
    }
    
    private ListBox createTargetListBox(EditPermissionsModel model)
    {
        ListBox box = new ListBox();
        box.addItem("", "");
        box.addItem("Global", "global/-1");
        ((SelectElement) box.getElement().cast()).getOptions().getItem(1)
                .setClassName("bznetwork-PermsTableNewGlobal");
        for (Group g : model.getGroups())
        {
            box.addItem("group: " + g.getName(), "group/" + g.getGroupid());
        }
        for (GroupedServer s : model.getServers())
        {
            box.addItem("server: " + s.getParent().getName() + "/"
                    + s.getName(), "server/" + s.getServerid());
        }
        for (Banfile b : model.getBanfiles())
        {
            box.addItem("banfile: " + b.getName(), "banfile/"
                    + b.getBanfileid());
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
        select();
    }
    
    @Override
    public void select()
    {
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
            BZNetwork.setCellTitle(table, i, 0, "Id: " + result[i].getRoleid());
            // Anchor viewUsersLink = new Anchor("View users");
            // table.setWidget(i,1,viewUsersLink);
            Anchor renameLink = new Anchor("rename");
            renameLink
                    .setTitle("Use this link to rename the role. This will work correctly "
                            + "even if the role is already in use, since roles are referenced "
                            + "internally by their unique id.");
            table.setWidget(i, 1, renameLink);
            Anchor permissionsLink = new Anchor("permissions");
            permissionsLink
                    .setTitle("This link allows you to edit the permissions for "
                            + "this role.");
            table.setWidget(i, 2, permissionsLink);
            Anchor deleteLink = new Anchor("delete");
            deleteLink
                    .setTitle("You can use this to delete the role. Anything that depends "
                            + "on the role, such as callsigns and authgroups that use it, will "
                            + "also be deleted, so you should make sure that you're not "
                            + "going to inadvertently delete something you didn't want "
                            + "to before you use this link.");
            table.setWidget(i, 3, deleteLink);
            deleteLink.addClickListener(new DeleteRoleListener(result[i]
                    .getRoleid()));
            permissionsLink.addClickListener(new PermissionsListener(result[i]
                    .getRoleid(), result[i].getName()));
            renameLink.addClickListener(new RenameListener(result[i]
                    .getRoleid(), result[i].getName()));
        }
        final TextBox addNameBox = new TextBox();
        addNameBox
                .setTitle("Type the name you'd like for your new role, then click Add.");
        addNameBox.setVisibleLength(15);
        table.setWidget(result.length, 0, addNameBox);
        Button addButton = new Button("Add");
        addButton
                .setTitle("Once you've typed a name for the new role, click this button "
                        + "to add it.");
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
                /*
                 * This is another easter egg, based on the fact that I
                 * (javawizard2539/jcp) hate anything whole-wheat because in my
                 * opinion we have whole-wheat food way too much around our
                 * house. I'm not saying that home-made whole-wheat bread (which
                 * my mom makes weekly) is a bad thing, I just think that having
                 * it for breakfast every day gets to be a bit much.
                 */
                if (addNameBox.getText().trim().equalsIgnoreCase("whole wheat")
                        || addNameBox.getText().trim().equalsIgnoreCase(
                                "whole-wheat"))
                {
                    Window
                            .alert("Seriously, what? Whole-wheat rolls are just "
                                    + "plain weird. White rolls are sooooo much better.");
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
