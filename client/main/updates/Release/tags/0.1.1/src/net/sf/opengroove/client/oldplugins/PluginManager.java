package net.sf.opengroove.client.oldplugins;

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
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
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

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.l2fprod.common.swing.JLinkButton;

import net.sf.opengroove.client.OpenGroove;
import net.sf.opengroove.client.UserContext;
import net.sf.opengroove.client.storage.Storage;
import net.sf.opengroove.client.storage.SubversionFileFilter;
import net.sf.opengroove.common.utils.Userids;

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
 * day, or when the user comes online and it has been a day or more before
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
    /**
     * The folder that contains the plugin files for all of the internal
     * plugins. There should be one file within this folder for each plugin. The
     * file's name is the plugin's id plus .xml .
     */
    public static final File internalPluginFolder = new File(
        "internalplugins");
    
    private ArrayList<PluginModel> failedPlugins = new ArrayList<PluginModel>();
    
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
    /**
     * The folder that holds all of the .ogvp files for the user's installed
     * plugins.
     */
    private File pluginFolder;
    /**
     * The folder that holds the configuration for all of the user's installed
     * plugins. It contains one folder for each installed plugin (including
     * internal plugins), who's name is the plugin's id. For example, if
     * pluginFolder contains 2 files, com.example.myplugin.ogvp and
     * com.example.anotherplugin.ogvp, and there are 2 files present in
     * internalFolder, i_test.xml and i_another.xml, then dataFolder will
     * contain the folders com.example.myplugin, com.example.anotherplugin,
     * i_test, and i_another.
     */
    private File dataFolder;
    /**
     * The folder that contains the language packs for all of the plugins. Under
     * it, it has one folder for each external plugin, and a folder called
     * i_opengroove that holds OpenGroove's language packs. Within each of these
     * folders, there is one file for each language pack, which is the name of
     * the language pack followed by the extension .xml . When the plugin
     * manager is created, it copies all language packs out of the plugin jar
     * files into the paths within this folder just mentioned. It then loads all
     * of the language packs into LanguagePack objects, and assigns them to
     * their plugins. When the user wants to edit one of these language packs, a
     * new one is created from it, and prefixed with the name "user_". Language
     * packs that do not start with "user_" cannot be edited by the user, and
     * must be copied to another one that starts with "user_" before editing.
     */
    private File languageFolder;
    /**
     * The storage object for the user that this PluginManager belongs to.
     */
    private Storage storage;
    
    private UserContext userContext;
    
    /**
     * Creates a plugin manager for the user context specified. Only one of
     * these should exist at a time.
     * 
     * @param userid
     */
    public PluginManager(UserContext userContext)
    {
        this.userContext = userContext;
        this.storage = Storage.get(userContext.getUserid());
        pluginFolder = new File(storage.getPluginStore(),
            "code");
    }
    
    private boolean pluginsLoaded = false;
    
    private Plugin[] plugins;
    
    public synchronized void loadPlugins() throws Exception
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
        ArrayList<PluginModel> pluginModelList = new ArrayList<PluginModel>();
        for (File file : internalPluginFolder
            .listFiles(new FilenameFilter()
            {
                
                @Override
                public boolean accept(File dir, String name)
                {
                    return name.endsWith(".xml");
                }
            }))
        {
            PluginModel model = new PluginModel();
            model.setConfigFile(file);
            model.setId(file.getName()
                .substring(
                    0,
                    file.getName().length()
                        - (".xml".length())));
            model.setInternal(true);
            try
            {
                loadModel(new FileInputStream(file), model);
                pluginModelList.add(model);
            }
            catch (Exception e)
            {
                StringWriter sw = new StringWriter();
                e
                    .printStackTrace(new PrintWriter(sw,
                        true));
                model.setFailureReason(sw.toString());
                failedPlugins.add(model);
            }
        }
        for (File file : pluginFolder
            .listFiles(new FilenameFilter()
            {
                
                @Override
                public boolean accept(File dir, String name)
                {
                    return name.endsWith(".ogvp");
                }
            }))
        {
            PluginModel model = new PluginModel();
            JarFile jarfile = new JarFile(file);
            model.setJarFile(jarfile);
            model.setJarSource(file);
            model.setId(file.getName().substring(
                0,
                file.getName().length()
                    - (".ogvp".length())));
            if (model.getId().startsWith("i_"))
                throw new RuntimeException(
                    "The plugin's id starts with i_, "
                        + "but it is not an internal "
                        + "plugin. This is not allowed.");
            model.setInternal(false);
            try
            {
                loadModel(jarfile.getInputStream(jarfile
                    .getEntry("plugin.xml")), model);
                pluginModelList.add(model);
            }
            catch (Exception e)
            {
                StringWriter sw = new StringWriter();
                e
                    .printStackTrace(new PrintWriter(sw,
                        true));
                model.setFailureReason(sw.toString());
                failedPlugins.add(model);
            }
        }
        // Ok, we've loaded all of the models. Now we create plugin objects for
        // each model, attach the model to the plugin object, and instantiate
        // it's supervisor.
        PluginModel[] models = pluginModelList
            .toArray(new PluginModel[0]);
        Plugin[] plugins = new Plugin[models.length];
        for (int i = 0; i < plugins.length; i++)
        {
            plugins[i] = new Plugin();
            plugins[i].setModel(models[i]);
            if (!plugins[i].getModel().isInternal())
            {
                PluginClassLoader loader = new PluginClassLoader(
                    plugins, plugins[i]);
                plugins[i].setClassLoader(loader);
            }
            PluginContext context = new PluginContext(
                plugins[i], this);
            plugins[i].setContext(context);
        }
        /*
         * The following step is separate from the step above it so that all
         * plugin class loaders are loaded even before we start initializing.
         * This way, plugin supervisor classes can reference plugin dependencies
         * without throwing a ClassNotFoundException.
         */
        for (int i = 0; i < plugins.length; i++)
        {
            ClassLoader loader = plugins[i]
                .getClassLoader();
            if (loader == null)
                loader = getClass().getClassLoader();
            Supervisor supervisor = ((Class<? extends Supervisor>) Class
                .forName(models[i].getSupervisorClass(),
                    true, loader)).newInstance();
            plugins[i].setSupervisor(supervisor);
            supervisor.init(plugins[i].getContext());
        }
        // The supervisors have been instantiated and initialized. Now we go
        // through and instantiate all of the extension points and register them
        // to their supervisors. The extension point instances will be stored in
        // the ExtensionPointContext, which is in turn stored in the plugin, so
        // that the extension point can be retrieved from the plugin when it
        // comes time to register the extension point's extensions.
        for (int i = 0; i < plugins.length; i++)
        {
            Plugin plugin = plugins[i];
            ExtensionPointModel[] pointModels = plugin
                .getModel().getExtensionPoints();
            ExtensionPointContext[] pointContexts = new ExtensionPointContext[pointModels.length];
            plugin.setExtensionPoints(pointContexts);
            for (int p = 0; p < pointModels.length; p++)
            {
                pointContexts[p] = new ExtensionPointContext();
                String className = pointModels[p]
                    .getExtensionPointClass();
                ClassLoader loader = plugin
                    .getClassLoader();
                if (loader == null)
                    loader = getClass().getClassLoader();
                Class<ExtensionPoint> pointClass = (Class<ExtensionPoint>) Class
                    .forName(className, true, loader);
                ExtensionPoint point = pointClass
                    .newInstance();
                pointContexts[p].setExtensionPoint(point);
                pointContexts[p].setModel(pointModels[p]);
                pointContexts[p].setPluginContext(plugin
                    .getContext());
                pointContexts[p].setSupervisor(plugin
                    .getSupervisor());
                point.init(pointContexts[p]);
                plugin.getSupervisor()
                    .registerExtensionPoint(point);
            }
        }
        /*
         * We've loaded all of the extension points and registered them to the
         * plugin supervisors. Now we create the extensions, and register them
         * to the extension point that they specify.
         */
        for (int i = 0; i < plugins.length; i++)
        {
            Plugin plugin = plugins[i];
            ExtensionModel[] extensionModels = plugin
                .getModel().getExtensions();
            ExtensionContext[] extensionContexts = new ExtensionContext[extensionModels.length];
            plugin.setExtensions(extensionContexts);
            for (int p = 0; p < extensionModels.length; p++)
            {
                extensionContexts[p] = new ExtensionContext();
                String className = extensionModels[p]
                    .getExtensionClass();
                ClassLoader loader = plugin
                    .getClassLoader();
                if (loader == null)
                    loader = getClass().getClassLoader();
                Class<Extension> extensionClass = (Class<Extension>) Class
                    .forName(className, true, loader);
                Extension extension = extensionClass
                    .newInstance();
                extensionContexts[p]
                    .setExtension(extension);
                extensionContexts[p]
                    .setModel(extensionModels[p]);
                extensionContexts[p]
                    .setPluginContext(plugin.getContext());
                extensionContexts[p].setSupervisor(plugin
                    .getSupervisor());
                extension.init(extensionContexts[p]);
                plugin.getSupervisor()
                    .registerLocalExtension(extension);
                /*
                 * Now we find the extension point that this extension should be
                 * registered to, and register it.
                 */
                String targetPlugin = extensionModels[p]
                    .getPlugin();
                String targetPoint = extensionModels[p]
                    .getPoint();
                for (Plugin testPlugin : plugins)
                {
                    if (testPlugin.getModel().getId()
                        .equals(targetPlugin))
                    {
                        for (ExtensionPointContext testPoint : testPlugin
                            .getExtensionPoints())
                        {
                            if (testPoint.getModel()
                                .getId()
                                .equals(targetPoint))
                            {
                                testPoint
                                    .getExtensionPoint()
                                    .registerExtension(
                                        new PluginInfo(
                                            testPlugin),
                                        new ExtensionInfo(
                                            extensionContexts[p]),
                                        extension);
                            }
                        }
                    }
                }
                /*
                 * We've now registered the extension with it's extension point.
                 */
            }
        }
        /*
         * Everything's registered. Now we call ready() on all of the
         * supervisors, and we're done.
         */
        for (Plugin p : plugins)
        {
            p.getSupervisor().ready();
        }
        this.plugins = plugins;
        /*
         * That's it. We've successfully set up the plugins.
         * 
         * TODO: currently, if an individual plugin throws an exception when
         * initializing, none of the plugins will initialize. This needs to be
         * changed.
         */
    }
    
    public Plugin[] getAllPlugins()
    {
        return plugins;
    }
    
    public Plugin getById(String id)
    {
        return getPluginById(id);
    }
    
    public Plugin getPluginById(String id)
    {
        for (Plugin plugin : plugins)
        {
            if (plugin.getModel().getId().equals(id))
                return plugin;
        }
        return null;
    }
    
    private void loadModel(InputStream file,
        PluginModel model) throws IOException
    {
        try
        {
            Document doc = new SAXBuilder().build(file);
            Element root = doc.getRootElement();
            if (!root.getName().equals("plugin"))
                throw new IOException(
                    "The root of the plugin descriptor "
                        + "was not a <plugin> tag");
            String name = root.getAttributeValue("name");
            String description = root
                .getAttributeValue("description");
            String license = root
                .getAttributeValue("license");
            String supervisorClass = root
                .getAttributeValue("class");
            if (supervisorClass == null)
                supervisorClass = EmptySupervisor.class
                    .getName();
            String updateSite = root
                .getAttributeValue("update-site");
            checkPresent(name);
            checkPresent(supervisorClass);
            model.setName(name);
            model.setDescription(description);
            model.setLicense(license);
            model.setSupervisorClass(supervisorClass);
            model.setUpdateSite(updateSite);
            Element[] permissionNodes = (Element[]) root
                .getChildren("permission").toArray(
                    new Element[0]);
            PermissionModel[] permissions = new PermissionModel[permissionNodes.length];
            for (int i = 0; i < permissions.length; i++)
            {
                permissions[i] = new PermissionModel();
                permissions[i].setName(permissionNodes[i]
                    .getAttributeValue("name"));
                permissions[i]
                    .setDescription(permissionNodes[i]
                        .getAttributeValue("description"));
                permissions[i]
                    .setRequired(permissionNodes[i]
                        .getAttributeValue("required") != null);
            }
            Element[] iconNodes = (Element[]) root
                .getChildren("icon")
                .toArray(new Element[0]);
            IconModel[] icons = new IconModel[iconNodes.length];
            for (int i = 0; i < icons.length; i++)
            {
                icons[i] = new IconModel();
                icons[i].setName(iconNodes[i]
                    .getAttributeValue("name"));
                icons[i].setLocation(iconNodes[i]
                    .getAttributeValue("location"));
                icons[i].setWidth(Integer
                    .parseInt(iconNodes[i]
                        .getAttributeValue("width")));
                icons[i].setHeight(Integer
                    .parseInt(iconNodes[i]
                        .getAttributeValue("height")));
            }
            Element[] extensionPointNodes = (Element[]) root
                .getChildren("extension-point").toArray(
                    new Element[0]);
            ExtensionPointModel[] extensionPoints = new ExtensionPointModel[extensionPointNodes.length];
            for (int i = 0; i < extensionPoints.length; i++)
            {
                extensionPoints[i] = new ExtensionPointModel();
                extensionPoints[i]
                    .setId(extensionPointNodes[i]
                        .getAttributeValue("id"));
                extensionPoints[i]
                    .setExtensionInterface(extensionPointNodes[i]
                        .getAttributeValue("interface"));
                extensionPoints[i]
                    .setExtensionPointClass(extensionPointNodes[i]
                        .getAttributeValue("class"));
            }
            Element[] extensionNodes = (Element[]) root
                .getChildren("extension").toArray(
                    new Element[0]);
            ExtensionModel[] extensions = new ExtensionModel[extensionNodes.length];
            for (int i = 0; i < extensionNodes.length; i++)
            {
                extensions[i] = new ExtensionModel();
                extensions[i].setId(extensionNodes[i]
                    .getAttributeValue("id"));
                extensions[i].setPlugin(extensionNodes[i]
                    .getAttributeValue("plugin"));
                extensions[i]
                    .setExtensionClass(extensionNodes[i]
                        .getAttributeValue("class"));
                extensions[i].setPoint(extensionNodes[i]
                    .getAttributeValue("point"));
                Element[] properties = (Element[]) extensionNodes[i]
                    .getChildren("property").toArray(
                        new Element[0]);
                for (int p = 0; p < properties.length; p++)
                {
                    extensions[i]
                        .getExtensionPointProperties()
                        .put(
                            properties[p]
                                .getAttributeValue("name"),
                            properties[p]
                                .getAttributeValue("value"));
                }
                Element[] extensionProperties = (Element[]) extensionNodes[i]
                    .getChildren("extension-property")
                    .toArray(new Element[0]);
                for (int p = 0; p < extensionProperties.length; p++)
                {
                    extensions[i]
                        .getExtensionProperties()
                        .put(
                            extensionProperties[p]
                                .getAttributeValue("name"),
                            extensionProperties[p]
                                .getAttributeValue("value"));
                }
            }
            Element[] dependencyNodes = (Element[]) root
                .getChildren("dependency").toArray(
                    new Element[0]);
            DependencyModel[] dependencies = new DependencyModel[dependencyNodes.length];
            for (int i = 0; i < dependencies.length; i++)
            {
                dependencies[i] = new DependencyModel();
                dependencies[i]
                    .setPlugin(dependencyNodes[i]
                        .getAttributeValue("plugin"));
                dependencies[i]
                    .setDetails(dependencyNodes[i]
                        .getAttributeValue("details"));
                dependencies[i]
                    .setRequired(dependencyNodes[i]
                        .getAttributeValue("required") != null);
                dependencies[i]
                    .setUpdateSite(dependencyNodes[i]
                        .getAttributeValue("update-site"));
            }
            model.setDependencies(dependencies);
            model.setExtensionPoints(extensionPoints);
            model.setExtensions(extensions);
            model.setIcons(icons);
            model.setPermissions(permissions);
        }
        catch (IOException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new IOException(e);
        }
    }
    
    private void checkPresent(String versionString)
    {
        if (versionString == null)
            throw new IllegalArgumentException(
                "Invalid null encountered");
    }
    
    File getDataFolder()
    {
        return dataFolder;
    }
    
    /**
     * Shows a dialog on top of the user's launchbar that allows them to manage
     * the plugins that they have installed. Changes to installed plugins won't
     * take effect until OpenGroove is restarted.
     */
    public static void showManageInstalledPluginsDialog()
    {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * Opens a dialog on top of the window specified that allows the user to
     * browse their update sites for new plugins, and configure new update sites
     * if they want.
     * 
     * @param frame
     */
    public void promptForDownload(Window frame)
    {
    }
}
