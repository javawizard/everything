package tests;

import java.awt.SplashScreen;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * A simple class for testing out splash screens. This application should be run
 * with the command line argument -splash:icons/splashscreen.png for it to
 * function correctly.
 * 
 * @author Alexander Boyd
 * 
 */
public class Test025
{
    
    /**
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args)
        throws InterruptedException
    {
        System.out.println("started");
        Thread.sleep(2000);
        SplashScreen ss = SplashScreen.getSplashScreen();
        if (ss == null)
        {
            System.err
                .println("You need to run this class with the command line option "
                    + "-splash:icons/splashscreen.png");
            return;
        }
        JFrame frame = new JFrame();
        frame.setTitle("OpenGroove");
        frame.setUndecorated(true);
        frame.setBounds(ss.getBounds());
        frame.getContentPane().add(
            new JLabel(new ImageIcon(ss.getImageURL())));
        frame.show();
        System.out.println("switched");
        Thread.sleep(2000);
        System.exit(0);
    }
    
}
