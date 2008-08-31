package tests;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sf.opengroove.client.ui.TestFrame;

public class Test039
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        TestFrame frame = new TestFrame();
        frame.getContentPane()
            .setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 10, 2, 2));
        frame.getContentPane().add(new JScrollPane(panel));
        frame.show();
        for (int i = 0x2190; i < 0x2200; i++)
        {
            JLabel label = new JLabel(" " + (char) i + " ");
            label.setFont(Font.decode("Dialog 18"));
            label.setToolTipText("0x"
                + Integer.toHexString(i));
            panel.add(label);
            panel.invalidate();
            panel.validate();
            panel.repaint();
            frame.invalidate();
            frame.validate();
            frame.repaint();
        }
    }
}
