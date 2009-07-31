package jw.bznetwork.client.screens;

import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.BoxCallback;
import jw.bznetwork.client.Screen;
import jw.bznetwork.client.data.EditConfigurationModel;
import jw.bznetwork.client.data.model.Configuration;
import jw.bznetwork.client.rt.RichTextToolbar;
import jw.bznetwork.client.ui.Header2;
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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("deprecation")
public class ConfigurationScreen implements Screen
{
    private VerticalPanel widget = new VerticalPanel();
    
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
    public Widget getWidget()
    {
        return widget;
    }
    
    @Override
    public void init()
    {
        widget.add(new Label("Configuration screen coming soon!"));
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

    protected void select1(final EditConfigurationModel result)
    {
        String ecEnabledInfoString = "The <b>Executable</b> field is the executable that "
                + "should be run to start bzfs. Normally this is exactly that: &quot;bzfs&quot;. "
                + "This can contain command arguments as well. Since allowing this to be edited "
                + "via the web poses a security risk (imagine someone changing this to &quot;"
                + "rm -rf /*&quot;), you can disable it by clicking <b>Disable Changes</b>. This "
                + "will create a file called <tt>"
                + result.getEcDisableFile()
                + "</tt>. You "
                + "will have to manually delete that file to re-enable changes.";
        final String ecDisabledInfoString = "Changes to the <b>Executable</b> field are currently "
                + "disabled. To re-enable them, manually delete the file <tt>"
                + result.getEcDisableFile() + "</tt> on your server.";
        final Configuration config = result.getConfiguration();
        widget.clear();
        widget.add(new Header2("Configuration"));
        FlexTable table = new FlexTable();
        table.setText(0, 0, "Site name: ");
        table.setText(1, 0, "Contact: ");
        table.setText(2, 0, "Executable: ");
        table.setText(3, 0, "Show menu to the left: ");
        table.setText(4, 0, "Show current page name in header: ");
        table.setText(5, 0, "Welcome message: ");
        table.getFlexCellFormatter().setVerticalAlignment(5, 0,
                VerticalPanel.ALIGN_TOP);
        final TextBox siteNameBox = new TextBox();
        siteNameBox.setText(config.getSitename());
        table.setWidget(0, 1, siteNameBox);
        final TextBox contactBox = new TextBox();
        contactBox.setText(config.getContact());
        table.setWidget(1, 1, contactBox);
        final TextBox executableBox = new TextBox();
        executableBox.setText(config.getExecutable());
        if (result.isEcDisabled())
            executableBox.setReadOnly(true);
        HorizontalPanel executablePanel = new HorizontalPanel();
        executablePanel.add(executableBox);
        executablePanel.add(new Spacer("5px", "5px"));
        table.setWidget(2, 1, executablePanel);
        final SimpleCheckBox menuLeftCheckbox = new SimpleCheckBox();
        menuLeftCheckbox.setChecked(config.isMenuleft());
        table.setWidget(3, 1, menuLeftCheckbox);
        final SimpleCheckBox currentNameCheckbox = new SimpleCheckBox();
        currentNameCheckbox.setChecked(config.isCurrentname());
        table.setWidget(4, 1, currentNameCheckbox);
        final TextArea welcomeField = new TextArea();
        welcomeField.setText(config.getWelcome());
        welcomeField.setCharacterWidth(65);
        welcomeField.setVisibleLines(7);
        table.setWidget(5, 1, welcomeField);
        table.getFlexCellFormatter().setColSpan(5, 1, 2);
        Button saveButton = new Button("Save");
        table.setWidget(6, 1, saveButton);
        final Button disableEcButton = new Button("Disable Changes");
        executablePanel.add(disableEcButton);
        widget.add(table);
        widget.add(new Spacer("8px", "8px"));
        final HTML ecInfoLabel = new HTML();
        ecInfoLabel.setHTML(result.isEcDisabled() ? ecDisabledInfoString
                : ecEnabledInfoString);
        widget.add(ecInfoLabel);
        widget.add(new Spacer("8px", "8px"));
        widget.add(new HTML(
                "The <b>Welcome message</b> field can contain HTML."));
        widget.add(new Spacer("8px", "8px"));
        widget.add(new HTML("Most of these settings won't take effect until "
                + "you log out and then log back in."));
        saveButton.addClickListener(new ClickListener()
        {
            
            @Override
            public void onClick(Widget sender)
            {
                if (siteNameBox.getText().trim().equals(""))
                {
                    Window.alert("The site has to have a name.");
                    return;
                }
                if (contactBox.getText().trim().equals(""))
                {
                    Window
                            .alert("The site has to have a contact. If you "
                                    + "don't want to give any contact information,"
                                    + " use something like \"No contact information\".");
                }
                if (executableBox.getText().trim().equals("")
                        && !result.isEcDisabled())
                {
                    Window.alert("The site has to have an executable. Use "
                            + "\"bzfs\" if you're unsure what to put here.");
                }
                config.setContact(contactBox.getText());
                config.setCurrentname(currentNameCheckbox.isChecked());
                config.setExecutable(executableBox.getText());
                config.setMenuleft(menuLeftCheckbox.isChecked());
                config.setSitename(siteNameBox.getText());
                config.setWelcome(welcomeField.getText());
                BZNetwork.authLink.updateConfiguration(config,
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
        disableEcButton.setVisible(!result.isEcDisabled());
        disableEcButton.addClickListener(new ClickListener()
        {
            
            @Override
            public void onClick(Widget sender)
            {
                if (Window
                        .confirm("Are you sure you want to disable changes to the executable?"))
                {
                    BZNetwork.authLink.disableEc(new BoxCallback<Void>()
                    {
                        
                        @Override
                        public void run(Void result2)
                        {
                            executableBox.setText(result.getConfiguration()
                                    .getExecutable());
                            executableBox.setReadOnly(true);
                            disableEcButton.setVisible(false);
                            ecInfoLabel.setHTML(ecDisabledInfoString);
                        }
                    });
                }
            }
        });
    }
}
