package net.sf.opengroove.client.ui;

import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;

public interface ProgressItem
{
    public static enum Status
    {
        PENDING(null), ACTIVE(
            new File("icons/thinking.gif")), FAILED(
            new File("icons/x.gif")), SUCCESSFUL(new File(
            "icons/check.gif"));
        private Image image;
        
        private Status(File imageFile)
        {
            if (imageFile != null)
                image = Toolkit.getDefaultToolkit()
                    .createImage(
                        imageFile.getAbsolutePath());
        }
        
        public Image getImage()
        {
            return image;
        }
    };
    
    public Component getNameComponent();
    
    public Component getDetailsComponent();
    
    public Status getStatus();
    
    public void setParent(ProgressPane parent);
}
