package net.sf.opengroove.client.installer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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
        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser
            .setFileSelectionMode(fileChooser.DIRECTORIES_ONLY);
        boolean traySupported = SystemTray.isSupported();
        if (!traySupported)
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
        boolean desktopSupported = Desktop
            .isDesktopSupported();
        if (!desktopSupported)
        {
            JFrame f = new JFrame("OpenGroove Installer");
            f.setSize(0, 0);
            f.setLocationRelativeTo(null);
            f.show();
            JOptionPane
                .showMessageDialog(
                    f,
                    "<html>Your system does not support AWT Native Desktop. You can still<br/>"
                        + "install OpenGroove. However, some features, such as file-sharing<br/>"
                        + "workspaces, will be disabled. Click OK to proceed with the installer,<br/>"
                        + "or send us an email at support@opengroove.org if you have questions.");
            f.dispose();
        }
        final JFrame frame = new JFrame(
            "OpenGroove Installer");
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
                + "Program Files folder.\n\nChoose the folder that you want "
                + "to install OpenGroove in, make sure you have an internet "
                + "connection, and click start.\n\nA shortcut will be created on "
                + "your desktop. You can move it to your start menu later, if"
                + " you want.");
        installDescription.setAlignmentX(0);
        top.add(installDescription);
        top.add(Box.createVerticalStrut(10));
        top.add(new JSeparator());
        top.add(Box.createVerticalStrut(10));
        final JTextField fileField = new JTextField();
        fileField.setText(new File(System
            .getProperty("user.home"), "opengroove")
            .getCanonicalPath());
        JPanel filePanel = new JPanel(new BorderLayout());
        filePanel.setMaximumSize(null);
        filePanel.setAlignmentX(0);
        JLabel fileLabel = new JLabel("Choose a folder:  ");
        filePanel.add(fileLabel, BorderLayout.WEST);
        filePanel.add(fileField, BorderLayout.CENTER);
        JButton fileButton = new JButton("Browse");
        fileButton.setMargin(new Insets(0, 0, 0, 0));
        filePanel.add(fileButton, BorderLayout.EAST);
        fileButton.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                File toShow = new File(fileField.getText())
                    .getParentFile();
                while (toShow == null || !toShow.exists())
                {
                    if (toShow != null)
                        toShow = toShow.getParentFile();
                    if (toShow == null)
                    {
                        toShow = new File(System
                            .getProperty("user.home"));
                        break;
                    }
                }
                fileChooser.setCurrentDirectory(toShow);
                int option = fileChooser
                    .showOpenDialog(frame);
                if (option != JFileChooser.APPROVE_OPTION)
                    return;
                try
                {
                    fileField.setText(fileChooser
                        .getSelectedFile()
                        .getCanonicalPath());
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        });
        top.add(filePanel);
        top.add(Box.createVerticalStrut(10));
        JProgressBar progress = new JProgressBar(0, 1);
        progress.setValue(0);
        progress.setString("");
        progress.setStringPainted(true);
        progress.setAlignmentX(0);
        final JButton startButton = new JButton("Start");
        startButton.setAlignmentX(0);
        startButton.setMaximumSize(new Dimension(
            Integer.MAX_VALUE, Integer.MAX_VALUE));
        startButton.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                startButton.setEnabled(false);
                new Thread()
                {
                    public void run()
                    {
                        File installFolder = new File(
                            fileField.getText());
                        if (!installFolder.exists())
                        {
                            if (JOptionPane
                                .showConfirmDialog(
                                    frame,
                                    "The folder you chose does not exist. "
                                        + "Should it be created?",
                                    null,
                                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                                installFolder.mkdirs();
                            else
                            {
                                startButton
                                    .setEnabled(true);
                                return;
                            }
                        }
                        if (!installFolder.isDirectory())
                        {
                            JOptionPane
                                .showMessageDialog(frame,
                                    "The folder you chose is a file, not a folder.");
                            startButton.setEnabled(true);
                            return;
                        }
                        if (!(installFolder.canRead() && installFolder
                            .canWrite()))
                        {
                            JOptionPane
                                .showMessageDialog(
                                    frame,
                                    "The folder you chose cannot be written. Choose another folder.");
                            startButton.setEnabled(true);
                            return;
                        }
                        
                    }
                }.start();
            }
        });
        top.add(startButton);
        top.add(Box.createVerticalStrut(10));
        top.add(progress);
        frame.show();
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
    }
}
