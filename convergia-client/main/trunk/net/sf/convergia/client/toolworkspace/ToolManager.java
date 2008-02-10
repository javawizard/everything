package net.sf.convergia.client.toolworkspace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import net.sf.convergia.client.plugins.Plugin;
import net.sf.convergia.client.plugins.PluginManager;
import net.sf.convergia.client.workspace.SetList;


public class ToolManager
{
	private ToolWorkspace workspace;

	WorkspaceStorage storage;

	List<ToolWrapper> tools = new SetList<ToolWrapper>();

	private List<String> warnedNXTools = new SetList<String>();

	private Object toolSyncLock = new Object();

	/**
	 * DO NOT START THIS FOR THE CREATOR OF THIS WORKSPACE!!!!!
	 */
	private Thread toolSyncThread = new Thread()
	{
		public void run()
		{
			while (true)
			{
				try
				{
					Thread.sleep(1 * 1000);
					String wInfo = workspace.getInfo(workspace.getCreator());
					if (wInfo != null)
					{
						synchronized (workspace)
						{
							checkNeedsGetNewTools(wInfo);
						}
					}
					synchronized (toolSyncLock)
					{
						toolSyncLock.wait(290 * 1000);
					}
				} catch (Exception ex1)
				{
					ex1.printStackTrace();
				}
			}
		}
	};

	private Thread toolSendThread = new Thread()
	{
		public void run()
		{
			while (true)
			{
				try
				{
					Thread.sleep(1000);
					try
					{
						workspace.buildAndSetInfo();
					} catch (Exception ex1)
					{
						ex1.printStackTrace();
					}
					Thread.sleep(300 * 1000);
				} catch (Exception ex1)
				{
					ex1.printStackTrace();
				}
			}
		}
	};

	public ToolWrapper[] list()
	{
		return tools.toArray(new ToolWrapper[0]);
	}

	protected synchronized void checkNeedsGetNewTools(String info)
	{
		synchronized (workspace)
		{
			System.out.println("*****CHECK NEEDS GET NEW TOOLS "
					+ workspace.getId());
			String infoFirstPart = info;
			if (info.indexOf("|||") != -1)
				infoFirstPart = info.substring(0, info.indexOf("|||"));
			String[] infoParts = infoFirstPart.split("\\|\\|");
			if (infoParts.length == 1 && infoParts[0].equals(""))
				infoParts = new String[0];
			ArrayList<ToolWrapper> remoteTools = new ArrayList<ToolWrapper>();
			ToolWrapper[] storageTools = storage.listTools();
			List<ToolWrapper> storageToolsList = new ArrayList<ToolWrapper>(Arrays.asList(storageTools));
			for (String part : infoParts)
			{
				String[] subparts = part.split("\\|");
				String toolId = subparts[0];
				String toolType = subparts[1];
				int toolIndex = Integer.parseInt(subparts[2]);
				String toolName = subparts[3];
				ToolWrapper w = new ToolWrapper();
				w.setId(toolId);
				w.setIndex(toolIndex);
				w.setName(toolName);
				w.setTypeId(toolType);
				remoteTools.add(w);
				if (!storageToolsList.contains(w))
				{
					storage.addOrUpdateTool(w);
					storageToolsList.add(w);
					try
					{
						reloadTools();
					} catch (Exception ex1)
					{
						ex1.printStackTrace();
					}
				}
				try
				{
					ToolWrapper memw = getById(toolId);
					memw.setIndex(toolIndex);
					memw.setName(toolName);
				} catch (Exception ex1)
				{
					ex1.printStackTrace();
				}
			}
			ArrayList<ToolWrapper> deletedTools = new ArrayList<ToolWrapper>(
					storageToolsList);
			deletedTools.removeAll(remoteTools);
			// at this point, deletedTools will have all tools that exist here
			// but
			// not on the creator's computer
			for (ToolWrapper w : deletedTools)
			{
				Tool tool = w.getTool();
				try
				{
					tool.shutdown();
				} catch (Exception ex1)
				{
					ex1.printStackTrace();
				}
				try
				{
					storage.recursiveDelete(w.getDatastore());
				} catch (Exception ex1)
				{
					ex1.printStackTrace();
				}
				storage.removeTool(w.getId());
				tools.remove(w);
			}
			System.out.println("*****DONE CHECK NEEDS GET NEW TOOLS");
			workspace.checkNeedsUpdateTabs();
			System.out.println("*****DONE DELEGATE TABS CHECKING");
		}
	}

