package jw.bznetwork.client.screens;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.BoxCallback;
import jw.bznetwork.client.VerticalScreen;
import jw.bznetwork.client.data.model.EmailGroup;
import jw.bznetwork.client.ui.Header2;

public class EmailScreen extends VerticalScreen
{
    
    public class DeleteClickHandler implements ClickHandler
    {
        private EmailGroup group;
        
        public DeleteClickHandler(EmailGroup group)
        {
            super();
            this.group = group;
        }
        
        @Override
        public void onClick(ClickEvent event)
        {
            if (!Window
                    .confirm("Are you sure you want to delete this email group? This "
                            + "will also delete any triggers that use this email "
                            + "group as their recipient."))
                return;
            BZNetwork.authLink.deleteEmailGroup(group.getEmailgroupid(),
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
    
    public class RenameClickHandler implements ClickHandler
    {
        private EmailGroup group;
        
        public RenameClickHandler(EmailGroup group)
        {
            super();
            this.group = group;
        }
        
        @Override
        public void onClick(ClickEvent event)
        {
            // TODO Auto-generated method stub
            
        }
        
    }
    
    public class EditClickHandler implements ClickHandler
    {
        private EmailGroup group;
        
        public EditClickHandler(EmailGroup group)
        {
            super();
            this.group = group;
        }
        
        @Override
        public void onClick(ClickEvent event)
        {
            // TODO Auto-generated method stub
            
        }
        
    }
    
    @Override
    public void deselect()
    {
    }
    
    @Override
    public String getName()
    {
        return "email";
    }
    
    @Override
    public String getTitle()
    {
        return "Email";
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
        BZNetwork.authLink.listEmailGroups(new BoxCallback<EmailGroup[]>()
        {
            
            @Override
            public void run(EmailGroup[] result)
            {
                select1(result);
            }
        });
    }
    
    protected void select1(EmailGroup[] result)
    {
        widget.clear();
        widget.add(new Header2("Email"));
        FlexTable table = new FlexTable();
        FlexCellFormatter format = table.getFlexCellFormatter();
        widget.add(table);
        int row = 0;
        for (EmailGroup group : result)
        {
            table.setText(row, 0, group.getName());
            table.setText(row, 1, group.getAddresses());
            Anchor renameLink = new Anchor("rename");
            Anchor editLink = new Anchor("edit");
            Anchor deleteLink = new Anchor("delete");
            table.setWidget(row, 2, renameLink);
            table.setWidget(row, 3, editLink);
            table.setWidget(row, 4, deleteLink);
            renameLink.addClickHandler(new RenameClickHandler(group));
            editLink.addClickHandler(new EditClickHandler(group));
            deleteLink.addClickHandler(new DeleteClickHandler(group));
            row += 1;
        }
        
    }
    
}
