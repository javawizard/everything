package net.sf.opengroove.client.loader;

import java.awt.BorderLayout;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.wc.SVNEventAction;

import net.interdirected.autoupdate.ChangelogEntry;
import net.interdirected.autoupdate.CustomGUI;

public class UpdateGUI implements CustomGUI
{
    private JFrame frame;
    private JProgressBar progress;
    private JTextArea textArea;
    
    public void buildComplete()
    {
        textArea.append("Build complete.");
        progress.setString("Build complete.");
        try
        {
            Thread.sleep(500);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        frame.dispose();
        return;
    }
    
    public void buildStarted()
    {
        progress.setString("Running build script...");
        textArea.append("Running build script...");
    }
    
    public void buildStatus(String arg0)
    {
        textArea.append(arg0 + "\n");
    }
    
    public void checkingForUpdates()
    {
    }
    
    public boolean error(Throwable arg0, boolean arg1)
    {
        frame.show();
        StringWriter sw = new StringWriter();
        arg0.printStackTrace(new PrintWriter(sw, true));
        textArea.append("==============   ERROR   ==============");
        textArea.append(sw.toString() + "\n");
        JOptionPane.showMessageDialog(frame,
            "<html>An error occured while trying to update. OpenGroove will start, but<br/>"
                + "we highly recommend that you don't use it until you contact<br/>"
                + "us. Send us an email at support@opengroove.org and we'll be<br/>"
                + "happy to help.");
        textArea.append("Starting OpenGroove in 10 seconds");
        try
        {
            Thread.sleep(10 * 1000);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        frame.dispose();
        return true;
    }
    
    public void init(Preferences prefs)
    {
        frame = new JFrame("Updates - OpenGroove");
        try
        {
            frame.setIconImage(ImageIO.read(new File("trayicon.gif")));
            /*
             * This really should be changed to load trayicon.png and replace
             * 255,0,0,0 with 0,0,0,255 (like OpenGroove.java does), but I don't
             * want to deal with that right now.
             */
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        frame.setSize(550, 350);
        frame.setLocationRelativeTo(null);
    }
    
    public void needsUpdate()
    {
        frame.show();
        progress.setString("");
        textArea.setText("Updates are available.");
    }
    
    public boolean shouldTryUpdate()
    {
        throw new RuntimeException(
            "OpenGroove auto updater should only be used in tag mode.");
    }
    
    private boolean shouldUpdate = false;
    
    public boolean shouldUpdate(ChangelogEntry[] arg0)
    {
        JDialog dialog = new JDialog(frame, "", true);
        JPanel inner = new JPanel();
        inner.setLayout(new BorderLayout());
        inner.setBorder(new EmptyBorder(10, 10, 10, 10));
        dialog.getContentPane().add(inner);
        JPanel lower = new JPanel();
        lower.setLayout(new BorderLayout());
        inner.add(lower, BorderLayout.SOUTH);
        JPanel lowerRight = new JPanel();
        lowerRight.setLayout(new BoxLayout(lowerRight, BoxLayout.X_AXIS));
        lower.add(lowerRight, BorderLayout.EAST);
        JButton ok = new JButton("Install updates");
        JButton cancel = new JButton("Don't install updates");
        dialog.setSize(400, 500);
        lowerRight.add(ok);
        lowerRight.add(cancel);
        JPanel middle = new JPanel();
        middle.setBorder(new EmptyBorder(0, 0, 10, 0));
        inner.add(middle);
        middle.add(new JLabel("<html>Updates are available.<br/>"
            + "Would you like to install them?"), BorderLayout.NORTH);
        /*
         * Build changelog inside dialog and show dialog, prompting user whether
         * to update, then return status from this method
         */
        return false;
    }
    
    public void upToDate(boolean arg0)
    {
        /*
         * TODO: just thought of something. Why exactly does this method take an
         * argument? A build won't even be needed if everything's up to date.
         */
    }
    
    public void updateComplete(boolean arg0)
    {
    }
    
    public void updating()
    {
        // TODO Auto-generated method stub
        
    }
    
    public void updateStatus(SVNEventAction action, String path, SVNNodeKind type,
        double progress)
    {
        // TODO Auto-generated method stub
        
    }
    
}
