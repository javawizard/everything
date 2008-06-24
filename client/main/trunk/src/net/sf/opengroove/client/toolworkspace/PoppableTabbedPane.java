package net.sf.opengroove.client.toolworkspace;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import net.sf.opengroove.client.Convergia;

public class PoppableTabbedPane extends JPanel
{
	private volatile static int nextId = 0;

	private static synchronized int getNextId()
	{
		return nextId++;
	}

	private class Tab extends JPanel
	{
		private int id;

		private JComponent tabComponent;

		private JComponent content;

		private String title;

		private JFrame frame;

		public String getTitle()
		{
			return title;
		}

		public void setTitle(String title)
		{
			this.title = title;
			if (frame != null)
				frame.setTitle(title);
		}

		public JComponent getContent()
		{
			return content;
		}

		public void setContent(JComponent content)
		{
			this.content = content;
		}

		public int getId()
		{
			return id;
		}

		public void setId(int id)
		{
			this.id = id;
		}

		public JComponent getTabComponent()
		{
			return tabComponent;
		}

		public void setTabComponent(JComponent tabComponent)
		{
			this.tabComponent = tabComponent;
		}

		public JFrame getFrame()
		{
			return frame;
		}

		public void setFrame(JFrame frame)
		{
			this.frame = frame;
		}

		@Override
		public int hashCode()
		{
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + id;
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final Tab other = (Tab) obj;
			if (id != other.id)
				return false;
			return true;
		}
	}

	private class ComponentWrapper extends JPanel
	{
		private JComponent component;

		private int id;

		public ComponentWrapper(JComponent component, int id)
		{
			super();
			this.component = component;
			this.id = id;
			setLayout(new BorderLayout());
			add(component);
		}

		public JComponent getComponent()
		{
			return component;
		}

		public void setComponent(JComponent component)
		{
			this.component = component;
		}

		public int getId()
		{
			return id;
		}

		public void setId(int id)
		{
			this.id = id;
		}

		public String toString()
		{
			return "(ComponentWrapper with id " + id + ")";
		}
	}

	private JTabbedPane tabbedPane;

	private ArrayList<Tab> tabs = new ArrayList<Tab>();

	public PoppableTabbedPane()
	{
		setLayout(new BorderLayout());
		tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		add(tabbedPane);
	}

	public void pop(int index)
	{
		popOut(index);
	}

	private void popOut(int index)
	{
		Tab tab = tabs.get(index);
		if (getTabIndex(tab.getId()) == -1)// tab is already popped out
			return;
		// if we get here then the tab is not popped out yet
		int tpIndex = getTabIndex(tab.getId());
		tabbedPane.removeTabAt(tpIndex);
		tab.getFrame().getContentPane().removeAll();
		tab.getFrame().getContentPane().add(tab.getContent());
		tab.getFrame().show();
	}

	private int getTabIndex(int id)
	{
		System.out.println("searching for id " + id);
		for (int i = 0; i < tabbedPane.getTabCount(); i++)
		{
			System.out.println("matching with " + tabbedPane.getComponentAt(i));
			if (tabbedPane.getComponentAt(i) instanceof ComponentWrapper
					&& ((ComponentWrapper) tabbedPane.getComponentAt(i))
							.getId() == id)
			{
				return i;
			}
		}
		return -1;
	}

	@SuppressWarnings("deprecation")
	private void popIn(int index)
	{
		Tab tab = tabs.get(index);
		tab.getFrame().getContentPane().removeAll();
		tab.getFrame().hide();
		addToTabbedPane(tab);
	}

	public int getTabCount()
	{
		return tabs.size();
	}

	public JComponent getTabComponentAt(int i)
	{
		if (i < 0 || i >= tabs.size())
			return null;
		if (tabs.get(i) == null)
			return null;
		return tabs.get(i).getTabComponent();
	}

	/**
	 * if the specified tab is popped in, then this method delegates to the
	 * tabbed pane using the appropriate index. if not, the window for this tab
	 * has it's show() method called.
	 * 
	 * @param index
	 */
	public void setSelectedIndex(int index)
	{
		if (getTabIndex(index) != -1)
			tabbedPane.setSelectedIndex(getTabIndex(index));
		else if (index >= 0 && index < tabs.size()
				&& tabs.get(index).getFrame().isShowing())
			Convergia.bringToFront(tabs.get(index).getFrame());
		else
			throw new IndexOutOfBoundsException("The index specifed (" + index
					+ ") is not within 0 - " + tabs.size() + ", max-exclusive");

	}

