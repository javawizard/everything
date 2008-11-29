package tests;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.sf.opengroove.client.ui.TestFrame;

public class Test052
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        TestFrame frame = new TestFrame();
        frame.setLayout(new BorderLayout());
        JButton b1 = new JButton("Button 1 (flashing)");
        JButton b2 = new JButton("Button 2 (not flashing)");
        JPanel b1p = new JPanel(new BorderLayout());
        b1p.setPreferredSize(new Dimension(0, 0));
        JPanel b2p = new JPanel(new BorderLayout());
        b1p.add(b1);
        b2p.add(b2);
        frame.getContentPane().add(b1p, BorderLayout.NORTH);
        frame.getContentPane()
            .add(b2p, BorderLayout.CENTER);
        frame.show();
        while (true)
        {
            Thread.sleep(1000);
            System.out.println("showing");
            b1p.setPreferredSize(null);
            b1p.invalidate();
            b1p.validate();
            b1p.repaint();
            frame.invalidate();
            frame.validate();
            frame.repaint();
            Thread.sleep(1000);
            System.out.println("hiding");
            b1p.setPreferredSize(new Dimension(0, 0));
            b1p.invalidate();
            b1p.validate();
            b1p.repaint();
            frame.invalidate();
            frame.validate();
            frame.repaint();
        }
    }
    
}
