package net.sf.opengroove.client.download;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.html.FormSubmitEvent;
import javax.swing.text.html.HTMLEditorKit;

import net.sf.opengroove.client.Convergia;
import net.sf.opengroove.client.plugins.PluginManager;

public class PluginDownloadManager
{
    public static final String PLUGIN_DOWNLOAD_URL = "http://trivergia.com:8080/convergiaptl.jsp";
    
    // parameters are installedplugins which is comma-separated, and plugintypes
    // which is the type
    // of plugins to show (comma-separated), or null (IE nonexistant) to show
    // all plugin types
    /**
     * this method prompts the user to select plugins on the ptl that they wish
     * to download. this method should only be called by Convergia. If you are a
     * developer creating your own plugin, consider using
     * Convergia.findNewPlugins() instead.
     * 
     * @throws MalformedURLException
     */
    public static void promptForDownload(JFrame parent,
        String[] types, String[] alreadyInstalled)
        throws MalformedURLException
    {
        final JDialog dialog = new JDialog(parent, true);
        dialog.setTitle("Get new plugins - OpenGroove");
        dialog.getContentPane().setLayout(
            new BorderLayout());
        dialog.setSize(470, 590);
        dialog.setLocationRelativeTo(parent);
        final JEditorPane p = new JEditorPane();
        p.setContentType("text/html");
        p.setEditable(false);
        p.setOpaque(false);
        dialog.getContentPane().add(new JScrollPane(p),
            BorderLayout.CENTER);
        JPanel topControls = new JPanel();
        topControls.setLayout(new BorderLayout());
        final ArrayList<URL> trail = new ArrayList<URL>();
        trail.add(createMainUrl(types, alreadyInstalled));
        final JButton backButton = new JButton();
        backButton.setIcon(new ImageIcon(
            Convergia.Icons.BACK_BUTTON_32.getImage()));
        topControls.add(backButton, BorderLayout.WEST);
        final JProgressBar pbar = new JProgressBar(0, 100);
        topControls.add(pbar, BorderLayout.CENTER);
        dialog.getContentPane().add(topControls,
            BorderLayout.NORTH);
        backButton.setEnabled(false);
        backButton.addActionListener(new ActionListener()
        {
            
            public void actionPerformed(ActionEvent e)
            {
                if (trail.size() <= 1)
                {
                    backButton.setEnabled(false);
                    return;
                }
                trail.remove(trail.size() - 1);
                if (trail.size() <= 1)
                {
                    backButton.setEnabled(false);
                }
                else
                {
                    backButton.setEnabled(true);
                }
                pbar.setIndeterminate(true);
                try
                {
                    p.setPage(trail.get(trail.size() - 1));
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                    setErrorPage(p);
                }
                pbar.setIndeterminate(false);
            }
        });
        ((HTMLEditorKit) p.getEditorKit())
            .setAutoFormSubmission(false);
        p.addHyperlinkListener(new HyperlinkListener()
        {
            
            public void hyperlinkUpdate(HyperlinkEvent e)
            {
                if (!p.isEnabled())
                    return;
                if (!e.getEventType().equals(
                    EventType.ACTIVATED))
                    return;
                final URL url = e.getURL();
                String urlString = e.getDescription();
                if (url == null
                    && urlString.startsWith("installtool:"))
                {
                    final String targetUrlString = urlString
                        .substring("installtool:".length());
                    try
                    {
                        final URL targetUrl = new URL(
                            targetUrlString);
                        // FIXME: add code for installing the tool specified and
                        // reloading the main page here
                        if (JOptionPane
                            .showConfirmDialog(
                                dialog,
                                "Are you sure you want to download and install this plugin?",
                                null,
                                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                        {
                            int slashIndex = targetUrl
                                .getPath().lastIndexOf("/");
                            final String newFileName = targetUrl
                                .getPath().substring(
                                    slashIndex + 1);
                            System.out
                                .println("newfilename is "
                                    + newFileName);
                            if ((!new File(
                                PluginManager.pluginFolder,
                                newFileName).exists())
                                || JOptionPane
                                    .showConfirmDialog(
                                        dialog,
                                        "This plugin is already installed. Would you like to reinstall it?",
                                        null,
                                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                            {
                                
                                new Thread()
                                {
                                    public void run()
                                    {
                                        try
                                        {
                                            p
                                                .setEnabled(false);
                                            pbar
                                                .setStringPainted(true);
                                            pbar
                                                .setString("Downloading plugin...");
                                            pbar
                                                .setIndeterminate(false);
                                            pbar
                                                .setMinimum(0);
                                            pbar
                                                .setValue(0);
                                            URLConnection connection = targetUrl
                                                .openConnection();
                                            int totalSize = connection
                                                .getContentLength();
                                            if (totalSize < 0)
                                                pbar
                                                    .setIndeterminate(true);
                                            else
                                                pbar
                                                    .setMaximum(totalSize / 1024);
                                            int countSoFar = 0;
                                            byte[] buffer = new byte[1024];
                                            int amount;
                                            InputStream stream = connection
                                                .getInputStream();
                                            File tmpfile = File
                                                .createTempFile(
                                                    "convergiadownload",
                                                    ".jar");
                                            tmpfile
                                                .deleteOnExit();
                                            FileOutputStream fos = new FileOutputStream(
                                                tmpfile);
                                            while ((amount = stream
                                                .read(buffer)) != -1)

                                            {
                                                fos.write(
                                                    buffer,
                                                    0,
                                                    amount);
                                                countSoFar += amount;
                                                if (totalSize >= 0)
                                                {
                                                    pbar
                                                        .setValue(countSoFar / 1024);
                                                    pbar
                                                        .repaint();
                                                }
                                            }
                                            fos.flush();
                                            fos.close();
                                            pbar
                                                .setValue(pbar
                                                    .getMaximum());
                                            pbar
                                                .setString("Installing plugin...");
                                            if (new File(
                                                PluginManager.pluginFolder,
                                                newFileName)
                                                .exists())
                                            {
                                                new File(
                                                    PluginManager.pluginFolder,
                                                    newFileName)
                                                    .delete();
                                            }
                                            tmpfile
                                                .renameTo(new File(
                                                    PluginManager.pluginFolder,
                                                    newFileName));
                                            JarFile jarfile = new JarFile(
                                                new File(
                                                    PluginManager.pluginFolder,
                                                    newFileName));
                                            Manifest manifest = jarfile
                                                .getManifest();
                                            Attributes attributes = manifest
                                                .getMainAttributes();
                                            Convergia
                                                .saveJarFile(
                                                    new File(
                                                        PluginManager.pluginFolder,
                                                        newFileName),
                                                    jarfile,
                                                    manifest);
                                            pbar
                                                .setString("");
                                            pbar
                                                .setValue(0);
                                            pbar
                                                .setMinimum(0);
                                            pbar
                                                .setMaximum(0);
                                            if (JOptionPane
                                                .showConfirmDialog(
                                                    dialog,
                                                    "<html>The plugin has been successfully installed.<br/>"
                                                        + "You will need to restart OpenGroove for the plugin to work.<br/>"
                                                        + "Would you like to restart OpenGroove now?",
                                                    null,
                                                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                                                Convergia
                                                    .restartConvergia();
                                            dialog
                                                .dispose();
                                        }
                                        catch (Exception e)
                                        {
                                            throw new RuntimeException(
                                                e);
                                        }
                                    }
                                }.start();
                            }
                        }
                    }
                    catch (Exception e1)
                    {
                        // TODO Feb 10, 2008 Auto-generated catch block
                        throw new RuntimeException(
                            "TODO auto generated on Feb 10, 2008 : "
                                + e1.getClass().getName()
                                + " - " + e1.getMessage(),
                            e1);
                    }
                }
                else
                // either form submit or link activate, set page to the url
                // specified
                {
                    new Thread()
                    {
                        public void run()
                        {
                            pbar.setIndeterminate(true);
                            System.out
                                .println("link activated, url is "
                                    + url);
                            trail.add(url);
                            if (trail.size() >= 2)
                                backButton.setEnabled(true);
                            try
                            {
                                p.setPage(url);
                            }
                            catch (IOException e1)
                            {
                                e1.printStackTrace();
                                setErrorPage(p);
                            }
                            pbar.setIndeterminate(false);
                        }
                    }.start();
                }
            }
        });
        p.setContentType("text/html");
        p
            .setText("<html><body align='center'><b>Please wait...</b></body></html>");
        new Thread()
        {
            public void run()
            {
                try
                {
                    Thread.sleep(500);
                    System.out.println("trail url is "
                        + trail.get(0));
                    p.setPage(trail.get(0));
                }
                catch (Exception ex1)
                {
                    ex1.printStackTrace();
                    setErrorPage(p);
                }
            }
        }.start();
        dialog
            .setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.show();
    }
    
    protected static void setErrorPage(JEditorPane p)
    {
        p.setContentType("text/html");
        p
            .setText("<html><body align='center'><b>An error has occured. Check to make sure "
                + "you are connected to the internet, and then "
                + "try again. If you are sure you are connected "
                + "to the internet, then we may be having technical "
                + "difficulties with our web server, so use the "
                + "contact us menu item on the help menu on the "
                + "launchbar to tell us about the problem.</b></body></html>");
    }
    
    private static URL createMainUrl(String[] types,
        String[] alreadyInstalled)
        throws MalformedURLException
    {
        String queryString = "?installedplugins="
            + URLEncoder.encode(Convergia.delimited(Arrays
                .asList(alreadyInstalled), ","));
        if (types != null)
            queryString += "&plugintypes="
                + URLEncoder.encode(Convergia.delimited(
                    Arrays.asList(types), ","));
        return new URL(PLUGIN_DOWNLOAD_URL + queryString);
    }
}
