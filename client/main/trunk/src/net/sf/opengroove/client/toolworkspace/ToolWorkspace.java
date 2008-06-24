package net.sf.opengroove.client.toolworkspace;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.TextArea;
import java.awt.TextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import net.sf.opengroove.client.OpenGroove;
import net.sf.opengroove.client.UserInformationLink;
import net.sf.opengroove.client.notification.TaskbarNotification;
import net.sf.opengroove.client.plugins.Plugin;
import net.sf.opengroove.client.plugins.PluginManager;
import net.sf.opengroove.client.workspace.SetList;
import net.sf.opengroove.client.workspace.Workspace;
import net.sf.opengroove.utils.ItemChooser;

import com.l2fprod.common.swing.JLinkButton;

/**
 * A workspace that allows you to add tools to it. these tools show up as tabs
 * in a tabbed pane and can communicate with their same tool on another
 * computer, much the same way that workspaces can communicate with their same
 * workspace on another computer.
 * 
 * all of the protected methods from Workspace are overriden and call super.XXX,
 * to increase visibility from protected to public so that other classes, such
 * as ToolWrapper, can call those methods on the ToolWorkspace.
 * 
 * @author Alexander Boyd
 * 
 */
public class ToolWorkspace extends Workspace
{
	@Override
	public void addNotification(TaskbarNotification notification,
			boolean requestDisplay)
	{
		// TODO Auto-generated method stub
		super.addNotification(notification, requestDisplay);
	}

	@Override
	public void broadcastMessage(String message)
	{
		// TODO Auto-generated method stub
		super.broadcastMessage(message);
	}

	@Override
	public TaskbarNotification[] listNotifications()
	{
		// TODO Auto-generated method stub
		return super.listNotifications();
	}

	@Override
	public void removeNotification(TaskbarNotification notification)
	{
		// TODO Auto-generated method stub
		super.removeNotification(notification);
	}

	@Override
	public void requestMessagingCapacity()
	{
		// TODO Auto-generated method stub
		super.requestMessagingCapacity();
	}

	@Override
	public String getProperty(String key)
	{
		// TODO Auto-generated method stub
		return super.getProperty(key);
	}

	@Override
	public String[] listProperties()
	{
		// TODO Auto-generated method stub
		return super.listProperties();
	}

	@Override
	public String[] listProperties(String prefix)
	{
		// TODO Auto-generated method stub
		return super.listProperties(prefix);
	}

	@Override
	public void setProperty(String key, String value)
	{
		// TODO Auto-generated method stub
		super.setProperty(key, value);
	}

	private static final String VERSION_NUMBER = "1.1";

	WorkspaceFrame frame;

	ToolManager manager;

	private WorkspaceStorage storage;

	private static int tixidx = 1;

	private static int thidx = 1;

	private ThreadGroup threads = new ThreadGroup("ToolWorkspaceThreads"
			+ tixidx++);

