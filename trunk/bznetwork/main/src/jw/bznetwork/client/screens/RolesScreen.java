package jw.bznetwork.client.screens;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;

import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.VerticalScreen;
import jw.bznetwork.client.data.model.Role;
import jw.bznetwork.client.ui.Header2;

public class RolesScreen extends VerticalScreen
{
    
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
                select1(result);
            }
        });
    }
    
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
        }
        TextBox addNameBox = new TextBox();
        addNameBox.setVisibleLength(15);
        table.setWidget(result.length, 0, addNameBox);
        Button addButton = new Button("Add");
        table.setWidget(result.length, 0, addButton);
        widget.add(table);
    }
    
}
