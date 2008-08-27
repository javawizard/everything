package net.sf.opengroove.client.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

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
    private JLabel label;
    
    public StatusDialog(Window parent, String text)
    {
        super(parent, "OpenGroove",
            ModalityType.APPLICATION_MODAL);
        setResizable(false);
        label = new JLabel();
        label.setHorizontalAlignment(label.CENTER);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.add(label, BorderLayout.CENTER);
        panel.add(new AnimatedImage(
            ProgressItem.Status.ACTIVE.getImage()),
            BorderLayout.WEST);
        getContentPane().add(panel);
        pack();
        setText(text);
        setLocationRelativeTo(parent);
        invalidate();
        validate();
        repaint();
        getContentPane().invalidate();
        getContentPane().validate();
        getContentPane().repaint();
        panel.invalidate();
        panel.validate();
        panel.repaint();
        invalidate();
        validate();
        repaint();
    }
    
    public static void main(String[] args)
    {
        ProgressItem.Status.ACTIVE.getImage();
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        TestFrame f = new TestFrame();
        f.setSize(500, 300);
        f.show();
        StatusDialog d = new StatusDialog(f,
            "Please wait while your security keys are generated...");
        d.showImmediate();
    }
    
    /**
     * Same as show() or setVisible(true), but the method returns immediately
     * instead of waiting for the dialog to be hidden. This is accomplished by
     * showing the dialog in a separate thread, and then polling the dialog
     * until it becomes visible to avoid it being hidden (in the event of a
     * really quick task, so that the dialog is not shown for very long) so
     * quick after being shown that the new threads ends up showing the dialog
     * after the request to hide it.
     */
    public void showImmediate()
    {
        new Thread()
        {
            @SuppressWarnings("deprecation")
            public void run()
            {
                show();
            }
        }.start();
        while (!isVisible())
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                break;
            }
    }
    
    public void setText(String text)
    {
        label.setText(text);
        Dimension size = label.getPreferredSize();
        setSize(size.width + 116 + getInsets().left
            + getInsets().right, size.height + 60
            + getInsets().top + getInsets().bottom);
    }
}
