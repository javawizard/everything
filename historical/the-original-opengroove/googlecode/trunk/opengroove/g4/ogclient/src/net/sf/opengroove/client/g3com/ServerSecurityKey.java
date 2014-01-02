package net.sf.opengroove.client.g3com;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

/**
 * A class for parsing .ogvs files.
 * 
 * @author Alexander Boyd
 * 
 */
public class ServerSecurityKey
{
    public BigInteger rsaPublic;
    public BigInteger rsaMod;
    /**
     * The realm that these keys are for. This is not required (and is not set
     * upon an instance of this class being returned from parse()), but can be
     * set and read by the application using this class as it sees fit, to make
     * it easier to track security keys throughout the application.
     */
    public String realm;
    
    public static ServerSecurityKey parse(File file)
    {
        String keyMerged = readFile(file);
        keyMerged = keyMerged.trim();
        String[] keySplit = keyMerged.split("x");
        if (keySplit.length != 4)
        {
            System.out.println("Invalid key");
            System.exit(0);
        }
        ServerSecurityKey result = new ServerSecurityKey();
        result.rsaPublic = new BigInteger(keySplit[0], 16);
        result.rsaMod = new BigInteger(keySplit[1], 16);
        return result;
    }
    
    /**
     * reads the file specified in to a string. the file must not be larger than
     * 5 MB.
     * 
     * @param file.
     * @return
     */
    public static String readFile(File file)
    {
        try
        {
            if (file.length() > (5 * 1000 * 1000))
                throw new RuntimeException(
                    "the file is "
                        + file.length()
                        + " bytes. that is too large. it can't be larger than 5000000 bytes.");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(file);
            copy(fis, baos);
            fis.close();
            baos.flush();
            baos.close();
            return new String(baos.toByteArray(), "UTF-8");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static void writeFile(String string, File file)
    {
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(
                string.getBytes("UTF-8"));
            FileOutputStream fos = new FileOutputStream(
                file);
            copy(bais, fos);
            bais.close();
            fos.flush();
            fos.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static void copy(InputStream in, OutputStream out)
        throws IOException
    {
        byte[] buffer = new byte[8192];
        int amount;
        while ((amount = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, amount);
        }
    }
    
}
