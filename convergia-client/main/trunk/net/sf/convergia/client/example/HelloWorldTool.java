package net.sf.convergia.client.example;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sf.convergia.client.toolworkspace.Tool;


/**
 * a simple tool that shows the user a button. when they click it, the button's
 * text changes to XXX clicked me at YYY, where XXX is the user's username and
 * YYY is the current date and time. when one user clicks on the button, all
 * user's buttons change to show that the one user clicked the button. for
 * example, if test1, test2, and test3 are participants of a workspace with this
 * tool, and test1 clicks the button, test1's button, test2's button, and
 * test3's button change to "test1 clicked me at Jan 1, 1970 12:00 AM" or
 * something like that.
 * 
 * this tool is called HelloWorldTool because, like most Hello World
 * applications, it is used to be a very simple example of a tool.
 * 
 * @author Alexander Boyd
 * 
 */
public class HelloWorldTool extends Tool
{
	private JPanel component = new JPanel(new FlowLayout());

	private JButton get = new JButton("get");

	private JButton set = new JButton("set");

	private JButton send = new JButton("send");

	private JTextField value = new JTextField(20);

	@Override
	public JComponent getComponent()
	{
		return component;
	}

	@Override
	public void initialize()
	{
		component.add(get);
		component.add(set);
		component.add(send);
		component.add(value);
		get.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				value.setText(getProperty("value"));
			}
		});
		set.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				setProperty("value", value.getText());
			}
		});
		send.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				String m = value.getText();
				System.out.println("sending the value " + m);
				for (String u : listOnlineUsers())
				{
					try
					{
						sendMessage(u, m);
					} catch (Exception ex1)
					{
						ex1.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public void receiveMessage(String from, String message)
	{
		value.setText(from + ": " + message);
	}

	@Override
	public void shutdown()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void userStatusChanged()
	{
		// TODO Auto-generated method stub

	}

}
