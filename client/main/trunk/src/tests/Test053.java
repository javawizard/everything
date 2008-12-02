package tests;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;

import net.sf.opengroove.client.ui.TestFrame;
import net.sf.opengroove.common.utils.StringUtils;
import net.sf.opengroove.common.utils.StringUtils.ToString;

/**
 * A class for experimenting with Drag and Drop from/to external applications.
 * In particular, I want to get it so that you can drag files into a component
 * on a frame and it will correctly read and handle them.
 * 
 * @author Alexander Boyd
 * 
 */
public class Test053
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        final TestFrame frame = new TestFrame();
        JLabel to = new JLabel("Drag files here.");
        JLabel from = new JLabel("Drag this as a file.");
        frame.add(to);
        frame.add(from);
        frame.show();
        TransferHandler toHandler = new TransferHandler()
        {
            
            public boolean canImport(TransferSupport support)
            {
                if (Arrays
                    .asList(support.getDataFlavors())
                    .contains(DataFlavor.javaFileListFlavor))
                    return true;
                return false;
            }
            
            public boolean importData(
                TransferSupport support)
            {
                Transferable transfer = support
                    .getTransferable();
                try
                {
                    List<File> fileList = (List<File>) transfer
                        .getTransferData(DataFlavor.javaFileListFlavor);
                    JOptionPane.showMessageDialog(frame,
                        "<html>You dragged these files to here: <br/><br/>"
                            + StringUtils.delimited(
                                fileList
                                    .toArray(new File[0]),
                                new ToString<File>()
                                {
                                    
                                    public String toString(
                                        File object)
                                    {
                                        return object
                                            .getAbsolutePath();
                                    }
                                }, "<br/>"));
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
                return true;
            }
        };
        to.setTransferHandler(toHandler);
        final File testfile = File.createTempFile("prefix",
            "suffix.txt");
        FileOutputStream fos = new FileOutputStream(
            testfile);
        fos.write("This is some test text".getBytes());
        fos.flush();
        fos.close();
        TransferHandler fromHandler = new TransferHandler()
        {
            
            protected Transferable createTransferable(
                JComponent c)
            {
                return new Transferable()
                {
                    
                    public Object getTransferData(
                        DataFlavor flavor)
                        throws UnsupportedFlavorException,
                        IOException
                    {
                        return Arrays
                            .asList(new File[] { testfile });
                    }
                    
                    public DataFlavor[] getTransferDataFlavors()
                    {
                        return new DataFlavor[] { DataFlavor.javaFileListFlavor };
                    }
                    
                    public boolean isDataFlavorSupported(
                        DataFlavor flavor)
                    {
                        return flavor
                            .equals(DataFlavor.javaFileListFlavor);
                    }
                };
            }
            
            protected void exportDone(JComponent source,
                Transferable data, int action)
            {
                System.out.println("export done");
            }
            
            public int getSourceActions(JComponent c)
            {
                return TransferHandler.COPY;
            }
        };
        from.setTransferHandler(fromHandler);
    }
}
