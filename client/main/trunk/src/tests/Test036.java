package tests;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class Test036
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        JComponent component = new JComponent()
        {
            public void paintComponent(Graphics g1)
            {
                if (!(g1 instanceof Graphics2D))
                {
                    System.err.println("Not a 2D graphics");
                }
                Graphics2D g = (Graphics2D) g1;
                g.setClip(new Polygon(new int[] { 50, 250,
                    150 }, new int[] { 50, 50, 250 }, 3));
                g.setPaint(new GradientPaint(
                    new Point(0, 0), Color.RED, new Point(
                        150, 600), Color.BLUE));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        frame.getContentPane().add(component);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.show();
    }
    
}
