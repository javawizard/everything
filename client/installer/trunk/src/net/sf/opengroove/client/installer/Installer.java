package net.sf.opengroove.client.installer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class Installer
{
    private static final String DOWNLOAD_URL = "http://sysup.ogis.opengroove.org";
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        frame.setSize(500, 450);
        frame.setLocationRelativeTo(null);
        JLabel mainLabel = new JLabel(
            "Welcome to the OpenGroove Installer");
        mainLabel.setFont(Font.decode(null).deriveFont(22f)
            .deriveFont(Font.BOLD));
        mainLabel
            .setHorizontalAlignment(SwingConstants.CENTER);
        frame.getContentPane()
            .setLayout(new BorderLayout());
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout()
        frame.getContentPane().add(topWrapper, BorderLayout.NORTH);
        top.add(Box.createVerticalStrut(15));
        top.add(mainLabel);
        top.add(Box.createVerticalStrut(15));
        frame.show();
    }
}
