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
                g.drawRect(x, y, getIconWidth(),
                    getIconHeight());
            }
        });
        menu = new JPopupMenu();
        ok = new JButton("OK");
        menu.setLayout(new BorderLayout());
        menu.add(ok, BorderLayout.SOUTH);
        menu.add(chooser, BorderLayout.CENTER);
        ok.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                menu.hide();
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
