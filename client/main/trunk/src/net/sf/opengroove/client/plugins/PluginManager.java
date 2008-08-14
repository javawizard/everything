package net.sf.opengroove.client.plugins;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import com.l2fprod.common.swing.JLinkButton;

import net.sf.opengroove.client.OpenGroove;
import net.sf.opengroove.client.Storage;
import net.sf.opengroove.client.SubversionFileFilter;
import net.sf.opengroove.client.UserIds;
import net.sf.opengroove.client.download.PluginDownloadManager;

/**
 * NOTES TO ME: the update site of a plugin is the URL that should be used to
 * update the plugin and get version information for it. this can be null, in
 * which case the plugin has no update site.
 * 
 * only external plugins have an update site. internal plugins don't, because
 * since their code is part of the main OpenGroove code, they get updated with
 * OpenGroove itself.
 * 
 * while OpenGroove is running, updates for plugins will be checked once every
 * day, or when the user comes online and it has been an hour or more before
 * last checking for plugins. in the future, the user will be allowed to change
 * this setting. anyway, the user can also manually initiate a check for
 * updates. when updates are detected, the user is presented with the list of
 * those updates, and they can choose which ones they would like to download and
 * install. they may also choose not to install a particular update, in which
 * case they will not be informed about updates for that plugin until the next
 * version is released. they may also choose to be reminded later of installing
 * a version of a plugin. if a newer version is released before the time
 * specified, the new version will still be shown to the user. anyway, the user
 * chooses which ones to download and install. these are downloaded in the
 * background, and a window showing the status of each can be displayed,
 * probably as an option on the right-click menu of the task tray icon. once all
 * updates are downloaded, a window pops up telling the user that the updates
 * have been downloaded, and that the user needs to restart OpenGroove to
 * install the updates. when OpenGroove starts the next time, it will detect
 * that their are updates that have been downloaded but not installed. it will
 * pop open a window showing the user the updates that have been downloaded, and
 * allows them to choose which ones to install right now. the updates are then
 * installed. also in this list are new plugins that the user downloaded while
 * OpenGroove was running the last time. anyway, when the user clicks the button
 * to install updates, the updates are installed, with a progress bar showing
 * each one being installed.
 * 
 * the update site for a plugin can be any URL understood by the JVM, and in the
 * future will be allowed to be an it3sr:// url (see it3srdesc.txt), but for now
 * usually should be HTTP. for testing plugins, this URL could be a file:// url.
 * when it is time to check for updates, or, for that matter, when downloading a
 * new plugin to install from directly within OpenGroove, the update url is
 * contacted. it should return a properties format file. it should have the
 * following keys: (UPDATE: any of the attributes suitable for the update jar
 * should be included in the update site, and they will override any values
 * already in the jar.)
 * 
 * versionindex: this is the version index. it is a number (parseable by
 * Integer.parseInt()), which increments for each version of the plugin
 * released. it doesn't nessecarily need to increment by one for each new
 * version, it could increment by 100 or even 5,128,493. anyway, if the version
 * index is larger in this properties file than the one we have on our system,
 * there have been new updates.
 * 
 * versionstring: this is a user-friendly version string. this could be "v1.6",
 * "Version 1.6", "Mustang", or any other string. this will be presented to the
 * user when downloading and installing updates. for example, the user might see
 * "Updates are available for MyCoolPlugin. you are running Version 1.5, and the
 * latest version is Version 1.6".
 * 
 * website: (optional) this is a url, usually HTTP, that is a website the user
 * can go to for more information about the current version. this should
 * generally be a version-specific url, for example,
 * http://www.example.com/mycoolplugin/1.6/info.html. if website is not present,
 * the website is not presented to the user.
 * 
 * url: this is the url at which the jar file for the plugin may be downloaded.
 * this url should be in a form that could be passed to new URL() on an average
 * java vm.
 * 
 * name: the name of this plugin. should be the same as in jar file.
 * 
 * description: (optional but highly reccomended) the description. should be the
 * same as in jar file.
 * 
 * currently, plugins cannot depend on one another. this means that if you have
 * Class1 in the jar for a plugin, and another plugin uses Class1, then the code
 * for Class1 must appear in that other jar file. furthermore, Class1 from one
 * jar file will not be type-compatible with Class1 from another jar file. This
 * is because each jar file is loaded using a separate URLClassLoader.
 * 
 * the jar manifest of each plugin should contain a few attributes. those are as
 * follows:
 * 
 * it3-type: this is the plugin type. there are 2 types built in to OpenGroove:
 * workspace and tool. other plugins may themselves declare plugin types. for
 * example, a tool plugin that is a document editor may use plugins of type
 * wysiwyg for the visual editor to use.
 * 
 * it3-class: this is the plugin class. for workspace plugins, this class would
 * extend Workspace. for tool plugins, this class would extend Tool.
 * 
 * it3-update-site: (optional) this specifies the url of an update site to use.
 * update sites are discussed above.
 * 
 * it3-version-index: (required if it3-update-site is used, not allowed
 * otherwise) this specifies the version index of this plugin. this is used when
 * checking for updates. the update site will be contacted, and if it reports a
 * higher version index than we currently have, the plugin will be updated. on
 * update systems where simultaniously changing the version index of the plugin
 * jar file and the version index of the update site file is not possible, the
 * jar version index should be changed first.
 * 
 * it3-version-string (required if it3-update-site is used, optional otherwise)
 * this specifies a user-presentable string that represents this version.
 * 
 * it3-name: the name of the plugin. this should be a very short description of
 * the plugin, such as "Document Editor Tool" or "File Sharing Workspace".
 * 
 * it3-description: (optional but highly recomended) a more lengthy description
 * of the plugin, usually not longer than a paragraph.
 * 
 * it3-large-icon: (optional) an icon, 64x64 pixels in size, that represents
 * this plugin.
 * 
 * it3-mmedium-icon: (optional) an icon, 32x32 pixels in size, that represents
 * this plugin.
 * 
 * it3-small-icon: (optional) an icon, 16x16 pixels in size, that represents
 * this plugin. right now, no scaling is done, so if a plugin chooses to have
 * any icons at all, it should have all. these attribues are the path, relative
 * to the plugin jar file, of the icon, in any format loadable by
 * ImageIO.read().
 * 
 * the above attribute names are for external plugins only. external plugins are
 * those contained within a jar. for internal plugins, plugins where the code is
 * on the classpath and the descriptor file (instead of a manifest file) is in
 * the folder internalplugins, the attributes are the same, except that they do
 * not have it3- on the beginning and they are studley caps instead of
 * hyphenated. for example, it3-large-icon would be largeIcon. updateSite and
 * versionIndex are not supported on internal plugins, as their code is updated
 * when the main OpenGroove system is updated. examples of internal plugins are
 * tool workspace and file sharing workspace, which are both plugins of type
 * workspace.<br/><br/>
 * 
 * <b>UPDATE:</b>Each user has their own plugin manager instance which manages
 * their personal plugins. The folder that stores the user's personal plugins
 * can be retrieved from the user's Storage instance. Internal plugins will be
 * instantiated once for each user.
 * 
 * @author Alexander Boyd
 * 
 */
