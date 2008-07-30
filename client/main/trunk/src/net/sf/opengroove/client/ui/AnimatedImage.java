package net.sf.opengroove.client.ui;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.io.File;

import javax.swing.JComponent;

/**
 * This class is a component that shows an animated image. It takes care of
 * repainting the image whenever the next animation frame is ready.
 * 
 * @author Alexander Boyd
 * 
 */
public class AnimatedImage extends JComponent
{
    private Image image;
    
    public AnimatedImage(File imageFile)
    {
        image = Toolkit.getDefaultToolkit().createImage(
            imageFile.getAbsolutePath());
        image.getWidth(new ImageObserver()
        {
            
            @Override
            public boolean imageUpdate(Image img,
                int infoflags, int x, int y, int width,
                int height)
            {
                repaint();
                return true;
            }
        });
        new Applet().getImage(null);
    }
    
    public void paintComponent(Graphics g)
    {
        g.setColor(new Color(255, 255, 255, 0));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(image, 0, 0, null);
    }
    
    public Dimension getPreferredSize()
    {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        if (width == -1 || height == -1)
        {
            return super.getPreferredSize();
        }
        return new Dimension(width, height);
    }
    
    public Dimension getMinimumSize()
    {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        if (width == -1 || height == -1)
        {
            return super.getMinimumSize();
        }
        return new Dimension(width, height);
    }
    
}
