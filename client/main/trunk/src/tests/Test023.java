package tests;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

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
        DefaultProgressItem[] items = new DefaultProgressItem[7];
        frame.getContentPane().add(
            new JLabel("<html><b>Some items to do:</b>"),
            BorderLayout.NORTH);
        frame.getContentPane().add(progress);
        frame.setSize(400, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.show();
        Thread.sleep(100);
        for (int i = 0; i < items.length; i++)
        {
            items[i] = new DefaultProgressItem("Test item "
                + (i + 1)
                + (i == 2 ? " (This item will fail)" : ""),
                null);
            if (i == 0)
                items[i]
                    .setStatus(ProgressItem.Status.ACTIVE);
            progress.addItem(items[i]);
        }
        for (int i = 0; i < items.length; i++)
        {
            Thread.sleep(1500);
            if (i != 2)
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
