package net.sf.convergia.client.toolworkspace;

import com.elevenworks.swing.panel.SimpleGradientPanel;
import com.l2fprod.common.swing.JLinkButton;
import com.l2fprod.common.swing.JTaskPane;
import com.l2fprod.common.swing.JTaskPaneGroup;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import net.sf.convergia.client.Convergia;

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
public class WorkspaceFrame extends javax.swing.JFrame
{
	private SimpleGradientPanel simpleGradientPanel1;

	private JLinkButton configureWorkspaceCommonTask;

	private JPanel chatPanel;

	private JScrollPane jScrollPane1;

	private JLinkButton chatPopOutButton;

	private JPanel bothChatButtonsPanel;

	private JTextArea chatTextArea;

	private JLinkButton renameToolCommonTask;

	private JLinkButton deleteToolCommonTask;

	private JLinkButton advancedOptionsCommonTask;

	private JPanel workspaceMembersPanel;

	private JLinkButton manageToolsCommonTask;

	private JLinkButton findNewToolsCommonTask;

	private JButton sendChatButton;

	private JTextField chatTextField;

	private JPanel lowerChatPanel;

	private JLinkButton addToolsCommonTask;

	private JTaskPaneGroup commonTasksTaskPaneGroup;

	private JTaskPaneGroup chatTaskPaneGroup;

	private JTaskPaneGroup membersTaskPaneGroup;

	private JTaskPane jTaskPane1;

	private PoppableTabbedPane toolsTabbedPane;

	private JSplitPane jSplitPane1;

	private JPanel j_mainPanel;

	private JFrame chatPopoutFrame;

	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				WorkspaceFrame inst = new WorkspaceFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public WorkspaceFrame()
	{
		super();
		initGUI();
	}

	public JFrame getChatPopoutFrame()
	{
		return chatPopoutFrame;
	}

