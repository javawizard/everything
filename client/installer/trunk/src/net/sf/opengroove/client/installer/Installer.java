package net.sf.opengroove.client.installer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Installer
{
    private static final String DOWNLOAD_URL = "http://sysup.ogis.opengroove.org";
    
    public static JFileChooser fileChooser;
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        fileChooser = new
        JFileChooser();
        boolean traySupported = !SystemTray.isSupported();
        if (traySupported)
        {
            JFrame f = new JFrame("OpenGroove Installer");
            f.setSize(0, 0);
            f.setLocationRelativeTo(null);
            f.show();
            JOptionPane
                .showMessageDialog(
                    f,
                    "<html>Your system does not support tray icons. OpenGroove <br/>"
                        + "can't run on a system that does not support tray icons.<br/>"
                        + "Click OK to exit the installer, or send us an email at <br/>"
                        + "support@opengroove.org if you have questions.");
            System.exit(0);
        }
        JFrame frame = new JFrame("OpenGroove Installer");
        frame.setSize(500, 550);
        frame.setLocationRelativeTo(null);
        JLabel mainLabel = new JLabel(
            "OpenGroove Installer");
        mainLabel.setFont(Font.decode(null).deriveFont(28f)
            .deriveFont(Font.BOLD));
        mainLabel
            .setHorizontalAlignment(SwingConstants.CENTER);
        mainLabel.setMaximumSize(new Dimension(
            Integer.MAX_VALUE, Integer.MAX_VALUE));
        frame.getContentPane()
            .setLayout(new BorderLayout());
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.getContentPane().add(top, BorderLayout.NORTH);
        top.add(Box.createVerticalStrut(5));
        top.add(mainLabel);
        top.add(Box.createVerticalStrut(15));
        JTextArea installDescription = new JTextArea();
        installDescription.setEditable(false);
        installDescription.setOpaque(false);
        installDescription.setLineWrap(true);
        installDescription.setWrapStyleWord(true);
        installDescription
            .append("Welcome to the OpenGroove Installer. This program will "
                + "install OpenGroove on your computer. If you're running "
                + "Windows Vista, you'll need to install the program somewhere "
                + "that you have permission to modify, such as in your home "
                + "folder (for example, "
                + new File(System.getProperty("user.home"),
                    "opengroove").getCanonicalPath()
                + "), not your "
                + "Program Files folder. Choose where you want to install "
                + "OpenGroove, make sure you have an internet "
                + "connection, and click start.");
        installDescription.setAlignmentX(0);
        top.add(installDescription);
        JTextField fileField = new JTextField();
        frame.show();
    }
}
