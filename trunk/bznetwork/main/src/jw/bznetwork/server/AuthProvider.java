package jw.bznetwork.server;

import java.net.URLEncoder;

public class AuthProvider
{
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
    }
    
    /**
     * Returns the url for this provider, but with {path} replaced by this path
     * and {path-encoded} replaced by the url-encoded form of this path.
     * 
     * @param path
     * @return
     */
    public String getUrl(String path)
    {
        return getUrl().replace("{path}", path).replace("{path-encoded}",
            URLEncoder.encode(path));
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
