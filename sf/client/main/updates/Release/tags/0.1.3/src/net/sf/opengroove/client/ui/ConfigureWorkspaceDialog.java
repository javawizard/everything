package net.sf.opengroove.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;

import javax.swing.JButton;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

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
public class ConfigureWorkspaceDialog extends javax.swing.JDialog
{
	private JTabbedPane mainTabbedPane;

	private JPanel jPanel1;

	private JTextField workspaceNameField;

	private JScrollPane jScrollPane1;

	private JList allowedMemberList;

	private JScrollPane jScrollPane2;

	private JLabel jLabel5;

	private JLabel typeLabel;

	private JLabel jLabel6;

	private JTextField idLabel;

	private JList participantList;

	private DefaultListModel participantModel = new DefaultListModel();

	private DefaultListModel allowedMembersModel = new DefaultListModel();

	private String[] allUsers = new String[0];

	private JButton okButton;

	private JLabel managerLabel;

	private JLabel jLabel4;

	private JButton addMemberButton;

	private JButton removeMemberButton;

	private JLabel jLabel3;

	private JLabel jLabel2;

	private JLabel jLabel1;

	private JPanel usersTab;

	private JPanel generalTab;

	/**
	 * Auto-generated main method to display this JDialog
	 */
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				JFrame frame = new JFrame();
				ConfigureWorkspaceDialog inst = new ConfigureWorkspaceDialog(
						frame);
				inst.setVisible(true);
			}
		});
	}

	public ConfigureWorkspaceDialog(JFrame frame)
	{
		super(frame, true);
		initGUI();
	}

	private void initGUI()
	{
		try
		{
			{
				mainTabbedPane = new JTabbedPane();
				getContentPane().add(getMainTabbedPane(), BorderLayout.CENTER);
				{
					generalTab = new JPanel();
					mainTabbedPane.addTab("general", null, generalTab, null);
					generalTab.setLayout(null);
					// START >> jLabel1
					jLabel1 = new JLabel();
					generalTab.add(jLabel1);
					jLabel1.setText("Workspace name:");
					jLabel1.setBounds(38, 31, 175, 20);
					// END << jLabel1
					// START >> workspaceNameField
					workspaceNameField = new JTextField();
					generalTab.add(getWorkspaceNameField());
					workspaceNameField.setBounds(38, 63, 309, 20);
					// END << workspaceNameField
					// START >> jLabel4
					jLabel4 = new JLabel();
					generalTab.add(jLabel4);
					jLabel4.setText("Creator:");
					jLabel4.setBounds(38, 136, 100, 17);
					// END << jLabel4
					// START >> managerLabel
					managerLabel = new JLabel();
					generalTab.add(getManagerLabel());
					managerLabel.setText("unknown");
					managerLabel.setBounds(95, 135, 319, 18);
					// START >> jLabel5
					jLabel5 = new JLabel();
					generalTab.add(jLabel5);
					jLabel5.setText("ID:");
					jLabel5.setBounds(38, 101, 33, 23);
					// END << jLabel5
					// START >> idLabel
					idLabel = new JTextField();
					idLabel.setEditable(false);
					idLabel.setBorder(null);
					idLabel.setBackground(new Color(200, 200, 200, 0));
					idLabel.setOpaque(false);
					generalTab.add(getIdLabel());
					idLabel.setBounds(95, 104, 379, 19);
					idLabel.setText("unknown");
					// END << idLabel
					// START >> jLabel6
					jLabel6 = new JLabel();
					generalTab.add(jLabel6);
					jLabel6.setText("Type:");
					jLabel6.setBounds(38, 173, 45, 19);
					// END << jLabel6
					// START >> typeLabel
					typeLabel = new JLabel();
					generalTab.add(getTypeLabel());
					typeLabel.setBounds(95, 173, 372, 19);
					typeLabel.setText("unknown");
					// END << typeLabel
					// END << managerLabel
				}
				{
					usersTab = new JPanel();
					mainTabbedPane.addTab("users", null, usersTab, null);
					usersTab.setLayout(null);
					// START >> jLabel2
					jLabel2 = new JLabel();
					usersTab.add(jLabel2);
					jLabel2.setText("participants:");
					jLabel2.setBounds(35, 35, 134, 19);
					// END << jLabel2
					// START >> jScrollPane1
					// START >> participantTextArea
					// END << participantTextArea
					// END << jScrollPane1
					// START >> jLabel3
					jLabel3 = new JLabel();
					usersTab.add(jLabel3);
					jLabel3.setText("allowed members:");
					jLabel3.setBounds(35, 189, 173, 18);
					// END << jLabel3
					// START >> jScrollPane2
					// START >> allowedMembersTextArea
					// END << allowedMembersTextArea
					// END << jScrollPane2
					// START >> removeMemberButton
					removeMemberButton = new JButton();
					removeMemberButton.addActionListener(new ActionListener()
					{

						public void actionPerformed(ActionEvent e)
						{
							if (allowedMemberList.getSelectedIndex() != -1)
							{
								String username = (String) allowedMemberList
										.getSelectedValue();
								if (participantModel.contains(username))
								{
									JOptionPane
											.showMessageDialog(
													ConfigureWorkspaceDialog.this,
													"<html>That user is a participant. Tell the user to delete the workspace from their computer, then try again.<br/>"
															+ "If the user is abusing the workspace, and you would like to remove them without their consent, send<br/>"
															+ "an email to webmaster@trivergia.com and we will deal with the issue.");
								} else
								{
									if (JOptionPane
											.showConfirmDialog(
													ConfigureWorkspaceDialog.this,
													"Are you sure you want to remove this user from the list of allowed users?",
													null,
													JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
									{
										allowedMembersModel
												.removeElement(username);
										allowedMemberList.repaint();
									}
								}
							} else
							{
								JOptionPane
										.showMessageDialog(
												ConfigureWorkspaceDialog.this,
												"You must first select an allowed member to remove.");
							}
						}
					});
					usersTab.add(getRemoveMemberButton());
					removeMemberButton.setText("remove");
					removeMemberButton.setBounds(312, 334, 77, 26);
					// END << removeMemberButton
					// START >> addMemberButton
					addMemberButton = new JButton();
					addMemberButton.addActionListener(new ActionListener()
					{

						public void actionPerformed(ActionEvent e)
						{
							String newUsername = JOptionPane
									.showInputDialog(
											ConfigureWorkspaceDialog.this,
											"Type the username of the user you want to add");
							if (newUsername != null)
							{
								if (Arrays.asList(allUsers).contains(
										newUsername))
								{
									if (!allowedMembersModel
											.contains(newUsername))
										allowedMembersModel
												.addElement(newUsername);
								} else
								{
									JOptionPane
											.showMessageDialog(
													ConfigureWorkspaceDialog.this,
													"That isn't an existing username. Check to make sure you spelled the username correctly.");
								}
							}
						}
					});
					usersTab.add(getAddMemberButton());
					addMemberButton.setText("add");
					addMemberButton.setBounds(252, 334, 55, 26);
					// START >> jScrollPane1
					jScrollPane1 = new JScrollPane();
					usersTab.add(jScrollPane1);
					jScrollPane1.setBounds(35, 66, 371, 102);
					// START >> participantList
					participantList = new JList();
					jScrollPane1.setViewportView(getParticipantList());
					participantList.setModel(participantModel);
					participantList.setBounds(35, 66, 375, 99);
					// END << participantList
					// END << jScrollPane1
					// START >> jScrollPane2
					jScrollPane2 = new JScrollPane();
					usersTab.add(jScrollPane2);
					jScrollPane2.setBounds(37, 219, 371, 104);
					// START >> allowedMemberList
					allowedMemberList = new JList();
					jScrollPane2.setViewportView(getAllowedMemberList());
					allowedMemberList.setModel(allowedMembersModel);
					allowedMemberList.setBounds(35, 219, 371, 103);
					// END << allowedMemberList
					// END << jScrollPane2
					// END << addMemberButton
				}
			}
			{
				jPanel1 = new JPanel();
				getContentPane().add(jPanel1, BorderLayout.SOUTH);
				jPanel1.setPreferredSize(new java.awt.Dimension(484, 50));
				jPanel1.setLayout(null);
				// START >> okButton
				okButton = new JButton();
				jPanel1.add(okButton);
				okButton.setText("ok");
				okButton.setBounds(436, 24, 48, 26);
				okButton.addActionListener(new ActionListener()
				{

					public void actionPerformed(ActionEvent e)
					{
						ConfigureWorkspaceDialog.this.hide();
					}
				});
				// END << okButton
			}
			this.setSize(500, 600);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public JTabbedPane getMainTabbedPane()
	{
		return mainTabbedPane;
	}

	public JTextField getWorkspaceNameField()
	{
		return workspaceNameField;
	}

	public JButton getRemoveMemberButton()
	{
		return removeMemberButton;
	}

	public JButton getAddMemberButton()
	{
		return addMemberButton;
	}

	public JLabel getManagerLabel()
	{
		return managerLabel;
	}

	public JList getParticipantList()
	{
		return participantList;
	}

	public JList getAllowedMemberList()
	{
		return allowedMemberList;
	}

	public DefaultListModel getAllowedMembersModel()
	{
		return allowedMembersModel;
	}

	public DefaultListModel getParticipantModel()
	{
		return participantModel;
	}

	public String[] getAllUsers()
	{
		return allUsers;
	}

	public void setAllUsers(String[] allUsers)
	{
		this.allUsers = allUsers;
	}

	public JTextField getIdLabel()
	{
		return idLabel;
	}

	public JLabel getTypeLabel()
	{
		return typeLabel;
	}

}
