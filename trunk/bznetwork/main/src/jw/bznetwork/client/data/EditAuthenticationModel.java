package jw.bznetwork.client.data;

import java.util.Properties;

import jw.bznetwork.client.AuthProvider;

public class EditAuthenticationModel
{
    public EditAuthenticationModel()
    {
        
    }
    
    private AuthProvider[] providers;
    
    public AuthProvider[] getProviders()
    {
        return providers;
    }
    
    public void setProviders(AuthProvider[] providers)
    {
        this.providers = providers;
    }
    
    public Properties getEnabledProps()
    {
        return enabledProps;
    }
    
    public void setEnabledProps(Properties enabledProps)
    {
        this.enabledProps = enabledProps;
    }
    
    private Properties enabledProps;
}
