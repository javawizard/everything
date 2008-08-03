package net.sf.opengroove.client.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.sf.opengroove.client.OpenGroove;
import net.sf.opengroove.client.com.StatisticsListener;
import net.sf.opengroove.client.notification.NotificationAdapter;

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
public class ConfigureOpenGrooveDialog extends javax.swing.JDialog
{
	private JTabbedPane optionsTabbedPane;

	private JPanel generalPanel;
	private JLabel jLabel9;

	private JPanel responseStatPanel;

	private JPanel jPanel3;

	private JScrollPane jScrollPane2;

	private JPanel commandStatPanel;

	private JScrollPane jScrollPane1;

	private JPanel jPanel2;

	private JLabel jLabel8;

	private JLabel jLabel7;

	private JLabel jLabel6;

	private JButton notificationUntilClickedTestButton;

	private JButton notificationOneTimeTestButton;

	private JPanel advancedTab;

	private JLabel jLabel5;

	private JLabel mConnectionSecurityStatus;

	private JLabel jLabel4;

	private JLabel jLabel3;

	private JLabel mConnectivityStatus;

	private JLabel mConnectedPortLabel;

	private JLabel jLabel2;

	private HashMap<String, JLabel> commandSizeLabels = new HashMap<String, JLabel>();

	private HashMap<String, JLabel> commandAmountLabels = new HashMap<String, JLabel>();

	private HashMap<String, JLabel> responseSizeLabels = new HashMap<String, JLabel>();

	private HashMap<String, JLabel> responseAmountLabels = new HashMap<String, JLabel>();

	private JLabel mConnectedServerLabel;

	private JLabel jLabel1;

	private JButton okButton;

	private JPanel jPanel1;

	private JPanel securityTab;