public class PluginManager
{
    
    public static final File internalPluginFolder = new File(
        "internalplugins");
    
    private static Map<String, Plugin> pluginsById = new HashMap<String, Plugin>();
    
    private static ArrayList<String> failedPlugins = new ArrayList<String>();
    
    private static ArrayList<Plugin> disabledPlugins = new ArrayList<Plugin>();
    
    /*
     * for (File file : pluginFolder.listFiles()) { try { Properties p = new
     * Properties(); p.load(new FileInputStream(file)); String type =
     * p.getProperty("type"); String implClass = p.getProperty("class");
     * p.remove("type"); p.remove("class"); Class cz = Class.forName(implClass);
     * Plugin plugin = new Plugin(cz); plugin.setId(file.getName());
     * plugin.setImplClass(cz); plugin.setMetadata(p); plugin.setType(type);
     * pluginsById.put(plugin.getId(), plugin); ArrayList<Plugin> l =
     * pluginsByType.get(type); if (l == null) { l = new ArrayList<Plugin>();
     * pluginsByType.put(type, l); } l.add(plugin); } catch (Exception e) {
     * e.printStackTrace(); failedPlugins.add(file.getName()); } }
     */

    public static final String PLUGIN_EXTENTION = ".ogvp";
    
    private File pluginFolder;
    
    private Storage storage;
    
    private String userid;
    
