package net.sf.opengroove.client.loader;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

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
        // TODO Auto-generated method stub
        
    }
    
    public void buildStarted()
    {
        // TODO Auto-generated method stub
        
    }
    
    public void buildStatus(String arg0)
    {
        // TODO Auto-generated method stub
        
    }
    
    public void checkingForUpdates()
    {
        // TODO Auto-generated method stub
        
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
        // TODO Auto-generated method stub
        
    }
    
    public boolean shouldTryUpdate()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    public boolean shouldUpdate(ChangelogEntry[] arg0)
    {
        // TODO Auto-generated method stub
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
