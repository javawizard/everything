package tests;

import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.UIManager;

import net.sf.opengroove.client.OpenGroove;
import net.sf.opengroove.client.OpenGroove.Icons;
import net.sf.opengroove.client.ui.TestFrame;

public class Test044
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
//        UIManager.setLookAndFeel(UIManager
//            .getSystemLookAndFeelClassName());
        for (Icons icon : Icons.values())
        {
            icon.setImage(OpenGroove.scaleImage(OpenGroove
                .loadImage(icon.getIconPath()), icon
                .getSize(), icon.getSize()));
        }
        TestFrame frame = new TestFrame();
        JButton button = new JButton(new ImageIcon(
            OpenGroove.Icons.GENERIC_ADD_16.getImage()));
        button.setFocusable(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        frame.add(button);
        JButton button2 = new JButton(new ImageIcon(
            OpenGroove.Icons.GENERIC_CANCEL_16.getImage()));
        button2.setFocusable(false);
        button2.setMargin(new Insets(0, 0, 0, 0));
        frame.add(button2);
        frame.show();
    }
    
}
