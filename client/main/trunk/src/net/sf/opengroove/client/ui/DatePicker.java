package net.sf.opengroove.client.ui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JPanel;

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
    private static class Clock extends JComponent
    {
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
            int width = getWidth();
            int height = getHeight();
            width = height = Math.min(width, height);
            if (width < 2 || height < 2)
                return;
            int radius = width / 2;
            g.setColor()
        }
    }
}
