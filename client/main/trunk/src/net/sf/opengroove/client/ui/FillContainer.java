package net.sf.opengroove.client.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import net.sf.opengroove.projects.filleditor.FillImage;

/**
 * A FillContainer takes a FillImage and draws it as the background of itself.
 * The image will be scaled (discarding aspect ratio) to fit the size of the
 * panel.
 * 
 * @author Alexander Boyd
 * 
 */
public class FillContainer extends JPanel
{
    private FillImage image;
    
    public FillContainer(FillImage image)
    {
        this.image = image;
    }
    
    public void setFillImage(FillImage image)
    {
        this.image = image;
    }
    
    public void paintComponent(Graphics g1)
    {
        Graphics2D g = (Graphics2D) g1;
        image.draw(g, getWidth(), getHeight());
    }
}
