package net.sf.opengroove.client.loader;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import net.interdirected.autoupdate.ChangelogEntry;
import net.interdirected.autoupdate.CustomGUI;

import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.wc.SVNEventAction;

public class UpdateGUI implements CustomGUI
{
    private JFrame frame;
    private JProgressBar progress;
    private JTextArea textArea;
    private JScrollPane scroll;
    /*
     * Yes, static.
     */
    public static boolean errorOccured = false;
    
    public void buildComplete()
    {
        append("Build complete.");
        append("");
        appendProgress("OpenGroove is now up to date.");
        append("");
        append("OpenGroove will start in 2 seconds.");
        try
        {
            Thread.sleep(2000);
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
        errorOccured = true;
        frame.show();
        StringWriter sw = new StringWriter();
        arg0.printStackTrace(new PrintWriter(sw, true));
        append("==============   ERROR   ==============");
        append(sw.toString() + "\n");
        JOptionPane.showMessageDialog(frame,
            "<html>An error occured while trying to update. OpenGroove will start, but<br/>"
                + "we highly recommend that you don't use it until you contact<br/>"
                + "us. Send us an email at support@opengroove.org and we'll be<br/>"
                + "happy to help.");
        textArea.append("Starting OpenGroove in 20 seconds");
        try
        {
            Thread.sleep(20 * 1000);
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
        frame.setDefaultCloseOperation(frame.DO_NOTHING_ON_CLOSE);
        try
        {
            frame.setIconImage(ImageIO.read(new File("trayicon.gif")));
            /*
             * This really should be changed to load trayicon.png and replace
             * 255,0,0,0 with 0,0,0,255 (like OpenGroove.java does), but I don't
             * want to deal with that right now.
             * 
             * For now, the progress bar will have a min of 0 and a max of 1000.
             * Then, we'll multiply any fraction input on update progress (if
             * that ends up working) by 1000 to obtain the value to pass to the
             * progress bar. I'm actually thinking that the progress bar should
             * be indeterminate, so this may not even be relevant.
             */
            progress = new JProgressBar(0, 1000);
            progress.setString("Initializing");
            progress.setStringPainted(true);
            progress.setIndeterminate(true);
            JPanel outerPanel = new JPanel();
            outerPanel.setLayout(new BorderLayout());
            outerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            /*
             * TODO: is this next statement even necessary? If I remember
             * correctly, the default layout for the content pane of a JFrame is
             * BorderLayout (in which case this wouldn't need to be set), but
             * I'm working offline right now, so I don't have access to the
             * javadocs.
             */
            frame.getContentPane().setLayout(new BorderLayout());
            frame.add(outerPanel);
            JPanel northPanel = new JPanel();
            northPanel.setLayout(new BorderLayout());
            northPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
            outerPanel.add(northPanel, BorderLayout.NORTH);
            textArea = new JTextArea();
            textArea.setEditable(false);
            scroll = new JScrollPane(textArea);
            northPanel.add(progress, BorderLayout.CENTER);
            outerPanel.add(scroll, BorderLayout.CENTER);
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
        append("Updates are available.");
    }
    
    public boolean shouldTryUpdate()
    {
        throw new RuntimeException(
            "OpenGroove auto updater should only be used in tag mode.");
    }
    
    /**
     * A boolean that is set by the two buttons on the dialog created in
     * {@link #shouldUpdate}
     */
    private boolean shouldUpdate = false;
    
    public boolean shouldUpdate(ChangelogEntry[] changelogEntries)
    {
        progress.setIndeterminate(false);
        if (new File("appdata/updates/noprompt").exists())
            return true;
        final JDialog dialog = new JDialog(frame, "", true);
        JPanel inner = new JPanel();
        inner.setLayout(new BorderLayout());
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
        JPanel middle = new JPanel(new BorderLayout());
        middle.setBorder(new EmptyBorder(10, 10, 10, 10));
        JEditorPane editor = new JEditorPane();
        editor.setEditable(false);
        editor.setContentType("text/html");
        StringBuilder changelog = new StringBuilder();
        changelog.append("<html><body>" + "<b>Updates are available for OpenGroove. "
            + "Would you like to download and install them?"
            + "</b><br/><br/>Here's what will change when "
            + "you install these updates:<br/><br/>");
        for (ChangelogEntry entry : changelogEntries)
        {
            changelog.append("<b>Version " + entry.getTagName() + "-r" + entry.getRevision()
                + "</b><br/>");
            if (entry.getCommitMessage() != null)
                changelog.append(entry.getCommitMessage());
            else
                changelog
                    .append("<font color='#999999'>No information for this version</font>");
            changelog.append("<br/><br/>");
        }
        editor.setText(changelog.toString());
        JScrollPane editorScroll =
            new JScrollPane(editor, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        middle.add(editorScroll, BorderLayout.CENTER);
        inner.add(middle);
        ok.addActionListener(new ActionListener()
        {
            
            public void actionPerformed(ActionEvent e)
            {
                shouldUpdate = true;
                dialog.dispose();
            }
        });
        cancel.addActionListener(new ActionListener()
        {
            
            public void actionPerformed(ActionEvent e)
            {
                shouldUpdate = false;
                dialog.dispose();
            }
        });
        dialog.setLocationRelativeTo(frame);
        dialog.show();
        progress.setIndeterminate(true);
        if (!shouldUpdate)
            frame.dispose();
        return shouldUpdate;
    }
    
    public void upToDate(boolean needsBuild)
    {
        /*
         * TODO: just thought of something. Why exactly does this method take an
         * argument? A build won't even be needed if everything's up to date.
         */
        /*
         * I just realized that this isn't even necessary, since the frame isn't
         * shown unless updates are needed. I'll remove it sometime.
         */
        frame.hide();
    }
    
    public void updateComplete(boolean needsBuild)
    {
        if (!needsBuild)
        {
            System.err.println("OpenGroove was downloaded without build support"
                + " enabled. Contact support@opengroove.org as soon "
                + "as possible and report this error. OpenGroove "
                + "might not function properly until you do.");
            frame.dispose();
            return;
        }
        append("");
        appendProgress("Install complete, getting ready to build...");
        append("Various messages related to the build file will be printed below.");
        append("You can usually ignore these messages, unless an error occurs.");
        append("");
    }
    
    public void updating()
    {
        appendProgress("Downloading and installing updates...");
        append("As files are downloaded and installed, a message will be printed below.");
        append("");
    }
    
    public void appendProgress(String progressString)
    {
        progress.setString(progressString);
        textArea.append(progressString);
        textArea.append("\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
    
    public void append(String progressString)
    {
        textArea.append(progressString);
        textArea.append("\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
    
    public void updateStatus(SVNEventAction action, String path, SVNNodeKind type,
        double progress)
    {
        String actionName;
        if (action.equals(SVNEventAction.UPDATE_ADD))
            actionName = "Added ";
        else if (action.equals(SVNEventAction.UPDATE_DELETE))
            actionName = "Deleted ";
        else if (action.equals(SVNEventAction.UPDATE_UPDATE))
            actionName = "Updated ";
        else
            return;
        append(actionName + path);
    }
}
