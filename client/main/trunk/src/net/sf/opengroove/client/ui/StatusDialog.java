package net.sf.opengroove.client.ui;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JLabel;

import com.jidesoft.swing.MultilineLabel;

/**
 * A modal JDialog that contains a JLabel, and an animated spinner icon on the
 * left. The label's text can be set using the setText() and getText() methods.
 * 
 * @author Alexander Boyd
 * 
 */
public class StatusDialog extends JDialog
{
    private MultilineLabel label;
    
    public StatusDialog(Window parent, String text)
    {
        super(parent, "OpenGroove",
            ModalityType.APPLICATION_MODAL);
        label = new MultilineLabel(text);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(label, BorderLayout.CENTER);
        getContentPane().add(
            new AnimatedImage(ProgressItem.Status.ACTIVE
                .getImage()), BorderLayout.WEST);
        setSize(400, 150);
        invalidate();
        validate();
        repaint();
    }
    
    public static void main(String[] args)
    {
        TestFrame f = new TestFrame();
        f.show();
        StatusDialog d = new StatusDialog(f,"This is some test status information");
        d.show();
    }
    
    public void setText(String text)
    {
        label.setText(text);
    }
}
