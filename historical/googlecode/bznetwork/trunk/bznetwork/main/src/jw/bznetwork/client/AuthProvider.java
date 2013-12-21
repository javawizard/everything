package jw.bznetwork.client;

import java.io.Serializable;

public class AuthProvider implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 3962776083585075455L;
    private String id;
    private String name;
    private String text;
    private String description;
    private String url;
    
    /**
     * Creates an authentication provider from an authentication provider line
     * in the provider configuration file.
     * 
     * @param line
     */
    public AuthProvider(String line)
    {
        String[] tokens = line.split("\\|");
        id = tokens[0];
        name = tokens[1];
        text = tokens[2];
        url = tokens[3];
        description = tokens[4];
    }
    
    public AuthProvider()
    {
        
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getText()
    {
        return text;
    }
    
    public void setText(String text)
    {
        this.text = text;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String getUrl()
    {
        return url;
    }
    
    public void setUrl(String url)
    {
        this.url = url;
    }
}
