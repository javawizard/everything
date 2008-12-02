package tests;

import javax.swing.JList;

import net.sf.opengroove.client.ui.TestFrame;

public class Test054
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        JList list = new JList(new String[] { "Item 1",
            "Item 2", "Item 3" });
        TestFrame f = new TestFrame();
        f.getContentPane().add(list);
        f.show();
    }
    
}
