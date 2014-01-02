package net.sf.opengroove.client.ui.transitions;

import java.awt.Window;

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
     * Gets the number of steps that this transition requires. It's best to keep
     * this as small as possible without the animation looking jittery, as
     * rounding errors will force the length of the animation (specified by the
     * user) to round by this number of milliseconds. For example, if this
     * returns 100, then an animation length of 463 would either be rounded to
     * 400 or 500, it's not specified which one will occur.
     * 
     * @return The number of steps that this transition requires.
     */
    public int getStepCount();
    
    /**
     * Initializes this transition, and requests that the frame be initialized
     * as well. This method should add the panel specified to the frame's
     * content pane, or wrap it with it's own custom panel or container. The
     * layout of the frame's content pane is whatever the default is for JFrame.
     * At the time of this writing, the default is BorderLayout. The only
     * requirement is that the layout's preferred size always be equal (or
     * greater) than the panel passed in, since, when an item is added to the
     * notification frame, the notification frame {@link Window#pack() packs}
     * itself.
     * 
     * @param frame
     *            The taskbar notification frame
     * @param panel
     *            The panel that contains the notification frame's contents
     */
    public void initialize(TaskbarNotificationFrame frame,
        JPanel panel);
    
    /**
     * Updates the window's target position. This is where the notification
     * frame's upper-left corner should be located at when it is completely
     * visible. If a notification does not need to move the notification frame,
     * then it should just set the size of the notification frame accordingly.
     * 
     * @param x
     *            The X coordinate of where the upper-left corner of the window
     *            should reside
     * @param y
     *            the Y coordinate of where the upper-left corner of the window
     *            should reside
     */
    public void setWindowPosition(int x, int y);
    
    /**
     * Applies this notification to the frame specified in
     * {@link #initialize(TaskbarNotificationFrame, JPanel)}, for the step
     * specified. The step will range from 0 to getStepCount(), with 0 being
     * completely hidden and getStepCount() being completely visible.<br/><br/>
     * 
     * This method will never be called before initialize().<br/><br/>
     * 
     * Note that this method may be called frequently with a value equal to
     * either 0 or getStepCount() to ensure that the transition is completely
     * showing or completely hiding the frame. If each transition frame takes a
     * lot of processing time to compute, if/else checks should be included to
     * check for a value of 0 or a value of transition.getStepCount() and
     * completely show or completely hide the frame.<br/><br/>
     * 
     * Also note that the taskbar notification frame should never be hidden by
     * itself. It will be automatically hidden when the step reaches 0, and will
     * be automatically shown for any other step value.
     * 
     * @param step
     *            The current step to apply
     */
    public void apply(int step);
}
