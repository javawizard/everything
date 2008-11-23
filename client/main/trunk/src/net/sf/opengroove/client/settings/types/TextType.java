package net.sf.opengroove.client.settings.types;

import javax.swing.JComponent;
import javax.swing.JTextField;

import net.sf.opengroove.client.settings.SettingParameters;
import net.sf.opengroove.client.settings.SettingType;
import net.sf.opengroove.client.settings.SettingValue;

/**
 * A SettingType that allows the user to enter arbitrary text into a text box.
 * It's value type is {@link String}. It does not require any parameters.
 * 
 * @author Alexander Boyd
 * 
 */
public class TextType implements SettingType
{
    
    public JComponent createComponent(
        SettingParameters parameters)
    {
        return new JTextField();
    }
    
    public JComponent createReadOnlyComponent(
        JComponent component, SettingParameters parameters)
    {
        JTextField field = new JTextField();
        field.setEditable(false);
        field.setText(((JTextField) component).getText());
        return field;
    }
    
    public Object getValue(SettingValue value,
        SettingParameters parameters)
    {
        if (value.getStringValue() == null)
            return "";
        return value.getStringValue();
    }
    
    public void loadValue(JComponent component,
        SettingValue value, SettingParameters parameters)
    {
        ((JTextField) component).setText(value
            .getStringValue());
    }
    
    public void storeValue(JComponent component,
        SettingValue value, SettingParameters parameters)
    {
        value.setStringValue(((JTextField) component)
            .getText());
    }
    
    public String validate(JComponent component,
        SettingParameters parameters)
    {
        /*
         * We don't need to perform any validation here
         */
        return null;
    }
    
}
