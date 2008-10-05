package net.sf.opengroove.client.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;

import javax.swing.CellRendererPane;
import javax.swing.JList;
import javax.swing.JPanel;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.gvt.GVTTreeRendererListener;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;

public class SVGPanel extends JPanel
{
    private BufferedImage image;
    
    private JSVGCanvas canvas;
    
    public SVGPanel(String sourceUrl)
    {
        canvas = new JSVGCanvas()
        {
            
            @Override
            public boolean isShowing()
            {
                return true;
            }
        };
        canvas
            .addGVTTreeRendererListener(new GVTTreeRendererAdapter()
            {
                
                @Override
                public void gvtRenderingCompleted(
                    GVTTreeRendererEvent event)
                {
                    SVGPanel.this.repaint();
                }
            });
        canvas
            .addGVTTreeBuilderListener(new GVTTreeBuilderAdapter()
            {
                
                @Override
                public void gvtBuildCompleted(
                    GVTTreeBuilderEvent e)
                {
                    SVGPanel.this.repaint();
                }
            });
        canvas.setURI(sourceUrl);
    }
    
    public void paintComponent(Graphics g)
    {
        Dimension2D svgDocumentSize = null;
        try
        {
            svgDocumentSize = canvas.getSVGDocumentSize();
        }
        catch (NullPointerException e)
        {
        }
        if (svgDocumentSize == null)
            return;
        if (image == null)// && svgDocumentSize != null
        {
            image = new BufferedImage((int) svgDocumentSize
                .getWidth(), (int) svgDocumentSize
                .getHeight(), BufferedImage.TYPE_INT_ARGB);
        }
        if (image.getWidth() != ((int) svgDocumentSize
            .getWidth())
            || image.getHeight() != ((int) svgDocumentSize
                .getHeight()))
        {
            image = new BufferedImage((int) svgDocumentSize
                .getWidth(), (int) svgDocumentSize
                .getHeight(), BufferedImage.TYPE_INT_ARGB);
        }
        canvas
            .setSize(new Dimension((int) svgDocumentSize
                .getWidth(), (int) svgDocumentSize
                .getHeight()));
        Graphics2D ig = image.createGraphics();
        ig.setColor(new Color(0, 0, 0, 0));
        ig.fillRect(0, 0, image.getWidth(), image
            .getHeight());
        canvas.paint(ig);
        g.drawImage(image.getScaledInstance(getWidth(),
            getHeight(), Image.SCALE_AREA_AVERAGING), 0, 0,
            getWidth(), getHeight(), null);
    }
}
