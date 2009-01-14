package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A simple program that reads all files in a directory that end with the
 * specified string, and counts the total number of lines within them.
 * 
 * @author Alexander Boyd
 * 
 */
public class LineCounter
{
    private static int lines = 0;
    
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args)
        throws IOException
    {
        String folder = "C:/workspace3.0/OpenGroove Client/src/net/sf/opengroove";
        String suffix = ".java";
        recursiveScan(new File(folder), suffix);
        System.out.println("" + lines + " lines");
    }
    
    private static void recursiveScan(File file,
        String suffix) throws IOException
    {
        if (file.getAbsolutePath().contains(".svn"))
            return;
        if (file.isDirectory())
        {
            for (File subfile : file.listFiles())
            {
                recursiveScan(subfile, suffix);
            }
        }
        else
        {
            System.out.println(file.getAbsolutePath());
            FileInputStream fis = new FileInputStream(file);
            int i;
            while ((i = fis.read()) != -1)
            {
                if (i == '\n')
                    lines++;
            }
            fis.close();
        }
    }
    
}
