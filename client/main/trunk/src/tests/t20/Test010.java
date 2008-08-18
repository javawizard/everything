package tests.t20;

import java.awt.Font;
import java.util.Date;

import javax.swing.*;

public class Test010
{

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException
	{
		// tests how much memory a JEditorPane with chat messages takes up
		JFrame frame = new JFrame();
		frame.setSize(400, 500);
		frame.setLocationRelativeTo(null);
		JEditorPane ed = new JEditorPane("text/html",
				"<html><body></body></html>");
		StringBuffer b = new StringBuffer();
		frame.getContentPane().add(new JScrollPane(ed));
		frame.show();
		frame.invalidate();
		frame.validate();
		frame.repaint();
		int n = 0;
		while (true)
		{
			n++;
			Thread.sleep(10);
			b.append("<font color='#000000'>test1</font><font color='#888888'>" + new Date() + "</font><br/>This is message " + n + ", and blah blah blah this is some text and this is some more chat text and anyway hello.<br/><br/>");
			ed.setText("<html><body><font size='3' family='sans-serif'>" + b.toString() + "</font></body></html>");
			System.gc();
			System.out.println("at " + n + ", mx=" + Runtime.getRuntime().maxMemory() + ",mt=" + Runtime.getRuntime().totalMemory() + ",mf=" + Runtime.getRuntime().freeMemory());
		}
	}

}
