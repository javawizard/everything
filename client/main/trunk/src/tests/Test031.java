package tests;

import javax.swing.JFrame;

import net.sf.opengroove.client.ui.DatePicker.Clock;

public class Test031
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // A class to test out the DatePicker class
        JFrame frame = new JFrame();
        Clock clock = new Clock(12, 0, 0);
        frame.getContentPane().add(clock);
        frame.pack();
        frame.show();
    }
    
}
