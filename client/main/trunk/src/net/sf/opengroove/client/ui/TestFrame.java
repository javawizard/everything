package net.sf.opengroove.client.ui;

import java.awt.FlowLayout;

import javax.swing.JFrame;

/**
 * A frame for doing general testing. It sets it's width to 400 by 300, centers
 * itself on the screen, and sets it's content pane layout to a FlowLayout.
 * 
 * @author Alexander Boyd
 * 
 */
public class TestFrame extends JFrame
{
    public TestFrame()
    {
        setSize(400, 300);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new FlowLayout());
    }
    
    public TestFrame(String string)
    {
        this();
        setTitle(string);
    }
}
