package net.sf.opengroove.client.settings;

import javax.swing.JComponent;

/**
 * This interface represents a specific type of setting. In general, only one
 * instance of a particular implementation of this interface will ever exist at
 * the same time. On a setingsmanager, the registerType method can be called,
 * passing in an instance of this class. This class is then responsible for
 * displaying settings and generating the corresponding widgets.
 * 
 * @author Alexander Boyd
 */
public interface SettingType
{
    /**
     * Stores the value in the component into the setting value provided.
     * 
     * @param component
     * @param value
     */
    public void storeValue(JComponent component,
        SettingValue value, SettingParameters parameters);
    
    /**
     * Validates that the contents of the specified component are valid, using
     * the parameters specified.
     * 
     * @param component
     * @return null if the contents are valid, a string decribing the problem if
     *         the contents are not. If this is not null, the user will be shown
     *         it, and they will not be allowed to exit the settings dialog.
     */
    public String validate(JComponent component,
        SettingParameters parameters);
    
    public JComponent createComponent(
        SettingParameters parameters);
    
    /**
     * Creates a component that displays a read-only view of the value contained
     * within the component specified. It is recommended that the component
     * returned update itself with changes to the component specified; this is,
     * however, not required.<br/><br/>
     * 
     * The component returned should be as compact as possible; returned
     * components should, for example, omit buttons instead of include disabled
     * ones if the editable form of the component includes buttons.
     * 
     * @param component
     * @return
     */
    public JComponent createReadOnlyComponent(
        JComponent component, SettingParameters parameters);
    
    /**
     * Loads the value in the setting value specified into the component
     * specified. If the value specified is invalid according to the parameters
     * specified, then the most that this method can do is correct the value. It
     * must not throw an exception upon seeing an invalid value. It is also
     * recommended not to allow the value through, since the user will be
     * prompted when they try to exit the setings dialog that a setting that
     * they might not have even changed is invalid.
     * 
     * @param component
     * @param value
     */
    public void loadValue(JComponent component,
        SettingValue value, SettingParameters parameters);
    
    /**
     * Gets the value in a form suitable for passing out of calls to
     * getSettingValue() on SettingsManager.
     * 
     * @param value
     * @return
     */
    public Object getValue(SettingValue value,
        SettingParameters parameters);
}
