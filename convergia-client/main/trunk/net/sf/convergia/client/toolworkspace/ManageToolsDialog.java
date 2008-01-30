package net.sf.convergia.client.toolworkspace;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JList;
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
public class ManageToolsDialog extends javax.swing.JDialog
{
	private JList toolList;

	private JScrollPane jScrollPane1;

	private JButton renameButton;

	private JButton removeButton;

	private JButton downButton;

	private JButton upButton;

	private JButton okButton;

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
				ManageToolsDialog inst = new ManageToolsDialog(frame);
				inst.setVisible(true);
			}
		});
	}

	public ManageToolsDialog(JFrame frame)
	{
		super(frame,true);
		initGUI();
	}

	private void initGUI()
	{
		try
		{
			// START >> this
			getContentPane().setLayout(null);
			// START >> jScrollPane1
			jScrollPane1 = new JScrollPane();
			getContentPane().add(jScrollPane1);
			jScrollPane1.setBounds(34, 23, 292, 458);
			// START >> toolList
			ListModel toolListModel = new DefaultComboBoxModel(new String[]
			{ "Item One", "Item Two" });
			toolList = new JList();
			jScrollPane1.setViewportView(toolList);
			toolList.setModel(toolListModel);
			toolList.setBounds(34, 23, 292, 458);
			// END << toolList
			// END << jScrollPane1
			// START >> okButton
			okButton = new JButton();
			getContentPane().add(getOkButton());
			okButton.setText("ok");
			okButton.setBounds(536, 538, 48, 26);
			okButton.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					hide();
				}
			});
			// END << okButton
			// START >> upButton
			upButton = new JButton();
			getContentPane().add(getUpButton());
			upButton.setText("up");
			upButton.setBounds(354, 122, 88, 26);
			// END << upButton
			// START >> downButton
			downButton = new JButton();
			getContentPane().add(getDownButton());
			downButton.setText("down");
			downButton.setBounds(354, 159, 88, 26);
			// END << downButton
			// START >> removeButton
			removeButton = new JButton();
			getContentPane().add(getRemoveButton());
			removeButton.setText("remove");
			removeButton.setBounds(354, 233, 88, 26);
			// END << removeButton
			// START >> renameButton
			renameButton = new JButton();
			getContentPane().add(getRenameButton());
			renameButton.setText("rename");
			renameButton.setBounds(354, 196, 88, 26);
			// END << renameButton
			// END << this
			this.setSize(600, 600);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public JButton getOkButton()
	{
		return okButton;
	}

	public JButton getUpButton()
	{
		return upButton;
	}

	public JButton getDownButton()
	{
		return downButton;
	}

	public JButton getRemoveButton()
	{
		return removeButton;
	}

	public JButton getRenameButton()
	{
		return renameButton;
	}

}
