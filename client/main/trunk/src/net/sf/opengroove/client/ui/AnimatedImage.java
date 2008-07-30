package net.sf.opengroove.client.ui;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.io.File;
import java.lang.ref.WeakReference;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.h2.server.ShutdownHandler;

/**
 * This class is a component that shows an animated image. It takes care of
 * repainting the image whenever the next animation frame is ready.
 * 
 * @author Alexander Boyd
 * 
 */
public class AnimatedImage extends JComponent
{
    public static class AnimatedThread extends Thread
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
        protected boolean stopWorking;
        
        public AnimatedThread(AnimatedImage component)
        {
            this.componentRef = new WeakReference<AnimatedImage>(
                component);
        }
        
        public void run()
        {
            while (true)
            {
                try
                {
                    Thread.sleep(50);
                    imageUpdate();
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        
        public void imageUpdate()
        {
            try
            {
                final AnimatedImage component = componentRef
                    .get();
                if (component == null)
                {
                    System.out.println("lost reference");
                    throw new RuntimeException();
                }
                if ((!component.isRunning) || stopWorking)
                {
                    componentRef.clear();
                    System.out
                        .println("component shutdown");
                    throw new RuntimeException();
                }
                if (component.getParent() != null)
                    component.repaint();
            }
            catch (Exception e)
            {
                System.err
                    .println("caught exception in animated observer");
                e.printStackTrace();
            }
        }
    }
    
    private Image image;
    
    private AnimatedThread thread;
    
    public AnimatedImage(File imageFile)
    {
        this(Toolkit.getDefaultToolkit().createImage(
            imageFile.getAbsolutePath()));
        // FIXME: adding an anonymous inner class as a listener will prevent
        // this class (IE AnimatedImage) from being finalized
        addAncestorListener(new AncestorListener()
        {
            
            @Override
            public synchronized void ancestorAdded(
                AncestorEvent event)
            {
                if (thread == null || !thread.isAlive())
                {
                    thread = new AnimatedThread(
                        AnimatedImage.this);
                    thread.start();
                }
            }
            
            @Override
            public void ancestorMoved(AncestorEvent event)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public synchronized void ancestorRemoved(
                AncestorEvent event)
            {
                if (thread != null && thread.isAlive())
                {
                    thread.stopWorking = true;
                    thread = null;
                }
            }
        });
    }
    
    private boolean isRunning = true;
    
    public void stop()
    {
        isRunning = false;
    }
    
    public AnimatedImage(Image image)
    {
        this.image = image;
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
        g.drawImage(image, (getWidth() / 2)
            - (imageWidth / 2), (getHeight() / 2)
            - (imageHeight / 2), null);
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
