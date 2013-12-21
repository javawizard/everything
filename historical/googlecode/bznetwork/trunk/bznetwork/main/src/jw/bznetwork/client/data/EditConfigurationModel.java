package jw.bznetwork.client.data;

import java.io.Serializable;

import jw.bznetwork.client.data.model.Configuration;

public class EditConfigurationModel implements Serializable
{
    private Configuration configuration;
    private boolean ecDisabled;
    private String ecDisableFile;
    
    public String getEcDisableFile()
    {
        return ecDisableFile;
    }
    
    public void setEcDisableFile(String ecDisableFile)
    {
        this.ecDisableFile = ecDisableFile;
    }
    
    public Configuration getConfiguration()
    {
        return configuration;
    }
    
    public void setConfiguration(Configuration configuration)
    {
        this.configuration = configuration;
    }
    
    public boolean isEcDisabled()
    {
        return ecDisabled;
    }
    
    public void setEcDisabled(boolean ecDisabled)
    {
        this.ecDisabled = ecDisabled;
    }
}
