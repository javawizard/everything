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
import javax.swing.JTextField;
import javax.swing.JToggleButton;
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
public class CreateWorkspaceDialog extends javax.swing.JDialog
{
	private JLabel jLabel2;

	private JPanel jPanel1;

	private JPanel typePanel;

	private JScrollPane jScrollPane1;

	private JButton okButton;

	private JButton cancelButton;

	private JLabel jLabel1;

	private JTextField nameField;

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
				CreateWorkspaceDialog inst = new CreateWorkspaceDialog(frame);
				inst.setVisible(true);
			}
		});
	}

	public CreateWorkspaceDialog(JFrame frame)
	{
		super(frame, true);
		initGUI();
	}

	private void initGUI()
	{
		try
		{
			{
				getContentPane().setLayout(null);
				{
					jLabel2 = new JLabel();
					getContentPane().add(jLabel2);
					jLabel2.setText("Name of workspace:");
					jLabel2.setBounds(63, 17, 198, 25);
				}
				{
					nameField = new JTextField();
					getContentPane().add(getNameField());
					nameField.setBounds(63, 48, 301, 20);
				}
				{
					jPanel1 = new JPanel();
					BorderLayout jPanel1Layout = new BorderLayout();
					jPanel1.setLayout(jPanel1Layout);
					getContentPane().add(jPanel1);
					jPanel1.setBounds(51, 143, 372, 298);
					{
						jScrollPane1 = new JScrollPane();
						jPanel1.add(jScrollPane1, BorderLayout.CENTER);
						jScrollPane1.setPreferredSize(new java.awt.Dimension(
								372, 252));
						{
							typePanel = new JPanel();
							BoxLayout typePanelLayout = new BoxLayout(
									typePanel, javax.swing.BoxLayout.Y_AXIS);
							typePanel.setLayout(typePanelLayout);
							jScrollPane1.setViewportView(getTypePanel());
						}
					}
				}
				{
					jLabel1 = new JLabel();
					getContentPane().add(jLabel1);
					jLabel1.setText("Type of workspace:");
					jLabel1.setBounds(63, 106, 209, 25);
				}
				// START >> cancelButton
				cancelButton = new JButton();
				getContentPane().add(cancelButton);
				cancelButton.setText("cancel");
				cancelButton.setBounds(412, 488, 72, 26);
				cancelButton.addActionListener(new ActionListener()
				{

					public void actionPerformed(ActionEvent e)
					{
						wasOkClicked = false;
						hide();
					}
				});
				// END << cancelButton
				// START >> okButton
				okButton = new JButton();
				getContentPane().add(okButton);
				okButton.setText("ok");
				okButton.setBounds(365, 488, 48, 26);
				okButton.addActionListener(new ActionListener()
				{

					public void actionPerformed(ActionEvent e)
					{
						wasOkClicked = true;
						hide();
					}
				});
				// END << okButton
			}
			setSize(500, 550);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private boolean wasOkClicked = false;

	public boolean wasOkClicked()
	{
		return wasOkClicked;
	}

	public JTextField getNameField()
	{
		return nameField;
	}

	public JPanel getTypePanel()
	{
		return typePanel;
	}

}
