package tests;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import net.sf.opengroove.client.ui.Breadcrumb;
import net.sf.opengroove.client.ui.BreadcrumbListener;

public class Test028
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        frame.setSize(300, 100);
        frame.setLocationRelativeTo(null);
        JPanel top = new JPanel();
        top.setLayout(new BorderLayout());
        frame.getContentPane().add(top, BorderLayout.NORTH);
        final Breadcrumb bc = new Breadcrumb();
        top.add(bc, BorderLayout.NORTH);
        frame.show();
        bc.setItems(new String[] { "Add another" });
        bc.addBreadcrumbListener(new BreadcrumbListener()
        {
            
            private int ii = 1;
            
            @Override
            public void itemClicked(Breadcrumb breadcrumb,
                String[] items, int index)
            {
                String item = items[index];
                if (item.equalsIgnoreCase("Add another"))
                {
                    String[] newItems = new String[items.length + 1];
                    System.arraycopy(items, 0, newItems, 0,
                        items.length - 1);
                    newItems[items.length - 1] = "Item "
                        + ii++;
                    newItems[items.length] = "Add another";
                    bc.setItems(newItems);
                }
                else
                {
                    String[] newItems = new String[items.length - 1];
                    System.arraycopy(items, 0, newItems, 0,
                        index);
                    System.arraycopy(items, index + 1,
                        newItems, index, newItems.length
                            - index);
                    bc.setItems(newItems);
                }
            }
        });
    }
}
