package tests;

import javax.swing.JFrame;

import com.jidesoft.swing.JideButton;

import net.sf.opengroove.client.Statics;
import net.sf.opengroove.client.ui.TestFrame;

public class Test034
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Statics.run();
        JFrame frame = new TestFrame();
        JideButton button = new JideButton("Test button");
        button.setButtonStyle(button.HYPERLINK_STYLE);
        frame.getContentPane().add(button);
        frame.show();
    }
}
