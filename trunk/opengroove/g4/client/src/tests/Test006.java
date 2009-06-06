package tests;

import javax.swing.JFrame;

import org.opengroove.g4.client.ui.ItemChooser;

public class Test006
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        JFrame f = new JFrame();
        f.setLocationRelativeTo(null);
        f.show();
        System.out.println(ItemChooser.showItemChooser(f, "What would you like to do?",
            new String[] {
                "<html><b>Create a new account</b><br/>"
                    + "Choose this option if this is your first time using G4, or<br/>"
                    + "if you'd like to create a new account.",
                "<html><b>Use an existing account</b><br/>"
                    + "Choose this option if you've used G4 before and<br/>"
                    + "would like to use your account on this computer." }, true));
    }
}
