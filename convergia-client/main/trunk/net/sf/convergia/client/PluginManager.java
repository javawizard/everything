package net.sf.convergia.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;

import javax.imageio.ImageIO;

/**
 * NOTES TO ME: the update site of a plugin is the URL that should be used to
 * update the plugin and get version information for it. this can be null, in
 * which case the plugin has no update site.
 * 
 * only external plugins have an update site. internal plugins don't, because
 * since their code is part of the main Convergia code, they get updated with
 * Convergia itself.
 * 
 * while Convergia is running, updates for plugins will be checked once every
 * hour, or when the user comes online and it has been an hour or more before
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
 * have been downloaded, and that the user needs to restart Convergia to install
 * the updates. when Convergia starts the next time, it will detect that their
 * are updates that have been downloaded but not installed. it will pop open a
 * window showing the user the updates that have been downloaded, and allows
 * them to choose which ones to install right now. the updates are then
 * installed. also in this list are new plugins that the user downloaded while
 * Convergia was running the last time. anyway, when the user clicks the button
 * to install updates, the updates are installed, with a progress bar showing
 * each one being installed.
 * 
 * the update site for a plugin can be any URL understood by the JVM, and in the
 * future will be allowed to be an it3sr:// url (see it3srdesc.txt), but for now
 * usually should be HTTP. for testing plugins, this URL could be a file:// url.
 * when it is time to check for updates, or, for that matter, when downloading a
 * new plugin to install from directly within Convergia, the update url is
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
 * it3-type: this is the plugin type. there are 2 types built in to Convergia:
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
 * when the main Convergia system is updated. examples of internal plugins are
 * tool workspace and file sharing workspace, which are both plugins of type
 * workspace.
 * 
 * @author Alexander Boyd
 * 
 */
public class PluginManager
{
	public static final File pluginFolder = new File("plugins");

	public static final File internalPluginFolder = new File("internalplugins");

	private static Map<String, ArrayList<Plugin>> pluginsByType = new HashMap<String, ArrayList<Plugin>>();

	private static Map<String, Plugin> pluginsById = new HashMap<String, Plugin>();

	private static ArrayList<String> failedPlugins = new ArrayList<String>();

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

	public static final String PLUGIN_EXTENTION = ".jar";

	public static final File UPDATE_DOWNLOAD_FOLDER = new File(Convergia.sfile,
			"pluginupdates");
	static
	{
		UPDATE_DOWNLOAD_FOLDER.mkdirs();
	}

	/**
	 * checks for updates for all of the external plugins currently installed.
	 * if no new updates are found, this method will return. if autoDownload is
	 * false, the user will then be prompted to select the ones to download.
	 * this method will block while the user is doing so. then, after prompting,
	 * the updates selected, or all updates if autoDownload is true, will be
	 * downloaded, one at a time. if showProgress is true, a (non-closeable)
	 * window will pop open, displaying the progress of downloading the updates.
	 * updates will be downloaded to a temporary folder, then moved to
	 * UPDATE_DOWNLOAD_FOLDER. this means that if Convergia exits or crashes
	 * while updates are being downloaded, only completed updates will be
	 * installed when Convergia starts the next time. after updates have
	 * finished downloading, the progress window is disposed, and if
	 * notifyOnComplete is true, a dialog opens on top of the Convergia
	 * launchbar telling the user that updates have been successfully downloaded
	 * and the user should restart Convergia to install the updates. right now,
	 * if downloading updates fails because of a problem such as no internet
	 * connection, 404 when the server was contacted, etc. this method will
	 * treat this situation as if the plugin had no new updates available. it
	 * will not show an error message to the user.
	 * 
	 */
	public static void downloadPluginUpdates(boolean autoDownload,
			boolean showProgress, boolean notifyOnComplete)
	{
		HashMap<Plugin, Properties> pluginsWithUpdates = new HashMap<Plugin, Properties>();
		// map of plugins to be updated and the properties file corresponding to
		// the
		// download from the update site
		for (Plugin p : pluginsById.values())
		{
			System.out.println("checking for plugin " + p.getId());
			if (p.getUpdateSite() == null)
				continue;
			try
			{
				System.out.println("plugin has an update site");
				URL updateUrl = p.getUpdateSite();
				Properties updateProperties = new Properties();
				updateProperties.load(updateUrl.openStream());
				int localVersionIndex = p.getVersionIndex();
				int remoteVersionIndex = Integer.parseInt(updateProperties
						.getProperty("versionindex"));
				if (localVersionIndex >= remoteVersionIndex)// we already have
					// the latest
					// version
					continue;
				// if we get here then we do not have the latest version so we
				// should update
				System.out.println("plugin needs to be updated");
				pluginsWithUpdates.put(p, updateProperties);
			} catch (Exception ex1)
			{
				ex1.printStackTrace();
			}
		}
		HashMap<Plugin, Properties> confirmedPlugins;
		if (autoDownload)
			confirmedPlugins = pluginsWithUpdates;
		else
			confirmedPlugins = askUserAboutDownloading(pluginsWithUpdates);
		for (Plugin p : pluginsWithUpdates.keySet())
		{
			Properties up = pluginsWithUpdates.get(p);
		}
	}

	private static HashMap<Plugin, Properties> askUserAboutDownloading(
			HashMap<Plugin, Properties> pluginsWithUpdates)
	{
		// PluginUpdateDialog dialog = new
		// PluginUpdateDialog(Convergia.launchbar);
		return pluginsWithUpdates;
	}

