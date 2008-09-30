package tests.t40;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFrame;

import net.sf.opengroove.client.ui.AnimatedImage;

public class Test022
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // A test for showing an animated gif inside a frame.
        JFrame frame = new JFrame();
        frame.getContentPane().add(
            new AnimatedImage(
                new File("icons/thinking.gif")),
            BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame
            .setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.show();
    }
}
