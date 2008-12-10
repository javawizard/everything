package tests;

import java.awt.BorderLayout;

import javax.swing.*;

/**
 * A class for testing out javascript inside a JEditorPane.
 * 
 * @author Alexander Boyd
 * 
 */
public class Test056
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("test");
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        JEditorPane editorPane = new JEditorPane();
        frame.getContentPane()
            .setLayout(new BorderLayout());
        frame.getContentPane().add(editorPane);
        frame.show();
        editorPane.setContentType("text/html");
        editorPane
            .setText("<html><head></head><body> test <b>text</b>" +
            		"<script type=\"text/javascript\">" +
            		"</script></body></html>");
    }
    
}
