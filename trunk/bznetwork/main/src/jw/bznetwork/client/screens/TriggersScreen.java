package jw.bznetwork.client.screens;

import java.util.Map.Entry;

import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.BoxCallback;
import jw.bznetwork.client.VerticalScreen;
import jw.bznetwork.client.data.EditPermissionsModel;
import jw.bznetwork.client.data.EditTriggersModel;
import jw.bznetwork.client.data.GroupedServer;
import jw.bznetwork.client.data.model.Banfile;
import jw.bznetwork.client.data.model.Group;
import jw.bznetwork.client.data.model.Trigger;
import jw.bznetwork.client.ui.Header2;
import jw.bznetwork.client.ui.Header3;

public class TriggersScreen extends VerticalScreen
{
    
    public class DeleteTriggerClickListener implements ClickListener
    {
        private Trigger trigger;
        
        @Override
        public void onClick(Widget sender)
        {
            if (!Window
                    .confirm("Are you sure you want to delete this trigger?"))
                return;
            BZNetwork.authLink.deleteTrigger(trigger.getTriggerid(),
                    new BoxCallback<Void>()
                    {
                        
                        @Override
                        public void run(Void result)
                        {
                            select();
                        }
                    });
        }
        
        public DeleteTriggerClickListener(Trigger trigger)
        {
            super();
            this.trigger = trigger;
        }
        
    }
    
    public class EditTriggerClickListener implements ClickListener
    {
        private Trigger trigger;
        private EditTriggersModel model;
        
        public EditTriggerClickListener(Trigger trigger, EditTriggersModel model)
        {
            super();
            this.trigger = trigger;
            this.model = model;
        }
        
        @Override
        public void onClick(Widget sender)
        {
            showEditTriggerBox(trigger, model);
        }
        
    }
    
    @Override
    public void deselect()
    {
    }
    
    @Override
    public String getName()
    {
        return "triggers";
    }
    
    @Override
    public String getTitle()
    {
        return "Triggers";
    }
    
    @Override
    public void init()
    {
    }
    
    @Override
    public void reselect()
    {
    }
    
    @Override
    public void select()
    {
        BZNetwork.authLink
                .getEditTriggersModel(new BoxCallback<EditTriggersModel>()
                {
                    
                    @Override
                    public void run(EditTriggersModel result)
                    {
                        select1(result);
                    }
                });
    }
    
    @SuppressWarnings("deprecation")
    protected void select1(EditTriggersModel result)
    {
        widget.clear();
        widget.setWidth("100%");
        widget.add(new Header2("Triggers"));
        FlexTable table = new FlexTable();
        table.setWidth("100%");
        FlexCellFormatter format = table.getFlexCellFormatter();
        widget.add(table);
        int row = 0;
        table.setHTML(row, 0, "<b>Event</b>");
        table.setHTML(row, 1, "<b>Target</b>");
        table.setHTML(row, 2, "<b>Recipient</b>");
        table.setHTML(row, 3, "<b>Subject</b>");
        table.setHTML(row, 4, "<b>Message</b>");
        row += 1;
        for (Trigger trigger : result.getTriggers())
        {
            table.setText(row, 0, trigger.getEvent());
            String targetName = result.getTargets().get(trigger.getTarget());
            if (targetName == null)
                targetName = "(unknown)";
            table.setText(row, 1, targetName);
            String recipientName = result.getRecipients().get(
                    trigger.getRecipient());
            if (recipientName == null)
                recipientName = "(unknown)";
            table.setText(row, 2, recipientName);
            table.setText(row, 3, trigger.getSubject());
            String shortMessage = trigger.getMessage();
            if (shortMessage.length() > 40)
                shortMessage = shortMessage.substring(0, 50) + "...";
            table.setText(row, 4, shortMessage);
            Anchor editLink = new Anchor("edit");
            Anchor deleteLink = new Anchor("delete");
            table.setWidget(row, 5, editLink);
            table.setWidget(row, 6, deleteLink);
            editLink.addClickListener(new EditTriggerClickListener(trigger,
                    result));
            deleteLink
                    .addClickListener(new DeleteTriggerClickListener(trigger));
            row += 1;
        }
        Button addButton = new Button("Add");
        widget.add(addButton);
        Trigger blankTrigger = new Trigger();
        blankTrigger.setEvent("");
        blankTrigger.setMessage("");
        blankTrigger.setRecipient(-1);
        blankTrigger.setSendtype("");
        blankTrigger.setSubject("");
        blankTrigger.setTarget(-1);
        blankTrigger.setTriggerid(-1);
        addButton.addClickListener(new EditTriggerClickListener(blankTrigger,
                result));
    }
    
