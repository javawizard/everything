package net.sf.opengroove.client.ui;

import java.awt.Color;
import java.awt.Graphics;
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
        private int hour = 12;
        private int minute = 0;
        private int second = 0;
        private Hand selected;
        private Hand hovered;
        private int original;
        private Color borderColor = Color.BLACK;
        private Color fillColor = Color.WHITE;
        private Color textColor = Color.BLACK;
        private Color notchColor = Color.DARK_GRAY;
        private Color hourColor = Color.BLUE;
        private double hourLength = 0.5;
        private int hourWidthAdd = 12;
        private double hourWidthMul = 0;
        private Color minuteColor = Color.GREEN;
        private double minuteLength = 0.65;
        private int minuteWidthAdd = 8;
        private double minuteWidthMul = 0;
        private Color secondColor = Color.RED;
        private double secondLength = 0.75;
        private int secondWidthAdd = 3;
        private int secondWidthMul = 0;
        
        private enum Hand
        {
            HOUR, MINUTE, SECOND
        }
        
        public Clock(int hour, int minute, int second)
        {
            this.hour = hour;
            this.minute = minute;
            this.second = second;
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
            }
            drawHand(
                g,
                (hour % 12) * (360 / 12),
                (int) hourLength,
                (int) (hourWidthAdd + (hourWidthMul * size)),
                selected == Hand.HOUR ? hourColor.darker()
                    : ((selected == null && hovered == Hand.HOUR) ? hourColor
                        .brighter()
                        : hourColor));
        }
        
        private void drawHand(Graphics g, int angle,
            int length, int width, Color color)
        {
            Polygon polygon = generatePolygon(angle,
                length, width);
            g.setColor(color);
            g.fillPolygon(polygon);
        }
        
        private Polygon generatePolygon(int angle,
            int length, int width)
        {
            
        }
    }
}
