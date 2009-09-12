package jw.bznetwork.client.screens;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;

import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.BoxCallback;
import jw.bznetwork.client.VerticalScreen;
import jw.bznetwork.client.data.model.Banfile;
import jw.bznetwork.client.ui.Header2;

public class BanfilesScreen extends VerticalScreen
{
    
    @Override
    public void deselect()
    {
    }
    
    @Override
    public String getName()
    {
        return "banfiles";
    }
    
    @Override
    public String getTitle()
    {
        return "Banfiles";
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
        BZNetwork.authLink.listBanfiles(new BoxCallback<Banfile[]>()
        {
            
            @Override
            public void run(Banfile[] result)
            {
                select1(result);
            }
        });
    }
    
    protected void select1(Banfile[] result)
    {
        widget.clear();
        FlexTable table = new FlexTable();
        widget.add(new Header2("Banfiles"));
        widget.add(table);
        for (int i = 0; i < result.length; i++)
        {
            final Banfile banfile = result[i];
            table.setText(i, 0, result[i].getName());
            BZNetwork
                    .setCellTitle(table, i, 0, "Id: " + banfile.getBanfileid());
            Anchor deleteLink = new Anchor("delete");
            deleteLink
                    .setTitle("Deletes this banfile. All of the bans in this banfile will be "
                            + "discarded, and any servers that are using this banfile will be set "
                            + "not to use a banfile. All servers that currently use this banfile "
                            + "must be shut down before you can delete it.");
            deleteLink.addClickHandler(new ClickHandler()
            {
                
                @Override
                public void onClick(ClickEvent event)
                {
                    BZNetwork.authLink.deleteBanfile(banfile.getBanfileid(),
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
            table.setWidget(i, 1, deleteLink);
        }
        final TextBox nameField = new TextBox();
        nameField.setTitle("Type a name for the new banfile here.");
        nameField.setVisibleLength(15);
        table.setWidget(result.length, 0, nameField);
        Button addButton = new Button("Add");
        addButton
                .setTitle("Once you've typed a name for the new banfile, click this "
                        + "button to add it.");
        table.setWidget(result.length, 1, addButton);
        addButton.addClickHandler(new ClickHandler()
        {
            
            @Override
            public void onClick(ClickEvent event)
            {
                if (nameField.getText().trim().equals(""))
                {
                    Window
                            .alert("You need to type a name for the new banfile.");
                    return;
                }
                BZNetwork.authLink.addBanfile(nameField.getText(),
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
