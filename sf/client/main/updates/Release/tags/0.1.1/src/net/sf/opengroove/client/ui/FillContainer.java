package net.sf.opengroove.client.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;

import javax.swing.JPanel;

import net.sf.opengroove.projects.filleditor.FillEditor;
import net.sf.opengroove.projects.filleditor.FillImage;

/**
 * A FillContainer takes a FillImage and draws it as the background of itself.
 * The image will be scaled (discarding aspect ratio) to fit the size of the
 * panel.
 * 
 * @author Alexander Boyd
 * 
 */
public class FillContainer extends JPanel
{
    private static File nameRelativeTo = new File(
        ".");
    private FillImage image;
    
    private String fillName;
    
    public FillContainer()
    {
    }
    
    public void setFillImage(FillImage image)
    {
        this.image = image;
    }
    
    public String getFillImageName()
    {
        return fillName;
    }
    
    public void setFillImageName(String name)
    {
        try
        {
            this.image = (FillImage) FillEditor
                .readObjectFromFile(new File(new File(
                    nameRelativeTo, "backgrounds"), name
                    + ".fdsc"));
        }
        catch (Exception e)
        {
            this.image = null;
        }
    }
    
    public void paintComponent(Graphics g1)
    {
        if (image != null)
        {
            Graphics2D g = (Graphics2D) g1;
            image.draw(g, getWidth(), getHeight());
        }
    }
}
