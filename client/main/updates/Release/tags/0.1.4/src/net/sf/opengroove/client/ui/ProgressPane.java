package net.sf.opengroove.client.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.TextArea;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import net.sf.opengroove.client.ui.ProgressItem.Status;

/**
 * This class is a progress pane. It shows a bunch of items in it, where each
 * item has a name and it's current status (pending, active, failed,
 * successful), which shows up to the left of it's name as an empty space, an
 * animated progress icon, a red X, or a green checkmark, respectively. An item
 * can additionally have a details component that shows up directly below the
 * item itself in the task pane. This details component could, for example,
 * contain another task pane, to show subtasks of a particular task.<br/> <br/>
 * 
 * 
 * Tasks can be added and removed on-the-fly. Bear in mind that the entire panel
 * is re-constructed upon adding or removing a task, which may take some time.
 * 
 * @author Alexander Boyd
 * 
 */
public class ProgressPane extends JPanel
{
    private static final Spring PAD = Spring.constant(2, 5,
        5);
    private ArrayList<ProgressItem> tasks = new ArrayList<ProgressItem>();
    
    /**
     * re-builds the contents of this progress pane.
     */
    public synchronized void refresh()
    {
        removeAll();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        Component lowestActive = null;
        // holds the component to pin the status component's top to
        Component lastStatus = this;
        // hold the component to pin the name component's top to
        Component lastName = this;
        for (int i = 0; i < tasks.size(); i++)
        {
            ProgressItem task = tasks.get(i);
            Component nameComponent = task
                .getNameComponent();
            Component detailsComponent = task
                .getDetailsComponent();
            JComponent statusComponent;
            Status status = task.getStatus();
            statusComponent = task.getStatusComponent();
            JPanel leftBox = createLeftBox(statusComponent,
                nameComponent);
            if (status.equals(Status.ACTIVE))
                lowestActive = leftBox;
            add(leftBox);
            lastStatus = statusComponent;
            lastName = nameComponent;
            if (detailsComponent != null)
            {
                add(createLeftBox(task.getEmptyStatus(),
                    detailsComponent));
                lastName = detailsComponent;
            }
        }
        invalidate();
        validate();
        repaint();
        if (lowestActive != null)
        {
            scrollRectToVisible(lowestActive.getBounds());
        }
    }
    
    private JPanel createLeftBox(Component left,
        Component center)
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(left, BorderLayout.WEST);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }
    
    public void finalize() throws Throwable
    {
        tasks.clear();
        removeAll();
        super.finalize();
    }
    
    public ProgressPane()
    {
    }
    
    public void addItem(ProgressItem task)
    {
        task.setParent(this);
        tasks.add(task);
        refresh();
    }
    
    public void removeItem(ProgressItem task)
    {
        tasks.remove(task);
        task.setParent(null);
        refresh();
    }
    
    public ProgressItem[] listItems()
    {
        return tasks.toArray(new ProgressItem[0]);
    }
    
    public ProgressItem getItem(int i)
    {
        return tasks.get(i);
    }
    
    public int countItems()
    {
        return tasks.size();
    }
}
