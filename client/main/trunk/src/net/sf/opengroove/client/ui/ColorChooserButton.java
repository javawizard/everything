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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ColorChooserButton extends JButton
{
    private JColorChooser chooser = new JColorChooser();
    
    public JColorChooser getChooser()
    {
        return chooser;
    }
    
    private JPopupMenu menu;
    private JButton ok;
    
    public ColorChooserButton(Color color)
    {
        chooser.setColor(color);
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
                g.setColor(chooser.getColor());
                g.fillRect(x, y, getIconWidth(),
                    getIconHeight());
                g.setColor(Color.BLACK);
            }
        });
        menu = new JPopupMenu();
        menu.setLayout(new BorderLayout());
        menu.add(chooser, BorderLayout.CENTER);
        chooser.getSelectionModel().addChangeListener(
            new ChangeListener()
            {
                
                @Override
                public void stateChanged(ChangeEvent e)
                {
                    ColorChooserButton.this.repaint();
                }
            });
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
}
