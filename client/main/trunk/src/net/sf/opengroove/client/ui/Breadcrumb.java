package net.sf.opengroove.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JViewport;

import net.sf.opengroove.client.com.ListenerManager;
import net.sf.opengroove.client.com.Notifier;
import net.sf.opengroove.common.ui.ComponentUtils;

/**
 * This class shows a simple breadcrumb component. An application can hand it a
 * String[], which represents the current path that it should display. The user
 * can click on any of the path components, which calls back to any
 * BreadcrumbListeners registered. Those listeners can update the path that is
 * displayed. It also supports registering a BreadcrumbModel, which should
 * provide to the breadcrumb component the children of any given element. If a
 * breadcrumb model is supplied, then the breadcrumb will display TBD.
 * 
 * @author Alexander Boyd
 * 
 */
public class Breadcrumb extends JPanel implements
    AdjustmentListener, ComponentListener
{
    private ListenerManager<BreadcrumbListener> listeners = new ListenerManager<BreadcrumbListener>();
    
    private String[] items = new String[0];
    private JPanel inner = new JPanel();
    private JViewport viewport;
    private ScrollButtons scroll;
    
    public void addBreadcrumbListener(
        BreadcrumbListener listener)
    {
        listeners.add(listener);
    }
    
    public void removeBreadcrumbListener(
        BreadcrumbListener listener)
    {
        listeners.remove(listener);
    }
    
    public Breadcrumb()
    {
        setLayout(new BorderLayout());
        inner.setLayout(new BoxLayout(inner,
            BoxLayout.X_AXIS));
        viewport = new JViewport();
        viewport.setView(ComponentUtils.pad(inner, 0, 0, 0,
            4));
        viewport.setViewPosition(new Point(0, 0));
        add(viewport, BorderLayout.CENTER);
        scroll = new ScrollButtons(
            ScrollButtons.Orientation.HORIZONTAL, 4);
        add(scroll, BorderLayout.EAST);
        viewport.addComponentListener(this);
        addComponentListener(this);
        scroll.addAdjustmentListener(this);
        componentResized(null);
    }
    
    public void setItems(String[] items)
    {
        this.items = items;
        inner.removeAll();
        for (int i = 0; i < items.length; i++)
        {
            JButton button = new JButton(items[i]);
            button.setBorder(BorderFactory
                .createCompoundBorder(BorderFactory
                    .createLineBorder(Color.GRAY, 1),
                    BorderFactory.createEmptyBorder(0, 2,
                        0, 2)));
            inner.add(button);
            final int fI = i;
            button.addActionListener(new ActionListener()
            {
                
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    listeners
                        .notify(new Notifier<BreadcrumbListener>()
                        {
                            
                            @Override
                            public void notify(
                                BreadcrumbListener listener)
                            {
                                listener.itemClicked(
                                    Breadcrumb.this,
                                    Breadcrumb.this.items,
                                    fI);
                            }
                        });
                }
            });
        }
        inner.invalidate();
        inner.validate();
        inner.repaint();
        invalidate();
        validate();
        repaint();
        componentResized(null);
        scroll.setValue(scroll.getMaximum());
        invalidate();
        validate();
        repaint();
    }
    
    @Override
    public void adjustmentValueChanged(AdjustmentEvent e)
    {
        viewport.setViewPosition(new Point(scroll
            .getValue(), 0));
    }
    
    @Override
    public void componentHidden(ComponentEvent e)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void componentMoved(ComponentEvent e)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void componentResized(ComponentEvent e)
    {
        scroll.setMaximum(inner.getWidth()
            - viewport.getWidth());
        viewport.setViewPosition(new Point(scroll
            .getValue(), 0));
    }
    
    @Override
    public void componentShown(ComponentEvent e)
    {
        // TODO Auto-generated method stub
        
    }
}
