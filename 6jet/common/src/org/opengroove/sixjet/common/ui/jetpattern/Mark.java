package org.opengroove.sixjet.common.ui.jetpattern;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

/**
 * A mark on a jet pattern editor.
 * 
 * @author Alexander Boyd
 * 
 */
public class Mark extends JComponent implements MouseListener, MouseMotionListener
{
    /**
     * True if the mouse is hovering over this mark, false if it is not
     */
    private boolean hovered = false;
    
    private Track track;
    /**
     * True if the mouse is pressed down and it was initially pressed within
     * this mark, false if not
     */
    private boolean down = false;
    
    private boolean wasDragged = false;
    /**
     * The position of this mark, relative to its parent track, that the mark
     * was located at when the mouse was clicked.
     */
    private Point locationAtMouseDown = null;
    
    private Dimension sizeAtMouseDown = null;
    
    private enum DragTarget
    {
        LEFT, RIGHT, MARK, NONE
    }
    
    private DragTarget dragTarget = DragTarget.NONE;
    
    private JetPatternEditor editor;
    
    private static final Border markBorder =
        BorderFactory.createLineBorder(JetPatternEditorColors.markBorder, 1);
    
    Mark(JetPatternEditor editor)
    {
        this.editor = editor;
        setBorder(markBorder);
        addMouseListener(this);
    }
    
    protected void paintComponent(Graphics g)
    {
    }
    
    Paint createNormalPaint()
    {
        return shadedVertical(JetPatternEditorColors.markNormalStart,
            JetPatternEditorColors.markNormalEnd);
    }
    
    private Paint shadedVertical(Color top, Color bottom)
    {
        return new GradientPaint(0, 0, top, 0, getHeight(), bottom);
    }
    
    Paint createHoveredPaint()
    {
        return shadedVertical(JetPatternEditorColors.markNormalStart.brighter(),
            JetPatternEditorColors.markNormalEnd.brighter());
    }
    
    Paint createSelectedPaint()
    {
        return shadedVertical(JetPatternEditorColors.markSelectedStart,
            JetPatternEditorColors.markSelectedEnd);
    }
    
    Paint createSelectedHoveredPaint()
    {
        return shadedVertical(JetPatternEditorColors.markSelectedStart.brighter(),
            JetPatternEditorColors.markSelectedEnd.brighter());
    }
    
    public void mouseClicked(MouseEvent e)
    {
    }
    
    public void mouseEntered(MouseEvent e)
    {
        hovered = true;
    }
    
    public void mouseExited(MouseEvent e)
    {
        hovered = false;
    }
    
    public void mousePressed(MouseEvent e)
    {
        down = true;
        locationAtMouseDown = getLocation();
        sizeAtMouseDown = getSize();
        int width = getWidth();
        int mx = e.getX();
        if (mx < 4)
        {
            dragTarget = DragTarget.LEFT;
        }
        else if (mx > (width - 4))
        {
            dragTarget = DragTarget.RIGHT;
        }
        else
        {
            dragTarget = DragTarget.NONE;
        }
        /*
         * DragTarget.mark will be added soon, which is the middle of the mark
         * somewhere. This shifts the mark left or right.
         */
    }
    
    public void mouseReleased(MouseEvent e)
    {
        down = false;
        if (wasDragged)
        {
            finalizeDrag();
        }
    }
    
    /**
     * Finalizes a drag operation. This sends back to the parent jet pattern
     * editor that a resize or drag has taken place, and tells it to persist the
     * drag or resize to its internal channel/channelevent model.
     */
    private void finalizeDrag()
    {
    }
    
    public void mouseDragged(MouseEvent e)
    {
        if (dragTarget == DragTarget.NONE)
            return;
        Point mouseTrackPoint =
            SwingUtilities.convertPoint(this, locationAtMouseDown, track);
    }
    
    public void mouseMoved(MouseEvent e)
    {
        if (e.getX() < 4)
        {
            setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
        }
        else if (e.getX() > (getWidth() - 4))
        {
            setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
        }
        else
        {
            setCursor(Cursor.getDefaultCursor());
        }
    }
    
}
