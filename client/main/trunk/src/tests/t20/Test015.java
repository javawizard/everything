package tests.t20;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.FormSubmitEvent;
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
		p.setOpaque(false);
		p.setEditable(false);
		p.setContentType("text/html");
		p
				.setText("hello there. <a href='/'>this is a link</a>. <span style='display:none'>this should not be here.</span>"
						+ "<form action='about:installtools'><input type='checkbox' name='chkname' value='chkv'>this is a checkbox.<bogustag>inside a bogus tag.</bogustag> "
						+ "<input type='submit' name='buttonname' value='buttonvalue'/></form><br/>"
						+ "<table border='0' cellspacing='5' celpadding='0'>"
						+ "<tr><td>1,1</td><td>1,2</td><td>1,3</td></tr>"
						+ "<tr><td>2,1</td><td>2,2</td><td>3,2</td></tr></table>");
		((HTMLEditorKit) p.getEditorKit()).setAutoFormSubmission(false);
		p.addHyperlinkListener(new HyperlinkListener()
		{

			public void hyperlinkUpdate(HyperlinkEvent e)
			{
				System.out.println(e);
				if (e instanceof FormSubmitEvent)
				{
					FormSubmitEvent fe = (FormSubmitEvent) e;
					System.out.println(fe.getURL());
					System.out.println(fe.getData());
					System.out.println(fe.getDescription());
				}
			}
		});
		HTMLDocument doc = (HTMLDocument) p.getDocument();
		f.invalidate();
		f.validate();
		f.repaint();
		f.show();
	}

}
