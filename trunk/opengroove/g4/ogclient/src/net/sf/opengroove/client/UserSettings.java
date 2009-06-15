package net.sf.opengroove.client;

import net.sf.opengroove.client.com.CommandCommunicator;

/**
 * This class contains enum constants that represent some common user settings
 * 
 * @author Alexander Boyd
 * 
 */
public enum UserSettings
{
    KEY_ENC_PUB("public-rsa-enc-pub"), KEY_ENC_MOD(
        "public-rsa-enc-mod"), KEY_SIG_PUB(
        "public-rsa-sig-pub"), KEY_SIG_MOD(
        "public-rsa-sig-mod");
    private String value;
    
    private UserSettings(String value)
    {
        this.value = value;
    }
    
    /**
     * Returns the key of the user property that this is being called on. This
     * is in a format suitable for passing to
     * {@link CommandCommunicator#getUserSetting(String, String)}.
     */
    public String toString()
    {
        return value;
    }
}
