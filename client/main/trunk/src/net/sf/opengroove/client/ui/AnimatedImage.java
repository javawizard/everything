package net.sf.opengroove.client.ui;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.io.File;
import java.lang.ref.WeakReference;

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
    public static class AnimatedObserver implements
        ImageObserver
    {
        /**
         * We're using a WeakReference because anonymous inner class instances
         * maintain a strong reference to their enclosing instance, whould would
         * prevent the AnimatedImage from being finalized, because it is
         * referenced by the observer, which is in turn referenced by the
         * image's observer queue. If we use a weak reference, then the
         * AnimatedImage can still be finalized, at which point we can return
         * false from the image's next call to this observer to de-register the
         * observer.
         */
        private WeakReference<AnimatedImage> componentRef;
        
        public AnimatedObserver(AnimatedImage component)
        {
            this.componentRef = new WeakReference<AnimatedImage>(
                component);
        }
        
        @Override
        public boolean imageUpdate(Image img,
            int infoflags, int x, int y, int width,
            int height)
        {
            AnimatedImage component = componentRef.get();
            if (component == null)
                return false;
            if (!component.isRunning)
            {
                componentRef.clear();
                return false;
            }
            component.repaint();
            return true;
        }
        
    }
    
    private Image image;
    
    public AnimatedImage(File imageFile)
    {
        this(Toolkit.getDefaultToolkit().createImage(
            imageFile.getAbsolutePath()));
    }
    
    private boolean isRunning;
    
    public void stop()
    {
        isRunning = false;
    }
    
    public AnimatedImage(Image image)
    {
        this.image = image;
        image.getWidth(new AnimatedObserver(this));
    }
    
    public void finalize() throws Throwable
    {
        isRunning = false;
        super.finalize();
    }
    
    public void paintComponent(Graphics g)
    {
        g.setColor(new Color(255, 255, 255, 0));
        g.fillRect(0, 0, getWidth(), getHeight());
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        if (imageWidth == -1)
            imageWidth = 0;
        if (imageHeight == -1)
            imageHeight = 0;
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
    
    private boolean hExpand = false;
    private boolean vExpand = false;
    
    public boolean isHExpand()
    {
        return hExpand;
    }
    
    public boolean isVExpand()
    {
        return vExpand;
    }
    
    public void setHExpand(boolean expand)
    {
        hExpand = expand;
    }
    
    public void setVExpand(boolean expand)
    {
        vExpand = expand;
    }
    
    public Dimension getMaximumSize()
    {
        Dimension preferred = getPreferredSize();
        return new Dimension(hExpand ? Integer.MAX_VALUE
            : preferred.width, vExpand ? Integer.MAX_VALUE
            : preferred.height);
    }
}
