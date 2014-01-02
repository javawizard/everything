package net.sf.opengroove.client.ui;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class PluginUpdateDialog extends javax.swing.JDialog
{
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
				PluginUpdateDialog inst = new PluginUpdateDialog(frame);
				inst.setVisible(true);
			}
		});
	}

	public PluginUpdateDialog(JFrame frame)
	{
		super(frame, true);
		initGUI();
	}

	private void initGUI()
	{
		try
		{
			//START >>  this
			getContentPane().setLayout(null);
			//START >>  jLabel1
			jLabel1 = new JLabel();
			getContentPane().add(jLabel1);
			jLabel1.setText("<html>The following updates have been found. Select which ones you would like to install, then click \"install\". You will be reminded later to install ones that you don't select.");
			jLabel1.setBounds(12, 12, 560, 38);
			//END <<  jLabel1
			//END <<  this
			this.setSize(600, 350);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
