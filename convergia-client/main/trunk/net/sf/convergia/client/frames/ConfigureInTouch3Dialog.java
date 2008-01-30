package net.sf.convergia.client.frames;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import net.sf.convergia.client.InTouch3;
import net.sf.convergia.client.notification.NotificationAdapter;


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
public class ConfigureInTouch3Dialog extends javax.swing.JDialog
{
	private JTabbedPane optionsTabbedPane;

	private JPanel generalPanel;

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
				ConfigureInTouch3Dialog inst = new ConfigureInTouch3Dialog(
						frame);
				inst.setVisible(true);
			}
		});
	}

	public ConfigureInTouch3Dialog(JFrame frame)
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
			// END << jLabel5
			// END << mConnectedPortLabel
			// END << connectionTab
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
							InTouch3.notificationFrame.addNotification(
									new NotificationAdapter(new JLabel(
											"test notification at "
													+ new Date()), true, false)
									{
										public void clicked()
										{
											InTouch3.notificationFrame
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
							InTouch3.notificationFrame
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

}
