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
import jw.bznetwork.client.data.EditCallsignsModel;
import jw.bznetwork.client.data.model.Authgroup;
import jw.bznetwork.client.data.model.Callsign;
import jw.bznetwork.client.ui.Header2;

@SuppressWarnings("deprecation")
public class CallsignsScreen extends VerticalScreen implements Screen
{
    
    @Override
    public void deselect()
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public String getName()
    {
        return "callsigns";
    }
    
    @Override
    public String getTitle()
    {
        return "Callsigns";
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
                .getEditCallsignsModel(new BoxCallback<EditCallsignsModel>()
                {
                    
                    @Override
                    public void run(EditCallsignsModel result)
                    {
                        select1(result);
                    }
                });
    }
    
    protected void select1(EditCallsignsModel result)
    {
        widget.clear();
        widget.add(new Header2("Callsigns"));
        FlexTable table = new FlexTable();
        for (int i = 0; i < result.getCallsigns().length; i++)
        {
            final Callsign callsign = result.getCallsigns()[i];
            table.setText(i, 0, callsign.getCallsign());
            table.setText(i, 1, result.getRoleIdsToNames().get(
                    callsign.getRole()));
            Anchor deleteLink = new Anchor("delete");
            table.setWidget(i, 2, deleteLink);
            deleteLink.addClickListener(new ClickListener()
            {
                
                @Override
                public void onClick(Widget sender)
                {
                    BZNetwork.authLink.deleteCallsign(callsign.getCallsign(),
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
        final TextBox callsignBox = new TextBox();
        callsignBox.setTitle("Type a BZFlag callsign here.");
        callsignBox.setVisibleLength(15);
        table.setWidget(result.getCallsigns().length, 0, callsignBox);
        final ListBox roleBox = BZNetwork.createRoleBox(result
                .getRoleIdsToNames());
        table.setWidget(result.getCallsigns().length, 1, roleBox);
        Button addCallsignButton = new Button("Add");
        table.setWidget(result.getCallsigns().length, 2, addCallsignButton);
        widget.add(table);
        addCallsignButton.addClickListener(new ClickListener()
        {
            
            @Override
            public void onClick(Widget sender)
            {
                if (roleBox.getSelectedIndex() == 0)
                {
                    Window
                            .alert("You need to select a role to apply to this callsign.");
                    return;
                }
                if (callsignBox.getText().trim().equals(""))
                {
                    Window.alert("You need to type a callsign.");
                    return;
                }
                int roleIndex = roleBox.getSelectedIndex();
                String roleIdString = ((SelectElement) roleBox.getElement()
                        .cast()).getOptions().getItem(roleIndex).getValue();
                int roleid = Integer.parseInt(roleIdString);
                BZNetwork.authLink.addCallsign(callsignBox.getText(), roleid,
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
