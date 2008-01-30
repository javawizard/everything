package net.sf.convergia.client.notification;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import net.sf.convergia.client.Convergia;

import com.sun.jna.examples.WindowUtils;

public class TaskbarNotificationFrame extends javax.swing.JWindow implements
		MouseListener
{
	private ArrayList<TaskbarNotification> notifications = new ArrayList<TaskbarNotification>();

	private float tensOfSecondsUntilHide = 0;

	private int defaultNumVisibleSeconds = 12;

	private boolean isMouseOver = false;

	public boolean ignoreMouseOver = false;

	private boolean isFadingToVisible = false;

	public int currentVisibilityLevel = 0;

	/**
	 * returns true if any of the notifications report that they are alerts.
	 * 
	 * @return
	 */
	public boolean containsAlerts()
	{
		for (TaskbarNotification notification : notifications)
		{
			if (notification.isAlert())
				return true;
		}
		return false;
	}

	private JPanel notificationPanel;

	private JLabel exitLabel;

	private JLabel mainLabel;

	private Border normalExitBorder;

	private Border hoverExitBorder;

	private Border clickExitBorder;

	/**
	 * creates a new taskbar notification frame. currently, this constructor
	 * starts a thread that never exits, so you must call System.exit(0) to
	 * exit.
	 */
	public TaskbarNotificationFrame()
	{
		super();
		System.setProperty("sun.java2d.noddraw", "true");
		setAlwaysOnTop(true);
		addMouseListener(this);
		JPanel content = new JPanel();
		getContentPane().add(content);
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		content.setLayout(new BorderLayout());
		Border contentBorder = BorderFactory.createCompoundBorder(BorderFactory
				.createLineBorder(Color.GRAY, 1), BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		content.setBorder(contentBorder);
		notificationPanel = new JPanel();
		notificationPanel.setLayout(new BoxLayout(notificationPanel,
				BoxLayout.Y_AXIS));
		notificationPanel.setOpaque(true);
		notificationPanel.setBackground(new Color(230, 235, 255));
		topPanel.setOpaque(true);
		topPanel.setBackground(new Color(200, 212, 245));
		content.add(notificationPanel, BorderLayout.CENTER);
		content.add(topPanel, BorderLayout.NORTH);
		final JLabel mainLabel = new JLabel("  Convergia Alerts    ");
		final JProgressBar pbar = new JProgressBar(0,
				defaultNumVisibleSeconds * 10);
		pbar.setValue(defaultNumVisibleSeconds * 10);
		pbar.setFont(mainLabel.getFont().deriveFont(Font.PLAIN));
		pbar.setPreferredSize(new Dimension(280, 3));
		normalExitBorder = new EmptyBorder(2, 2, 2, 2);
		hoverExitBorder = new BevelBorder(BevelBorder.RAISED);
		clickExitBorder = new BevelBorder(BevelBorder.LOWERED);
		exitLabel = new JLabel("X");
		exitLabel.setBorder(normalExitBorder);
		exitLabel.addMouseListener(this);
		exitLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		Border topPanelBorder = BorderFactory.createMatteBorder(0, 0, 1, 0,
				Color.GRAY);
		topPanel.setBorder(topPanelBorder);
		topPanel.add(mainLabel, BorderLayout.CENTER);
		topPanel.add(exitLabel, BorderLayout.EAST);
		content.add(pbar, BorderLayout.SOUTH);
		new Thread()
		{
			public void run()
			{
				while (true)
				{
					try
					{
						if (isMouseOver)
						{
							tensOfSecondsUntilHide = defaultNumVisibleSeconds * 10;
							isFadingToVisible = true;
							currentVisibilityLevel = 64;
							if (Convergia.useWindowTransparency())// FIXME:
								// setWindowAlpha
								// only
								// works
								// on
								// Windows
								// Vista,
								// find
								// a
								// version
								// that
								// works
								// on
								// all
								// windows
								// versions
								WindowUtils.setWindowAlpha(
										TaskbarNotificationFrame.this, 1.0f);
							pbar.setValue(defaultNumVisibleSeconds * 10);
							Thread.sleep(100);
						} else if (isFadingToVisible
								&& currentVisibilityLevel < 64)
						{
							pbar.setValue(defaultNumVisibleSeconds * 10);
							currentVisibilityLevel++;
							if (Convergia.useWindowTransparency())// FIXME:
							// setWindowAlpha
							// only
							// works
							// on
							// Windows
							// Vista,
							// find
							// a
							// version
							// that
							// works
							// on
							// all
							// windows
							// versions
							{
								WindowUtils
										.setWindowAlpha(
												TaskbarNotificationFrame.this,
												((currentVisibilityLevel * 1.0f) / 64f));
								Thread.sleep(15);
							}
							if (!TaskbarNotificationFrame.this.isShowing())
								TaskbarNotificationFrame.this.show();
							if (currentVisibilityLevel == 64)
							{
								invalidate();
								validate();
								repaint();
								notificationPanel.invalidate();
								notificationPanel.validate();
								notificationPanel.repaint();
							}
						} else if ((!isFadingToVisible)
								&& currentVisibilityLevel > 0)
						{
							pbar.setValue(0);
							currentVisibilityLevel--;
							if (Convergia.useWindowTransparency())// FIXME:
							// setWindowAlpha
							// only
							// works
							// on
							// Windows
							// Vista,
							// find
							// a
							// version
							// that
							// works
							// on
							// all
							// windows
							// versions
							{
								WindowUtils
										.setWindowAlpha(
												TaskbarNotificationFrame.this,
												((currentVisibilityLevel * 1.0f) / 64f));
								Thread.sleep(25);
							}
							if (Convergia.useWindowTransparency()
									&& currentVisibilityLevel == 0)// FIXME:
							// setWindowAlpha
							// only
							// works
							// on
							// Windows
							// Vista,
							// find
							// a
							// version
							// that
							// works
							// on
							// all
							// windows
							// versions
							{
								WindowUtils.setWindowAlpha(
										TaskbarNotificationFrame.this, 1.0f);
							}

							if (currentVisibilityLevel == 0)
							{
								TaskbarNotificationFrame.this.hide();
								for (TaskbarNotification n : listAllNotifications())
								{
									if (n.isOneTimeOnly())
										removeNotification(n);
								}
							}
						} else if (!isFadingToVisible)
						{
							Thread.sleep(200);
						} else if (isFadingToVisible)
						{
							Thread.sleep(100);
							tensOfSecondsUntilHide--;
							pbar.setValue((int) tensOfSecondsUntilHide);
							if (tensOfSecondsUntilHide <= 0)
							{
								tensOfSecondsUntilHide = 0;
								isFadingToVisible = false;
							}
						}
					} catch (Exception ex1)
					{
						ex1.printStackTrace();
					}
				}
			}
		}.start();
	}

	public void requestDisplay()
	{
		ignoreMouseOver = false;
		isFadingToVisible = true;
		tensOfSecondsUntilHide = defaultNumVisibleSeconds * 10;
	}

	public synchronized void addNotification(TaskbarNotification notification,
			boolean requestDisplay)
	{
		System.out.println("checking for rqd");
		if (!Arrays.asList(notification.getComponent().getMouseListeners())
				.contains(this))
			notification.getComponent().addMouseListener(this);
		if (!notifications.contains(notification))
		{
			System.out.println("adding notification");
			notifications.add(notifications.size(), notification);
		} else
		{
			System.out.println("not adding notification");
		}
		reloadNotifications();
		if (requestDisplay)
			requestDisplay();
	}

	public synchronized void removeNotification(TaskbarNotification notification)
	{
		notifications.remove(notification);
		reloadNotifications();
	}

	public synchronized TaskbarNotification[] listAllNotifications()
	{
		return listNotificationsByClass(TaskbarNotification.class);
	}

	public synchronized <T extends TaskbarNotification> T[] listNotificationsByClass(
			Class<? extends T> c)
	{
		ArrayList<T> arraylist = new ArrayList<T>();
		for (TaskbarNotification notification : notifications)
		{
			if (c.isInstance(notification))
			{
				arraylist.add((T) notification);
			}
		}
		return arraylist.toArray((T[]) Array.newInstance(c, 0));
	}

	private void reloadNotifications()
	{
		System.out.println("removing all");
		notificationPanel.removeAll();
		if (notifications.size() == 0)
		{
			System.out.println("none");
			notificationPanel
					.add(new JLabel(
							"<html>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color='#888888'>(no notifications)</font>"));
			isFadingToVisible = false;
			tensOfSecondsUntilHide = 0;
			ignoreMouseOver = true;
			isMouseOver = false;
		}
		for (TaskbarNotification n : notifications)
		{
			System.out.println("has notification");
			notificationPanel.add(n.getComponent());
			System.out.println("added");
		}
		notificationPanel.invalidate();
		notificationPanel.validate();
		notificationPanel.repaint();
		invalidate();
		validate();
		repaint();
		notificationPanel.invalidate();
		notificationPanel.validate();
		notificationPanel.repaint();
		invalidate();
		validate();
		repaint();
		System.out.println("bsize: " + getSize());
		pack();
		System.out.println("asize: " + getSize());
		GraphicsEnvironment env = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice dv = env.getDefaultScreenDevice();
		GraphicsConfiguration cfg = dv.getDefaultConfiguration();
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(cfg);
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((size.width - insets.right) - getWidth(),
				(size.height - insets.bottom) - getHeight());
		notificationPanel.invalidate();
		notificationPanel.validate();
		notificationPanel.repaint();
		invalidate();
		validate();
		repaint();
		notificationPanel.invalidate();
		notificationPanel.validate();
		notificationPanel.repaint();
		invalidate();
		validate();
		repaint();
	}

	public void mouseClicked(MouseEvent e)
	{
		System.out.println("clicked");
		System.out.println(e.getComponent().getClass().getName());
		if (e.getComponent().equals(exitLabel))
		{
			System.out.println("exitLabel");
			isFadingToVisible = false;
			tensOfSecondsUntilHide = 0;
			ignoreMouseOver = true;
			isMouseOver = false;
			return;
		}
		for (TaskbarNotification n : new ArrayList<TaskbarNotification>(
				notifications))
		{
			if (n.getComponent().equals(e.getComponent()))
				n.clicked();
		}
	}

	public void mouseEntered(MouseEvent e)
	{
		if (!ignoreMouseOver)
			isMouseOver = true;
		if (e.getComponent() == exitLabel)
		{
			exitLabel.setForeground(Color.RED.darker().darker());
			exitLabel.setBorder(hoverExitBorder);
		} else
		{
			for (TaskbarNotification n : new ArrayList<TaskbarNotification>(
					notifications))
			{
				if (n.getComponent().equals(e.getComponent()))
					n.mouseOver();
			}
		}
	}

	public void mouseExited(MouseEvent e)
	{
		isMouseOver = false;
		if (e.getComponent() == exitLabel)
		{
			exitLabel.setForeground(Color.BLACK);
			exitLabel.setBorder(normalExitBorder);
		} else
		{
			for (TaskbarNotification n : new ArrayList<TaskbarNotification>(
					notifications))
			{
				if (n.getComponent().equals(e.getComponent()))
					n.mouseOut();
			}
		}
	}

	public void mousePressed(MouseEvent e)
	{
		if (e.getComponent() == exitLabel)
		{
			exitLabel.setBorder(clickExitBorder);
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		if (e.getComponent() == exitLabel)
		{
			exitLabel.setBorder(normalExitBorder);
		}
	}

}
