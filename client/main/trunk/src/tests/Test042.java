package tests;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sf.opengroove.client.ui.TestFrame;

public class Test042
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        final TestFrame f = new TestFrame();
        f.show();
        final JTextArea area = new JTextArea();
        final JLabel label = new JLabel("");
        label.setFont(Font.decode(null));
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(new JScrollPane(area),
            BorderLayout.CENTER);
        f.getContentPane().add(label, BorderLayout.NORTH);
        area.getDocument().addDocumentListener(
            new DocumentListener()
            {
                public void something()
                {
                    try
                    {
                        label.setText(area.getText());
                    }
                    catch (Exception e)
                    {
                    }
                    f.invalidate();
                    f.validate();
                    f.repaint();
                }
                
                @Override
                public void changedUpdate(DocumentEvent e)
                {
                    something();
                }
                
                @Override
                public void insertUpdate(DocumentEvent e)
                {
                    something();
                }
                
                @Override
                public void removeUpdate(DocumentEvent e)
                {
                    something();
                }
            });
    }
    
}
