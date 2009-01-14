package net.sf.opengroove.client.help;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class HelpViewer extends javax.swing.JFrame
{
	private JSplitPane jSplitPane1;

	private JTree helpTree;

	private JScrollPane jScrollPane1;

	private JScrollPane jScrollPane2;

	private JEditorPane helpViewPane;

	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				HelpViewer inst = new HelpViewer(null);
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public HelpViewer(File helpPath)
	{
		super();
		initGUI(helpPath);
		loadHelpModules(helpPath, rootNode, "/help");
		treeModel.reload();
		helpTree.invalidate();
		helpTree.validate();
		helpTree.repaint();
		treeModel.reload();
	}

	/**
	 * loads the help from the specified folder into the specified node. This
	 * method is recursive, and only operates on folders, and files that end
	 * with .html. it does not use default.html files as help content
	 * themselves, it uses these as the help content to show when it's parent
	 * folder is selected.
	 * 
	 * @param helpFolder
	 * @param node
	 */
	private void loadHelpModules(File helpFolder, DefaultMutableTreeNode node,
			String path)
	{
		path = path.toLowerCase();
		for (File file : helpFolder.listFiles(new FileFilter()
		{

			public boolean accept(File pathname)
			{
				// TODO Auto-generated method stub
				return (!pathname.getName().equals(".svn"))
						&& (pathname.isDirectory() || (pathname.getName()
								.endsWith(".html") && !pathname.getName()
								.equalsIgnoreCase("default.html")));
			}
		}))
		{
			if (file.isDirectory())
			{
				File defaultFile = new File(file, "default.html");
				if (!defaultFile.exists())
					defaultFile = null;
				String name = file.getName();
				int index = name.indexOf("_");
				String id;
				String label;
				if (index != -1)
				{
					id = name.substring(0, index);
					label = name.substring(index + 1);
				} else
				{
					id = name.toLowerCase();
					label = name;
				}
				id = id.toLowerCase();
				HelpContent content = new HelpContent(label, id, defaultFile,
						null);
				System.out.println("setting module with path " + path + "/"
						+ id);
				modulesByPath.put(path + "/" + id, content);
				DefaultMutableTreeNode subnode = new DefaultMutableTreeNode(
						content, true);
				content.treeNode = subnode;
				node.add(subnode);
				loadHelpModules(file, subnode, path + "/" + id);
			} else
			{
				String name = file.getName().substring(0,
						file.getName().lastIndexOf("."));
				int index = name.indexOf("_");
				String id;
				String label;
				if (index != -1)
				{
					id = name.substring(0, index);
					label = name.substring(index + 1);
				} else
				{
					id = name.toLowerCase();
					label = name;
				}
				id = id.toLowerCase();
				HelpContent content = new HelpContent(label, id, file, null);
				System.out.println("setting module with path " + path + "/"
						+ id);
				modulesByPath.put(path + "/" + id, content);
				DefaultMutableTreeNode subnode = new DefaultMutableTreeNode(
						content);
				content.treeNode = subnode;
				node.add(subnode);
			}
		}
	}

	private DefaultTreeModel treeModel;

	private DefaultMutableTreeNode rootNode;

	private HashMap<String, HelpContent> modulesByPath = new HashMap<String, HelpContent>();

	protected static class HelpContent
	{
		private String name;

		private String refId;

		private File content;

		private TreeNode treeNode;

		public HelpContent(String name, String refId, File content,
				TreeNode treeNode)
		{
			this.name = name;
			this.refId = refId.toLowerCase();
			this.content = content;
			this.treeNode = treeNode;
		}

		public String toString()
		{
			return this.name;
		}
	}

	private void initGUI(File helpFolder)
	{
		try
		{
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			// START >> jSplitPane1
			jSplitPane1 = new JSplitPane();
			getContentPane().add(jSplitPane1, BorderLayout.CENTER);
			jSplitPane1.setDividerLocation(160);
			jSplitPane1.setPreferredSize(new java.awt.Dimension(550, 441));
			// START >> helpTree
			helpTree = new JTree();
			helpTree.setToggleClickCount(2);
			helpTree.setShowsRootHandles(true);
			helpTree.setRootVisible(false);
			HelpContent rootContent = new HelpContent("Help", "help", new File(
					helpFolder, "default.html"), null);
			rootNode = new DefaultMutableTreeNode(rootContent, true);
			rootContent.treeNode = rootNode;
			modulesByPath.put("/help", rootContent);
			treeModel = new DefaultTreeModel(rootNode);
			helpTree.setModel(treeModel);
			jSplitPane1.add(new JScrollPane(helpTree), JSplitPane.LEFT);
			// END << helpTree
			// START >> jScrollPane1
			jScrollPane1 = new JScrollPane();
			jSplitPane1.add(jScrollPane1, JSplitPane.RIGHT);
			jScrollPane1.setPreferredSize(new java.awt.Dimension(367, 439));
			// START >> jScrollPane2
			jScrollPane2 = new JScrollPane();
			jScrollPane1.setViewportView(jScrollPane2);
			jScrollPane2.setPreferredSize(new java.awt.Dimension(156, 436));
			// START >> helpViewPane
			helpViewPane = new JEditorPane();
			jScrollPane2.setViewportView(helpViewPane);
			helpViewPane.setEditable(false);
			helpViewPane.addHyperlinkListener(new HyperlinkListener()
			{

				public void hyperlinkUpdate(HyperlinkEvent e)
				{
					if (e.getEventType().equals(EventType.ENTERED))
					{
						helpViewPane.setCursor(Cursor
								.getPredefinedCursor(Cursor.HAND_CURSOR));
					}
					if (e.getEventType().equals(EventType.EXITED))
					{
						helpViewPane.setCursor(Cursor
								.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
					if (e.getEventType().equals(EventType.ACTIVATED))
					{
						if (e.getDescription().startsWith("help://"))
						{
							String path = e.getDescription().substring(6);
							showHelpTopic(path);
						} else
						{
							try
							{
								Desktop.getDesktop().browse(e.getURL().toURI());
							} catch (IOException e1)
							{
								e1.printStackTrace();
							} catch (URISyntaxException e1)
							{
								e1.printStackTrace();
							}
						}
					}
				}
			});
			helpTree.addTreeSelectionListener(new TreeSelectionListener()
			{

				public void valueChanged(TreeSelectionEvent e)
				{
					if (helpTree.getSelectionPath() == null)
					{
						return;
					}
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) helpTree
							.getSelectionPath().getLastPathComponent();
					HelpContent content = (HelpContent) selectedNode
							.getUserObject();
					setTitle(content.name + " - Help - OpenGroove");
					File contentFile = content.content;
					if (contentFile == null)
						helpViewPane.setText("");
					else
						try
						{
							helpViewPane.setPage(contentFile.toURI().toURL());
						} catch (MalformedURLException e1)
						{
							e1.printStackTrace();
						} catch (IOException e1)
						{
							e1.printStackTrace();
						}
				}
			});
			helpTree.setSelectionPath(new TreePath(rootNode));
			// END << helpViewPane
			// END << jScrollPane2
			// END << jScrollPane1
			// END << jSplitPane1
			pack();
			this.setSize(554, 477);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public JEditorPane getHelpViewPane()
	{
		return helpViewPane;
	}

	/**
	 * shows the help topic with the specified path. the path MUST start with a
	 * leading forward slash, and it's first element must be "help".
	 * 
	 * @param path
	 */
	public void showHelpTopic(String path)
	{
		path = path.toLowerCase();
		HelpContent module = modulesByPath.get(path);
		if (module == null)
			throw new IllegalArgumentException(
					"the path specified does not refer to an existing help module. the path is : "
							+ path);
		TreePath treePath = new TreePath(treeModel
				.getPathToRoot(module.treeNode));
		helpTree.setSelectionPath(treePath);
		show();
	}
}
