package tests.t40;

import javax.swing.JButton;

/**
 * Simple test class for printing out the class of a button's default border.
 * 
 * ...which turned out to be
 * javax.swing.plaf.BorderUIResource$CompoundBorderUIResource
 * 
 * @author Alexander Boyd
 * 
 */
public class Test024
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        System.out.println(new JButton().getBorder()
            .getClass().getName());
    }
    
}
