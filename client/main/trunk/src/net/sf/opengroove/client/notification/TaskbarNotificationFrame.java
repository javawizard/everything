package net.sf.opengroove.client.notification;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
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
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import net.sf.opengroove.client.OpenGroove;
import net.sf.opengroove.client.ui.transitions.NotificationFrameTransition;

import com.sun.jna.examples.WindowUtils;

/**
 * TODO: add the ability to plugin different animations for showing and hiding
 * the taskbar notification frame. These could have a method that is called to
 * start the animation, and every 100ms another method is called to see if the
 * animation is complete yet. This check method could also report that the user
 * reversed the action to hide the window (such as mouseovering while it's in
 * the process of fading away), and that it will be shown again.
 * 
 * NOTE: the above comment has been superseded by a comment later on in the
 * page.
 * 
 * @author Alexander Boyd
 * 
 */
public class TaskbarNotificationFrame extends
    javax.swing.JWindow implements MouseListener,
    ActionListener
{
    private Map<String, ArrayList<TaskbarNotification>> notifications = new Hashtable<String, ArrayList<TaskbarNotification>>();
    
    private NotificationFrameTransition transition;
    
    private ArrayList<TaskbarNotification> internalAllNotifications()
    {
        ArrayList<TaskbarNotification> all = new ArrayList<TaskbarNotification>();
        for (Collection<TaskbarNotification> c : new ArrayList<Collection<TaskbarNotification>>(
            notifications.values()))
        {
            all.addAll(c);
        }
        return all;
    }
    
    private float tensOfSecondsUntilHide = 0;
    
    private int defaultNumVisibleSeconds = 10;
    private int inTime = 650;
    private int outTime = 650;
    
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
        for (TaskbarNotification notification : internalAllNotifications())
        {
            if (notification.isAlert())
                return true;
        }
        return false;
    }
    
    private JPanel notificationPanel;
    
    private JButton exitButton;
    
    private JLabel mainLabel;
    
    private Border normalExitBorder;
    
    private Border hoverExitBorder;
    
    private Border clickExitBorder;
    
    private GroupLabelResolver labels = new GroupLabelResolver()
    {
        
        @Override
        public String resolveLabel(String group)
        {
            return group;
        }
    };
    
    public void setGroupLabelResolver(
        GroupLabelResolver resolver)
    {
        this.labels = resolver;
    }
    
    /**
     * creates a new taskbar notification frame. currently, this constructor
     * starts a thread that never exits, so you must call System.exit(0) to
     * exit.
     * 
     * @param transition
     *            The transition to use when showing and hiding this frame.
     */
    public TaskbarNotificationFrame(
        final NotificationFrameTransition transition)
    {
        super();
        this.transition = transition;
        setAlwaysOnTop(true);
        addMouseListener(this);
        JPanel content = new JPanel();
        transition.initialize(this, content);
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        content.setLayout(new BorderLayout());
        Border contentBorder = BorderFactory
            .createCompoundBorder(BorderFactory
                .createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0));
        content.setBorder(contentBorder);
        notificationPanel = new JPanel();
        notificationPanel.setLayout(new BoxLayout(
            notificationPanel, BoxLayout.Y_AXIS));
        notificationPanel.setOpaque(true);
        notificationPanel.setBackground(new Color(230, 235,
            255));
        topPanel.setOpaque(true);
        topPanel.setBackground(new Color(200, 212, 245));
        content.add(notificationPanel, BorderLayout.CENTER);
        content.add(topPanel, BorderLayout.NORTH);
        final JLabel mainLabel = new JLabel(
            "  OpenGroove Alerts   ");
        final JProgressBar pbar = new JProgressBar(0,
            defaultNumVisibleSeconds * 10);
        pbar.setValue(defaultNumVisibleSeconds * 10);
        pbar.setFont(mainLabel.getFont().deriveFont(
            Font.PLAIN));
        pbar.setPreferredSize(new Dimension(280, 3));
        normalExitBorder = new EmptyBorder(2, 2, 2, 2);
        hoverExitBorder = new BevelBorder(
            BevelBorder.RAISED);
        clickExitBorder = new BevelBorder(
            BevelBorder.LOWERED);
        exitButton = new JButton("X");
        exitButton.setMargin(new Insets(0, 2, 0, 2));
        exitButton.addActionListener(this);
        exitButton.setCursor(Cursor
            .getPredefinedCursor(Cursor.HAND_CURSOR));
        exitButton.addMouseListener(this);
        JPanel exitPanel = new JPanel();
        exitPanel.setLayout(new BorderLayout());
        exitPanel.setBorder(new LineBorder(topPanel
            .getBackground(), 2));
        exitPanel.add(exitButton);
        Border topPanelBorder = BorderFactory
            .createMatteBorder(0, 0, 1, 0, Color.GRAY);
        topPanel.setBorder(topPanelBorder);
        topPanel.add(mainLabel, BorderLayout.CENTER);
        topPanel.add(exitPanel, BorderLayout.EAST);
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
                            pbar
                                .setValue(defaultNumVisibleSeconds * 10);
                            if (currentVisibilityLevel < transition
                                .getStepCount())
                            {
                                currentVisibilityLevel++;
                                transition
                                    .apply(currentVisibilityLevel);
                                Thread.sleep(inTime
                                    / transition
                                        .getStepCount());
                            }
                            else
                            {
                                Thread.sleep(100);
                            }
                        }
                        else if (isFadingToVisible
                            && currentVisibilityLevel < transition
                                .getStepCount())
                        {
                            pbar
                                .setValue(defaultNumVisibleSeconds * 10);
                            currentVisibilityLevel++;
                            transition
                                .apply(currentVisibilityLevel);
                            Thread
                                .sleep(inTime
                                    / transition
                                        .getStepCount());
                            if (!TaskbarNotificationFrame.this
                                .isShowing())
                                TaskbarNotificationFrame.this
                                    .show();
                            if (currentVisibilityLevel == transition
                                .getStepCount())
                            {
                                invalidate();
                                validate();
                                repaint();
                                notificationPanel
                                    .invalidate();
                                notificationPanel
                                    .validate();
                                notificationPanel.repaint();
                            }
                        }
                        else if ((!isFadingToVisible)
                            && currentVisibilityLevel > 0)
                        {
                            pbar.setValue(0);
                            currentVisibilityLevel--;
                            transition
                                .apply(currentVisibilityLevel);
                            Thread
                                .sleep(outTime
                                    / transition
                                        .getStepCount());
                            if (currentVisibilityLevel == 0)
                            {
                                TaskbarNotificationFrame.this
                                    .hide();
                                for (TaskbarNotification n : internalAllNotifications())
                                {
                                    if (n.isOneTimeOnly())
                                        removeNotification(n);
                                }
                            }
                        }
                        else if (!isFadingToVisible)
                        {
                            Thread.sleep(250);
                        }
                        else if (isFadingToVisible)
                        {
                            Thread.sleep(100);
                            tensOfSecondsUntilHide--;
                            pbar
                                .setValue((int) tensOfSecondsUntilHide);
                            if (tensOfSecondsUntilHide <= 0)
                            {
                                tensOfSecondsUntilHide = 0;
                                isFadingToVisible = false;
                            }
                        }
                    }
                    catch (Exception ex1)
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
    
    public synchronized void addNotification(String group,
        TaskbarNotification notification,
        boolean requestDisplay)
    {
        if (!Arrays
            .asList(
                notification.getComponent()
                    .getMouseListeners()).contains(this))
            notification.getComponent().addMouseListener(
                this);
        if (!internalAllNotifications().contains(
            notification))
        {
            internalPutNotification(group, notification);
        }
        else
        {
            System.out
                .println("not adding notification because "
                    + "it's already in the list");
        }
        reloadNotifications();
        if (requestDisplay)
            requestDisplay();
    }
    
    private synchronized void internalPutNotification(
        String group, TaskbarNotification notification)
    {
        ArrayList<TaskbarNotification> list = notifications
            .get(group);
        if (list == null)
        {
            list = new ArrayList<TaskbarNotification>();
            notifications.put(group, list);
        }
        list.add(notification);
    }
    
    private synchronized void internalDeleteNotification(
        TaskbarNotification notification)
    {
        for (Map.Entry<String, ArrayList<TaskbarNotification>> entry : new ArrayList<Map.Entry<String, ArrayList<TaskbarNotification>>>(
            notifications.entrySet()))
        {
            ArrayList<TaskbarNotification> list = entry
                .getValue();
            if (list.contains(notification))
                list.remove(notification);
            if (list.size() == 0)
                notifications.remove(entry.getKey());
        }
    }
    
    public synchronized void removeNotification(
        TaskbarNotification notification)
    {
        internalDeleteNotification(notification);
        reloadNotifications();
    }
    
    public synchronized TaskbarNotification[] listNotifications(
        String group)
    {
        return listNotificationsByClass(
            TaskbarNotification.class, group);
    }
    
    @SuppressWarnings("unchecked")
    public synchronized <T extends TaskbarNotification> T[] listNotificationsByClass(
        Class<? extends T> c, String group)
    {
        ArrayList<T> arraylist = new ArrayList<T>();
        for (Map.Entry<String, ArrayList<TaskbarNotification>> entry : new ArrayList<Map.Entry<String, ArrayList<TaskbarNotification>>>(
            notifications.entrySet()))
        {
            if (group == null
                || group.equals(entry.getKey()))
            {
                for (TaskbarNotification notification : entry
                    .getValue())
                {
                    if (c.isInstance(notification))
                    {
                        arraylist.add((T) notification);
                    }
                }
            }
        }
        return arraylist.toArray((T[]) Array.newInstance(c,
            0));
    }
    
    /**
     * Reloads all notifications in this notification frame.<br/><br/>
     * 
     * This typically doesn't need to be called by other classes
     * (TaskbarNotificationFrame takes care of calling it when it's needed), but
     * if the label for a particular group changes, then this should be called
     * to get the new label to propegate into the UI.
     */
    public synchronized void reloadNotifications()
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
        for (Map.Entry<String, ArrayList<TaskbarNotification>> entry : new ArrayList<Map.Entry<String, ArrayList<TaskbarNotification>>>(
            notifications.entrySet()))
        {
            JPanel cpanel = new JPanel();
            cpanel.setLayout(new BoxLayout(cpanel,
                BoxLayout.Y_AXIS));
            cpanel.setBorder(new CompoundBorder(
                new CompoundBorder(new EmptyBorder(1, 1, 1,
                    1), new TitledBorder(new LineBorder(
                    Color.DARK_GRAY, 1), labels
                    .resolveLabel(entry.getKey()),
                    TitledBorder.LEFT, TitledBorder.TOP)),
                new EmptyBorder(1, 2, 1, 2)));
            cpanel.setOpaque(false);
            notificationPanel.add(cpanel);
            for (TaskbarNotification n : new ArrayList<TaskbarNotification>(
                entry.getValue()))
            {
                System.out.println("has notification");
                cpanel.add(createNotificationWrapper(n
                    .getComponent()));
                System.out.println("added");
            }
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
        GraphicsConfiguration cfg = dv
            .getDefaultConfiguration();
        Insets insets = Toolkit.getDefaultToolkit()
            .getScreenInsets(cfg);
        Dimension size = Toolkit.getDefaultToolkit()
            .getScreenSize();
        transition.setWindowPosition(
            (size.width - insets.right) - getWidth(),
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
    
    private JComponent createNotificationWrapper(
        Component component)
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(component, BorderLayout.CENTER);
        Border innerBorder = new EmptyBorder(1, 1, 1, 1);
        Border middleBorder = new LineBorder(Color.GRAY, 1);
        Border outerBorder = new EmptyBorder(1, 2, 1, 2);
        panel.setBorder(new CompoundBorder(outerBorder,
            new CompoundBorder(middleBorder, innerBorder)));
        return panel;
    }
    
    public void mouseClicked(MouseEvent e)
    {
        System.out.println("clicked");
        System.out.println(e.getComponent().getClass()
            .getName());
        for (TaskbarNotification n : new ArrayList<TaskbarNotification>(
            internalAllNotifications()))
        {
            if (n.getComponent().equals(e.getComponent()))
                n.clicked();
        }
    }
    
    public void mouseEntered(MouseEvent e)
    {
        if (!ignoreMouseOver)
            isMouseOver = true;
        else
        {
            for (TaskbarNotification n : new ArrayList<TaskbarNotification>(
                internalAllNotifications()))
            {
                if (n.getComponent().equals(
                    e.getComponent()))
                    n.mouseOver();
            }
        }
    }
    
    public void mouseExited(MouseEvent e)
    {
        isMouseOver = false;
        for (TaskbarNotification n : new ArrayList<TaskbarNotification>(
            internalAllNotifications()))
        {
            if (n.getComponent().equals(e.getComponent()))
                n.mouseOut();
        }
    }
    
    public void mousePressed(MouseEvent e)
    {
        toFront();
    }
    
    public void mouseReleased(MouseEvent e)
    {
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == exitButton)
        {
            System.out.println("exitLabel");
            isFadingToVisible = false;
            tensOfSecondsUntilHide = 0;
            ignoreMouseOver = true;
            isMouseOver = false;
            return;
        }
    }
    
    public TaskbarNotification[] listAllNotifications()
    {
        return internalAllNotifications().toArray(
            new TaskbarNotification[0]);
    }
    
    public boolean containsNotification(String group,
        TaskbarNotification notification)
    {
        return Arrays.asList(listNotifications(group))
            .contains(notification);
    }
    
    public int getInTime()
    {
        return inTime;
    }
    
    public int getOutTime()
    {
        return outTime;
    }
    
    /**
     * Sets the number of milliseconds that the frame should take to transition
     * from not visible to visible.
     * 
     * @param inTime
     */
    public void setInTime(int inTime)
    {
        this.inTime = inTime;
    }
    
    /**
     * Sets the number of milliseconds that the frame should take to transition
     * from visible to not visible.
     * 
     * @param outTime
     */
    public void setOutTime(int outTime)
    {
        this.outTime = outTime;
    }
    
}