	private ToolWrapper getById(String toolId)
	{
		System.out.println("getting tool by id " + toolId + " and there are "
				+ tools.size() + " tools to search in");
		for (ToolWrapper w : new ArrayList<ToolWrapper>(tools))
		{
			if (w.getId().equals(toolId))
				return w;
		}
		System.out.println("did not find tool");
		return null;
	}

	public ToolWorkspace getWorkspace()
	{
		return workspace;
	}

	public synchronized void reloadTools()
	{
		ToolWrapper[] fsTools = storage.listTools();
		System.out.println("found " + fsTools.length + " tools in storage");
		for (ToolWrapper fst : fsTools)
		{
			if (!tools.contains(fst))
			{
				loadTool(fst);
			}
		}
		workspace.checkNeedsUpdateTabs();
	}

	private void loadTool(ToolWrapper fst)
	{
		Plugin<Tool> plugin = PluginManager.getById(fst.getTypeId());
		if (plugin == null)
		{
			// FIXME: show the user an error message saying that they do not
			// have this tool's plugin installed, and they need to install it.
			// in the future, an option could be added to download the tool
			// plugin from another user who has it, at which point the plugin
			// would be sent from another user who is signed on and has the
			// plugin, if one exists, in multiple messages.
			if (warnedNXTools.contains(fst.getTypeId()))
			{

			} else
			{
				warnedNXTools.add(fst.getTypeId());
				JOptionPane
						.showMessageDialog(
								workspace.frame,
								"<html>Another user has added a tool, but you do not have the<br/>"
										+ "plugin for that tool installed on your computer. the plugin<br/>"
										+ "id of the tool is "
										+ fst.getTypeId()
										+ " . please download<br/>"
										+ "and install this tool. if you need help, send an<br/>"
										+ "email to webmaster@trivergia.com<br/><br/>"
										+ "this message will not be shown again for this<br/>"
										+ "tool until you restart Convergia.");
			}
			return;
		}
		fst.setManager(this);
		fst.setPluginMetadata(plugin.getMetadata());
		Tool tool = plugin.create();
		fst.setTool(tool);
		tool.setWrapper(fst);
		try
		{
			tool.initialize();
		} catch (Exception ex1)
		{
			ex1.printStackTrace();
		}
		tools.add(fst);
	}

	public void shutdown()
	{
		try
		{
			toolSyncThread.stop();
		} catch (Exception ex1)
		{
			ex1.printStackTrace();
		}
		try
		{
			toolSendThread.stop();
		} catch (Exception ex1)
		{
			ex1.printStackTrace();
		}
		for (ToolWrapper t : tools)
		{
			try
			{
				t.getTool().shutdown();
			} catch (Exception ex1)
			{
				ex1.printStackTrace();
			}
		}
	}

	public ToolManager(ToolWorkspace workspace, WorkspaceStorage storage)
	{
		this.workspace = workspace;
		this.storage = storage;
		if (workspace.isCreator())
			toolSendThread.start();
		else
			toolSyncThread.start();
	}

	public void processMessage(String from, String message)
	{
		if (message.startsWith("toolmanager|"))
			message = message.substring("toolmanager|".length());
		if (message.startsWith("toolmessage|"))
		{
			String nm = message.substring("toolmessage|".length());
			String toolId = nm.substring(0, nm.indexOf("|"));
			String toolMessage = nm.substring(nm.indexOf("|") + 1);
			if (getById(toolId) != null)
				try
				{
					getById(toolId).getTool().receiveMessage(from, toolMessage);
				} catch (Exception ex1)
				{
					ex1.printStackTrace();
				}
		} else if (message.equals("reloadtoolsfromcreator")
				&& from.equals(workspace.getCreator())
				&& !from.equals(workspace.getUsername()))// if the message is
		// reloadtoolsfromcreator
		// AND the message
		// was sent from the
		// creator AND we
		// are not the
		// creator (in case
		// we accidentally
		// send the message
		// to ourselves)
		{
			synchronized (toolSyncLock)
			{
				toolSyncLock.notifyAll();
			}
		}
	}
}
