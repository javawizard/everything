package tests;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JViewport;
import javax.swing.border.LineBorder;

import net.sf.opengroove.client.ui.ScrollButtons;

public class Test027
{
    
    /**
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args)
        throws InterruptedException
    {
        JFrame frame = new JFrame();
        frame.setSize(300, 100);
        frame.setLocationRelativeTo(null);
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,
            BoxLayout.X_AXIS));
        JPanel outer = new JPanel();
        outer.setLayout(new BorderLayout());
        for (int i = 0; i < 10; i++)
        {
            JButton b = new JButton("Test button "
                + (i + 1));
            b
                .setBorder(new LineBorder(Color.LIGHT_GRAY,
                    1));
            panel.add(b);
        }
        final JViewport viewport = new JViewport();
        viewport.setView(panel);
        viewport.setViewPosition(new Point(0, 0));
        outer.add(viewport, BorderLayout.CENTER);
        final ScrollButtons bar = new ScrollButtons(
            ScrollButtons.Orientation.HORIZONTAL, 3);
        bar.setMinimumSize(new Dimension(20, 10));
        frame.getContentPane().add(outer,
            BorderLayout.NORTH);
        outer.add(bar, BorderLayout.EAST);
        viewport
            .addComponentListener(new ComponentListener()
            {
                
                @Override
                public void componentHidden(ComponentEvent e)
                {
                    // TODO Auto-generated method stub
                    
                }
                
                @Override
                public void componentMoved(ComponentEvent e)
                {
                    // TODO Auto-generated method stub
                    
                }
                
                @Override
                public void componentResized(
                    ComponentEvent e)
                {
                    bar.setMaximum(panel.getWidth()
                        - viewport.getWidth());
                    viewport.setViewPosition(new Point(bar
                        .getValue(), 0));
                }
                
                @Override
                public void componentShown(ComponentEvent e)
                {
                    // TODO Auto-generated method stub
                    
                }
            });
        frame.show();
        bar.addAdjustmentListener(new AdjustmentListener()
        {
            
            @Override
            public void adjustmentValueChanged(
                AdjustmentEvent e)
            {
                viewport.setViewPosition(new Point(bar
                    .getValue(), 0));
            }
        });
    }
}
