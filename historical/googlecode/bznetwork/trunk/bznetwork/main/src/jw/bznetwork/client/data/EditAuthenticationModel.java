package jw.bznetwork.client.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Properties;

import jw.bznetwork.client.AuthProvider;

public class EditAuthenticationModel implements Serializable
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
    
    public HashMap<String, String> getEnabledProps()
    {
        return enabledProps;
    }
    
    public void setEnabledProps(HashMap<String, String> enabledProps)
    {
        this.enabledProps = enabledProps;
    }
    
    private HashMap<String, String> enabledProps;
}
