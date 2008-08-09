package tests;

import java.awt.BorderLayout;
import java.awt.Point;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JViewport;

public class Test027
{
    
    /**
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args)
        throws InterruptedException
    {
        JFrame frame = new JFrame();
        frame.setSize(300, 100);
        frame.setLocationRelativeTo(null);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,
            BoxLayout.X_AXIS));
        for (int i = 0; i < 20; i++)
        {
            panel
                .add(new JButton("Test button " + (i + 1)));
        }
        JViewport viewport = new JViewport();
        viewport.setView(panel);
        viewport.setViewPosition(new Point(0, 0));
        frame.getContentPane().add(viewport,
            BorderLayout.NORTH);
        frame.show();
        while (true)
        {
            Thread.sleep(20);
            try
            {
                viewport.setViewPosition(new Point(viewport
                    .getViewPosition().x + 1, 0));
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
    }
    
}
