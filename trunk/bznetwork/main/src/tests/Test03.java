package tests;

import java.text.SimpleDateFormat;
import java.util.Date;

import jw.bznetwork.server.BZNetworkServer;

/**
 * A benchmark for formatting strings
 * 
 * @author Alexander Boyd
 * 
 */
public class Test03
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Date date = new Date();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 5000; i++)
        {
            String s = String.format(
                    "%1$tY/%1$tm/%1$td %1$tI:%1$tM.%1$tS %1$Tp", date);
        }
        System.out.println("5000 printf cycles took "
                + (System.currentTimeMillis() - start) + "ms");
        start = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm.ss aa");
        for (int i = 0; i < 5000; i++)
        {
            String s = dateFormat.format(date);
        }
        System.out.println("5000 dateformat cycles took "
                + (System.currentTimeMillis() - start) + "ms");
    }
    
}
