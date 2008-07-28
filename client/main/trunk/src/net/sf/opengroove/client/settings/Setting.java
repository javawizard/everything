package net.sf.opengroove.client.settings;

import javax.swing.JComponent;

public abstract class Setting
{
    public abstract String getStringValue();
    
    public abstract int getIntValue();
    
    public abstract long getLongValue();
    
    public abstract boolean getBooleanValue();
    
    /**
     * Gets a component that can be used to change the value of this setting.
     * For example, a boolean setting might return a checkbox from this method,
     * or a multiple-choice setting might return a JComboBox.
     * 
     * @return
     */
    public abstract JComponent getComponent();
    
    public abstract void setStringValue(String string);
    
    protected Setting(String id, String name,
        String description)
    {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    
    protected String id;
    
    public String getId()
    {
        return id;
    }
    
    protected String name;
    
    public String getName()
    {
        return name;
    }
    
    protected String description;
    
    public String getDescription()
    {
        return description;
    }
}
