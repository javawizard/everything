package net.sf.opengroove.client.features;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.opengroove.client.OpenGroove;
import net.sf.opengroove.client.Storage;
import net.sf.opengroove.client.com.MessageSink;
import net.sf.opengroove.client.plugins.Plugin;
import net.sf.opengroove.client.plugins.PluginManager;

public class FeatureManager
{
	private static ArrayList<Feature> features = new ArrayList<Feature>();

	private static HashMap<String, FeatureComponentHandler> handlers = new HashMap<String, FeatureComponentHandler>();

	private static class ComponentPair
	{
		private String handlerId;

		private Object component;
	}

	public static synchronized void loadFeatures()
	{
		if (features.size() > 0)// already loaded features
			return;
		Plugin<Feature>[] plugins = PluginManager.getByType("feature").toArray(
				new Plugin[]
				{});
		for (Plugin<Feature> plugin : plugins)
		{
			try
			{
				Feature feature = plugin.create();
				feature.setPluginMetadata(plugin.getMetadata());
				feature.setTypeId(plugin.getId());
				feature.setCommunicator(OpenGroove.ocom);
				features.add(feature);
				feature.initialize();
			} catch (Exception ex1)
			{
				ex1.printStackTrace();
			}
		}
		OpenGroove.ocom.addSink(new MessageSink()
		{

			public void process(String from, String message)
			{
				if (!message.startsWith("fm|"))// message not intended for
					// FeatureManager
					return;
				processMessage(from, message);
			}
		});
	}

	public static void processMessage(String from, String message)
	{
		if (message.startsWith("f|"))
		{
			message = message.substring("f|".length());
			String featureId = message.split("\\|", 2)[0];
			message = message.substring(featureId.length() + 1);
			for (Feature feature : features)
			{
				if (feature.typeId.equals(featureId))
					feature.receiveMessage0(from, message);
			}
		}
	}

	static File getBaseStorageFolder()
	{
		return new File(Storage.getFeatureStorage(), "features");
	}

	public static synchronized void registerComponentHandler(String id,
			FeatureComponentHandler handler)
	{
		handlers.put(id.toLowerCase(), handler);
		loadToHandlers();
	}

	static synchronized <T> void registerComponent(String handlerId, T component)
	{
		ComponentPair pair = new ComponentPair();
		pair.handlerId = handlerId.toLowerCase();
		pair.component = component;
		pendingRegistrations.add(pair);
		loadToHandlers();
	}

	private static ArrayList<ComponentPair> pendingRegistrations = new ArrayList<ComponentPair>();

	private static synchronized void loadToHandlers()
	{
		for (ComponentPair pair : new ArrayList<ComponentPair>(
				pendingRegistrations))
		{
			FeatureComponentHandler handler = handlers.get(pair.handlerId
					.toLowerCase());
			if (handler == null)// not registered yet
				continue;
			try
			{
				handler.registerComponent(pair.handlerId.toLowerCase(),
						pair.component);
			} catch (Exception ex1)
			{
				ex1.printStackTrace();
			}
			pendingRegistrations.remove(pair);
		}
	}
}
