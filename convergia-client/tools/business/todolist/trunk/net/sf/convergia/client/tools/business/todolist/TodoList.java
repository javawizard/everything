package net.sf.convergia.client.tools.business.todolist;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

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
					Convergia.Icons.DELETE_WORKSPACE.getImage()));
			deleteButton.setBorder(new EmptyBorder(1, 1, 1, 10));
			JLinkButton editButton = new JLinkButton(new ImageIcon(
					Convergia.Icons.CONFIGURE_WORKSPACE.getImage()));
			editButton.setBorder(new EmptyBorder(1, 1, 1, 1));
			deleteButton.setFocusable(false);
			editButton.setFocusable(false);
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			final LiveProperties properties = new LiveProperties(new File(
					itemsFolder, id));
			JLabel taskLabel = new JLabel();
			taskLabel.setText(properties.getProperty("name"));
			if (properties.getProperty("completed") == null)
				properties.setProperty("completed", "false");
			checkbox.setSelected(properties.getProperty("completed")
					.equalsIgnoreCase("true"));
			add(editButton);
			add(deleteButton);
			add(checkbox);
			add(taskLabel);
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

	private void reloadTasks()
	{
		taskListPanel.removeAll();
		for (File file : itemsFolder.listFiles(new SubversionFileFilter()))
		{
			taskListPanel.add(new TaskComponent(file.getName()));
			taskListPanel.invalidate();
			taskListPanel.validate();
			taskListPanel.repaint();
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
