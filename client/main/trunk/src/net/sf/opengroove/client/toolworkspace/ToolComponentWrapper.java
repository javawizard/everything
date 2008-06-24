package net.sf.opengroove.client.toolworkspace;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class ToolComponentWrapper extends JPanel
{
	private ToolWrapper wrapper;

	public ToolComponentWrapper(JComponent component, ToolWrapper wrapper)
	{
		setOpaque(false);
		setLayout(new BorderLayout());
		this.wrapper = wrapper;
		add(component);
	}

	public ToolWrapper getWrapper()
	{
		return wrapper;
	}

	public void setWrapper(ToolWrapper wrapper)
	{
		this.wrapper = wrapper;
	}
}
