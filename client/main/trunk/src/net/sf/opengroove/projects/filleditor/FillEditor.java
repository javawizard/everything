package net.sf.opengroove.projects.filleditor;

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
    private JComponent imageComponent = new JComponent()
    {
        public void paintComponent(Graphics g)
        {
            Graphics2D g2 = (Graphics2D) g;
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
