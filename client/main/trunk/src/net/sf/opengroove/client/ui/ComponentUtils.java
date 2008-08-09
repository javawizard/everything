package net.sf.opengroove.client.ui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class ComponentUtils
{
    public static JPanel pad(JComponent component, int top,
        int left, int bottom, int right)
    {
        javax.swing.JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(
            top, left, bottom, right));
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }
}
