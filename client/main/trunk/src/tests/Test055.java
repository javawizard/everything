package tests;

import javax.swing.AbstractListModel;
import javax.swing.JFrame;
import javax.swing.JList;

/**
 * A class for testing how list model modification firing impacts the model
 * displayed.
 * 
 * @SuppressWarnings("serial")
 * 
 * @author Alexander Boyd
 * 
 */
public class Test055 extends AbstractListModel
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        JFrame frame = new JFrame();
        frame.setSize(400, 300);
        Test055 listModel = new Test055();
        JList list = new JList(listModel);
        frame.getContentPane().add(list);
        frame.show();
        Thread.sleep(2000);
        listModel.hasAdded = true;
        listModel.fireIntervalAdded(listModel,1,1);
        frame.getContentPane().invalidate();
        frame.getContentPane().validate();
        frame.getContentPane().repaint();
    }
    
    private boolean hasAdded = false;
    
    public Object getElementAt(int index)
    {
        if (index == 0)
            return "Test0";
        else if (index == 1 && hasAdded)
            return "Test1";
        return null;
    }
    
    public int getSize()
    {
        return hasAdded ? 2 : 1;
    }
    
}
