package net.sf.opengroove.client.com;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class OgvFile
{
    public static Properties load(File file) throws FileNotFoundException, IOException
    {
        Properties props = new Properties();
        props.load(new FileInputStream(file));
        return props;
    }
}
