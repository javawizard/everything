package net.sf.opengroove.client.toolworkspace;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
public class AddToolDialog extends javax.swing.JDialog
{
	private JButton addButton;

	private boolean clickedOk;

	private JScrollPane jScrollPane1;

	private JPanel toolsPanel;

	private JButton cancelButon;

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
				AddToolDialog inst = new AddToolDialog(frame);
				inst.setVisible(true);
			}
		});
	}

	public AddToolDialog(JFrame frame)
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
			// START >> jLabel1
			jLabel1 = new JLabel();
			getContentPane().add(jLabel1);
			jLabel1
					.setText("Choose which type of tool you want, then click add.");
			jLabel1.setBounds(31, 25, 450, 20);
			// END << jLabel1
			// START >> cancelButon
			cancelButon = new JButton();
			getContentPane().add(getCancelButon());
			cancelButon.setText("cancel");
			cancelButon.setBounds(462, 488, 72, 26);
			cancelButon.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					clickedOk = false;
					hide();
				}
			});
			// END << cancelButon
			// START >> addButton
			addButton = new JButton();
			getContentPane().add(getAddButton());
			addButton.setText("add");
			addButton.setBounds(407, 488, 55, 26);
			addButton.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					clickedOk = true;
					hide();
				}
			});
			// END << addButton
			// START >> jScrollPane1
			jScrollPane1 = new JScrollPane();
			getContentPane().add(jScrollPane1);
			jScrollPane1.setBounds(31, 67, 461, 354);
			// START >> toolsPanel
			toolsPanel = new JPanel();
			jScrollPane1.setViewportView(getToolsPanel());
			toolsPanel.setBounds(31, 67, 461, 354);
			toolsPanel.setBackground(new java.awt.Color(238, 238, 238));
			// END << toolsPanel
			// END << jScrollPane1
			// END << this
			this.setSize(550, 550);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public JButton getCancelButon()
	{
		return cancelButon;
	}

	public JButton getAddButton()
	{
		return addButton;
	}

	public JPanel getToolsPanel()
	{
		return toolsPanel;
	}

	public boolean isClickedOk()
	{
		return clickedOk;
	}

}
