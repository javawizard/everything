package net.sf.opengroove.client.settings.types;

import net.sf.opengroove.client.settings.SettingParameters;

public class CheckboxParameters implements
    SettingParameters
{
    private boolean isInverted;
    
    public CheckboxParameters(boolean isInverted)
    {
        super();
        this.isInverted = isInverted;
    }

    public boolean isInverted()
    {
        return isInverted;
    }
    
    public void setInverted(boolean isInverted)
    {
        this.isInverted = isInverted;
    }
}
