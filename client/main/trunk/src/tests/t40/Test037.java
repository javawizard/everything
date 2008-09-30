package tests.t40;

import java.awt.Color;

import net.sf.opengroove.client.ui.ColorChooserButton;
import net.sf.opengroove.client.ui.TestFrame;

public class Test037
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        TestFrame frame = new TestFrame();
        frame.getContentPane().add(
            new ColorChooserButton(Color.RED));
        frame.show();
    }
}
