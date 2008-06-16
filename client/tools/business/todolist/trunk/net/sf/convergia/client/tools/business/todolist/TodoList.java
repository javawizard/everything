package net.sf.convergia.client.tools.business.todolist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.l2fprod.common.swing.JLinkButton;

import net.sf.convergia.client.Convergia;
import net.sf.convergia.client.Storage;
import net.sf.convergia.client.SubversionFileFilter;
import net.sf.convergia.client.toolworkspace.Tool;
import net.sf.convergia.utils.LiveProperties;

public class TodoList extends Tool
{

	private JPanel panel;

	private JPanel taskListPanel;

	private JPanel p1;

	private JPanel p2;

	private File itemsFolder;

	@Override
	public JComponent getComponent()
	{
		return panel;
	}

	@Override
	public void initialize()
	{
		itemsFolder = new File(getStorageFile(), "items");
		itemsFolder.mkdirs();
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JPanel lowerPanel = new JPanel();
		JPanel lowerRightPanel = new JPanel();
		lowerPanel.setLayout(new BorderLayout());
		lowerRightPanel.setLayout(new BoxLayout(lowerRightPanel,
				BoxLayout.X_AXIS));
		lowerPanel.add(lowerRightPanel, BorderLayout.EAST);
		panel.add(lowerPanel, BorderLayout.SOUTH);
		JButton addButton = new JButton("New Task");
		addButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				String name = JOptionPane.showInputDialog(panel,
						"Type the name for the new task");
				if (name == null)// clicked on the cancel button
					return;
				LiveProperties properties = new LiveProperties(new File(
						itemsFolder, getUsername() + "-"
								+ System.currentTimeMillis()));
				properties.setProperty("name", name);
				properties.setProperty("completed", "false");
				new Thread()
				{
					public void run()
					{
						try
						{
							Thread.sleep(100);
						} catch (Exception ex1)
						{
							ex1.printStackTrace();
						}
						reloadTasks();
					}
				}.start();
			}
		});
		lowerRightPanel.add(addButton);
		taskListPanel = new JPanel();
		taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
		taskListPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		p1 = new JPanel();
		p2 = new JPanel();
		p1.setLayout(new BorderLayout());
		p2.setLayout(new BorderLayout());
		panel.add(new JScrollPane(p1), BorderLayout.CENTER);
		p1.add(p2, BorderLayout.NORTH);
		p2.add(taskListPanel, BorderLayout.WEST);
		reloadTasks();
	}

	@Override
	public void receiveMessage(String from, String message)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdown()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void userStatusChanged()
	{
		// TODO Auto-generated method stub

	}

	private class TaskComponent extends JPanel
	{
		public TaskComponent(final String id)
		{
			setAlignmentX(0);
			setAlignmentY(0);
			final JCheckBox checkbox = new JCheckBox();
			checkbox.setFocusable(false);
			JLinkButton deleteButton = new JLinkButton(new ImageIcon(
					Convergia.Icons.DELETE_WORKSPACE_16.getImage()));
			deleteButton.setBorder(new EmptyBorder(1, 1, 1, 13));
			JLinkButton editButton = new JLinkButton(new ImageIcon(
					Convergia.Icons.CONFIGURE_WORKSPACE_16.getImage()));
			editButton.setBorder(new EmptyBorder(1, 1, 1, 1));
			JLinkButton changeGroupButton = new JLinkButton(new ImageIcon(
					Convergia.Icons.FOLDER_DOCS_16.getImage()));
			JLinkButton notesButton = new JLinkButton(new ImageIcon(
					Convergia.Icons.NOTES_16.getImage()));
			notesButton.setBorder(new EmptyBorder(1, 1, 1, 1));
			changeGroupButton.setBorder(new EmptyBorder(1, 1, 1, 1));
			deleteButton.setFocusable(false);
			editButton.setFocusable(false);
			changeGroupButton.setFocusable(false);
			notesButton.setFocusable(false);
			setLayout(new BorderLayout());
			JPanel mp = new JPanel();
			mp.setLayout(new BorderLayout());
			add(mp, BorderLayout.WEST);
			JPanel controlsPanel = new JPanel();
			JPanel textPanel = new JPanel();
			controlsPanel.setLayout(new BoxLayout(controlsPanel,
					BoxLayout.X_AXIS));
			textPanel.setLayout(new BorderLayout());
			JPanel cw1 = new JPanel();
			cw1.setLayout(new BorderLayout());
			cw1.add(controlsPanel, BorderLayout.NORTH);
			mp.add(cw1, BorderLayout.WEST);
			mp.add(textPanel, BorderLayout.CENTER);
			final LiveProperties properties = new LiveProperties(new File(
					itemsFolder, id));
			JLabel taskLabel = new JLabel();
			taskLabel.setText(properties.getProperty("name"));
			final String notes = properties.getProperty("notes");
			String tooltip;
			if (notes == null || "".equals(notes.trim()))
			{
				tooltip = "No notes";
			} else
			{
				System.out.println("notes are " + notes);
				System.out.println("replaced, we have "
						+ notes.replace("\n", "<br/>"));
				tooltip = "<html>" + notes.replace("\n", "<br/>");
			}
			taskLabel.setToolTipText(notes);
			if (properties.getProperty("completed") == null)
				properties.setProperty("completed", "false");
			checkbox.setSelected(properties.getProperty("completed")
					.equalsIgnoreCase("true"));
			controlsPanel.add(editButton);
			controlsPanel.add(changeGroupButton);
			controlsPanel.add(deleteButton);
			controlsPanel.add(checkbox);
			controlsPanel.add(notesButton);
			taskLabel.setAlignmentX(0);
			taskLabel.setAlignmentY(0);
			textPanel.add(taskLabel, BorderLayout.NORTH);
			JLabel notesLabel = new JLabel(notes);
			notesLabel.setAlignmentX(0);
			notesLabel.setAlignmentY(0);
			taskLabel.setHorizontalAlignment(2);
			notesLabel.setHorizontalAlignment(2);
			notesLabel.setFont(notesLabel.getFont().deriveFont(Font.PLAIN));
			if (notes != null)
				textPanel.add(notesLabel, BorderLayout.SOUTH);
			notesButton.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					final JDialog dialog = new JDialog(getParentFrame(), true);
					dialog.setSize(300, 400);
					dialog.setLocationRelativeTo(getParentFrame());
					dialog.getContentPane().setLayout(new BorderLayout());
					dialog.setTitle("Type some notes for this task");
					final JTextArea textarea = new JTextArea();
					dialog.getContentPane().add(new JScrollPane(textarea));
					JButton done = new JButton("Save");
					JButton cancel = new JButton("Cancel");
					JPanel lowerPanel = new JPanel();
					lowerPanel.setLayout(new BorderLayout());
					dialog.getContentPane().add(lowerPanel, BorderLayout.SOUTH);
					JPanel lowerRightPanel = new JPanel();
					lowerRightPanel.setLayout(new BorderLayout());
					lowerPanel.add(lowerRightPanel, BorderLayout.EAST);
					lowerRightPanel.add(done, BorderLayout.WEST);
					lowerRightPanel.add(cancel, BorderLayout.EAST);
					if (notes != null)
						textarea.setText(notes);
					cancel.addActionListener(new ActionListener()
					{

						@Override
						public void actionPerformed(ActionEvent e)
						{
							dialog.hide();
						}
					});
					done.addActionListener(new ActionListener()
					{

						@Override
						public void actionPerformed(ActionEvent e)
						{
							dialog.hide();
							properties.setProperty("notes", textarea.getText());
							reloadTasks();
						}
					});
					dialog.setDefaultCloseOperation(dialog.DO_NOTHING_ON_CLOSE);
					dialog.show();
				}
			});
			checkbox.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					String prompt = "Are you sure you want to mark this task as ";
					if (!checkbox.isSelected())
						prompt += "not ";
					prompt += "completed?";
					if (JOptionPane.showConfirmDialog(panel, prompt, null,
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
					{
						properties.setProperty("completed", ""
								+ checkbox.isSelected());
					}
					reloadTasks();
				}
			});
			changeGroupButton.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					ArrayList<String> displayGroups = new ArrayList<String>(
							knownGroups);
					displayGroups.add("Create a group");
					String newGroup = (String) JOptionPane.showInputDialog(
							TaskComponent.this,
							"Choose a new group for this task.", null,
							JOptionPane.PLAIN_MESSAGE, null, displayGroups
									.toArray(new String[0]), properties
									.getProperty("group"));
					if (newGroup == null)
						return;
					if (newGroup.equals("Create a group"))
					{
						newGroup = JOptionPane.showInputDialog(
								TaskComponent.this,
								"Type the name of the new group.");
						if (newGroup == null
								|| newGroup.equals("Create a group")
								|| newGroup.equals("No group"))
							return;
					}
					properties.setProperty("group", newGroup);
					reloadTasks();
				}
			});
			deleteButton.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					if (JOptionPane.showConfirmDialog(panel,
							"Are you sure you want to delete this task?", null,
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
					{
						Storage.recursiveDelete(new File(itemsFolder, id));
						reloadTasks();
					}
				}
			});
			editButton.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					String newName = JOptionPane.showInputDialog(panel,
							"Type the new name of this task", properties
									.getProperty("name"));
					if (newName != null)
					{
						properties.setProperty("name", newName);
						reloadTasks();
					}
				}
			});
		}
	}

	private ArrayList<String> knownGroups = new ArrayList<String>();

	private void reloadTasks()
	{
		taskListPanel.removeAll();
		HashMap<String, JPanel> groups = new HashMap<String, JPanel>();
		knownGroups.clear();
		knownGroups.add("No group");
		File[] files = itemsFolder.listFiles(new SubversionFileFilter());
		Arrays.sort(files, new java.util.Comparator<File>()
		{

			public int compare(File o1, File o2)
			{
				LiveProperties o1p = new LiveProperties(o1);
				LiveProperties o2p = new LiveProperties(o2);
				if (o1p.getProperty("completed") == null)
					o1p.setProperty("completed", "false");
				if (o2p.getProperty("completed") == null)
					o2p.setProperty("completed", "false");
				if (o1p.getProperty("completed").equals("true")
						&& o2p.getProperty("completed").equals("false"))
					return 1;
				if (o2p.getProperty("completed").equals("true")
						&& o1p.getProperty("completed").equals("false"))
					return -1;
				else
					return 0;
			}
		});
		for (File file : files)
		{
			LiveProperties lp = new LiveProperties(file);
			if (lp.getProperty("group") == null)
				lp.setProperty("group", "No group");
			if (!knownGroups.contains(lp.getProperty("group")))
			{
				knownGroups.add(lp.getProperty("group"));
			}
			JPanel groupPanel = groups.get(lp.getProperty("group"));
			if (groupPanel == null)
			{
				groupPanel = new JPanel();
				groupPanel
						.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));
				groupPanel.setBorder(new CompoundBorder(new CompoundBorder(
						new EmptyBorder(3, 3, 3, 3), new TitledBorder(lp
								.getProperty("group"))), new EmptyBorder(3, 3,
						3, 3)));
				taskListPanel.add(groupPanel);
				groups.put(lp.getProperty("group"), groupPanel);
			}
			groupPanel.add(new TaskComponent(file.getName()));
			taskListPanel.invalidate();
			taskListPanel.validate();
			taskListPanel.repaint();
			groupPanel.invalidate();
			groupPanel.validate();
			groupPanel.repaint();
		}
		taskListPanel.invalidate();
		taskListPanel.validate();
		taskListPanel.repaint();
		taskListPanel.invalidate();
		taskListPanel.validate();
		taskListPanel.repaint();
		p1.invalidate();
		p1.validate();
		p2.invalidate();
		p2.validate();
		p1.repaint();
		p2.repaint();
		taskListPanel.repaint();
	}

}
