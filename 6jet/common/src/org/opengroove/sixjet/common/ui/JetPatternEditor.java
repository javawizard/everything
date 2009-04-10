package org.opengroove.sixjet.common.ui;

import javax.swing.JComponent;

/**
 * A component that shows a list of tracks, with marks in each track. The
 * component is created with a specified length, in milliseconds, and it sets
 * its initial zoom to be 10 milliseconds per pixel.
 * 
 * The editor itself shouldn't be added to a component. Instead, the scroll pane
 * returned from getScrollPane should be used instead. This scroll pane contains
 * headers for the jet names, and a time ruler. It will also draw a
 * "current position" on the ruler, if asked to.
 * 
 * The editor draws each channel as a row. Within a channel, marks are present.
 * Marks are added by clicking and dragging in an area where there is no mark. 
 * 
 * 
 * @author Alexander Boyd
 * 
 */
public class JetPatternEditor extends JComponent
{
    
}
