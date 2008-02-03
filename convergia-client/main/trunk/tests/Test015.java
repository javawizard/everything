package tests;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class Test015
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// this test is to experiment with rendering stuff in a JEditorPane
		JFrame f = new JFrame();
		f.setSize(500, 400);
		f.setLocationRelativeTo(null);
		JEditorPane p = new JEditorPane();
		f.getContentPane().add(p);
		p.setEditable(false);
		p.setContentType("text/html");
		p
				.setText("hello there. <a href='/'>this is a link</a>. <span style='display:none'>this should not be here.</span>"
						+ "<form action='/formaction'><input type='checkbox' bogusatt='bogusvalue'>this is a checkbox.<bogustag>inside a bogus tag.</bogustag> "
						+ "<input type='submit' name='buttonname' value='buttonvalue'/></form>");
		HTMLDocument doc = (HTMLDocument) p.getDocument();
		f.invalidate();
		f.validate();
		f.repaint();
		f.show();
	}

}
