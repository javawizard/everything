package jw.bznetwork.client.screens;

import com.google.gwt.user.client.ui.Label;

import jw.bznetwork.client.AuthProvider;
import jw.bznetwork.client.BZNetwork;
import jw.bznetwork.client.BoxCallback;
import jw.bznetwork.client.VerticalScreen;
import jw.bznetwork.client.data.EditAuthenticationModel;
import jw.bznetwork.client.ui.Header2;

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
    
    protected void select1(EditAuthenticationModel result)
    {
        widget.clear();
        widget.add(new Header2("Authentication"));
        for (AuthProvider provider : result.getProviders())
        {
            Label nameLabel = new Label(provider.getName());
            nameLabel.addStyleName("bznetwork-AuthEditorProviderName");
        }
    }
    
}
