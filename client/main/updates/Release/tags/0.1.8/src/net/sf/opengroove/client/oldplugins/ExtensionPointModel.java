package net.sf.opengroove.client.oldplugins;

public class ExtensionPointModel
{
    private String id;
    private String extensionPointClass;
    private String extensionInterface;
    
    public String getId()
    {
        return id;
    }
    
    public String getExtensionPointClass()
    {
        return extensionPointClass;
    }
    
    public String getExtensionInterface()
    {
        return extensionInterface;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public void setExtensionPointClass(
        String extensionPointClass)
    {
        this.extensionPointClass = extensionPointClass;
    }
    
    public void setExtensionInterface(
        String extensionInterface)
    {
        this.extensionInterface = extensionInterface;
    }
}
