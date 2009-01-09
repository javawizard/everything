package net.sf.opengroove.client.loader;

import java.io.File;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.wc.SVNEventAction;

import net.interdirected.autoupdate.ChangelogEntry;
import net.interdirected.autoupdate.CustomGUI;

public class UpdateGUI implements CustomGUI
{
    private JFrame frame;
    
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
        // TODO Auto-generated method stub
        return false;
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
        // TODO Auto-generated method stub
        
    }
    
    public void updateComplete(boolean arg0)
    {
        // TODO Auto-generated method stub
        
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
