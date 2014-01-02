package net.sf.opengroove.sandbox.misc;

import java.io.File;

public class FileFreeSpaceTest
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        File file = new File("C:/Users");
        System.out.println(file.getUsableSpace());
    }
    
}
