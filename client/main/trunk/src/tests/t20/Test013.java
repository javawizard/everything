package tests.t20;

import java.awt.BorderLayout;

import javax.swing.*;

import net.sf.opengroove.client.IMenu;


public class Test013
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// test for adding a JMenuBar to a JPanel
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
		JMenuBar mb = new JMenuBar();
		mb.add(new IMenu("testmenu", new JMenuItem[]
		{ new JMenuItem("testitem"), new JMenuItem("another item") }));
		p.add(mb, BorderLayout.NORTH);
		JFrame f = new JFrame();
		f.setSize(400, 300);
		f.setLocationRelativeTo(null);
		f.getContentPane().add(p);
		f.show();
	}

}
