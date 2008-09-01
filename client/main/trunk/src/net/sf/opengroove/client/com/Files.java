package net.sf.opengroove.client.com;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class Files
{
    public static Properties load(File file)
    {
        Properties props = new Properties();
        props.load(new FileInputStream(file));
        return props;
    }
}
