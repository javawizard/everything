package jw.bznetwork.client.screens;

import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.BoxCallback;
import jw.bznetwork.client.Screen;
import jw.bznetwork.client.SettingType;
import jw.bznetwork.client.Settings;
import jw.bznetwork.client.VerticalScreen;
import jw.bznetwork.client.data.EditConfigurationModel;
import jw.bznetwork.client.data.model.Configuration;
import jw.bznetwork.client.rt.RichTextToolbar;
import jw.bznetwork.client.ui.Header2;
import jw.bznetwork.client.ui.LeftToRightGroup;
import jw.bznetwork.client.ui.Spacer;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

@SuppressWarnings("deprecation")
public class ConfigurationScreen extends VerticalScreen
{
    
    @Override
    public void deselect()
    {
    }
    
    @Override
    public String getName()
    {
        return "configuration";
    }
    
    @Override
    public String getTitle()
    {
        return "Configuration";
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
                .getEditConfigurationModel(new BoxCallback<EditConfigurationModel>()
                {
                    
                    @Override
                    public void run(EditConfigurationModel result)
                    {
                        select1(result);
                    }
                });
    }
    
    /*
     * Code to add a rich text editor:
     * 
     * Grid panel = new Grid(2, 1);
     * panel.setStylePrimaryName("epc-StaticTextBuilder-editorgrid");
     * RichTextToolbar toolbar = new RichTextToolbar(textArea, true);
     * panel.setWidget(0, 0, toolbar); panel.setWidget(1, 0, textArea);
     * textArea.setWidth("100%");
     */
    // textareas are 65x7
    protected void select1(final EditConfigurationModel result)
    {
        String ecEnabledInfoString = "You can disable changes to some of the fields by clicking"
                + " <b>Disable Changes</b>. To re-enable changes, you "
                + "will have to delete the file <tt>"
                + result.getEcDisableFile() + "</tt> on your server.";
        String ecDisabledInfoString = "Changes to some of the fields are disabled. To re-enable "
                + "them, manually delete the file <tt>"
                + result.getEcDisableFile() + "</tt> on the server.";
        final Configuration config = result.getConfiguration();
        widget.clear();
        widget.add(new Header2("Configuration"));
        FlexTable table = new FlexTable();
        FlexCellFormatter format = table.getFlexCellFormatter();
        final Object[] fields = new Object[Settings.values().length];
        final Button disableEcButton = new Button("Disable Changes");
        boolean usedExecGroup = false;
        boolean isMidExec = false;
        int startExecRow = -1;
        int execRowCount = 0;
        int row = 0;
        for (Settings setting : Settings.values())
        {
            if (setting.getType() == SettingType.sensitive && usedExecGroup)
                throw new RuntimeException(
                        "All sensitive settings must be next to each other, but "
                                + setting.name() + " isn't.");
            if (isMidExec && setting.getType() != SettingType.sensitive)
            {
                isMidExec = false;
                usedExecGroup = true;
                format.setRowSpan(startExecRow, 2, execRowCount);
            }
            table.setWidget(row, 0, new Label(setting.getLabel()));
            format.setVerticalAlignment(row, 0, HorizontalPanel.ALIGN_TOP);
            BZNetwork.setCellTitle(table, row, 0, setting.getDesc());
            if (setting.getType() == SettingType.sensitive)
            {
                boolean isFirst = !isMidExec;
                isMidExec = true;
                TextBox field = new TextBox();
                field.setEnabled(!result.isEcDisabled());
                table.setWidget(row, 1, field);
                format.setWidth(row, 1, "1px");
                fields[row] = field;
                field.setText(config.getString(setting));
                /*
                 * Now we need to add either the button or the disabled label.
                 */
                if (isFirst)
                {
                    startExecRow = row;
                    execRowCount = 1;
                    FlexTable disablePanel = new FlexTable();
                    FlexCellFormatter disableFormat = disablePanel
                            .getFlexCellFormatter();
                    disablePanel.setHeight("100%");
                    table.setWidget(row, 2, disablePanel);
                    format.setHeight(row, 2, "100%");
                    disablePanel.setWidget(0, 0, new LeftToRightGroup("100%"));
                    disableFormat.setHeight(0, 0, "100%");
                    if (result.isEcDisabled())
                    {
                        disablePanel.setWidget(0, 1, new Label(
                                "Changes to these fields are disabled"));
                    }
                    else
                    {
                        disablePanel.setWidget(0, 1, disableEcButton);
                    }
                }
                else
                {
                    execRowCount += 1;
                }
            }
            else if (setting.getType() == SettingType.area)
            {
                TextArea field = new TextArea();
                field.setVisibleLines(7);
                field.setCharacterWidth(65);
                table.setWidget(row, 1, field);
                format.setColSpan(row, 1, 2);
                fields[row] = field;
                field.setText(config.getString(setting));
            }
            else if (setting.getType() == SettingType.checkbox)
            {
                SimpleCheckBox field = new SimpleCheckBox();
                table.setWidget(row, 1, field);
                fields[row] = field;
                field.setChecked(config.getBoolean(setting));
            }
            else if (setting.getType() == SettingType.text)
            {
                TextBox field = new TextBox();
                table.setWidget(row, 1, field);
                fields[row] = field;
                field.setText(config.getString(setting));
            }
            BZNetwork.setCellTitle(table, row, 1, setting.getDesc());
            row += 1;
        }
        Button saveButton = new Button("Save");
        final Button saveAndReload = new Button("Save and Reload");
        saveButton
                .setTitle("Once you've made your changes, click this button to save them. "
                        + "Note that disabling changes to the executable field gets saved "
                        + "immediately, so you don't need to click this button if all "
                        + "you did was disable changes to the executable field.");
        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(saveAndReload);
        table.setWidget(row, 1, buttonPanel);
        format.setColSpan(row++, 1, 2);
        saveAndReload
                .setTitle("Same as the Save button, but reloads your changes into your "
                        + "local session. If you don't use this, you will have to log out and then "
                        + "log back in for all of the settings to take effect on your session.");
        disableEcButton
                .setTitle("Since allowing the executable to be edited from the web "
                        + "poses a security risk, you can click this button to disable changes. "
                        + "See the message below the configuration settings for more information "
                        + "on what this button does.");
        widget.add(table);
        widget.add(new Spacer("8px", "8px"));
        final HTML ecInfoLabel = new HTML();
        ecInfoLabel.setHTML(result.isEcDisabled() ? ecDisabledInfoString
                : ecEnabledInfoString);
        widget.add(ecInfoLabel);
        widget.add(new Spacer("8px", "8px"));
        widget.add(new HTML("Most of these settings won't take effect until "
                + "you log out and then log back in."));
        ClickListener saveListener = new ClickListener()
        {
            
            @Override
            public void onClick(final Widget sender)
            {
                TextBox executableBox = (TextBox) fields[Settings.executable
                        .ordinal()];
                if (executableBox.getText().trim().equals("")
                        && !result.isEcDisabled())
                {
                    Window.alert("The site has to have an executable. Use "
                            + "\"bzfs\" if you're unsure what to put here.");
                    return;
                }
                for (Settings setting : Settings.values())
                {
                    Object field = fields[setting.ordinal()];
                    if (field instanceof SimpleCheckBox)
                    {
                        config.setBoolean(setting, ((SimpleCheckBox) field)
                                .isChecked());
                    }
                    else if (field instanceof TextBoxBase)
                    {
                        config.setString(setting, ((TextBoxBase) field)
                                .getText());
                    }
                    else
                    {
                        throw new RuntimeException("Invalid field type for "
                                + setting.name() + ": "
                                + field.getClass().getName());
                    }
                }
                BZNetwork.authLink.updateConfiguration(config,
                        new BoxCallback<Void>()
                        {
                            
                            @Override
                            public void run(Void result)
                            {
                                if (sender == saveAndReload)
                                    Window.Location.reload();
                                else
                                    select();
                            }
                        });
            }
        };
        saveButton.addClickListener(saveListener);
        saveAndReload.addClickListener(saveListener);
        disableEcButton.setVisible(!result.isEcDisabled());
        disableEcButton.addClickListener(new ClickListener()
        {
            
            @Override
            public void onClick(Widget sender)
            {
                if (Window
                        .confirm("Are you sure you want to disable changes to these "
                                + "fields? You should save your changes to all fields "
                                + "before you do this."))
                {
                    BZNetwork.authLink.disableEc(new BoxCallback<Void>()
                    {
                        
                        @Override
                        public void run(Void result2)
                        {
                            select();
                        }
                    });
                }
            }
        });
    }
    
    @Override
    public void init()
    {
    }
}
