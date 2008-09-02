package net.sf.opengroove.client.com;

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
    
    public void setField(String name, String value)
    {
        if (value == null)
            properties.remove(name);
        properties.setProperty(name, value);
    }
    
}
