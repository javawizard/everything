package jw.bznetwork.client.screens;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;

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
            table.setText(i, 0, result[i].getName());
            Anchor deleteLink = new Anchor("delete");
            table.setWidget(i, 1, deleteLink);
        }
    }
    
}