    /**
     * Creates a plugin manager for the userid specified. Only one of these
     * should exist at a time.
     * 
     * @param userid
     */
    public PluginManager(String userid)
    {
        this.storage = Storage.get(UserIds.toRealm(userid),
            UserIds.toUsername(userid));
        pluginFolder = new File(storage.getPluginStore(),
            "code");
    }
    
    private boolean pluginsLoaded = false;
    
    public synchronized void loadPlugins()
    {
        if (pluginsLoaded)// already loaded
            return;
        /*
         * Here's what needs to happen in this order: We load the list of
         * plugins and parse each plugin's information. Then, we instantiate
         * each plugin's supervisor and initialize it. We then instantiate all
         * of the extension points and register them to their plugins. Once this
         * is done, we create all of the extensions and register them to their
         * plugins and to their extension points. (TODO: register to supervisor
         * or extension points first?) Then, once this is complete, we tell the
         * supervisors that their plugins have finished loading.
         * 
         * Plugin Infos should be provided via an ExtensionInfo to every
         * extension point, off of which plugin names, descrtiptions, and icons
         * can be retrieved.
         * 
         * When the plugin manager is created, it should be passed a user
         * context. It can get info about the user for it's plugins that way.
         * Messaging and contact retrival, however, should be implemented as
         * extension points themselves, as well as help. A plugin could register
         * an extension point to those services and be provided with a context
         * object that it could use to send messages to instances of itself on
         * another computer. At some future date, this could allow for the
         * workspace functionality, and maybe even the contact functionality, to
         * be split out into their own plugins, so that opengroove by itself
         * only provides the server connectivity and the wizard for establishing
         * an account, and everything else (such as the tray icon, the
         * launchbar, workspaces, contacts, the workspace tab in the launchbar,
         * the contacts tab, etc) would be a plugin. This would further allow
         * such applications as an Evaluation Portal (another project I'm
         * working on, see http://evaluationportal.com) laptop surveyer to be
         * built on top of OpenGroove, and use OpenGroove's communications to
         * sync it's data.
         */
        pluginsLoaded = true;
        if (!pluginFolder.exists())
            pluginFolder.mkdirs();
        for (File file : pluginFolder
            .listFiles(new FileFilter()
            {
                
                public boolean accept(File pathname)
                {
                    return pathname.getName().endsWith(
                        PLUGIN_EXTENTION);
                }
            }))
        {
            try
            {
                System.out
                    .println("loading external plugin");
                JarFile jarfile = new JarFile(file);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                failedPlugins.add(file.getName());
                //
            }
        }
        ArrayList<File> l2 = new ArrayList<File>();
        l2.addAll(Arrays.asList(internalPluginFolder
            .listFiles(new SubversionFileFilter())));
        if (new File("devplugins").exists())
            l2.addAll(Arrays.asList(new File("devplugins")
                .listFiles(new SubversionFileFilter())));
        for (File file : l2.toArray(new File[0]))
        {
            try
            {
                Properties p = new Properties();
                p.load(new FileInputStream(file));
                String type = p.getProperty("type");
                String implClass = p.getProperty("class");
                p.remove("type");
                p.remove("class");
                Class cz = Class.forName(implClass);
                Plugin plugin = new Plugin(cz);
                plugin.setId(file.getName());
                plugin.setImplClass(cz);
                plugin.setMetadata(p);
                plugin.setType(type);
                plugin.setInternal(true);
                String largeIconPath = p
                    .getProperty("largeIcon");
                String mediumIconPath = p
                    .getProperty("mediumIcon");
                String smallIconPath = p
                    .getProperty("smallIcon");
                if (largeIconPath != null
                    && mediumIconPath != null
                    && smallIconPath != null)
                {
                    plugin.setLargeImage(ImageIO
                        .read(new File(largeIconPath)));
                    plugin.setMediumImage(ImageIO
                        .read(new File(mediumIconPath)));
                    plugin.setSmallImage(ImageIO
                        .read(new File(smallIconPath)));
                }
                pluginsById.put(plugin.getId(), plugin);
                ArrayList<Plugin> l = pluginsByType
                    .get(type);
                if (l == null)
                {
                    l = new ArrayList<Plugin>();
                    pluginsByType.put(type, l);
                }
                l.add(plugin);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                failedPlugins.add(file.getName());
            }
        }
        
    }
    
    public static Plugin getById(String id)
    {
        return pluginsById.get(id);
    }
    
    public static ArrayList<Plugin> getByType(String type)
    {
        if (pluginsByType.get(type) == null)
            return new ArrayList<Plugin>();
        return pluginsByType.get(type);
    }
    
