package tests;

import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.JButton;

import org.apache.batik.swing.JSVGCanvas;

import net.sf.opengroove.client.ui.SVGPanel;
import net.sf.opengroove.client.ui.TestFrame;

public class Test048
{
    
    /**
     * @param args
     * @throws MalformedURLException
     */
    public static void main(String[] args)
        throws MalformedURLException
    {
        final TestFrame f = new TestFrame();
        f.setSize(600, 400);
        f.setLayout(new BorderLayout());
        SVGPanel panel = new SVGPanel(new File(
            "icons/splash.svg").toURI().toURL().toString());
        panel.add(new JButton("A test button"));
        f.add(panel, BorderLayout.CENTER);
        f.show();
    }
}
