package tests;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sf.opengroove.client.ui.DefaultProgressItem;
import net.sf.opengroove.client.ui.ProgressItem;
import net.sf.opengroove.client.ui.ProgressPane;

public class Test023
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        // A class for showing a ProgressPane with some items in it.
        JFrame frame = new JFrame();
        ProgressPane progress = new ProgressPane();
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        DefaultProgressItem[] items = new DefaultProgressItem[15];
        frame.getContentPane().add(
            new JLabel("<html><b>Some items to do:</b>"),
            BorderLayout.NORTH);
        frame.getContentPane().add(new JScrollPane(panel));
        panel.add(progress, BorderLayout.NORTH);
        frame.setSize(400, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.show();
        Thread.sleep(100);
        for (int i = 0; i < items.length; i++)
        {
            items[i] = new DefaultProgressItem(
                "Test item "
                    + (i + 1)
                    + (i == 4 ? " (This item will fail)"
                        : ""),
                "This is a description about item " + (i+1),
                (i == 7 ? new JLabel(
                    "These are the details") : null));
            if (i == 0)
                items[i]
                    .setStatus(ProgressItem.Status.ACTIVE);
            progress.addItem(items[i]);
        }
        for (int i = 0; i < items.length; i++)
        {
            Thread.sleep(700);
            if (i != 4)
                items[i]
                    .setStatus(ProgressItem.Status.SUCCESSFUL);
            else
                items[i]
                    .setStatus(ProgressItem.Status.FAILED);
            if ((i + 1) != items.length)
                items[i + 1]
                    .setStatus(ProgressItem.Status.ACTIVE);
            progress.refresh();
        }
    }
}
