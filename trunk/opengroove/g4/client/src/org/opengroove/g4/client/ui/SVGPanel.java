package org.opengroove.g4.client.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.CellRendererPane;
import javax.swing.JList;
import javax.swing.JPanel;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.gvt.GVTTreeRendererListener;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;

public class SVGPanel extends JPanel
{
    private BufferedImage[] images;
    
    private BufferedImage buffer;
    
    private JSVGCanvas[] canvases;
    
    private SVGConstraints[] constraints;
    
    public SVGPanel(String[] sourceUrls,
        SVGConstraints[] constraints)
    {
        this.constraints = constraints;
        init(sourceUrls);
    }
    
    private void init(String[] sourceUrls)
    {
        images = new BufferedImage[sourceUrls.length];
        canvases = new JSVGCanvas[sourceUrls.length];
        for (int i = 0; i < sourceUrls.length; i++)
        {
            canvases[i] = new JSVGCanvas()
            {
                
                @Override
                public boolean isShowing()
                {
                    return true;
                }
            };
            canvases[i]
                .addGVTTreeRendererListener(new GVTTreeRendererAdapter()
                {
                    
                    @Override
                    public void gvtRenderingCompleted(
                        GVTTreeRendererEvent event)
                    {
                        SVGPanel.this.repaint();
                    }
                });
            canvases[i]
                .addGVTTreeBuilderListener(new GVTTreeBuilderAdapter()
                {
                    
                    @Override
                    public void gvtBuildCompleted(
                        GVTTreeBuilderEvent e)
                    {
                        SVGPanel.this.repaint();
                    }
                });
            canvases[i]
                .addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter()
                {
                    
                    @Override
                    public void documentLoadingCompleted(
                        SVGDocumentLoaderEvent e)
                    {
                        SVGPanel.this.repaint();
                    }
                });
            canvases[i].setURI(sourceUrls[i]);
            canvases[i].setOpaque(false);
            canvases[i]
                .setBackground(new Color(0, 0, 0, 0));
        }
    }
    
    public SVGPanel(File[] files,
        SVGConstraints[] constraints)
    {
        try
        {
            this.constraints = constraints;
            String[] urls = new String[files.length];
            for (int i = 0; i < urls.length; i++)
            {
                urls[i] = files[i].toURI().toURL()
                    .toString();
            }
            init(urls);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public void paintComponent(Graphics g)
    {
        if (buffer == null
            || buffer.getWidth() != getWidth()
            || buffer.getHeight() != getHeight())
            buffer = new BufferedImage(getWidth(),
                getHeight(), BufferedImage.TYPE_INT_ARGB);
        int transparent = new Color(0, 0, 0, 0).getRGB();
        for (int x = 0; x < buffer.getWidth(); x++)
            for (int y = 0; y < buffer.getHeight(); y++)
                buffer.setRGB(x, y, transparent);
        Graphics2D bufferGraphics = buffer.createGraphics();
        for (int i = 0; i < canvases.length; i++)
        {
            Dimension2D svgDocumentSize = null;
            try
            {
                svgDocumentSize = canvases[i]
                    .getSVGDocumentSize();
            }
            catch (NullPointerException e)
            {
            }
            if (svgDocumentSize == null)
                return;
            if (images[i] == null)// && svgDocumentSize != null
            {
                images[i] = new BufferedImage(
                    (int) svgDocumentSize.getWidth(),
                    (int) svgDocumentSize.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            }
            if (images[i].getWidth() != ((int) svgDocumentSize
                .getWidth())
                || images[i].getHeight() != ((int) svgDocumentSize
                    .getHeight()))
            {
                images[i] = new BufferedImage(
                    (int) svgDocumentSize.getWidth(),
                    (int) svgDocumentSize.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            }
            for (int x = 0; x < images[i].getWidth(); x++)
                for (int y = 0; y < images[i].getHeight(); y++)
                    images[i].setRGB(x, y, transparent);
            canvases[i].setSize(new Dimension(
                (int) svgDocumentSize.getWidth(),
                (int) svgDocumentSize.getHeight()));
            Graphics2D ig = images[i].createGraphics();
            ig
                .setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            ig.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            ig.setColor(new Color(0, 0, 0, 0));
            ig.fillRect(0, 0, images[i].getWidth(),
                images[i].getHeight());
            canvases[i].paint(ig);
            SVGConstraints c = constraints[i];
            int targetX;
            int targetY;
            int targetWidth;
            int targetHeight;
            if (c.isFull())
            {
                targetX = 0;
                targetY = 0;
                targetWidth = getWidth();
                targetHeight = getHeight();
            }
            else
            {
                targetWidth = (int) svgDocumentSize
                    .getWidth();
                targetHeight = (int) svgDocumentSize
                    .getHeight();
                double extraX = getWidth() - targetWidth;
                double extraY = getHeight() - targetHeight;
                extraX *= c.getHorizontalAlignment();
                extraY *= c.getVerticalAlignment();
                targetX = (int) extraX;
                targetY = (int) extraY;
            }
            bufferGraphics.drawImage(images[i], targetX,
                targetY, targetWidth, targetHeight, null);
        }
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(buffer, 0, 0, buffer.getWidth(), buffer
            .getHeight(), null);
    }
}
