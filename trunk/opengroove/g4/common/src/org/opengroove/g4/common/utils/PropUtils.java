package org.opengroove.g4.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class PropUtils
{
    /**
     * Gets the specified property, returning null if the property doesn't exist
     * or the file doesn't exist.
     * 
     * @param file
     *            The file to read from
     * @param key
     *            The name of the property
     * @return The property's value, or null if the property doesn't exist or
     *         the file doesn't exist
     */
    public static String getProperty(File file, String key)
    {
        try
        {
            Properties props = new Properties();
            if (file.exists())
                props.load(new FileInputStream(file));
            return props.getProperty(key);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Gets the properties in the file specified, or returns an empty Properties
     * object if the file does not exist.
     * 
     * @param file
     * @return
     */
    public static Properties getProperties(File file)
    {
        try
        {
            Properties props = new Properties();
            if (file.exists())
                props.load(new FileInputStream(file));
            return props;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Sets the specified property. If the file doesn't already exist, it will
     * be created (even if the value is null). If the property's value is null,
     * then the property will be removed.
     * 
     * @param file
     *            The file to write to
     * @param key
     *            The name of the property to set
     * @param value
     *            The new value that the property should have, or null to remove
     *            the property from this file
     */
    public static void setProperty(File file, String key, String value)
    {
        try
        {
            Properties props = new Properties();
            if (file.exists())
                props.load(new FileInputStream(file));
            if (value == null)
                props.remove(key);
            else
                props.setProperty(key, value);
            props.store(new FileOutputStream(file),
                "org.opengroove.g4.common.utils.PropUtils.setProperty()");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
