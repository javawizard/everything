package org.opengroove.g4.mobile;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class G4Mobile
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        final Frame f = new Frame("G4 Mobile");
        f.setLayout(new FlowLayout());
        Button button = new Button("Show dialog");
        f.add(button);
        button.addActionListener(new ActionListener()
        {
            
            public void actionPerformed(ActionEvent arg0)
            {
                final Dialog dialog = new Dialog(f, true);
                dialog.setTitle("A test dialog");
                dialog.add(new Label("This is a label inside a dialog."));
                dialog.addWindowListener(new WindowAdapter()
                {
                    
                    public void windowClosing(WindowEvent arg0)
                    {
                        dialog.dispose();
                    }
                });
                dialog.show();
                dialog.dispose();
            }
        });
        f.show();
        f.addWindowListener(new WindowAdapter()
        {
            
            public void windowClosing(WindowEvent e)
            {
                f.dispose();
            }
        });
    }
    
}
