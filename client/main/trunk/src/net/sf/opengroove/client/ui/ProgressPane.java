package net.sf.opengroove.client.ui;

import java.awt.TextArea;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

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
    private ArrayList<ProgressItem> tasks = new ArrayList<ProgressItem>();
    
    /**
     * re-builds the contents of this progress pane.
     */
    public synchronized void refresh()
    {
        removeAll();
        SpringLayout layout = new SpringLayout();
        setLayout(layout);
        for (int i = 0; i < tasks.size(); i++)
        {
            ProgressItem task = tasks.get(i);
            
        }
        new JTextArea().setCaretPosition(0);
    }
    
    public ProgressPane()
    {
    }
}
