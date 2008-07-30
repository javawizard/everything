package net.sf.opengroove.client.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class DefaultProgressItem implements ProgressItem
{
    private static final Image EMPTY_IMAGE = new BufferedImage(
        16, 16, BufferedImage.TYPE_INT_ARGB);
    private JLabel nameLabel;
    private Component details;
    private Status status;
    private ProgressPane parent;
    
    public DefaultProgressItem(String name,
        Component details)
    {
        this.nameLabel = new JLabel(name);
        this.nameLabel.setMaximumSize(new Dimension(
            Integer.MAX_VALUE, Integer.MAX_VALUE));
        this.details = details;
        setStatus(Status.PENDING);
    }
    
    @Override
    public Component getDetailsComponent()
    {
        return details;
    }
    
    @Override
    public Component getNameComponent()
    {
        return nameLabel;
    }
    
    public Status getStatus()
    {
        return status;
    }
    
    private JComponent statusComponent;
    
    public JComponent getStatusComponent()
    {
        return statusComponent;
    }
    
    public synchronized void setStatus(Status newStatus)
    {
        this.status = newStatus;
        if (statusComponent != null
            && statusComponent instanceof AnimatedImage)
        {
            ((AnimatedImage) statusComponent).stop();
        }
        Image i = status.getImage();
        if (i == null)
            i = EMPTY_IMAGE;
        statusComponent = new AnimatedImage(i);
        ((AnimatedImage) statusComponent).setVExpand(true);
        ((AnimatedImage) statusComponent).setMinWidth(24);
        ((AnimatedImage) statusComponent).setMinHeight(24);
        if (parent != null)
            parent.refresh();
        else
            System.out.println("parent null");
    }
    
    @Override
    public void setParent(ProgressPane parent)
    {
        this.parent = parent;
    }
}
