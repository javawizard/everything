package net.sf.opengroove.client.settings;

public interface SettingListener
{
    public void settingChanged(SettingSpec spec, Object newValue);
}