    public void showEditTriggerBox(final Trigger trigger,
            EditTriggersModel model)
    {
        VerticalPanel panel = new VerticalPanel();
        final PopupPanel box = new PopupPanel(false, true);
        box.setWidget(panel);
        boolean isAdding = trigger.getTriggerid() == -1;
        panel.add(new Header3(isAdding ? "Add a new trigger"
                : "Editing a trigger"));
        FlexTable table = new FlexTable();
        panel.add(table);
        FlexCellFormatter format = table.getFlexCellFormatter();
        // event,target,recipient,subject,message
        final TextBox eventField = new TextBox();
        final ListBox targetField = createTargetListBox(trigger.getTarget(),
                model);
        final ListBox recipientField = createRecipientListBox(trigger
                .getRecipient(), model);
        final TextBox subjectField = new TextBox();
        final TextArea messageField = new TextArea();
        messageField.setVisibleLines(5);
        messageField.setCharacterWidth(50);
        eventField.setText(trigger.getEvent());
        subjectField.setText(trigger.getSubject());
        messageField.setText(trigger.getMessage());
        table.setText(0, 0, "Event");
        table.setText(1, 0, "Target");
        table.setText(2, 0, "Recipient");
        table.setText(3, 0, "Subject");
        table.setText(4, 0, "Message");
        format.setVerticalAlignment(4, 0, VerticalPanel.ALIGN_TOP);
        table.setWidget(0, 1, eventField);
        table.setWidget(1, 1, targetField);
        table.setWidget(2, 1, recipientField);
        table.setWidget(3, 1, subjectField);
        table.setWidget(4, 1, messageField);
        HorizontalPanel buttonPanel = new HorizontalPanel();
        Button saveButton = new Button(isAdding ? "Create" : "Save");
        Button cancelButton = new Button("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        table.setWidget(5, 1, buttonPanel);
        box.center();
        saveButton.addClickHandler(new ClickHandler()
        {
            
            @Override
            public void onClick(ClickEvent event)
            {
                String recipientString = BZNetwork.getSelectionValue(
                        recipientField, recipientField.getSelectedIndex());
                String recipientLabel = recipientField
                        .getItemText(recipientField.getSelectedIndex());
                String targetString = BZNetwork.getSelectionValue(targetField,
                        targetField.getSelectedIndex());
                if (recipientString.equals(""))
                {
                    Window.alert("You need to select a recipient.");
                    return;
                }
                trigger.setEvent(eventField.getText());
                trigger.setMessage(messageField.getText());
                trigger.setRecipient(Integer.parseInt(recipientString));
                trigger
                        .setSendtype(getSendTypeForRecipientLabel(recipientLabel));
                trigger.setSubject(subjectField.getText());
                trigger.setTarget(Integer.parseInt(targetString));
                BZNetwork.authLink.updateTrigger(trigger,
                        new BoxCallback<Void>()
                        {
                            
                            @Override
                            public void run(Void result)
                            {
                                box.hide();
                                select();
                            }
                        });
            }
        });
        cancelButton.addClickHandler(new ClickHandler()
        {
            
            @Override
            public void onClick(ClickEvent event)
            {
                box.hide(); 
            }
        });
        
    }
    
    protected String getSendTypeForRecipientLabel(String recipientLabel)
    {
        if (recipientLabel.startsWith("irc"))
            return "ircbot";
        else if (recipientLabel.startsWith("email"))
            return "emailgroup";
        else
            throw new RuntimeException("Invalid label: " + recipientLabel);
    }
    
    public static ListBox createRecipientListBox(int recipient,
            EditTriggersModel model)
    {
        ListBox box = new ListBox();
        if (model.getRecipients().size() == 0)
        {
            box.addItem("(no recipients)", "");
        }
        else
        {
            for (Entry<Integer, String> entry : model.getRecipients()
                    .entrySet())
            {
                box.addItem(entry.getValue(), "" + entry.getKey());
                if (recipient == entry.getKey())
                    box.setSelectedIndex(box.getItemCount() - 1);
            }
        }
        return box;
        
    }
    
    private ListBox createTargetListBox(int target, EditTriggersModel model)
    {
        ListBox box = new ListBox();
        // The server automatically adds a global item, so this is commented
        // out for now.
        // box.addItem("Global", "-1");
        for (Entry<Integer, String> entry : model.getTargets().entrySet())
        {
            box.addItem(entry.getValue(), "" + entry.getKey());
            if (target == entry.getKey())
                box.setSelectedIndex(box.getItemCount() - 1);
        }
        return box;
    }
}
