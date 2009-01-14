package net.sf.opengroove.client.settings.types;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import net.sf.opengroove.client.settings.SettingParameters;
import net.sf.opengroove.client.settings.SettingType;
import net.sf.opengroove.client.settings.SettingValue;

/**
 * A type that allows the user to check or uncheck a check box. It's value type
 * is java.lang.Boolean. It takes a CheckboxParameters argument as parameters.
 * The checkbox parameters has only one bean, inverted. If this is true, then
 * the checkbox's state and the value type are inverted. This essentially has
 * the effect of making the checkbox's default state checked instead of
 * unchecked.
 * 
 * @author Alexander Boyd
 * 
 */
public class CheckboxType implements SettingType
{
    
    public JComponent createComponent(
        SettingParameters parameters)
    {
        return new JCheckBox();
    }
    
    public JComponent createReadOnlyComponent(
        JComponent component, SettingParameters parameters)
    {
        final JCheckBox box = new JCheckBox();
        final boolean selected = ((JCheckBox) component)
            .isSelected();
        box.addActionListener(new ActionListener()
        {
            
            public void actionPerformed(ActionEvent e)
            {
                box.setSelected(selected);
            }
        });
        box.setSelected(selected);
        return box;
    }
    
    public Object getValue(SettingValue value,
        SettingParameters parameters)
    {
        if (((CheckboxParameters) parameters).isInverted())
            return !value.getBooleanValue();
        return value.getBooleanValue();
    }
    
    public void loadValue(JComponent component,
        SettingValue value, SettingParameters parameters)
    {
        if (((CheckboxParameters) parameters).isInverted())
            ((JCheckBox) component).setSelected(!value
                .getBooleanValue());
        else
            ((JCheckBox) component).setSelected(value
                .getBooleanValue());
    }
    
    public void storeValue(JComponent component,
        SettingValue value, SettingParameters parameters)
    {
        if (((CheckboxParameters) parameters).isInverted())
            value.setBooleanValue(!((JCheckBox) component)
                .isSelected());
        else
            value.setBooleanValue(((JCheckBox) component)
                .isSelected());
    }
    
    public String validate(JComponent component,
        SettingParameters parameters)
    {
        return null;
    }
    
}
