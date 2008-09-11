package net.sf.opengroove.client.ui.transitions;

import javax.swing.JPanel;

import net.sf.opengroove.client.notification.TaskbarNotificationFrame;

/**
 * A transition for showing and hiding {@link TaskbarNotificationFrame}s.
 * 
 * @author Alexander Boyd
 * 
 */
public interface NotificationFrameTransition
{
    /**
     * Gets the number of steps that this notification requires.
     * 
     * @return
     */
    public int getStepCount();
    
    /**
     * Initializes this transition, and requests that the frame be initialized
     * as well. This method should add the panel specified to the frame's
     * content pane, or wrap it with it's own custom panel or container. The
     * layout of the frame's content pane is whatever the default is for JFrame.
     * At the time of this writing, the default is BorderLayout. The only
     * requirement is that either the layout's preferred size always be equal
     * (or greater) than the panel passed in, or that the {@link #apply(int)}
     * method resize the frame to be the panel's preferred size at that time.
     * 
     * @param frame
     *            The taskbar notification frame
     * @param panel
     *            The panel that contains the notification frame's contents
     */
    public void initialize(TaskbarNotificationFrame frame,
        JPanel panel);
    
    /**
     * Applies this notification to the frame specified in
     * {@link #initialize(TaskbarNotificationFrame, JPanel)}, for the step
     * specified. The step will range from 0 to getStepCount(), with 0 being
     * completely hidden and getStepCount() being completely visible.<br/><br/>
     * 
     * This method will never be called before initialize().
     * 
     * @param step
     *            The current step to apply
     */
    public void apply(int step);
}
