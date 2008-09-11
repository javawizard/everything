package net.sf.opengroove.client.ui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

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
    
    public static void addPopup(Component c, JPopupMenu popup)
    {
        
    }
}
