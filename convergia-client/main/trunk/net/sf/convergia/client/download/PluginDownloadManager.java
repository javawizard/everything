package net.sf.convergia.client.download;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.html.FormSubmitEvent;
import javax.swing.text.html.HTMLEditorKit;

import net.sf.convergia.client.Convergia;

public class PluginDownloadManager
{
	public static final String PLUGIN_DOWNLOAD_URL = "http://trivergia.com:8080/convergiaptl.jsp";

	// parameters are installedplugins which is comma-separated, and plugintypes
	// which is the type
	// of plugins to show (comma-separated), or null (IE nonexistant) to show
	// all plugin types
	/**
	 * this method prompts the user to select plugins on the ptl that they wish
	 * to download. this method should only be called by Convergia. If you are a
	 * developer creating your own plugin, consider using
	 * Convergia.promptForPluginDownload(), as it handles generating a lot of
	 * arguments that have to be passed into here.
	 * 
	 * @throws MalformedURLException
	 */
	public static void promptForDownload(JFrame parent, String[] types,
			String[] alreadyInstalled) throws MalformedURLException
	{
		JDialog dialog = new JDialog(parent, true);
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.setSize(450, 550);
		dialog.setLocationRelativeTo(parent);
		final JEditorPane p = new JEditorPane();
		p.setContentType("text/html");
		p.setEditable(false);
		p.setOpaque(false);
		dialog.getContentPane().add(new JScrollPane(p), BorderLayout.CENTER);
		JPanel topControls = new JPanel();
		topControls.setLayout(new BorderLayout());
		final ArrayList<URL> trail = new ArrayList<URL>();
		trail.add(createMainUrl(types, alreadyInstalled));
		final JButton backButton = new JButton();
		backButton.setIcon(new ImageIcon(Convergia.Icons.BACK_BUTTON_32
				.getImage()));
		topControls.add(backButton, BorderLayout.WEST);
		dialog.getContentPane().add(topControls, BorderLayout.NORTH);
		backButton.setEnabled(false);
		backButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				if (trail.size() <= 1)
				{
					backButton.setEnabled(false);
					return;
				}
				trail.remove(trail.size() - 1);
				if (trail.size() <= 1)
				{
					backButton.setEnabled(false);
				} else
				{
					backButton.setEnabled(true);
				}
				try
				{
					p.setPage(trail.get(trail.size() - 1));
				} catch (IOException e1)
				{
					e1.printStackTrace();
					setErrorPage(p);
				}
			}
		});
		((HTMLEditorKit) p.getEditorKit()).setAutoFormSubmission(false);
		p.addHyperlinkListener(new HyperlinkListener()
		{

			public void hyperlinkUpdate(HyperlinkEvent e)
			{
				if (!e.getEventType().equals(EventType.ACTIVATED))
					return;
				final URL url = e.getURL();
				String urlString = e.getDescription();
				if (url == null && urlString.startsWith("installtool:"))
				{
					// FIXME: add code for installing the tool specified and
					// reloading the main page here
				} else
				// either form submit or link activate, set page to the url
				// specified
				{
					new Thread()
					{
						public void run()
						{
							System.out.println("link activated, url is " + url);
							trail.add(url);
							if (trail.size() >= 2)
								backButton.setEnabled(true);
							try
							{
								p.setPage(url);
							} catch (IOException e1)
							{
								e1.printStackTrace();
								setErrorPage(p);
							}
						}
					}.start();
				}
			}
		});
		p.setContentType("text/html");
		p.setText("<html><body><b>Please wait...</b></body></html>");
		new Thread()
		{
			public void run()
			{
				try
				{
					Thread.sleep(500);
					p.setPage(trail.get(0));
				} catch (Exception ex1)
				{
					ex1.printStackTrace();
					setErrorPage(p);
				}
			}
		}.start();
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.show();
	}

	protected static void setErrorPage(JEditorPane p)
	{
		p.setContentType("text/html");
		p
				.setText("<html><body><h1>An error has occued</h1>"
						+ "An error occured while loading the page. Check to "
						+ "make sure that you are connected to the internet.</body></html>");
	}

	private static URL createMainUrl(String[] types, String[] alreadyInstalled)
			throws MalformedURLException
	{
		String queryString = "?installedplugins="
				+ URLEncoder.encode(Convergia.delimited(Arrays
						.asList(alreadyInstalled), ","));
		if (types != null)
			queryString += "&plugintypes="
					+ URLEncoder.encode(Convergia.delimited(Arrays
							.asList(types), ","));
		return new URL(PLUGIN_DOWNLOAD_URL/* + queryString */);
	}
}