    public PluginUpdateSite downloadUpdateSite(Plugin plugin)
    {
        if (plugin == null)
            return null;
        if (plugin.getUpdateSite() == null)
            return null;
        return downloadUpdateSite(plugin.getUpdateSite());
    }
    
    public static Plugin[] getAllPlugins()
    {
        return pluginsById.values().toArray(new Plugin[0]);
    }
    
    public PluginUpdateSite downloadUpdateSite(
        URL updateSiteUrl)
    {
        if (updateSiteUrl == null)
            return null;
        try
        {
            InputStream in = updateSiteUrl.openStream();
            Properties properties = new Properties();
            properties.load(in);
            PluginUpdateSite site = new PluginUpdateSite();
            if (properties.getProperty("description") != null)
                site.setDescription(properties
                    .getProperty("description"));
            if (properties.getProperty("name") != null)
                site
                    .setName(properties.getProperty("name"));
            if (properties.getProperty("url") != null)
                site.setUrl(new URL(properties
                    .getProperty("url")));
            if (properties.getProperty("versionindex") != null)
                site.setVersionIndex(Integer
                    .parseInt(properties
                        .getProperty("versionindex")));
            if (properties.getProperty("versionstring") != null)
                site.setVersionString(properties
                    .getProperty("versionstring"));
            if (properties.getProperty("websiteurl") == null)
                properties.setProperty("websiteurl",
                    properties.getProperty("websiteUrl"));
            if (properties.getProperty("websiteurl") != null)
                site.setWebsiteUrl(new URL(properties
                    .getProperty("websiteurl")));
            return site;
        }
        catch (Exception ex1)
        {
            ex1.printStackTrace();
            return null;
        }
    }
    
    public static void showManageInstalledPluginsDialog(
        final JFrame parent)
    {
        final JDialog dialog = new JDialog(parent, true);
        dialog
            .setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        dialog.setTitle("Manage plugins - OpenGroove");
        dialog.setSize(600, 600);
        dialog.setLocationRelativeTo(parent);
        Container content = dialog.getContentPane();
        content.setLayout(new BorderLayout());
        JLinkButton downloadNewLink = new JLinkButton(
            "<html><font color='#0000ff'><u>Find new plugins</u></font>");
        downloadNewLink
            .setHorizontalAlignment(SwingConstants.RIGHT);// right-justify
        downloadNewLink.setFocusable(false);
        downloadNewLink
            .addActionListener(new ActionListener()
            {
                
                public void actionPerformed(ActionEvent e)
                {
                    dialog.hide();
                    OpenGroove.findNewPlugins(parent, null);
                }
            });
        content.add(
            OpenGroove.pad(downloadNewLink, 10, 10),
            BorderLayout.SOUTH);
        JPanel mainPanelWrapper = new JPanel();
        BorderLayout wrapperLayout = new BorderLayout();
        mainPanelWrapper.setLayout(wrapperLayout);
        JScrollPane mainPanelScrollPane = new JScrollPane(
            mainPanelWrapper);
        mainPanelScrollPane.setBorder(new CompoundBorder(
            new EmptyBorder(10, 10, 0, 10),
            new EtchedBorder()));
        content.add(mainPanelScrollPane);
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        mainPanelWrapper.add(p, BorderLayout.NORTH);
        JLabel eLabel = new JLabel(
            "<html><b><big>Enabled:</big></b>");
        JLabel dLabel = new JLabel(
            "<html><b><big>Disabled:</big></b>");
        JLabel nLabel = new JLabel(
            "<html><b><big>You have no plugins installed.<Br/>"
                + "Click find new plugins below to download plugins!</big></b>");
        eLabel.setHorizontalAlignment(0);
        dLabel.setHorizontalAlignment(0);
        nLabel.setHorizontalAlignment(0);
        Plugin[] enabledPlugins = getAllPlugins();
        Plugin[] disabledPlugins = PluginManager.disabledPlugins
            .toArray(new Plugin[0]);
        if (enabledPlugins.length == 0
            && disabledPlugins.length == 0)
        {
            p.add(nLabel);
        }
        if (enabledPlugins.length > 0)
        {
            p.add(eLabel);
            for (Plugin plugin : enabledPlugins)
            {
                JPanel pluginPanel = createPluginPanel(
                    dialog, plugin, true);
                p.add(pluginPanel);
            }
        }
        if (disabledPlugins.length > 0)
        {
            p.add(dLabel);
            for (Plugin plugin : disabledPlugins)
            {
                JPanel pluginPanel = createPluginPanel(
                    dialog, plugin, false);
                p.add(pluginPanel);
            }
        }
        new Thread()
        {
            public void run()
            {
                dialog.show();
                dialog.dispose();
            }
        }.start();
    }
    
