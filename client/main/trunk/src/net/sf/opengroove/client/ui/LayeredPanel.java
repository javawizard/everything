package net.sf.opengroove.client.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;

/**
 * A class that is constructed with a list of JComponents, which are then
 * layered one on top of each other, with the first panel lowest in depth and
 * the last panel highest (IE the last panel will appear above everything before
 * it). This class is mostly a simplification of JLayeredPane, which it uses
 * internally.
 * 
 * @author Alexander Boyd
 * 
 */
public class LayeredPanel extends JComponent
{
    private JLayeredPane pane;
    private JComponent[] components;
    
    public LayeredPanel(JComponent[] components)
    {
        pane = new JLayeredPane();
        pane.setLayout(null);
        this.components = components;
        setLayout(new BorderLayout());
        
    }
    
    public void setSize(int w, int h)
    {
        for (JComponent component : components)
        {
            component.setSize(w, h);
        }
    }
}
