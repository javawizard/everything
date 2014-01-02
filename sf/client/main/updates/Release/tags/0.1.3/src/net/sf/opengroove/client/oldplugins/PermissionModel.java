package net.sf.opengroove.client.oldplugins;

public class PermissionModel
{
    private String name;
    private boolean required;
    private String description;
    
    public String getName()
    {
        return name;
    }
    
    public boolean isRequired()
    {
        return required;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setRequired(boolean required)
    {
        this.required = required;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
}
