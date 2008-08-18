package net.sf.opengroove.client.ui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPopupMenu;

public class ColorChooserButton extends JButton
{
    private JColorChooser chooser;
    private JPopupMenu menu;
    private JButton ok;
    
    public ColorChooserButton()
    {
        chooser = new JColorChooser();
        menu = new JPopupMenu();
        ok = new JButton("OK");
        menu.setLayout(new BorderLayout());
    }
}
