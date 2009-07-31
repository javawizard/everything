package jw.bznetwork.client.screens;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import jw.bznetwork.client.AuthProvider;
import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.BoxCallback;
import jw.bznetwork.client.VerticalScreen;
import jw.bznetwork.client.data.EditAuthenticationModel;
import jw.bznetwork.client.ui.Header2;

@SuppressWarnings("deprecation")
public class AuthenticationScreen extends VerticalScreen
{
    
    @Override
    public void deselect()
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public String getName()
    {
        return "authentication";
    }
    
    @Override
    public String getTitle()
    {
        return "Authentication";
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
        BZNetwork.authLink
                .getEditAuthenticationModel(new BoxCallback<EditAuthenticationModel>()
                {
                    
                    @Override
                    public void run(EditAuthenticationModel result)
                    {
                        select1(result);
                    }
                });
    }
    
    protected void select1(final EditAuthenticationModel result)
    {
        widget.clear();
        widget.add(new Header2("Authentication"));
        for (final AuthProvider provider : result.getProviders())
        {
            Label nameLabel = new Label(provider.getName());
            nameLabel.addStyleName("bznetwork-AuthEditorProviderName");
            widget.add(nameLabel);
            HorizontalPanel providerPanel = new HorizontalPanel();
            providerPanel.add(new jw.bznetwork.client.ui.Spacer("12px", "1px"));
            VerticalPanel panel = new VerticalPanel();
            providerPanel.add(panel);
            panel.add(new Label(provider.getDescription()));
            final ListBox box = new ListBox();
            box.addItem("default");
            box.addItem("enabled");
            box.addItem("disabled");
            String status = result.getEnabledProps().get(provider.getId());
            if ("default".equals(status))
                box.setSelectedIndex(0);
            else if ("enabled".equals(status))
                box.setSelectedIndex(1);
            else
                box.setSelectedIndex(2);
            box.addChangeListener(new ChangeListener()
            {
                
                @Override
                public void onChange(Widget sender)
                {
                    result.getEnabledProps().put(provider.getId(),
                            box.getItemText(box.getSelectedIndex()));
                    if (result.getEnabledProps().get(provider.getId()).equals(
                            "default"))
                    {
                        for (String key : result.getEnabledProps().keySet())
                        {
                            if ((!key.equals(provider.getId()))
                                    && result.getEnabledProps().get(key)
                                            .equals("default"))
                            {
                                result.getEnabledProps().put(key, "enabled");
                            }
                        }
                    }
                    BZNetwork.authLink.updateAuthentication(result
                            .getEnabledProps(), new BoxCallback<Void>()
                    {
                        
                        @Override
                        public void run(Void result)
                        {
                            select();
                        }
                    });
                }
            });
            panel.add(box);
            widget.add(providerPanel);
        }
    }
    
}
