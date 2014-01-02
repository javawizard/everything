package net.sf.opengroove.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
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
public class ImportWorkspaceDialog extends javax.swing.JDialog
{
	private JButton okButton;

	private JButton cancelButton;

	private JTextField workspaceId;

	private boolean wasOkClicked = false;

	private JLabel jLabel1;

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
				ImportWorkspaceDialog inst = new ImportWorkspaceDialog(frame);
				inst.setVisible(true);
			}
		});
	}

	public ImportWorkspaceDialog(JFrame frame)
	{
		super(frame, true);
		initGUI();
	}

	private void initGUI()
	{
		try
		{
			// START >> this
			getContentPane().setLayout(null);
			// START >> okButton
			okButton = new JButton();
			getContentPane().add(okButton);
			okButton.setText("ok");
			okButton.setBounds(264, 338, 48, 26);
			okButton.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					wasOkClicked = true;
					hide();
				}
			});
			// END << okButton
			// START >> cancelButton
			cancelButton = new JButton();
			getContentPane().add(cancelButton);
			cancelButton.setText("cancel");
			cancelButton.setBounds(312, 338, 72, 26);
			cancelButton.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					wasOkClicked = false;
					hide();
				}
			});
			// END << cancelButton
			// START >> jLabel1
			jLabel1 = new JLabel();
			getContentPane().add(jLabel1);
			jLabel1.setText("Type the ID of the workspace, then click OK.");
			jLabel1.setBounds(23, 21, 332, 32);
			jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
			// END << jLabel1
			// START >> workspaceId
			workspaceId = new JTextField();
			getContentPane().add(getWorkspaceId());
			workspaceId.setBounds(53, 65, 265, 20);
			// END << workspaceId
			// END << this
			this.setSize(400, 400);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public JTextField getWorkspaceId()
	{
		return workspaceId;
	}

	public boolean isWasOkClicked()
	{
		return wasOkClicked;
	}

}
