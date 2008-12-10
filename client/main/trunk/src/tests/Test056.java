package tests;

import java.awt.BorderLayout;
import java.net.URL;

import javax.swing.*;

import org.jdesktop.jdic.browser.BrowserEngineManager;
import org.jdesktop.jdic.browser.IWebBrowser;
import org.jdesktop.jdic.browser.WebBrowser;

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
    public static void main(String[] args)throws Throwable
    {
        JFrame frame = new JFrame("test");
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        WebBrowser browser = new WebBrowser(
            new URL(
                "file:///c:/Users/amboyd/Desktop/testbrowser.html"));
        frame.getContentPane()
            .setLayout(new BorderLayout());
        frame.getContentPane().add(browser);
        frame.show();
    }
    
}