	/**
	 * removes the tab at the specified index. if the tab is popped out, the
	 * window that it is in will be disposed.
	 * 
	 * @param i
	 */
	public void removeTabAt(int i)
	{
		System.out.println("removing " + i);
		int internalIndex = getTabIndex(i);
		System.out.println("removing ii " + internalIndex);
		if (internalIndex != -1)
			tabbedPane.removeTabAt(internalIndex);
		tabs.get(i).getFrame().dispose();
		tabs.remove(i);
	}

	/**
	 * sets the title at the specified index.
	 * 
	 * @param i
	 * @param name
	 */
	public void setTitleAt(int i, String name)
	{
		tabs.get(i).setTitle(name);
		// setTitle takes care of updating the frame title so all we need to do
		// is update the tab title if nessecary.
		int internalIndex = getTabIndex(i);
		if (i != -1)
			tabbedPane.setTitleAt(i, name);
	}

	public JFrame getPopFrame(int index)
	{
		return tabs.get(index).getFrame();
	}

	public String getTitleAt(int i)
	{
		return tabs.get(i).getTitle();
	}

	/**
	 * returns the selected tab within the internal tabbed pane. popped out tabs
	 * cannot be currently selected.
	 * 
	 * @return
	 */
	public int getSelectedIndex()
	{
		int tabSelectedIndex = tabbedPane.getSelectedIndex();
		if (tabSelectedIndex == -1)
			return -1;
		for (int i = 0; i < tabs.size(); i++)
		{
			Tab tab = tabs.get(i);
			if (getTabIndex(tab.getId()) == tabSelectedIndex)
				return i;
		}
		return -1;
	}

	public void addTab(String name, JComponent component)
	{
		final Tab tab = new Tab();
		int id = getNextId();
		tab.setId(id);
		tab.setTabComponent(null);
		tab.setTitle(name);
		tab.setContent(component);
		JFrame frame = new JFrame(name);
		frame.setIconImage(Convergia.getWindowIcon());
		frame.setSize(600, 450);
		if (SwingUtilities.getWindowAncestor(this) != null)
			frame.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
		frame.setDefaultCloseOperation(frame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowClosing(WindowEvent e)
			{
				System.out.println("windowclosing for poppable tab window id "
						+ tab.getId() + " index " + tabs.indexOf(tab));
				popIn(tabs.indexOf(tab));
			}
		});
		tab.setFrame(frame);
		tabs.add(tab);
		addToTabbedPane(tab);
	}

	private void addToTabbedPane(Tab tab)
	{
		if (getTabIndex(tab.getId()) != -1)// already in the tabbed pane
			return;
		int tabIndex = tabs.indexOf(tab);
		System.out.println("tabIndex=" + tabIndex);
		int nextTabIndex = -1;
		for (int i = tabIndex; i < tabs.size(); i++)
		{
			if (getTabIndex(tabs.get(i).getId()) != -1)
			{
				nextTabIndex = getTabIndex(tabs.get(i).getId());
				break;
			}
		}
		System.out.println("nextTabIndex=" + nextTabIndex);
		if (nextTabIndex == -1)
		{
			tabbedPane.addTab(tab.getTitle(), new ComponentWrapper(tab
					.getContent(), tab.getId()));
			if (tab.getTabComponent() != null)
				tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tab
						.getTabComponent());
		} else
		{
			tabbedPane.insertTab(tab.getTitle(), null, new ComponentWrapper(tab
					.getContent(), tab.getId()), null, nextTabIndex);
			if (tab.getTabComponent() != null)
				tabbedPane.setTabComponentAt(nextTabIndex, tab
						.getTabComponent());
		}
	}

	public void setTabComponentAt(int i, JComponent wp)
	{
		Tab tab = tabs.get(i);
		tab.setTabComponent(wp);
		int internalIndex = getTabIndex(i);
		if (internalIndex != -1)
			tabbedPane.setTabComponentAt(internalIndex, wp);
	}

}