	/**
	 * installs any updates that have been downloaded using
	 * downloadPluginUpdates. this cannot be called after loadPlugins has been
	 * called. if there are no plugins to install, this method returns.
	 * 
	 * @param autoInstall
	 * @param showProgress
	 */
	public static void installPluginUpdates(boolean autoInstall,
			boolean showProgress)
	{
		if (pluginsLoaded)
			throw new IllegalStateException(
					"loadPlugins() has already been called, installPluginUpdates "
							+ "cannot be called after loadPlugins is called");
	}

	private static boolean pluginsLoaded = false;

	static void loadPlugins()
	{
		pluginsLoaded = true;
		if (!pluginFolder.exists())
			pluginFolder.mkdirs();
		for (File file : pluginFolder.listFiles(new FileFilter()
		{

			public boolean accept(File pathname)
			{
				// TODO Auto-generated method stub
				return pathname.getName().endsWith(PLUGIN_EXTENTION);
			}
		}))
		{
			try
			{
				System.out.println("loading external plugin");
				JarFile jarfile = new JarFile(file);
				Manifest manifest = jarfile.getManifest();
				Attributes attributes = manifest.getMainAttributes();
				System.out.println("attributes:");
				for (Object s : attributes.keySet())
				{
					System.out.println("k:|" + s + "|");
					System.out.println(("" + s).startsWith("it3-"));
					System.out.println(("" + s).equals("it3-type"));
					System.out.println("v:|" + attributes.getValue("" + s)
							+ "|");
				}
				System.out.println("contains a type attributes");
				String type = attributes.getValue("it3-type");
				if (type == null)
				{
					System.out.println("type was null");
					continue;
				}
				System.out.println("which is " + type);
				String implClass = attributes.getValue("it3-class");
				URLClassLoader loader = new URLClassLoader(new URL[]
				{ file.getAbsoluteFile().toURI().toURL() }, PluginManager.class
						.getClassLoader());
				Class cz = Class.forName(implClass, true, loader);
				Plugin plugin = new Plugin(cz);
				plugin.setId(file.getName().substring(0,
						file.getName().length() - PLUGIN_EXTENTION.length()));
				plugin.setImplClass(cz);
				String updateSite = attributes.getValue("it3-update-site");
				if (updateSite != null)
					plugin.setUpdateSite(new URL(updateSite));
				String versionIndex = attributes.getValue("it3-version-index");
				if (versionIndex == null && updateSite != null)
					throw new RuntimeException(
							"The plugin declares an update site but not a version index");
				else if (versionIndex != null)
					plugin.setVersionIndex(Integer.parseInt(versionIndex));
				String largeIconPath = attributes.getValue("it3-large-icon");
				String mediumIconPath = attributes.getValue("it3-medium-icon");
				String smallIconPath = attributes.getValue("it3-small-icon");
				if (largeIconPath != null && mediumIconPath != null
						&& smallIconPath != null)
				{
					plugin.setLargeImage(ImageIO.read(jarfile
							.getInputStream(jarfile.getEntry(largeIconPath))));
					plugin.setMediumImage(ImageIO.read(jarfile
							.getInputStream(jarfile.getEntry(mediumIconPath))));
					plugin.setSmallImage(ImageIO.read(jarfile
							.getInputStream(jarfile.getEntry(smallIconPath))));
				}
				Properties p = new Properties();
				for (Object oKey : attributes.keySet())
				{
					Name key = (Name) oKey;
					if (!key.toString().startsWith("it3-"))
						continue;
					p.setProperty(key.toString().substring(4), attributes
							.getValue(key));
				}
				plugin.setMetadata(p);
				plugin.setType(type);
				pluginsById.put(plugin.getId(), plugin);
				ArrayList<Plugin> l = pluginsByType.get(type);
				if (l == null)
				{
					l = new ArrayList<Plugin>();
					pluginsByType.put(type, l);
				}
				l.add(plugin);
			} catch (Exception e)
			{
				e.printStackTrace();
				failedPlugins.add(file.getName());
			}
		}

		for (File file : internalPluginFolder
				.listFiles(new SubversionFileFilter()))
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
				String largeIconPath = p.getProperty("largeIcon");
				String mediumIconPath = p.getProperty("mediumIcon");
				String smallIconPath = p.getProperty("smallIcon");
				if (largeIconPath != null && mediumIconPath != null
						&& smallIconPath != null)
				{
					plugin.setLargeImage(ImageIO.read(new File(largeIconPath)));
					plugin.setMediumImage(ImageIO
							.read(new File(mediumIconPath)));
					plugin.setSmallImage(ImageIO.read(new File(smallIconPath)));
				}
				pluginsById.put(plugin.getId(), plugin);
				ArrayList<Plugin> l = pluginsByType.get(type);
				if (l == null)
				{
					l = new ArrayList<Plugin>();
					pluginsByType.put(type, l);
				}
				l.add(plugin);
			} catch (Exception e)
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

	public PluginUpdateSite downloadUpdateSite(URL updateSiteUrl)
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
				site.setDescription(properties.getProperty("description"));
			if (properties.getProperty("name") != null)
				site.setName(properties.getProperty("name"));
			if (properties.getProperty("url") != null)
				site.setUrl(new URL(properties.getProperty("url")));
			if (properties.getProperty("versionindex") != null)
				site.setVersionIndex(Integer.parseInt(properties
						.getProperty("versionindex")));
			if (properties.getProperty("versionstring") != null)
				site.setVersionString(properties.getProperty("versionstring"));
			if (properties.getProperty("websiteurl") != null)
				properties.setProperty("websiteurl", properties
						.getProperty("websiteUrl"));
			if (properties.getProperty("websiteurl") != null)
				site
						.setWebsiteUrl(new URL(properties
								.getProperty("websiteurl")));
			return site;
		} catch (Exception ex1)
		{
			ex1.printStackTrace();
			return null;
		}
	}
}
