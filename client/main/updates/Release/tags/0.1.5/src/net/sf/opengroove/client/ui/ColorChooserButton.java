package net.sf.opengroove.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.opengroove.client.com.ListenerManager;
import net.sf.opengroove.client.com.Notifier;

public class ColorChooserButton extends JButton
{
    private JColorChooser chooser = new JColorChooser();
    
    private JSlider alphaSlider;
    
    private Color color;
    
    private ListenerManager<ChangeListener> listeners = new ListenerManager<ChangeListener>();
    
    public void addColorChangeListener(
        ChangeListener listener)
    {
        listeners.add(listener);
    }
    
    public void removeColorChangeListener(
        ChangeListener listener)
    {
        listeners.remove(listener);
    }
    
    public JColorChooser getChooser()
    {
        return chooser;
    }
    
    private JPopupMenu menu;
    private JButton ok;
    
    public ColorChooserButton(Color color)
    {
        chooser.setColor(color);
        this.color = color;
        alphaSlider = new JSlider();
        alphaSlider.setMinimum(0);
        alphaSlider.setMaximum(255);
        alphaSlider.setMajorTickSpacing(50);
        alphaSlider.setMinorTickSpacing(10);
        alphaSlider.setValue(color.getAlpha());
        setIcon(new Icon()
        {
            
            @Override
            public int getIconHeight()
            {
                return 12;
            }
            
            @Override
            public int getIconWidth()
            {
                return 16;
            }
            
            @Override
            public void paintIcon(Component c, Graphics g,
                int x, int y)
            {
                g.setColor(ColorChooserButton.this.color);
                g.fillRect(x, y, getIconWidth(),
                    getIconHeight());
            }
        });
        menu = new JPopupMenu();
        menu.setLayout(new BorderLayout());
        menu.add(chooser, BorderLayout.CENTER);
        menu.add(alphaSlider, BorderLayout.SOUTH);
        ChangeListener cl = new ChangeListener()
        {
            
            @Override
            public void stateChanged(ChangeEvent e)
            {
                ColorChooserButton.this.color = new Color(
                    chooser.getColor().getRed(), chooser
                        .getColor().getGreen(), chooser
                        .getColor().getBlue(), alphaSlider
                        .getValue());
                ColorChooserButton.this.repaint();
                listeners
                    .notify(new Notifier<ChangeListener>()
                    {
                        
                        @Override
                        public void notify(
                            ChangeListener listener)
                        {
                            listener.stateChanged(null);
                        }
                    });
            }
        };
        chooser.getSelectionModel().addChangeListener(cl);
        alphaSlider.addChangeListener(cl);
        addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                menu.show(ColorChooserButton.this, 0,
                    ColorChooserButton.this.getHeight());
            }
        });
    }
    
    public Color getColor()
    {
        return color;
    }
}