	private JPanel connectionTab;

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
				ConfigureOpenGrooveDialog inst = new ConfigureOpenGrooveDialog(
						frame);
				inst.setVisible(true);
			}
		});
	}

	public ConfigureOpenGrooveDialog(JFrame frame)
	{
		super(frame, true);
		initGUI();
	}

	private void initGUI()
	{
		try
		{
			// START >> this
			// END << this
			BorderLayout thisLayout = new BorderLayout();
			getContentPane().setLayout(thisLayout);
			// START >> optionsTabbedPane
			optionsTabbedPane = new JTabbedPane();
			getContentPane().add(optionsTabbedPane, BorderLayout.CENTER);
			optionsTabbedPane.setTabPlacement(JTabbedPane.LEFT);
			optionsTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			// START >> generalPanel
			generalPanel = new JPanel();
			optionsTabbedPane.addTab("General", null, generalPanel, null);
			//START >>  jLabel9
			jLabel9 = new JLabel();
			generalPanel.add(jLabel9);
			jLabel9.setText("<html>We're still working on the Options dialog.<br/>Check out the other tabs to see what we have done so far.");
			jLabel9.setPreferredSize(new java.awt.Dimension(450, 69));
			//END <<  jLabel9
			// END << generalPanel
			// START >> updatesTab
			// START >> checkForUpdatesButton
			// END << checkForUpdatesButton
			// START >> jLabel6
			// END << jLabel6
			// END << updatesTab
			// START >> connectionTab
			connectionTab = new JPanel();
			optionsTabbedPane.addTab("Connection", null, connectionTab, null);
			connectionTab.setLayout(null);
			// START >> jLabel1
			jLabel1 = new JLabel();
			connectionTab.add(jLabel1);
			jLabel1.setText("You are currently connected to this server:");
			jLabel1.setBounds(32, 78, 293, 20);
			jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
			// END << jLabel1
			// START >> mConnectedServerLabel
			mConnectedServerLabel = new JLabel();
			connectionTab.add(getMConnectedServerLabel());
			mConnectedServerLabel.setText("unknown");
			mConnectedServerLabel.setBounds(97, 104, 366, 19);
			mConnectedServerLabel.setFont(new java.awt.Font("Dialog", 1, 12));
			// END << mConnectedServerLabel
			// START >> jLabel2
			jLabel2 = new JLabel();
			connectionTab.add(jLabel2);
			jLabel2.setText("On this port:");
			jLabel2.setBounds(32, 135, 281, 17);
			jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
			// END << jLabel2
			// START >> mConnectedPortLabel
			mConnectedPortLabel = new JLabel();
			connectionTab.add(getMConnectedPortLabel());
			mConnectedPortLabel.setText("unknown");
			mConnectedPortLabel.setBounds(97, 158, 176, 18);
			mConnectedPortLabel.setFont(new java.awt.Font("Dialog", 1, 12));
			// START >> mConnectivityStatus
			mConnectivityStatus = new JLabel();
			connectionTab.add(getMConnectivityStatus());
			mConnectivityStatus.setText("unknown");
			mConnectivityStatus.setBounds(97, 51, 319, 21);
			mConnectivityStatus.setFont(new java.awt.Font("Dialog", 1, 12));
			// END << mConnectivityStatus
			// START >> jLabel3
			jLabel3 = new JLabel();
			connectionTab.add(jLabel3);
			jLabel3.setText("Your current connectivity status is:");
			jLabel3.setBounds(32, 29, 197, 17);
			jLabel3.setFont(new java.awt.Font("Dialog", 0, 12));
			// END << jLabel3
			// START >> jLabel4
			jLabel4 = new JLabel();
			connectionTab.add(jLabel4);
			jLabel4.setText("Using a");
			jLabel4.setBounds(32, 191, 65, 16);
			jLabel4.setFont(new java.awt.Font("Dialog", 0, 12));
			// END << jLabel4
			// START >> mConnectionSecurityStatus
			mConnectionSecurityStatus = new JLabel();
			connectionTab.add(getMConnectionSecurityStatus());
			mConnectionSecurityStatus.setText("unknown");
			mConnectionSecurityStatus.setBounds(97, 191, 99, 16);
			// END << mConnectionSecurityStatus
			// START >> jLabel5
			jLabel5 = new JLabel();
			connectionTab.add(jLabel5);
			jLabel5.setText("connection");
			jLabel5.setBounds(196, 191, 63, 16);
			jLabel5.setFont(new java.awt.Font("Dialog", 0, 12));
			// START >> jLabel6
			jLabel6 = new JLabel();
			connectionTab.add(jLabel6);
			jLabel6.setText("<html><font size='5'>Transmission:</font>");
			jLabel6.setBounds(170, 243, 164, 32);
			jLabel6.setHorizontalAlignment(SwingConstants.CENTER);
			// END << jLabel6
			// START >> jLabel7
			jLabel7 = new JLabel();
			connectionTab.add(jLabel7);
			jLabel7.setText("<html><font size='4'>Me &rarr; Server</font>");
			jLabel7.setBounds(80, 285, 105, 22);
			// END << jLabel7
			// START >> jLabel8
			jLabel8 = new JLabel();
			connectionTab.add(jLabel8);
			jLabel8.setText("<html><font size='4'>Server &rarr; Me</font>");
			jLabel8.setBounds(325, 287, 92, 19);
			// END << jLabel8
			// START >> jScrollPane1
			jScrollPane1 = new JScrollPane();
			connectionTab.add(jScrollPane1);
			jScrollPane1.setBounds(12, 318, 235, 200);
			// START >> jPanel2
			jPanel2 = new JPanel();
			FlowLayout jPanel2Layout = new FlowLayout();
			jScrollPane1.setViewportView(jPanel2);
			jPanel2.setBounds(32, 331, 207, 188);
			jPanel2.setLayout(jPanel2Layout);
			// START >> commandStatPanel
			commandStatPanel = new JPanel();
			GridLayout commandStatPanelLayout = new GridLayout(0, 2);
			commandStatPanelLayout.setHgap(5);
			commandStatPanelLayout.setVgap(5);
			commandStatPanel.setLayout(commandStatPanelLayout);
			jPanel2.add(getCommandStatPanel());
			// END << commandStatPanel
			// END << jPanel2
			// END << jScrollPane1
			// START >> jScrollPane2
			jScrollPane2 = new JScrollPane();
			connectionTab.add(jScrollPane2);
			jScrollPane2.setBounds(247, 318, 231, 200);
			// START >> jPanel3
			jPanel3 = new JPanel();
			FlowLayout jPanel3Layout = new FlowLayout();
			jScrollPane2.setViewportView(jPanel3);
			jPanel3.setLayout(jPanel3Layout);
			jPanel3.setBounds(32, 331, 207, 188);
			// START >> responseStatPanel
			responseStatPanel = new JPanel();
			GridLayout jPanel4Layout = new GridLayout(0, 2);
			jPanel4Layout.setColumns(2);
			jPanel4Layout.setRows(0);
			jPanel4Layout.setHgap(5);
			jPanel4Layout.setVgap(5);
			jPanel3.add(responseStatPanel);
			responseStatPanel.setLayout(jPanel4Layout);
			// END << responseStatPanel
			// END << jPanel3
			// END << jScrollPane2
			// END << jLabel5
			// END << mConnectedPortLabel
			// END << connectionTab
			try
			{
				// add a statisticslistener for the connection transmissions
				// section of the connection tab
				StatisticsListener statListener = new StatisticsListener()
				{

					public synchronized void statsUpdated()
					{
						if (isDisposed)
						{
							System.out
									.println("configure convergia window disposed, removing stat listener");
							OpenGroove.ocom.communicator
									.removeStatisticsListener(this);
							return;
						}
						for (Map.Entry<String, Long> e : OpenGroove.ocom.communicator.commandAmounts
								.entrySet())
						{
							String command = e.getKey();
							long amount = e.getValue();
							long size = OpenGroove.ocom.communicator.commandSizes
									.get(command);
							if (commandAmountLabels.get(command) == null)
							{
								JLabel label = new JLabel(command.toLowerCase()
										+ ":");
								OpenGroove.setPlainFont(label);
								JLabel amountLabel = new JLabel();
								OpenGroove.setPlainFont(amountLabel);
								JLabel sizeLabel = new JLabel();
								OpenGroove.setPlainFont(sizeLabel);
								commandAmountLabels.put(command, amountLabel);
								commandSizeLabels.put(command, sizeLabel);
								commandStatPanel.add(label);
								JPanel p1c = new JPanel();
								p1c.setLayout(new BorderLayout());
								p1c.add(amountLabel, BorderLayout.WEST);
								p1c.add(sizeLabel, BorderLayout.CENTER);
								commandStatPanel.add(p1c);
							}
							commandAmountLabels.get(command).setText(
									"" + amount + "|");
							commandSizeLabels.get(command).setText(
									OpenGroove.formatDataSize(size));
						}
						for (Map.Entry<String, Long> e : OpenGroove.ocom.communicator.responseAmounts
								.entrySet())
						{
							String response = e.getKey();
							long amount = e.getValue();
							long size = OpenGroove.ocom.communicator.responseSizes
									.get(response);
							if (responseAmountLabels.get(response) == null)
							{
								JLabel label = new JLabel(response
										.toLowerCase()
										+ ":");
								OpenGroove.setPlainFont(label);
								JLabel amountLabel = new JLabel();
								OpenGroove.setPlainFont(amountLabel);
								JLabel sizeLabel = new JLabel();
								OpenGroove.setPlainFont(sizeLabel);
								responseAmountLabels.put(response, amountLabel);
								responseSizeLabels.put(response, sizeLabel);
								responseStatPanel.add(label);
								JPanel p1c = new JPanel();
								p1c.setLayout(new BorderLayout());
								p1c.add(amountLabel, BorderLayout.WEST);
								p1c.add(sizeLabel, BorderLayout.CENTER);
								responseStatPanel.add(p1c);

							}
							responseAmountLabels.get(response).setText(
									"" + amount + "|");
							responseSizeLabels.get(response).setText(
									OpenGroove.formatDataSize(size));
						}
					}
				};
				statListener.statsUpdated();
				OpenGroove.ocom.communicator.addStatisticsListener(statListener);
			} catch (Exception ex1)
			{
				ex1.printStackTrace();
			}
			// START >> securityTab
			securityTab = new JPanel();
			optionsTabbedPane.addTab("Security", null, securityTab, null);
			// START >> advancedTab
			advancedTab = new JPanel();
			optionsTabbedPane.addTab("Advanced", null, advancedTab, null);
			advancedTab.setLayout(null);
			// START >> notificationUntilClickedTestButton
			notificationUntilClickedTestButton = new JButton();
			advancedTab.add(getNotificationUntilClickedTestButton());
			notificationUntilClickedTestButton
					.setText("Test clickable notification");
			notificationUntilClickedTestButton.setBounds(82, 47, 191, 26);
			notificationUntilClickedTestButton
					.addActionListener(new ActionListener()
					{

						public void actionPerformed(ActionEvent e)
						{
							OpenGroove.notificationFrame.addNotification(
									new NotificationAdapter(new JLabel(
											"test notification at "
													+ new Date()), true, false)
									{
										public void clicked()
										{
											OpenGroove.notificationFrame
													.removeNotification(this);
										}
									}, false);
						}
					});
			// END << notificationUntilClickedTestButton
			// START >> notificationOneTimeTestButton
			notificationOneTimeTestButton = new JButton();
			advancedTab.add(getNotificationOneTimeTestButton());
			notificationOneTimeTestButton.setText("Test one-time notification");
			notificationOneTimeTestButton.setBounds(82, 84, 191, 26);
			notificationOneTimeTestButton
					.addActionListener(new ActionListener()
					{

						public void actionPerformed(ActionEvent e)
						{
							OpenGroove.notificationFrame
									.addNotification(
											new NotificationAdapter(new JLabel(
													"test notification at "
															+ new Date()),
													false, true), true);
						}
					});
			// END << notificationOneTimeTestButton
			// END << advancedTab
			// END << securityTab
			// END << optionsTabbedPane
			// START >> jPanel1
			jPanel1 = new JPanel();
			BorderLayout jPanel1Layout = new BorderLayout();
			jPanel1.setLayout(jPanel1Layout);
			getContentPane().add(jPanel1, BorderLayout.SOUTH);
			// START >> okButton
			okButton = new JButton();
			okButton.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					hide();
				}
			});
			jPanel1.add(okButton, BorderLayout.EAST);
			okButton.setText("OK");
			// END << okButton
			// END << jPanel1
			setSize(600, 600);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public JLabel getMConnectedServerLabel()
	{
		return mConnectedServerLabel;
	}

	public JLabel getMConnectedPortLabel()
	{
		return mConnectedPortLabel;
	}

	public JLabel getMConnectivityStatus()
	{
		return mConnectivityStatus;
	}

	public JLabel getMConnectionSecurityStatus()
	{
		return mConnectionSecurityStatus;
	}

	public JButton getNotificationUntilClickedTestButton()
	{
		return notificationUntilClickedTestButton;
	}

	public JButton getNotificationOneTimeTestButton()
	{
		return notificationOneTimeTestButton;
	}

	public JPanel getCommandStatPanel()
	{
		return commandStatPanel;
	}

	private boolean isDisposed = false;

	public void dispose()
	{
		super.dispose();
		isDisposed = true;
	}

}
