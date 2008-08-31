package tests;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
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
        int start = Integer
            .decode(JOptionPane.showInputDialog(frame,
                "Enter a start number"));
        int end = Integer.decode(JOptionPane
            .showInputDialog(frame, "Enter an end number"));
        JProgressBar progress = new JProgressBar(start, end);
        progress.setValue(start);
        frame.getContentPane().add(progress,
            BorderLayout.NORTH);
        progress.setStringPainted(true);
        progress.setString("Loading...");
        for (int i = start; i <= end; i++)
        {
            progress.setValue(i);
            progress.setString("" + (end - i)
                + " to go (start: " + start + ",end: "
                + end + ")");
            final JLabel label = new JLabel(" " + (char) i
                + " ");
            final int fI = i;
            label.addMouseListener(new MouseAdapter()
            {
                
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    System.out.println("clicked");
                    if (e.getClickCount() == 2)
                    {
                        System.out.println("popup");
                        Toolkit.getDefaultToolkit()
                            .getSystemClipboard()
                            .setContents(
                                new StringSelection(""
                                    + (char) fI), null);
                        System.out.println("transferred");
                        new Thread()
                        {
                            public void run()
                            {
                                try
                                {
                                    for (int i = 0; i < 4; i++)
                                    {
                                        label
                                            .setForeground(Color.RED
                                                .darker());
                                        Thread.sleep(150);
                                        label
                                            .setForeground(Color.BLACK);
                                        Thread.sleep(150);
                                    }
                                }
                                catch (Exception exception)
                                {
                                    exception
                                        .printStackTrace();
                                }
                            }
                        }.start();
                    }
                }
            });
            label.setFont(Font.decode("Dialog 18"));
            label.setToolTipText("0x"
                + Integer.toHexString(i));
            panel.add(label);
            if ((i % 50) == 0)
            {
                panel.invalidate();
                panel.validate();
                panel.repaint();
                frame.invalidate();
                frame.validate();
                frame.repaint();
            }
        }
        panel.invalidate();
        panel.validate();
        panel.repaint();
        frame.invalidate();
        frame.validate();
        frame.repaint();
    }
}