	private void initGUI()
	{
		try
		{
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			// START >> simpleGradientPanel1
			chatPopoutFrame = new JFrame("");
			chatPopoutFrame.getContentPane().setLayout(new BorderLayout());
			chatPopoutFrame.setSize(300, 400);
			chatPopoutFrame.setLocationRelativeTo(null);
			chatPopoutFrame.setIconImage(Convergia.getWindowIcon());
			chatPopoutFrame
					.setDefaultCloseOperation(chatPopoutFrame.DO_NOTHING_ON_CLOSE);
			chatPopoutFrame.addWindowListener(new WindowAdapter()
			{

				@Override
				public void windowClosing(WindowEvent e)
				{
					JOptionPane
							.showMessageDialog(
									chatPopoutFrame,
									"To pop the chat window back in, click on the pop in icon next to the go button.");
				}
			});
			simpleGradientPanel1 = new SimpleGradientPanel();
			BorderLayout simpleGradientPanel1Layout = new BorderLayout();
			simpleGradientPanel1.setLayout(simpleGradientPanel1Layout);
			getContentPane().add(simpleGradientPanel1, BorderLayout.CENTER);
			simpleGradientPanel1.setEndColor(new java.awt.Color(200, 200, 200));
			simpleGradientPanel1
					.setStartColor(new java.awt.Color(192, 193, 211));
			// START >> j_mainPanel
			j_mainPanel = new JPanel();
			BorderLayout j_mainPanelLayout = new BorderLayout();
			j_mainPanel.setLayout(j_mainPanelLayout);
			simpleGradientPanel1.add(getJ_mainPanel(), BorderLayout.CENTER);
			j_mainPanel.setBorder(BorderFactory.createEmptyBorder(17, 17, 17,
					17));
			j_mainPanel.setOpaque(false);
			// START >> jSplitPane1
			jSplitPane1 = new JSplitPane();
			jSplitPane1.setBorder(null);
			jSplitPane1.setDividerLocation(575);
			j_mainPanel.add(jSplitPane1, BorderLayout.CENTER);
			jSplitPane1.setContinuousLayout(true);
			// START >> jTabbedPane1
			toolsTabbedPane = new PoppableTabbedPane();
			jSplitPane1.add(toolsTabbedPane, JSplitPane.LEFT);
			// END << jTabbedPane1
			// START >> jTaskPane1
			jTaskPane1 = new JTaskPane();
			jSplitPane1.add(new JScrollPane(jTaskPane1), JSplitPane.RIGHT);
			// START >> membersTaskPaneGroup
			membersTaskPaneGroup = new JTaskPaneGroup();
			jTaskPane1.add(membersTaskPaneGroup);
			membersTaskPaneGroup.setText("Workspace Members");
			// START >> workspaceMembersPanel
			workspaceMembersPanel = new JPanel();
			BoxLayout workspaceMembersPanelLayout = new BoxLayout(
					workspaceMembersPanel, javax.swing.BoxLayout.Y_AXIS);
			membersTaskPaneGroup.getContentPane().add(
					getWorkspaceMembersPanel());
			workspaceMembersPanel.setLayout(workspaceMembersPanelLayout);
			// END << workspaceMembersPanel
			// END << membersTaskPaneGroup
			// START >> chatTaskPaneGroup
			chatTaskPaneGroup = new JTaskPaneGroup();
			jTaskPane1.add(chatTaskPaneGroup);
			chatTaskPaneGroup.setText("Chat");
			chatTaskPaneGroup.setFocusable(true);
			// START >> jPanel1
			chatPanel = new JPanel();
			BorderLayout jPanel1Layout = new BorderLayout();
			chatPanel.setLayout(jPanel1Layout);
			chatTaskPaneGroup.getContentPane().add(chatPanel);
			chatPanel.setOpaque(false);
			// START >> jScrollPane1
			jScrollPane1 = new JScrollPane();
			chatPanel.add(jScrollPane1, BorderLayout.CENTER);
			jScrollPane1.setPreferredSize(new java.awt.Dimension(197, 235));
			// START >> chatTextArea
			chatTextArea = new JTextArea();
			jScrollPane1.setViewportView(getChatTextArea());
			chatTextArea.setEditable(false);
			chatTextArea.setWrapStyleWord(true);
			chatTextArea.setLineWrap(true);
			// END << chatTextArea
			// START >> chatTextArea
			// END << chatTextArea
			// END << jScrollPane1
			// START >> jPanel2
			lowerChatPanel = new JPanel();
			BorderLayout jPanel2Layout = new BorderLayout();
			lowerChatPanel.setLayout(jPanel2Layout);
			chatPanel.add(lowerChatPanel, BorderLayout.SOUTH);
			lowerChatPanel.setOpaque(false);
			// START >> chatTextField
			chatTextField = new JTextField();
			lowerChatPanel.add(getChatTextField(), BorderLayout.CENTER);
			chatTextField.setPreferredSize(new java.awt.Dimension(-1, 0));
			// START >> jPanel1
			bothChatButtonsPanel = new JPanel();
			BorderLayout jPanel1Layout1 = new BorderLayout();
			bothChatButtonsPanel.setLayout(jPanel1Layout1);
			lowerChatPanel.add(bothChatButtonsPanel, BorderLayout.EAST);

			sendChatButton = new JButton();
			bothChatButtonsPanel.add(sendChatButton, BorderLayout.WEST);
			sendChatButton.setText("Go");
			// START >> chatPopOutButton
			chatPopOutButton = new JLinkButton();
			chatPopOutButton.setBorder(new EmptyBorder(2, 2, 2, 2));
			chatPopOutButton.setFocusable(false);
			bothChatButtonsPanel.add(getChatPopOutButton(), BorderLayout.EAST);
			chatPopOutButton.setText(">>");
			chatPopOutButton.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					if (chatPopoutFrame.isShowing())// pop in
					{
						chatPopoutFrame.getContentPane().removeAll();
						chatPopoutFrame.hide();
						chatTaskPaneGroup.getContentPane().add(chatPanel);
						chatTaskPaneGroup.show();
						jTaskPane1.invalidate();
						jTaskPane1.validate();
						jTaskPane1.repaint();
					} else
					// pop out
					{
						chatTaskPaneGroup.getContentPane().removeAll();
						chatTaskPaneGroup.hide();
						chatPopoutFrame.getContentPane().add(chatPanel);
						chatPopoutFrame.show();
						jTaskPane1.invalidate();
						jTaskPane1.validate();
						jTaskPane1.repaint();
					}
				}
			});
			// END << chatPopOutButton

			// END << jPanel1
			// END << chatTextField
			// START >> sendChatButton
			// END << sendChatButton
			// END << jPanel2
			// END << jPanel1
			// END << chatTaskPaneGroup
			// START >> commonTasksTaskPaneGroup
			commonTasksTaskPaneGroup = new JTaskPaneGroup();
			jTaskPane1.add(commonTasksTaskPaneGroup);
			commonTasksTaskPaneGroup.setText("Common Tasks");
			// START >> addToolsCommonTask
			addToolsCommonTask = new JLinkButton();
			commonTasksTaskPaneGroup.getContentPane().add(addToolsCommonTask);
			addToolsCommonTask.setText("Add Tool");
			// END << addToolsCommonTask
			// START >> configureWorkspaceCommonTask
			configureWorkspaceCommonTask = new JLinkButton();
			commonTasksTaskPaneGroup.getContentPane().add(
					configureWorkspaceCommonTask);
			configureWorkspaceCommonTask.setText("Configure Workspace");
			// END << configureWorkspaceCommonTask
			// START >> manageToolsCommonTask
			manageToolsCommonTask = new JLinkButton();
			commonTasksTaskPaneGroup.getContentPane()
					.add(manageToolsCommonTask);
			manageToolsCommonTask.setText("Manage Tools");
			// END << manageToolsCommonTask
			// START >> findNewToolsCommonTask
			findNewToolsCommonTask = new JLinkButton();
			commonTasksTaskPaneGroup.getContentPane().add(
					findNewToolsCommonTask);
			findNewToolsCommonTask.setText("Find new tools");
			// END << findNewToolsCommonTask
			// START >> advancedOptionsCommonTask
			advancedOptionsCommonTask = new JLinkButton();
			commonTasksTaskPaneGroup.getContentPane().add(
					getAdvancedOptionsCommonTask());
			advancedOptionsCommonTask.setText("Advanced Options");
			// START >> deleteToolCommonTask
			deleteToolCommonTask = new JLinkButton();
			commonTasksTaskPaneGroup.getContentPane().add(
					getDeleteToolCommonTask());
			deleteToolCommonTask.setText("Delete this tool");
			// END << deleteToolCommonTask
			// START >> renameToolCommonTask
			renameToolCommonTask = new JLinkButton();
			commonTasksTaskPaneGroup.getContentPane().add(
					getRenameToolCommonTask());
			renameToolCommonTask.setText("Rename this tool");
			// END << renameToolCommonTask
			// END << advancedOptionsCommonTask
			// END << commonTasksTaskPaneGroup
			// END << jTaskPane1
			// END << jSplitPane1
			// END << j_mainPanel
			// END << simpleGradientPanel1
			pack();
			this.setSize(900, 730);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public JPanel getJ_mainPanel()
	{
		return j_mainPanel;
	}

	public JTextField getChatTextField()
	{
		return chatTextField;
	}

	public JPanel getWorkspaceMembersPanel()
	{
		return workspaceMembersPanel;
	}

	public JLinkButton getAddToolsCommonTask()
	{
		return addToolsCommonTask;
	}

	public JLinkButton getConfigureWorkspaceCommonTask()
	{
		return configureWorkspaceCommonTask;
	}

	public JLinkButton getFindNewToolsCommonTask()
	{
		return findNewToolsCommonTask;
	}

	public JLinkButton getManageToolsCommonTask()
	{
		return manageToolsCommonTask;
	}

	public JButton getSendChatButton()
	{
		return sendChatButton;
	}

	public JLinkButton getAdvancedOptionsCommonTask()
	{
		return advancedOptionsCommonTask;
	}

	public PoppableTabbedPane getToolsTabbedPane()
	{
		return toolsTabbedPane;
	}

	public JLinkButton getDeleteToolCommonTask()
	{
		return deleteToolCommonTask;
	}

	public JLinkButton getRenameToolCommonTask()
	{
		return renameToolCommonTask;
	}

	public JScrollPane getChatScrollPane()
	{
		return jScrollPane1;
	}

	public JTextArea getChatTextArea()
	{
		return chatTextArea;
	}

	public JPanel getChatPanel()
	{
		return chatPanel;
	}

	public JTaskPaneGroup getChatGroup()
	{
		return chatTaskPaneGroup;
	}

	public JLinkButton getChatPopOutButton()
	{
		return chatPopOutButton;
	}

}
