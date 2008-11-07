package net.sf.opengroove.client.ui;

import java.awt.Color;
import net.sf.opengroove.common.ui.ComponentUtils;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

import net.sf.opengroove.client.com.ListenerManager;
import net.sf.opengroove.client.ui.DatePicker.Clock.ClockListener;

/**
 * A component that allows a user to pick a date, a time, or both.
 * 
 * @author Alexander Boyd
 * 
 */
public class DatePicker extends JPanel
{
    /**
     * An analog clock. Tick marks are drawn for every hour, and numerals are
     * drawn for every 3 hours.
     * 
     * @author Alexander Boyd
     * 
     */
    public static class Clock extends JComponent
    {
        public interface ClockListener
        {
            public void timeChanged(Clock clock);
        }
        
        public void addClockListener(ClockListener l)
        {
            listeners.add(l);
        }
        
        public void removeClockListener(ClockListener l)
        {
            listeners.remove(l);
        }
        
        private ListenerManager<ClockListener> listeners = new ListenerManager<ClockListener>();
        private int time = 0;
        private Hand selected;
        private Hand hovered;
        private int original;
        private double notchMin = 0.92;
        private Color borderColor = Color.BLACK;
        private Color fillColor = Color.WHITE;
        private Color textColor = Color.BLACK;
        private Color notchColor = Color.DARK_GRAY;
        private Color hourColor = Color.BLUE.darker();
        private double hourLength = 0.5;
        private double hourWidthAdd = 4;
        private double hourWidthMul = 0;
        private Color minuteColor = Color.GREEN.darker();
        private double minuteLength = 0.65;
        private double minuteWidthAdd = 3;
        private double minuteWidthMul = 0;
        private Color secondColor = Color.RED.darker();
        private double secondLength = 0.75;
        private double secondWidthAdd = 2;
        private int secondWidthMul = 0;
        
        private enum Hand
        {
            HOUR, MINUTE, SECOND
        }
        
        /**
         * Creates a new clock.
         * 
         * @param time
         *            The time that the clock should display, in seconds. 0 is
         *            12:00. 3600 is 1:00.
         */
        public Clock(int time)
        {
            this.time = (time % 43200);
            setMinimumSize(new Dimension(100, 100));
            setPreferredSize(new Dimension(200, 200));
            setMaximumSize(new Dimension(Integer.MAX_VALUE,
                Integer.MAX_VALUE));
            setOpaque(false);
        }
        
        public void paintComponent(Graphics g)
        {
            
            int size = Math.min(getWidth(), getHeight());
            if (size < 4)
                return;
            int radius = size / 2;
            g.setColor(this.fillColor);
            g.fillOval(1, 1, size - 2, size - 2);
            g.setColor(this.borderColor);
            g.drawOval(1, 1, size - 2, size - 2);
            for (int i = 0; i < 12; i++)
            {
                double ix = ComponentUtils.toX(i
                    * (360 / 12));
                double iy = ComponentUtils.toY(i
                    * (360 / 12));
                g.setColor(this.notchColor);
                g
                    .drawLine(
                        (int) ((ix * (size / 2)) + size / 2),
                        (int) ((iy * (size / 2)) + size / 2),
                        (int) (((ix * (size / 2)) * notchMin) + size / 2),
                        (int) (((iy * (size / 2)) * notchMin))
                            + size / 2);
            }
            g.setColor(this.textColor);
            Font font = new Font("Dialog", Font.BOLD, 24);
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            int fh = fm.getHeight();
            int f12 = fm.stringWidth("12");
            int f3 = fm.stringWidth("3");
            int f6 = fm.stringWidth("6");
            int f9 = fm.stringWidth("9");
            g.drawString("12", (size / 2) - (f12 / 2), fh);
            g.drawString("3", size - f3, (size / 2)
                + (fh / 2));
            g.drawString("6", (size / 2) - (f6 / 2), size);
            g.drawString("9", 0, (size / 2) + (fh / 2));
            drawHand(
                size,
                g,
                (int) ((getHours(time)) * (360.0 / 12.0)),
                (int) (hourLength * (size / 2)),
                (int) (hourWidthAdd + (hourWidthMul * size)),
                selected == Hand.HOUR ? hourColor.darker()
                    : ((selected == null && hovered == Hand.HOUR) ? hourColor
                        .brighter()
                        : hourColor));
        }
        
        private void drawHand(int baseSize, Graphics g,
            int angle, int length, int width, Color color)
        {
            Polygon polygon = generatePolygon(baseSize,
                angle, length, width);
            g.setColor(color);
            g.fillPolygon(polygon);
        }
        
        private Polygon generatePolygon(int baseSize,
            int angle, int length, int width)
        {
            int center = baseSize / 2;
            Point p1 = generateHandPoint(angle, length);
            Point p2 = generateHandPoint(angle + 90, width);
            Point p3 = generateHandPoint(angle + 180, width);
            Point p4 = generateHandPoint(angle + 270, width);
            return new Polygon(
                new int[] { p1.x + center, p2.x + center,
                    p3.x + center, p4.x + center },
                new int[] { p1.y + center, p2.y + center,
                    p3.y + center, p4.y + center }, 4);
        }
        
        private Point generateHandPoint(int angle,
            int length)
        {
            return new Point((int) (ComponentUtils
                .toX(angle) * length),
                (int) (ComponentUtils.toY(angle) * length));
        }
        
        private double getHours(int time)
        {
            return ((time * 1.0d) % 43200) / (43200.0 / 12);
        }
    }
}
