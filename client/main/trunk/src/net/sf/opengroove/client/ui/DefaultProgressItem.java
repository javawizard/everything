package net.sf.opengroove.client.ui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;

public class DefaultProgressItem implements ProgressItem
{
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
        this.status = Status.PENDING;
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
    
    @Override
    public void setParent(ProgressPane parent)
    {
        this.parent = parent;
    }
}
