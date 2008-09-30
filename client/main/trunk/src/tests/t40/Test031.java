package tests.t40;

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
        Clock clock = new Clock(3600);
        frame.getContentPane().add(clock);
        frame.pack();
        frame.show();
    }
    
}
