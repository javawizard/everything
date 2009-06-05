package org.opengroove.g4.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectUtils
{
    /**
     * Writes a single object to the file specified. The file will be
     * overwritten if it already exists.
     * 
     * @param object
     * @param file
     */
    public static void writeObject(Object object, File file)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.flush();
            oos.close();
            fos.flush();
            fos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getClass().getName() + ": " + e.getMessage(),
                e);
        }
    }
    
    /**
     * Reads a single object from the file specified.
     * 
     * @param file
     * @return
     */
    public static Object readObject(File file)
    {
        try
        {
            FileInputStream fos = new FileInputStream(file);
            ObjectInputStream oos = new ObjectInputStream(fos);
            Object o = oos.readObject();
            oos.close();
            fos.close();
            return o;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getClass().getName() + ": " + e.getMessage(),
                e);
        }
    }
}