    private static JPanel createPluginPanel(
        final Window parent, final Plugin plugin,
        boolean isEnabled)
    {
        JPanel pluginPanel = new JPanel();
        pluginPanel.setLayout(new BorderLayout());
        JPanel lowerPanel = new JPanel();
        lowerPanel.setLayout(new BorderLayout());
        pluginPanel.add(lowerPanel, BorderLayout.SOUTH);
        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new BorderLayout());
        pluginPanel.add(upperPanel, BorderLayout.NORTH);
        JLabel pluginName = new JLabel(plugin.getMetadata()
            .getProperty("name"));
        JLabel pluginDesc = new JLabel(plugin.getMetadata()
            .getProperty("description"));
        pluginDesc.setFont(pluginDesc.getFont().deriveFont(
            Font.PLAIN));
        JLabel pluginType = new JLabel(plugin.getType());
        // now set the first letter of the plugin type to be upper case
        // a plugin type will have at least one letter, so there is no
        // need
        // to check to make sure that getText() is 1 char or longer
        pluginType.setText(pluginType.getText().substring(
            0, 1).toUpperCase()
            + pluginType.getText().substring(1));
        if (pluginType.getText().equals("Lookandfeel"))
            pluginType.setText("Look and feel");
        upperPanel.add(pluginName, BorderLayout.WEST);
        upperPanel.add(pluginType, BorderLayout.EAST);
        pluginPanel.add(pluginDesc, BorderLayout.CENTER);
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(
            controlsPanel, BoxLayout.X_AXIS));
        lowerPanel.add(controlsPanel, BorderLayout.EAST);
        if (plugin.isInternal())
        {
            controlsPanel.add(new JLabel(
                "This is a built-in plugin"));
        }
        else
        {
            JButton otherControlButton2 = null;
            if (isEnabled)
            {
                JButton disableButton = new JButton(
                    "Disable");
                otherControlButton2 = disableButton;
                controlsPanel.add(disableButton);
            }
            else
            {
            }
            final JButton otherControlButton = otherControlButton2;
            final JButton uninstallButton = new JButton(
                "Uninstall");
            uninstallButton
                .addActionListener(new ActionListener()
                {
                    
                    public void actionPerformed(
                        ActionEvent e)
                    {
                        if (JOptionPane
                            .showConfirmDialog(
                                parent,
                                "<html>Are you sure you want to uninstall this<br/>"
                                    + "plugin? You should delete anything that uses<br/>"
                                    + "this plugin. For example, if this is a workspace<br/>"
                                    + "plugin, you should delete all workspaces of this plugin's type.",
                                null,
                                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                        {
                            try
                            {
                                JarFile jarfile = new JarFile(
                                    plugin.getFile());
                                Manifest manifest = jarfile
                                    .getManifest();
                                Attributes attributes = manifest
                                    .getMainAttributes();
                                attributes
                                    .putValue(
                                        "it3-uninstall",
                                        "true");
                                OpenGroove.saveJarFile(
                                    plugin.getFile(),
                                    jarfile, manifest);
                                JOptionPane
                                    .showMessageDialog(
                                        parent,
                                        "The plugin will be uninstalled the next time OpenGroove restarts.");
                            }
                            catch (Exception ex1)
                            {
                                ex1.printStackTrace();
                                JOptionPane
                                    .showMessageDialog(
                                        parent,
                                        "An error occured while uninstalling the plugin. Please restart OpenGroove.");
                            }
                            uninstallButton
                                .setEnabled(false);
                            if (otherControlButton != null)
                                otherControlButton
                                    .setEnabled(false);
                        }
                    }
                });
            controlsPanel.add(uninstallButton);
        }
        pluginPanel.setBorder(new CompoundBorder(
            new CompoundBorder(new EmptyBorder(3, 6, 3, 6),
                new LineBorder(Color.GRAY)),
            new EmptyBorder(5, 5, 5, 5)));
        return pluginPanel;
    }
}
