package com.trivergia.intouch3.uploader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Properties;

public class IVersion
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        try
        {
            Properties p = new Properties();
            p
                .load(new FileInputStream(
                    "tmpupd.properties"));
            String newVersionString = ""
                + (Integer.parseInt(p
                    .getProperty("versionindex")) + 1);
            p.setProperty("versionindex", newVersionString);
            p
                .setProperty("url",
                    "http://trivergia.com/opengrooveupdates.jar");
            p
                .store(
                    new FileWriter(
                        "opengrooveupdates.properties"),
                    "Generated by com.trivergia.intouch3.uploader.IVersion r2 in OpenGroove");
            // try
            // {
            // new FileOutputStream("version").write(newVersionString
            // .getBytes());
            // } catch (Exception ex1)
            // {
            // ex1.printStackTrace();
            // }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            System.exit(255);
        }
    }
    
}