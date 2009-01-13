package net.sf.opengroove.client.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

public class DefaultProgressItem implements ProgressItem
{
    private static final int STATUS_WIDTH = 24;
    private static final int STATUS_HEIGHT = 18;
    private static final Image EMPTY_IMAGE = new BufferedImage(
        STATUS_WIDTH, STATUS_HEIGHT,
        BufferedImage.TYPE_INT_ARGB);
    private JLabel nameLabel;
    private Component details;
    private Status status;
    private ProgressPane parent;
    
    public DefaultProgressItem(String name,
        String description, Component details)
    {
        this.nameLabel = new JLabel(name);
        nameLabel.setToolTipText(description);
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
        ((AnimatedImage) statusComponent)
            .setMinWidth(STATUS_WIDTH);
        ((AnimatedImage) statusComponent)
            .setMinHeight(STATUS_HEIGHT);
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
    
    @Override
    public Component getEmptyStatus()
    {
        JLabel label = new JLabel(
            new ImageIcon(EMPTY_IMAGE));
        label.setMaximumSize(new Dimension(
            Integer.MAX_VALUE, Integer.MAX_VALUE));
        return label;
    }
}
