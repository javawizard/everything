package net.sf.opengroove.projects.filleditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class FillEditor
{
    private static JFrame frame;
    private static FillImage image;
    private Point selectedPoint;
    public static final int BOX_WIDTH = 8;
    public static final int BOX_HEIGHT = 8;
    public static final int HALF_BOX_WIDTH = BOX_WIDTH / 2;
    public static final int HALF_BOX_HEIGHT = BOX_HEIGHT / 2;
    public static final Color BOX_INNER_1 = new Color(100,
        100, 100, 160);
    public static final Color BOX_INNER_2 = new Color(255,
        0, 0, 160);
    private JComponent imageComponent = new JComponent()
    {
        public void paintComponent(Graphics g)
        {
            Graphics2D g2 = (Graphics2D) g;
            if (image != null)
                image.draw(g2, image.width, image.height);
            if (selectedPoint != null)
            {
                // TODO: change to %2000 to enable pulsing
                g2
                    .setColor((System.currentTimeMillis() % 1000) < 1000 ? BOX_INNER_1
                        : BOX_INNER_2);
                g2.drawRect(selectedPoint.x
                    - HALF_BOX_WIDTH, selectedPoint.y
                    - HALF_BOX_HEIGHT, BOX_WIDTH,
                    BOX_HEIGHT);
                g2.setColor(new Color(0, 0, 0, 200));
                g2.drawRect(selectedPoint.x
                    - HALF_BOX_WIDTH, selectedPoint.y
                    - HALF_BOX_HEIGHT, BOX_WIDTH,
                    BOX_HEIGHT);
            }
        }
    };
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        frame = new JFrame("FillEditor - OpenGroove");
    }
    
    /**
     * Builds the editor onto the panel spec
     * 
     * @param panel
     */
    public static void buildEditor(JPanel panel)
    {
        
    }
    
}
