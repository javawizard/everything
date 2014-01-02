package net.sf.opengroove.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import net.sf.opengroove.client.com.ListenerManager;
import net.sf.opengroove.client.com.Notifier;

public class ScrollButtons extends JPanel implements
    MouseListener
{
    private JButton decreaseButton;
    private JButton increaseButton;
    private int value;
    private int min;
    private int max;
    private Timer timer;
    private boolean isGoingUp;
    
    private ListenerManager<AdjustmentListener> listeners = new ListenerManager<AdjustmentListener>();
    
    /**
     * Adds a listener. Note that, currently, when the listener's
     * adjustmentValueChanged method is called, it is passed a null argument
     * instead of an AdjustmentEvent. This is something we'll add in the future.
     * 
     * @param l
     */
    public void addAdjustmentListener(AdjustmentListener l)
    {
        // TODO: create an actionevent to pass to these listeners instead of
        // just passing null
        listeners.addListener(l);
    }
    
    public void removeAdjustmentListener(
        AdjustmentListener l)
    {
        listeners.removeListener(l);
    }
    
    public static enum Orientation
    {
        HORIZONTAL, VERTICAL
    }
    
    /**
     * Create a new ScrollButtons component.
     * 
     * @param orientation
     *            One of HORIZONTAL or VERTICAL
     * @param pixelDelay
     *            the number of milliseconds to wait before incrementing one
     *            value. For example, if this was 10, then every second a scroll
     *            button was held down would translate to an increase in the
     *            value of 100.
     */
    public ScrollButtons(Orientation orientation,
        int pixelDelay)
    {
        setLayout(new BorderLayout());
        timer = new Timer(pixelDelay, new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                setValue(isGoingUp ? value + 1 : value - 1);
            }
        });
        timer.setRepeats(true);
        timer.setInitialDelay(1);
        String increaseLabel;
        String decreaseLabel;
        // TODO: change to drawn triangles instead of html arrows
        if (orientation == Orientation.HORIZONTAL)
        {
            increaseLabel = "<html>&rarr;";
            decreaseLabel = "<html>&larr;";
        }
        else
        {
            increaseLabel = "<html>&darr;";
            decreaseLabel = "<html>&uarr;";
        }
        increaseButton = new JButton(increaseLabel);
        decreaseButton = new JButton(decreaseLabel);
        increaseButton.setBorder(new LineBorder(Color.GRAY,
            1));
        decreaseButton.setBorder(new LineBorder(Color.GRAY,
            1));
        increaseButton.setFocusable(false);
        decreaseButton.setFocusable(false);
        if (orientation == Orientation.HORIZONTAL)
        {
            add(increaseButton, BorderLayout.EAST);
            add(decreaseButton, BorderLayout.WEST);
        }
        else
        {
            add(increaseButton, BorderLayout.SOUTH);
            add(decreaseButton, BorderLayout.NORTH);
        }
        
        increaseButton.addMouseListener(this);
        decreaseButton.addMouseListener(this);
    }
    
    public int getValue()
    {
        return value;
    }
    
    @Override
    public void mouseClicked(MouseEvent e)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void mouseEntered(MouseEvent e)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void mouseExited(MouseEvent e)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void mousePressed(MouseEvent e)
    {
        isGoingUp = e.getSource() == increaseButton;
        timer.start();
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        timer.stop();
    }
    
    public void setMaximum(int i)
    {
        if (i < 0)
            i = 0;
        max = i;
        if (value > max)
            value = max;
        increaseButton.setEnabled(value < max);
        decreaseButton.setEnabled(value > min);
    }
    
    public int getMaximum()
    {
        return max;
    }
    
    public void setValue(int i)
    {
        if (i > max)
            i = max;
        else if (i < min)
            i = min;
        value = i;
        increaseButton.setEnabled(value < max);
        decreaseButton.setEnabled(value > min);
        listeners.notify(new Notifier<AdjustmentListener>()
        {
            
            @Override
            public void notify(AdjustmentListener listener)
            {
                listener.adjustmentValueChanged(null);
            }
        });
        
    }
}
