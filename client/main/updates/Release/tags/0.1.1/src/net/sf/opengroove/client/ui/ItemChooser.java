package net.sf.opengroove.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * A simple dialog that shows the user the items specified, and allows the user
 * to select one or (optionaly) click cancel. the main method to use is
 * showItemChooser(). if showCancel is true, then a cancel button will be shown,
 * and if the user closes the dialog box or clicks cancel then null will be
 * returned. if showCancel is false, the cancel button will be hidden and
 * clicking the close button on the dialog will result in a message being shown
 * to the user informing them that they need to make a choice first. the choices
 * are shown in a format suitable for long choices, IE as a long list rather
 * than a JComboBox. the items may also contain HTML markup, as per the standard
 * java swing html markup.
 * 
 * @author Alexander Boyd
 * 
 */
public class ItemChooser<E> extends JDialog
{
    public E choice;
    
    /**
     * creates a new ItemChooser. if you don't need advanced functionality,
     * consider using one of the showItemChooser() methods.<br/><br/>
     * 
     * once you have created the item chooser, call show() to show it. the
     * show() method will block until it is hidden. if a choice is clicked, the
     * field choice will contain the item chosen. if the item chooser was hidden
     * by a call to hide(), choice will be null.
     * 
     * @param parent
     *            the parent frame
     * @param message
     *            the message to show at the top of the chooser.
     * @param choices
     *            the choices that the user can choose. toString() is called on
     *            these choices to get the text to display for each choice. the
     *            text returned from toString() may contain html markup, as per
     *            what JButton accepts for it's setText() method.
     * @param lowerButtons
     *            the buttons to show at the bottom. for the showItemChooser()
     *            methods, this array contains only one button, the cancel
     *            button.
     * @param allowClose
     *            whether or not to allow the item chooser to be closed by
     *            clicking the X in the upper right corner. if this is true and
     *            the item chooser is closed by this action, the field choice
     *            will be null.
     */
    public ItemChooser(JFrame parent, String message,
        E[] choices, JButton[] lowerButtons,
        final boolean allowClose)
    {
        super(parent, true);
        setSize(450, 550);
        setLocationRelativeTo(parent);
        JPanel p1 = new JPanel();
        p1.setBorder(new EmptyBorder(15, 15, 15, 15));
        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());
        getContentPane().add(p2);
        JPanel p3 = new JPanel();
        p3.setLayout(new BorderLayout());
        p2.add(p3, BorderLayout.SOUTH);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            
            @Override
            public void windowClosing(WindowEvent e)
            {
                if (allowClose)
                    hide();
                else
                    JOptionPane.showMessageDialog(
                        ItemChooser.this,
                        "You must select a choice.");
            }
        });
        JPanel p5 = new JPanel();
        p3.add(p5, BorderLayout.EAST);
        p5.setLayout(new BoxLayout(p5, BoxLayout.X_AXIS));
        for (JButton button : lowerButtons)
        {
            p5.add(button);
        }
        JScrollPane sc = new JScrollPane(p1);
        sc.setBorder(null);
        p2.add(sc, BorderLayout.CENTER);
        p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(message);
        label.setFont(label.getFont().deriveFont(Font.BOLD)
            .deriveFont(18f));
        label.setBorder(new EmptyBorder(0, 0, 15, 0));
        label.setAlignmentX(0);
        label.setAlignmentY(0);
        Dimension preferredLabelSize = label
            .getPreferredSize();
        p1.add(label);
        for (final E choice : choices)
        {
            final JButton choiceButton = new JButton(""
                + choice);
            choiceButton.setFocusable(false);
            choiceButton.setAlignmentX(0);
            choiceButton.setAlignmentY(0);
            p1.add(choiceButton);
            choiceButton.setFont(choiceButton.getFont()
                .deriveFont(Font.PLAIN));
            choiceButton.setOpaque(false);
            final Border border = choiceButton.getBorder();
            final Insets insets = border
                .getBorderInsets(choiceButton);
            choiceButton.setBorder(new EmptyBorder(insets));
            choiceButton.setBackground(new Color(255, 255,
                255, 0));
            choiceButton
                .setHorizontalAlignment(choiceButton.LEFT);
            choiceButton
                .addMouseListener(new MouseListener()
                {
                    
                    @Override
                    public void mouseClicked(MouseEvent e)
                    {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void mouseEntered(MouseEvent e)
                    {
                        choiceButton.setBorder(border);
                        choiceButton.setBackground(null);
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e)
                    {
                        choiceButton
                            .setBorder(new EmptyBorder(
                                insets));
                        choiceButton
                            .setBackground(new Color(255,
                                255, 255, 0));
                    }
                    
                    @Override
                    public void mousePressed(MouseEvent e)
                    {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void mouseReleased(MouseEvent e)
                    {
                        // TODO Auto-generated method stub
                        
                    }
                });
            choiceButton
                .addActionListener(new ActionListener()
                {
                    
                    @Override
                    public void actionPerformed(
                        ActionEvent e)
                    {
                        ItemChooser.this.choice = choice;
                        hide();
                    }
                });
        }
        setSize(Math.min(p2.getPreferredSize().width + 50,
            450), Math.min(
            p2.getPreferredSize().height + 70, 550));
        setLocationRelativeTo(parent);
    }
    
    public static <T> T showItemChooser(JFrame parent,
        String message, T[] choices, boolean showCancel)
    {
        JButton button = new JButton("Cancel");
        button.setFocusable(false);
        final ItemChooser<T> chooser = new ItemChooser<T>(
            parent, message, choices,
            showCancel ? new JButton[] { button } : null,
            showCancel);
        button.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                chooser.hide();
            }
        });
        chooser.show();
        return chooser.choice;
    }
    
    /**
     * shows a simple ItemChooser with only 2 items, the value of yes and the
     * value of no. if the value of yes is chosen, true is returned, false
     * otherwise. no cancel button is shown. the yes button will be the first in
     * the list of items to show, and the no button will be the second.
     * 
     * @param parent
     *            the parent frame
     * @param message
     *            the message to show at the top
     * @param yes
     *            the message to show on the yes button
     * @param no
     *            the message to show on the no button
     * @return true if the yes button was clicked, false if the no button was
     *         clicked
     */
    public boolean confirmChoice(JFrame parent,
        String message, String yes, String no)
    {
        return showItemChooser(parent, message,
            new String[] { "yes", "no" },
            new String[] { yes, no }, false).equals("yes");
    }
    
    public static <T> T showItemChooser(JFrame parent,
        String message, T[] choices,
        String[] representations, boolean showCancel)
    {
        if (choices.length != representations.length)
            throw new RuntimeException(
                "Choices and representations are not the same length");
        ListItem<T>[] items = new ListItem[choices.length];
        
        for (int i = 0; i < choices.length; i++)
        {
            items[i] = new ListItem(representations[i],
                choices[i]);
        }
        ListItem<T> item = showItemChooser(parent, message,
            items, showCancel);
        if (item == null)
            return null;
        return item.object;
    }
    
    private static class ListItem<T>
    {
        private T object;
        
        private String name;
        
        public ListItem(String name, T object)
        {
            this.object = object;
            this.name = name;
        }
        
        public String toString()
        {
            return name;
        }
    }
}
