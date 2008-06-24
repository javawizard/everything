package net.sf.opengroove.client.tools.games.multiplayer;

import com.l2fprod.common.swing.JLinkButton;
import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import javax.swing.WindowConstants;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
public class JoinGamePanel extends javax.swing.JPanel
{
	private JLabel jLabel1;

	private JPanel participantPanel;

	private JPanel jPanel1;

	private JScrollPane jScrollPane1;
	private JPanel jPanel3;

	private JLinkButton helpLinkButton;

	private JPanel jPanel2;

	private JButton startButton;

	private JButton leaveButton;

	private JButton joinButton;

	/**
	 * Auto-generated main method to display this JPanel inside a new JFrame.
	 */
	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.getContentPane().add(new JoinGamePanel());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	public JoinGamePanel()
	{
		super();
		initGUI();
	}

	private void initGUI()
	{
		try
		{
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);
			this.setPreferredSize(new java.awt.Dimension(474, 366));
			// START >> jPanel2
			jPanel2 = new JPanel();
			BorderLayout jPanel2Layout = new BorderLayout();
			jPanel2.setLayout(jPanel2Layout);
			this.add(jPanel2, BorderLayout.NORTH);
			jPanel2.setPreferredSize(new java.awt.Dimension(474, 16));
			// START >> jLabel1
			jLabel1 = new JLabel();
			jPanel2.add(jLabel1, BorderLayout.CENTER);
			jLabel1.setText("Current participants:");
			jLabel1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			// END << jLabel1
			// START >> helpLinkButton
			helpLinkButton = new JLinkButton();
			jPanel2.add(helpLinkButton, BorderLayout.EAST);
			helpLinkButton.setText("<html><u>Help</u> &nbsp;");
			helpLinkButton.setForeground(new java.awt.Color(0, 0, 255));
			helpLinkButton.setFocusable(false);
			helpLinkButton.setBorder(null);
			helpLinkButton.setHorizontalAlignment(helpLinkButton.RIGHT);
			// END << helpLinkButton
			// END << jPanel2
			// START >> jScrollPane1
			jScrollPane1 = new JScrollPane();
			this.add(jScrollPane1, BorderLayout.CENTER);
			jScrollPane1.setPreferredSize(new java.awt.Dimension(398, 350));
			//START >>  jPanel3
			jPanel3 = new JPanel();
			FlowLayout jPanel3Layout = new FlowLayout();
			jPanel3Layout.setAlignment(FlowLayout.LEFT);
			jScrollPane1.setViewportView(jPanel3);
			jPanel3.setPreferredSize(new java.awt.Dimension(395, 347));
			jPanel3.setLayout(jPanel3Layout);

			participantPanel = new JPanel();
			jPanel3.add(participantPanel);
			BoxLayout participantPanelLayout = new BoxLayout(participantPanel,
					javax.swing.BoxLayout.Y_AXIS);
			participantPanel.setLayout(participantPanelLayout);

			//END <<  jPanel3
			// START >> participantPanel
			// END << participantPanel
			// END << jScrollPane1
			// START >> jPanel1
			jPanel1 = new JPanel();
			jPanel1.setLayout(null);
			this.add(jPanel1, BorderLayout.EAST);
			jPanel1.setPreferredSize(new java.awt.Dimension(76, 350));
			// START >> joinButton
			joinButton = new JButton();
			jPanel1.add(joinButton);
			joinButton.setText("Join");
			joinButton.setBounds(0, 0, 76, 26);
			joinButton.setFocusable(false);
			joinButton.setEnabled(false);
			// END << joinButton
			// START >> leaveButton
			leaveButton = new JButton();
			jPanel1.add(leaveButton);
			leaveButton.setText("Leave");
			leaveButton.setBounds(0, 26, 76, 26);
			leaveButton.setFocusable(false);
			leaveButton.setEnabled(false);
			// END << leaveButton
			// START >> startButton
			startButton = new JButton();
			jPanel1.add(startButton);
			startButton.setText("Start");
			startButton.setBounds(0, 63, 76, 26);
			startButton.setFocusable(false);
			// END << startButton
			// END << jPanel1
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public JPanel getParticipantPanel()
	{
		return participantPanel;
	}

	public JButton getJoinButton()
	{
		return joinButton;
	}

	public JButton getLeaveButton()
	{
		return leaveButton;
	}

	public JButton getStartButton()
	{
		return startButton;
	}

	public JLinkButton getHelpButton()
	{
		return helpLinkButton;
	}

}
