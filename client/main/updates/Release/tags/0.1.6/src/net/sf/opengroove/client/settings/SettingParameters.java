package net.sf.opengroove.client.settings;

/**
 * A marker interface for parameters that a specific setting type might use.
 * This is passed into the settings manager when adding a particular setting.
 * For example, if a setting type represents a text box into which the user can
 * enter a number, then the setting type might accept an implementation of this
 * class that contains fields for bounding the minimum and maximum numeric
 * values that the setting field can have. Or a setting that displays a list of
 * items, and allows the user to select a choice, might accept a set of
 * parameters that contain the actual list of items to display.
 * 
 * @author Alexander Boyd
 * 
 */
public interface SettingParameters
{
    
}
