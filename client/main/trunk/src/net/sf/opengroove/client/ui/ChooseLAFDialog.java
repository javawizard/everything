package net.sf.opengroove.client.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
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
public class ChooseLAFDialog extends javax.swing.JDialog
{
	private JLabel jLabel1;

	private JPanel lookAndFeelPanel;

	private JScrollPane jScrollPane1;

	private JButton cancelButton;

	private JButton okButton;

	private boolean clickedOk;

	public boolean wasOkClicked()
	{
		return clickedOk;
	}

	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				ChooseLAFDialog inst = new ChooseLAFDialog(null);
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public ChooseLAFDialog(JFrame parent)
	{
		super(parent, true);
		initGUI();
	}

	private void initGUI()
	{
		try
		{
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			getContentPane().setLayout(null);
			// START >> jLabel1
			jLabel1 = new JLabel();
			getContentPane().add(jLabel1);
			jLabel1.setText("Select a Look And Feel to use:");
			jLabel1.setBounds(40, 39, 276, 22);
			// END << jLabel1
			// START >> jScrollPane1
			jScrollPane1 = new JScrollPane();
			getContentPane().add(jScrollPane1);
			jScrollPane1.setBounds(40, 93, 285, 204);
			// START >> lookAndFeelPanel
			lookAndFeelPanel = new JPanel();
			BoxLayout lookAndFeelPanelLayout = new BoxLayout(lookAndFeelPanel,
					javax.swing.BoxLayout.Y_AXIS);
			lookAndFeelPanel.setLayout(lookAndFeelPanelLayout);
			jScrollPane1.setViewportView(getLookAndFeelPanel());
			lookAndFeelPanel.setBounds(40, 93, 276, 204);
			// END << lookAndFeelPanel
			// END << jScrollPane1
			// START >> okButton
			okButton = new JButton();
			getContentPane().add(getOkButton());
			okButton.setText("ok");
			okButton.setBounds(244, 359, 48, 26);
			okButton.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					clickedOk = true;
					hide();
				}
			});
			// END << okButton
			// START >> cancelButton
			cancelButton = new JButton();
			getContentPane().add(getCancelButton());
			cancelButton.setText("cancel");
			cancelButton.setBounds(292, 359, 72, 26);
			cancelButton.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					clickedOk = false;
					hide();
				}
			});
			// END << cancelButton
			pack();
			this.setSize(380, 421);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public JPanel getLookAndFeelPanel()
	{
		return lookAndFeelPanel;
	}

	public JButton getOkButton()
	{
		return okButton;
	}

	public JButton getCancelButton()
	{
		return cancelButton;
	}

}
