package net.sf.opengroove.client;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
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
public class WaitingDialog extends javax.swing.JDialog
{
	private JLabel mainLabel;

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
				WaitingDialog inst = new WaitingDialog(frame);
				inst.setVisible(true);
			}
		});
	}

	public WaitingDialog(JFrame frame)
	{
		super(frame, true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		initGUI();
	}

	private void initGUI()
	{
		try
		{
			{
			}
			BorderLayout thisLayout = new BorderLayout();
			getContentPane().setLayout(thisLayout);
			{
				mainLabel = new JLabel();
				getContentPane().add(getMainLabel(), BorderLayout.CENTER);
				mainLabel.setText("MESSAGE GOES HERE");
				mainLabel.setHorizontalAlignment(SwingConstants.CENTER);
			}
			this.setSize(300, 50);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public JLabel getMainLabel()
	{
		invalidate();
		validate();
		repaint();
		return mainLabel;
	}

}
