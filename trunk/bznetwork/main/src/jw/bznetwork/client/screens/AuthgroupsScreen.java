package jw.bznetwork.client.screens;

import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.BoxCallback;
import jw.bznetwork.client.Screen;
import jw.bznetwork.client.VerticalScreen;
import jw.bznetwork.client.data.EditAuthgroupsModel;
import jw.bznetwork.client.data.model.Authgroup;
import jw.bznetwork.client.ui.Header2;

@SuppressWarnings("deprecation")
public class AuthgroupsScreen extends VerticalScreen implements Screen
{
    
    @Override
    public void deselect()
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public String getName()
    {
        return "authgroups";
    }
    
    @Override
    public String getTitle()
    {
        return "Authgroups";
    }
    
    @Override
    public void init()
    {
        // TODO Auto-generated method stub
        
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
                .getEditAuthgroupsModel(new BoxCallback<EditAuthgroupsModel>()
                {
                    
                    @Override
                    public void run(EditAuthgroupsModel result)
                    {
                        select1(result);
                    }
                });
    }
    
    protected void select1(EditAuthgroupsModel result)
    {
        widget.clear();
        widget.add(new Header2("Authgroups"));
        FlexTable table = new FlexTable();
        for (int i = 0; i < result.getAuthgroups().length; i++)
        {
            final Authgroup authgroup = result.getAuthgroups()[i];
            table.setText(i, 0, authgroup.getName());
            table.setText(i, 1, result.getRoleIdsToNames().get(
                    authgroup.getRole()));
            Anchor deleteLink = new Anchor("delete");
            table.setWidget(i, 2, deleteLink);
            deleteLink.addClickListener(new ClickListener()
            {
                
                @Override
                public void onClick(Widget sender)
                {
                    BZNetwork.authLink.deleteAuthgroup(authgroup.getName(),
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
        final TextBox groupNameBox = new TextBox();
        groupNameBox.setTitle("Type the name of a BZFlag group here. For "
                + "example, BZTRAINING.ADMIN or EXCESSIVEBZ.COP.");
        groupNameBox.setVisibleLength(15);
        table.setWidget(result.getAuthgroups().length, 0, groupNameBox);
        final ListBox roleBox = BZNetwork.createRoleBox(result
                .getRoleIdsToNames());
        roleBox
                .setTitle("Select a role to be applied to all members of the group "
                        + "you typed in the box at left.");
        table.setWidget(result.getAuthgroups().length, 1, roleBox);
        Button addGroupButton = new Button("Add");
        addGroupButton
                .setTitle("Click this button once you've chosen a group and a role.");
        table.setWidget(result.getAuthgroups().length, 2, addGroupButton);
        widget.add(table);
        addGroupButton.addClickListener(new ClickListener()
        {
            
            @Override
            public void onClick(Widget sender)
            {
                if (roleBox.getSelectedIndex() == 0)
                {
                    Window
                            .alert("You need to select a role to apply to this group.");
                    return;
                }
                if (groupNameBox.getText().trim().equals(""))
                {
                    Window.alert("You need to type a group name.");
                    return;
                }
                int roleIndex = roleBox.getSelectedIndex();
                String roleIdString = ((SelectElement) roleBox.getElement()
                        .cast()).getOptions().getItem(roleIndex).getValue();
                int roleid = Integer.parseInt(roleIdString);
                BZNetwork.authLink.addAuthgroup(groupNameBox.getText(), roleid,
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
}
