package net.sf.opengroove.common.ui;

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
     * {@link MouseEvent#isPopupTrigger()} method.<br/><br/>
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
    
    /**
     * Returns a string which represents the initial string, but with the wrap
     * string (which would usually be a newline or, for html content, something
     * like "&lt;br/&gt;") inserted such that none of the text lines in the
     * resulting string is longer than <code>width</code>, unless the line is
     * made up of only one word and the word itself is longer than
     * <code>width</code>. Lines are only wrapped on space characters, and
     * the space character is retained (with the wrap string inserted directly
     * after the space).
     * 
     * @param input
     * @param width
     * @return
     */
    public static String lineWrap(String input,
        String wrapString, int width)
    {
        if (input.length() <= width)
            /*
             * The input is shorter than the specified width, so it should take
             * up only one line
             */
            return input;
        if (input.indexOf(" ") == -1)
            /*
             * The input is longer than one line but contains no spaces
             */
            return input;
        /*
         * The input is longer than the specified length and contains a space.
         * Now we need to find the last space within the width specified, and
         * wrap around it. If there isn't a space within the width specified, we
         * find the first space after the width specified and wrap on that one
         * instead.
         */
        String firstWidth = input.substring(0, width);
        String remainder = input.substring(width);
        int lastSpaceIndex = firstWidth.lastIndexOf(" ");
        int firstSpaceIndex = remainder.indexOf(" ");
        assert firstSpaceIndex != -1
            || lastSpaceIndex != -1;
        int index = (lastSpaceIndex == -1) ? (firstSpaceIndex + width)
            : lastSpaceIndex;
        /*
         * Now we need to position the pointer directly after the space
         */
        index = index + 1;
        input = input.substring(0, index)
            + wrapString
            + lineWrap(input.substring(index), wrapString,
                width);
        return input;
    }
    
    /**
     * Returns the text specified, but line-wrapped and with &lt;html&gt; added
     * to the beginning. This is intended to be used with tool tips.
     * 
     * @param text
     *            The text to wrap
     * @return The wrapped text, with &lt;html&gt; at the beginning
     */
    public static String htmlTipWrap(String text)
    {
        return "<html>" + lineWrap(text, "<br/>", 80);
    }
}
