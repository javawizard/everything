package net.sf.opengroove.client.com;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class OgvFile
{
    private Properties properties = new Properties();
    
    public OgvFile(File file) throws FileNotFoundException,
        IOException
    {
        properties.load(new FileInputStream(file));
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
        properties.setProperty(name, value);
    }
}
