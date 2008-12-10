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
        Web
        frame.getContentPane()
            .setLayout(new BorderLayout());
        frame.getContentPane().add(editorPane);
        frame.show();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        editorPane
            .setText("<html><head>" +
            		"" +
            		"</head><body>" +
            		"<script language='JavaScript'>" +
            		"document.write('hi');"+
            		"</script>" +
            		"</body></html>");
    }
    
}
