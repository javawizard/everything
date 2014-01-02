package net.sf.opengroove.client.g3com;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class FieldFile
{
    /**
     * Some common fields used in some field files. The names of these enum
     * constants directly correspond with the property name of the field.
     * 
     * @author Alexander Boyd
     * 
     */
    public static enum Fields
    {
        encPub, encMod, encPrv, sigPub, sigMod, sigPrv
    }
    
    private Properties properties = new Properties();
    
    public FieldFile(File file)
        throws FileNotFoundException, IOException
    {
        properties.load(new FileInputStream(file));
    }
    
    public FieldFile()
    {
        
    }
    
    public void save(File file)
        throws FileNotFoundException, IOException
    {
        properties.store(new FileOutputStream(file),
            "OGVFile");
    }
    
    public String getField(String name)
    {
        return properties.getProperty(name);
    }
    
    /*
     * The methods that take objects in the place of strings are primarily to
     * support passing enum constants of the Fields enum directly into the
     * methods
     */
    public String getField(Object name)
    {
        return getField(name.toString());
    }
    
    public void setField(Object name, String value)
    {
        setField(name.toString(), value);
    }
    
    public void setField(String name, String value)
    {
        if (value == null)
            properties.remove(name);
        properties.setProperty(name, value);
    }
    
    /**
     * Checks that all of the fields named are present in this field file, and
     * do not have null values. For every string in the argument
     * <code>strings</code>, getField(string) is called, and if it returns
     * null, false is returned from this method. If all of the strings are
     * checked, with the result being that none of their values are null, true
     * is returned.
     * 
     * @param strings
     * @return
     */
    public boolean checkExists(String... strings)
    {
        for (String string : strings)
        {
            if (getField(string) == null)
                return false;
        }
        return true;
    }
    
    public boolean checkExists(Object... strings)
    {
        String[] st = new String[strings.length];
        for (int i = 0; i < st.length; i++)
        {
            st[i] = strings[i].toString();
        }
        return checkExists(st);
    }
    
}
