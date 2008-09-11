package net.sf.opengroove.client.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class ComponentUtils
{
    public static JPanel pad(JComponent component, int top,
        int left, int bottom, int right)
    {
        javax.swing.JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(
            top, left, bottom, right));
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Converts an angle (in degrees, assuming 0 degress is straight up, and
     * clockwise is an increase in angle, and positive X is right) to an X
     * location point. If this were used in conjunction with toY to generate
     * points for all angles from 0 to 360, the resulting shape would be a
     * circle with a radius of 1.
     * 
     * @param angle
     * @return
     */
    public static double toX(int angle)
    {
        return Math.sin(Math.toRadians(angle));
    }
    
    /**
     * Same as toX, but converts to Y component, assuming positive Y is down.
     * 
     * @param angle
     * @return
     */
    public static double toY(int angle)
    {
        return Math.sin(Math.toRadians(angle - 90));
    }
    
    /**
     * converts cartesian coordinates (positive Y is down, positive X is right)
     * into an angle in degrees (0 degrees is up, clockwise is positive). The
     * angle is between 0 and 359, inclusive.
     * 
     * @param x
     * @param y
     * @return
     */
    public static int toAngle(int x, int y)
    {
        return (int) ((Math.toDegrees(Math.atan2(y, x)) + 90) % 360);
    }
    
    /**
     * Adds a MouseListener to the component specified that will show the popup
     * specified (at the position that the mouse was clicked) when the mouse is
     * right-clicked, or whatever mouse event returns true from the
     * {@link MouseEvent#isPopupTrigger()} method.
     * 
     * @param c
     *            The component to add the mouse listener to
     * @param popup
     *            the popup to show whe the component is clicked
     */
    public static void addPopup(Component c,
        final JPopupMenu popup)
    {
        c.addMouseListener(new MouseAdapter()
        {
            
            @Override
            public void mousePressed(MouseEvent e)
            {
                if (e.isPopupTrigger())
                    popup.show(e.getComponent(), e.getX(),
                        e.getY());
            }
            
            @Override
            public void mouseReleased(MouseEvent e)
            {
                mousePressed(e);
            }
            
            @Override
            public void mouseClicked(MouseEvent e)
            {
                mousePressed(e);
            }
        });
    }
}