	private Thread creatorInfoUpdaterThread = new Thread(threads, "twst"
			+ thidx++)
	{
		public void run()
		{
			while (true)
			{
				try
				{
					refreshUsersDisplayList();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				try
				{
					Thread.sleep(30 * 1000);
				} catch (Exception ex1)
				{
					ex1.printStackTrace();
				}
			}
		}
	};

	private File chatStorageFile;

	@Override
	public String getInfo(String user)
	{
		// TODO Auto-generated method stub
		return super.getInfo(user);
	}

	@Override
	public boolean isOnline()
	{
		// TODO Auto-generated method stub
		return super.isOnline();
	}

	@Override
	public boolean isUnnamed()
	{
		// TODO Auto-generated method stub
		return super.isUnnamed();
	}

	@Override
	public void setInfo(String info)
	{
		// TODO Auto-generated method stub
		super.setInfo(info);
	}

	@Override
	public void shutdown()
	{
		// the thread.stops here are not good practice, they should be changed
		if (creatorInfoUpdaterThread.isAlive())
			creatorInfoUpdaterThread.stop();
		try
		{
			manager.shutdown();
		} catch (Exception ex1)
		{
			ex1.printStackTrace();
		}
		frame.dispose();
	}

	@Override
	public String getCreator()
	{
		// TODO Auto-generated method stub
		return super.getCreator();
	}

	@Override
	public String getId()
	{
		// TODO Auto-generated method stub
		return super.getId();
	}

	@Override
	public String getMyVersion()
	{
		// TODO Auto-generated method stub
		return super.getMyVersion();
	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return super.getName();
	}

	@Override
	public String getRegisteredType()
	{
		// TODO Auto-generated method stub
		return super.getRegisteredType();
	}

	@Override
	public File getStorageFile()
	{
		// TODO Auto-generated method stub
		return super.getStorageFile();
	}

	@Override
	public String getUsername()
	{
		// TODO Auto-generated method stub
		return super.getUsername();
	}

	@Override
	public boolean isCreator()
	{
		// TODO Auto-generated method stub
		return super.isCreator();
	}

	@Override
	public String[] listAllowedUsers()
	{
		// TODO Auto-generated method stub
		return super.listAllowedUsers();
	}

	@Override
	public String[] listOfflineUsers()
	{
		// TODO Auto-generated method stub
		return super.listOfflineUsers();
	}

	@Override
	public String[] listOnlineUsers()
	{
		// TODO Auto-generated method stub
		return super.listOnlineUsers();
	}

	@Override
	public String[] listUsers()
	{
		// TODO Auto-generated method stub
		return super.listUsers();
	}

	@Override
	public void sendMessage(String to, String message)
	{
		// TODO Auto-generated method stub
		super.sendMessage(to, message);
	}

	@Override
	public void setAttentionStatus(boolean attention)
	{
		// TODO Auto-generated method stub
		super.setAttentionStatus(attention);
	}

	@Override
	public void setName(String name)
	{
		// TODO Auto-generated method stub
		super.setName(name);
	}

	@Override
	public void setNewInformationStatus(boolean newInfo)
	{
		// TODO Auto-generated method stub
		super.setNewInformationStatus(newInfo);
	}

	@Override
	public boolean showConfigurationWindow()
	{
		// TODO Auto-generated method stub
		return super.showConfigurationWindow();
	}

	@Override
	public void configurationCancelled()

	{
		// TODO Auto-generated method stub

	}

	@Override
	public void configurationSaved()
	{

		setFrameTitle();
	}

	private void setFrameTitle()
	{
		frame.setTitle(getName() + " - OpenGroove");
		frame.getChatPopoutFrame().setTitle(
				"Chat - " + getName() + " - OpenGroove");
	}

	@Override
	public Map<String, JComponent> getConfigurationComponents()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize()
	{
		frame = new WorkspaceFrame();
		frame.setIconImage(OpenGroove.getWindowIcon());
		creatorInfoUpdaterThread.start();
		setFrameTitle();
		frame.getChatPopOutButton().setText("");
		frame.getChatPopOutButton().setIcon(
				new ImageIcon(OpenGroove.Icons.POP_OUT_16.getImage()));
		frame.invalidate();
		frame.validate();
		frame.repaint();
		frame.setLocationRelativeTo(null);
		frame.getConfigureWorkspaceCommonTask().addActionListener(
				new ActionListener()
				{

					public void actionPerformed(ActionEvent e)
					{
						showConfigurationWindow(frame);
					}
				});
		frame.getAddToolsCommonTask().addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				if (!isCreator())
				{
					JOptionPane
							.showMessageDialog(frame,
									"Currently, you must be the workspace creator to add tools.");
					return;
				}
				final Plugin[] plugins = PluginManager.getByType("tool")
						.toArray(new Plugin[0]);
				final String[] pluginStrings = new String[plugins.length];
				for (int i = 0; i < plugins.length; i++)
				{
					String s = "<html><b>"
							+ plugins[i].getMetadata().getProperty("name")
							+ "</b><br/>";
					s += plugins[i].getMetadata().getProperty("description")
							.replace("<html>", "");
					pluginStrings[i] = s;
				}
				Plugin p = ItemChooser.showItemChooser(frame, "Choose the type of tool you want to add.", plugins,
						pluginStrings, true);
				if (p == null)
					return;
				ToolWrapper w = new ToolWrapper();
				w.setId(OpenGroove.generateId());
				w.setDatastore(new File(storage.getToolDatastore(), w.getId()
						+ "-" + System.currentTimeMillis()));
				w.setIndex(0);// not implemented yet
				w
						.setName(p.getMetadata().getProperty("name") == null ? "unnamed tool"
								: p.getMetadata().getProperty("name"));
				w.setTypeId(p.getId());
				try
				{
					storage.addOrUpdateTool(w);
					try
					{
						manager.reloadTools();
					} catch (Exception ex1)
					{
						ex1.printStackTrace();
					}
					checkNeedsUpdateTabs();
					buildAndSetInfo();
					for (String u : listOnlineUsers())
						try
						{
							if (!u.equals(getUsername()))
								sendMessage(u,
										"toolmanager|reloadtoolsfromcreator");
						} catch (Exception ex1)
						{
							ex1.printStackTrace();
						}
				} catch (Exception e2)
				{
					e2.printStackTrace();
				}
				JOptionPane.showMessageDialog(frame,
						"The tool was successfully created.");

			}
		});
		frame.getManageToolsCommonTask().addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				if (!isCreator())
				{
					JOptionPane
							.showMessageDialog(frame,
									"Currently, you must be the workspace creator to manage tools.");
					return;
				}
				JOptionPane.showMessageDialog(frame,
						"This operation is not supported right now.");
			}

			ManageToolsDialog dialog = new ManageToolsDialog(frame);
		});
		frame.getFindNewToolsCommonTask().addActionListener(
				new ActionListener()
				{

					public void actionPerformed(ActionEvent e)
					{
						OpenGroove.findNewPlugins(frame, new String[]
						{ "tool" });
					}
				});
		frame.getAdvancedOptionsCommonTask().addActionListener(
				new ActionListener()
				{

					public void actionPerformed(ActionEvent e)
					{
						JOptionPane.showMessageDialog(frame,
								"This operation is not supported right now.");
					}
				});
		frame.getDeleteToolCommonTask().addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				System.out.println("DELETE CLICKED");
				if (!isCreator())
				{
					JOptionPane
							.showMessageDialog(frame,
									"Currently, you must be the workspace creator to delete tools.");
					return;
				}
				PoppableTabbedPane t = frame.getToolsTabbedPane();
				if (t.getTabComponentAt(t.getSelectedIndex()) instanceof ToolComponentWrapper)
				{
					ToolComponentWrapper tcw = (ToolComponentWrapper) t
							.getTabComponentAt(t.getSelectedIndex());
					ToolWrapper w = tcw.getWrapper();
					if (JOptionPane.showConfirmDialog(frame,
							"Are you sure you want to delete the tool "
									+ w.getName()
									+ "? This operation cannot be undone.") != JOptionPane.YES_OPTION)
						return;
					Tool tool = w.getTool();
					try
					{
						tool.shutdown();
					} catch (Exception ex1)
					{
						ex1.printStackTrace();
					}
					storage.recursiveDelete(w.getDatastore());
					storage.removeTool(w.getId());
					manager.tools.remove(w);
					try
					{
						buildAndSetInfo();
					} catch (Exception ex1)
					{
						ex1.printStackTrace();
					}
					checkNeedsUpdateTabs();
					try
					{
						for (String u : listOnlineUsers())
						{
							try
							{
								if (!u.equals(getUsername()))
									sendMessage(u,
											"toolmanager|reloadtoolsfromcreator");
							} catch (Exception ex1)
							{
								ex1.printStackTrace();
							}
						}
					} catch (Exception ex1)
					{
						ex1.printStackTrace();
					}
					JOptionPane.showMessageDialog(frame,
							"The tool has been successfully deleted.");
				}
			}
		});
		frame.getRenameToolCommonTask().addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				if (!isCreator())
				{
					JOptionPane
							.showMessageDialog(frame,
									"Currently, you must be the workspace creator to rename a tool.");
					return;
				}
				PoppableTabbedPane t = frame.getToolsTabbedPane();
				if (t.getTabComponentAt(t.getSelectedIndex()) instanceof ToolComponentWrapper)
				{
					ToolComponentWrapper tcw = (ToolComponentWrapper) t
							.getTabComponentAt(t.getSelectedIndex());
					ToolWrapper w = tcw.getWrapper();
					String newName = JOptionPane.showInputDialog(frame,
							"Type the new name for the tool " + w.getName(), w
									.getName());
					if (newName == null)
						return;
					if (newName.equals(""))
						newName = " ";
					w.setName(newName);
					w.getTool().save();
					try
					{
						buildAndSetInfo();
					} catch (Exception ex1)
					{
						ex1.printStackTrace();
					}
					checkNeedsUpdateTabs();
					try
					{
						for (String u : listOnlineUsers())
						{
							try
							{
								if (!u.equals(getUsername()))
									sendMessage(u,
											"toolmanager|reloadtoolsfromcreator");
							} catch (Exception ex1)
							{
								ex1.printStackTrace();
							}
						}
					} catch (Exception ex1)
					{
						ex1.printStackTrace();
					}
					JOptionPane.showMessageDialog(frame,
							"The tool has been successfully renamed.");
				}
			}
		});
		ActionListener sendChatListener = new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				String text = frame.getChatTextField().getText().trim();
				if (text.length() > 0)
				{
					try
					{
						String chatMessageId = ""
								+ OpenGroove.com.getServerTime() + "-"
								+ getUsername();
						ChatMessage message = new ChatMessage();
						message.setId(chatMessageId);
						message.setMessage(text);
						storage.addChatMessage(message);
						try
						{
							setProperty("chat-" + message.getId(), message
									.getMessage());
							trimServerMessagesToSize();
						} catch (Exception ex1)
						{
							ex1.printStackTrace();
						}
						receiveMessage(getUsername(), "chatmessage|"
								+ message.getTransmitString());
						broadcastMessage("chatmessage|"
								+ message.getTransmitString());
					} catch (Exception ex1)
					{
						ex1.printStackTrace();
					}
				}
				frame.getChatTextField().setText("");
			}
		};
		frame.getSendChatButton().addActionListener(sendChatListener);
		frame.getChatTextField().addActionListener(sendChatListener);
		frame.getConfigureWorkspaceCommonTask().setFocusable(false);
		frame.getAddToolsCommonTask().setFocusable(false);
		frame.getManageToolsCommonTask().setFocusable(false);
		frame.getFindNewToolsCommonTask().setFocusable(false);
		frame.getAdvancedOptionsCommonTask().setFocusable(false);
		frame.getDeleteToolCommonTask().setFocusable(false);
		frame.getRenameToolCommonTask().setFocusable(false);
		frame.getSendChatButton().setFocusable(false);
		storage = new WorkspaceStorage(new File(getStorageFile(), "storage"));
		manager = new ToolManager(this, storage);
		manager.reloadTools();
		checkNeedsUpdateTabs();
		// frame.show();
		frame.invalidate();
		frame.validate();
		frame.repaint();
		frame.invalidate();
		frame.validate();
		frame.repaint();
		frame.hide();
		try
		{
			storage.trimChatMessagesToMaxLength();
			ChatMessage[] messages = storage.listAllChatMessages();
			for (ChatMessage message : messages)
			{
				frame.getChatTextArea().append(message.getDisplayMessage());
			}
		} catch (Exception ex1)
		{
			ex1.printStackTrace();
		}
	}

	protected void trimServerMessagesToSize()
	{
		String[] messageKeys = listProperties("chat-");
		Arrays.sort(messageKeys);
		System.out.println("" + messageKeys.length + " messages to start with");
		int attempt = 0;
		while (messageKeys.length > storage.MAX_CHAT_MESSAGES_LENGTH)
		{
			if (attempt++ > 10)
			{
				System.out.println("max attempts exceeded with "
						+ messageKeys.length + " messages still remaining");
				return;
			}
			setProperty(messageKeys[0], null);
			messageKeys = listProperties("chat-");
			Arrays.sort(messageKeys);
		}
	}

	private static final int MAX_CHAT_FIELD_LENGTH = 50 * 1000;

	@Override
	public void receiveMessage(String from, String message)
	{
		if (message.startsWith("toolmanager|"))
		{
			manager.processMessage(from, message);
		} else if (message.equals("version"))
		{
			sendMessage(from, "" + VERSION_NUMBER);
		} else if (message.startsWith("chatmessage|"))
		{
			/*
			 * NOTES TO MYSELF ON CHAT:
			 * 
			 * whenever someone sends a chat message, the chat message should be
			 * broadcast in the usual manner, except that the text of the
			 * message, besides the leading chatmessage|, should contain the id
			 * of the chat message, a pipe, and the text of the chat message.
			 * for chat messages, the id is the current server date, a hyphen,
			 * and the username of the user who sent the message. these messages
			 * are cached in a folder called TBD in the workspace storage file.
			 * every TBD (Consider: 5) minutes, or when the user clicks some
			 * sort of advanced option, the list of chat messages is downloaded
			 * from the server. this is then compared with the local copy. any
			 * ones on the server are downloaded and injected into the local
			 * copy. any local ones created by this user (our username is the
			 * last part of the message id) are uploaded, and a message sent out
			 * telling everyone to reload their messages instead of waiting TBD
			 * (see above) minutes for this to recur. anyway, if any remote
			 * messages were downloaded, the entire chat window is cleared and
			 * refreshed with the messages. only 800 messages will be cached on
			 * the server and in the local copy. after that, the ones with the
			 * earliest id, ordered lexicographically so as to order by
			 * ascending date, are deleted. when a message is entered by a user,
			 * the message is first sent to everyone, then uploaded, then stored
			 * in the local cache. if the user is offline, then inherently only
			 * the last step will be completed. above, where it says that we
			 * will check every TBD (Consider: 5) minutes, this is not entirely
			 * true. at the end of those 5 minutes, if we are offline, then we
			 * try to check once every TBD (consider: 45) seconds to see if we
			 * are online. if we are at this point, then we perform the check
			 * and possibly upload, and then go back to 5 minutes.
			 */
			JTextArea chatArea = frame.getChatTextArea();
			String chatMessageString = message.substring("chatmessage|"
					.length());
			ChatMessage chatMessage = new ChatMessage();
			String[] split = chatMessageString.split("\\|", 2);
			chatMessage.setId(split[0]);
			chatMessage.setMessage(split[1]);
			try
			{
				storage.addChatMessage(chatMessage);
			} catch (Exception ex1)
			{
				ex1.printStackTrace();
			}
			String toAdd = chatMessage.getDisplayMessage();
			if (chatArea.getText().length() > MAX_CHAT_FIELD_LENGTH)
				chatArea.setText(chatArea.getText().substring(
						chatArea.getText().length() - MAX_CHAT_FIELD_LENGTH));
			chatArea.append(toAdd);
			chatArea.setCaretPosition(chatArea.getDocument().getLength());
			if (frame.getChatPopoutFrame().isShowing())
				OpenGroove.bringToFront(frame.getChatPopoutFrame());
			else
				OpenGroove.bringToFront(frame);
			if (!getUsername().equals(from))
			{
				frame.getChatPanel().setBorder(
						new CompoundBorder(new EtchedBorder(
								EtchedBorder.RAISED, Color.RED.brighter(),
								Color.RED.darker()),
								new EmptyBorder(1, 1, 1, 1)));
				new Thread()
				{
					private boolean isBorderDarker = false;

					public void run()
					{
						while (true)
						{
							try
							{
								Thread.sleep(1000);
							} catch (InterruptedException e)
							{
								e.printStackTrace();
							}
							if (frame.getChatPanel().hasFocus()
									|| frame.getChatScrollPane().hasFocus()
									|| frame.getChatTextArea().hasFocus()
									|| frame.getChatTextField().hasFocus()
									|| frame.getChatGroup().hasFocus()
									|| frame.getSendChatButton().hasFocus())
								break;
							if (isBorderDarker)
							{
								isBorderDarker = false;
								frame.getChatPanel().setBorder(
										new CompoundBorder(new EtchedBorder(
												EtchedBorder.RAISED, Color.RED
														.brighter(), Color.RED
														.darker()),
												new EmptyBorder(1, 1, 1, 1)));
							} else
							{
								isBorderDarker = true;
								frame.getChatPanel().setBorder(
										new CompoundBorder(new EtchedBorder(
												EtchedBorder.RAISED, Color.CYAN
														.brighter(), Color.CYAN
														.darker()),
												new EmptyBorder(1, 1, 1, 1)));
							}
						}
						frame.getChatPanel().setBorder(null);
					}
				}.start();
			}
		}
	}

	@Override
	public void userActivate()
	{
		frame.show();
	}

	@Override
	public void userStatusChanged()
	{
		refreshUsersDisplayList();
	}

	private static final int MAX_FS_CHAT_MESSAGES = 5000;

	private static final int MAX_WINDOW_CHAT_MESSAGES = 500;

	public void buildAndSetInfo()
	{
		if (!isCreator())
			throw new RuntimeException(
					"If you are not the creator, you can't call buildAndSetInfo");
		if (!isOnline())// we are not online (so we can't send info to the
			// server) so just return
			return;
		StringBuilder infoBuilder = new StringBuilder();
		StringBuilder toolsBuilder = new StringBuilder();
		boolean f = true;
		for (ToolWrapper w : manager.list())
		{
			if (!f)
				toolsBuilder.append("||");
			f = false;
			String toolString = OpenGroove.delimited(Arrays.asList(new String[]
			{ w.getId(), w.getTypeId(), "" + w.getIndex(),
					w.getName().length() == 0 ? " " : w.getName() }), "|");
			toolsBuilder.append(toolString);
		}
		infoBuilder.append(toolsBuilder.toString());
		setInfo(infoBuilder.toString());
	}

	public synchronized void checkNeedsUpdateTabs()
	{
		System.out.println("*****CHECK NEEDS UPDATE TABS " + getId());
		boolean wereTabsChanged = false;
		List<ToolWrapper> tools = Arrays.asList(manager.list());
		// okTools is the tools (after the for statement that follows) that are
		// already in the tabbed pane
		System.out.println("" + tools.size() + " tools retreived from storage");
		ArrayList<ToolWrapper> okTools = new ArrayList<ToolWrapper>();
		System.out.println("currently, there are "
				+ frame.getToolsTabbedPane().getTabCount() + " tabs showing");
		for (int i = 0; i < frame.getToolsTabbedPane().getTabCount(); i++)
		{
			if (frame.getToolsTabbedPane().getTabComponentAt(i) == null)
			// this fixes some wierd bug i was having where the tabs would be
			// null
			{
				System.out.println("null tab " + i);
				frame.getToolsTabbedPane().removeTabAt(i);
				i--;
				wereTabsChanged = true;
			} else if (frame.getToolsTabbedPane().getTabComponentAt(i) instanceof ToolComponentWrapper)
			{
				System.out.println("tab is an instanceof toolcomponentwrapper");
				ToolComponentWrapper wp = (ToolComponentWrapper) frame
						.getToolsTabbedPane().getTabComponentAt(i);
				if ((!tools.contains(wp.getWrapper()))
						|| okTools.contains(wp.getWrapper()))// if the tab is
				// not a tool
				// (IE that tool
				// has been
				// deleted) OR
				// the tab is a
				// duplicate
				{
					System.out.println("need to remove this tab");
					frame.getToolsTabbedPane().removeTabAt(i);
					// now subtract 1 from i since we just removed this tab
					i--;
					wereTabsChanged = true;
				} else
				{
					System.out.println("this tab is OK");
					okTools.add(wp.getWrapper());
					// now check to see if the name has changed
					if (!frame.getToolsTabbedPane().getTitleAt(i).equals(
							wp.getWrapper().getName()))
					{
						wereTabsChanged = true;
						frame.getToolsTabbedPane().setTitleAt(i,
								wp.getWrapper().getName());
						((JLabel) ((JPanel) wp.getComponent(0)).getComponent(0))
								.setText(wp.getWrapper().getName());
					}
				}
			} else
			{
				System.out
						.println("tab is not null and not an instance of tab component wrapper, it is of class "
								+ frame.getToolsTabbedPane().getTabComponentAt(
										i));
			}
		}
		System.out.println("" + okTools.size() + " ok tabs");
		SetList<ToolWrapper> needToAdd = new SetList<ToolWrapper>(tools);
		needToAdd.removeAll(okTools);
		System.out.println("need to add " + needToAdd.size() + " tools");
		for (ToolWrapper w : needToAdd)
		{
			Plugin plugin = PluginManager.getById(w.getTypeId());
			Image smallImage = plugin.getSmallImage();
			JLabel toolLabel;
			toolLabel = new JLabel(w.getName());
			if (smallImage != null)
				toolLabel.setIcon(new ImageIcon(smallImage));
			toolLabel.setName("label");
			JPanel toolLabelPanel = new JPanel();
			toolLabelPanel.setLayout(new BorderLayout());
			toolLabelPanel.add(toolLabel, BorderLayout.CENTER);
			JLinkButton popLabel = new JLinkButton(new ImageIcon(
					OpenGroove.Icons.POP_OUT_16.getImage()));
			popLabel.setFocusable(false);
			popLabel.setBorder(new EmptyBorder(1, 1, 1, 1));
			toolLabelPanel.setOpaque(false);
			toolLabelPanel.add(popLabel, BorderLayout.EAST);
			ToolComponentWrapper wp = new ToolComponentWrapper(toolLabelPanel,
					w);
			final ToolWrapper wf = w;
			popLabel.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					PoppableTabbedPane tp = getFrame().getToolsTabbedPane();
					int index = -1;
					for (int i = 0; i < tp.getTabCount(); i++)
					{
						if (tp.getTabComponentAt(i) instanceof ToolComponentWrapper
								&& ((ToolComponentWrapper) tp
										.getTabComponentAt(i)).getWrapper()
										.getId().equals(wf.getId()))
						{
							index = i;
							break;
						}
					}
					if (index == -1)
						throw new RuntimeException(
								"The tool is not a member of any tool workspace");
					tp.pop(index);
				}
			});

			System.out.println("adding tab");
			wereTabsChanged = true;
			frame.getToolsTabbedPane().addTab(w.getName(),
					w.getTool().getComponent());
			frame.getToolsTabbedPane().setTabComponentAt(
					frame.getToolsTabbedPane().getTabCount() - 1, wp);
			if (smallImage != null)
				frame.getToolsTabbedPane().getPopFrame(
						frame.getToolsTabbedPane().getTabCount() - 1)
						.setIconImage(smallImage);
		}
		frame.invalidate();
		frame.validate();
		frame.repaint();
		frame.getToolsTabbedPane().invalidate();
		frame.getToolsTabbedPane().validate();
		frame.getToolsTabbedPane().repaint();
		if (wereTabsChanged)
		{
			System.out.println("tabbs were changed, there are now "
					+ frame.getToolsTabbedPane().getTabCount() + " tabs");
			try
			{
				Thread.sleep(500);
			} catch (InterruptedException e)
			{
				// TODO Dec 19, 2007 Auto-generated catch block
				throw new RuntimeException(
						"TODO auto generated on Dec 19, 2007 : "
								+ e.getClass().getName() + " - "
								+ e.getMessage(), e);
			}
		}
		System.out.println("*****DONE CHECK NEEDS UPDATE TABS");
	}

	public void updateWorkspaceInfo()
	{

	}

	protected void refreshUsersDisplayList()
	{
		JPanel p = frame.getWorkspaceMembersPanel();
		p.removeAll();
		JLabel onlineLabel = new JLabel("Online:");
		onlineLabel.setFont(onlineLabel.getFont().deriveFont(Font.BOLD));
		JLabel offlineLabel = new JLabel("Offline:");
		offlineLabel.setFont(offlineLabel.getFont().deriveFont(Font.BOLD));
		if (isOnline())
		{
			p.add(onlineLabel);
			for (String u : listOnlineUsers())
			{
				UserInformationLink l = new UserInformationLink(u);
				l.setFont(l.getFont().deriveFont(Font.PLAIN));
				p.add(l);
			}
			if (listOnlineUsers().length < 1)
			{
				JLabel label = new JLabel("   none");
				label.setFont(label.getFont().deriveFont(Font.PLAIN));
				label.setForeground(new Color(128, 128, 128));
				p.add(label);
			}
			p.add(offlineLabel);
			for (String u : listOfflineUsers())
			{
				UserInformationLink l = new UserInformationLink(u);
				l.setFont(l.getFont().deriveFont(Font.PLAIN));
				p.add(l);
			}
			if (listOfflineUsers().length < 1)
			{
				JLabel label = new JLabel("   none");
				label.setFont(label.getFont().deriveFont(Font.PLAIN));
				label.setForeground(new Color(128, 128, 128));
				p.add(label);
			}
		} else
		{
			p.add(onlineLabel);
			JLabel label = new JLabel("   none");
			label.setFont(label.getFont().deriveFont(Font.PLAIN));
			label.setForeground(new Color(140, 140, 140));
			p.add(label);
			p.add(offlineLabel);
			for (String u : listUsers())
			{
				JLabel l = new JLabel(u);
				l.setFont(l.getFont().deriveFont(Font.PLAIN));
				p.add(l);
			}
		}
		p.invalidate();
		p.validate();
		p.repaint();
	}

	public WorkspaceFrame getFrame()
	{
		return frame;
	}
}
