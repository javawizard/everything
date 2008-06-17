package net.sf.convergia.client.tools.games.pente;


import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class Bead
extends JComponent
{
    private Color color = null;
    private static Dimension preferredSize = new Dimension( 50, 50 );
    
    public Bead ( Color color )
    {
        this.color = color;
        this.setOpaque( false );
    }

    public Dimension getPreferredSize()
    {
        return preferredSize;
    }

    static int upperColorLimit = 230;
    static int none = 0;
    static int red = 1;
    static int green = 2;
    static int blue = 3;
        
    private int firstTo255 = none;

    public Color getColor()
    {
        return color;
    }
    
    public void paintComponent( Graphics grfx )
    {
        //final Rectangle lastRect = new Rectangle();
        //final Graphics cachedImage = new Graphics();
        
        
        // coord are with respect to this graphic's coords meaning the coords
        // inside of this component independent of where it is with respect to
        // its parent. ie: the coords of this graphics object will be x=0, y=0
        // and width and height equal to the width and height of the comp.
        // the clip bounds will be somewhere within this space to indicate 
        // which rectangle within has invalid pixels that need to be refreshed.

        Rectangle rect = this.getBounds();

        //if ( lastRect.x != rect.x || lastRect.y != rect.y ||
        //     lastRect.width != rect.width || lastRect.height != rect.height )
        //{
            // size or position changed so redraw cached image
        //}
        //else
        //{
        //}
        
 
        Color origColor = grfx.getColor();

        String colorName = "undefined";
        
        if ( color == Color.red )
            colorName = " red ";
        else if ( color == Color.green )
            colorName = " green ";
        else if ( color == Color.blue )
            colorName = " blue ";
        else if ( color == Color.cyan )
            colorName = " cyan ";
        else if ( color == Color.yellow )
            colorName = " yellow ";
        else if ( color == Color.black )
            colorName = " black ";
        
        int steps = rect.width /2;

        if (steps <= 0) 
            steps = 1;
        
        int xRange = rect.width/2;
        int yRange = rect.height/2;
        
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        //if ( r == 0 ) r=1;       
        //if ( g == 0 ) g=1;        
        //if ( b == 0 ) b=1;

        int rRange = (upperColorLimit - r <= 0 ? 0 : upperColorLimit - r);
        int gRange = (upperColorLimit - g <= 0 ? 0 : upperColorLimit - g);
        int bRange = (upperColorLimit - b <= 0 ? 0 : upperColorLimit - b);
        
        int rDelta = 2 * rRange / steps;
        int gDelta = 2 * gRange / steps;
        int bDelta = 2 * bRange / steps;

        int wDelta = rect.width /steps;
        int hDelta = rect.height / steps;
        int xDelta = xRange /steps;
        int yDelta = yRange /steps;

        //int x = rect.x;
        //int y = rect.y;
        int x = 0;
        int y = 0;
        int w = rect.width;
        int h = rect.height;
        int count = 0;
        

        for (int i=rect.width; i>0; i--)
        {
            count++;
            
            r = adjustColor( red, r, rDelta );
            g = adjustColor( green, g, gDelta );
            b = adjustColor( blue, b, bDelta );

            w -= wDelta;
            if ( w < 0 ) w = 0;
            
            h -= hDelta;
            if ( h < 0 ) h = 0;
            
            x += xDelta;
            //y = (rect.height - h)/2 + rect.y;
            y += yDelta; //(rect.height - h)/2;
            
            System.out.println( "r=" + r + ", g=" + g + ", b=" + b );
            
            grfx.setColor( new Color( r, g, b, (i>rect.width
                                                - 5 
                                                ? 100 : 20 ) ) );
            grfx.fillOval( x, y, w, h );
            if ( r >= upperColorLimit &&
                 g >= upperColorLimit &&
                 b >= upperColorLimit )
            {
                break;
            }
            
        }
        
        grfx.setColor( origColor );
    }
    
    private int adjustColor( int color,
                             int value, 
                             int delta )
    {
        int newVal = value + delta;
        
        if ( newVal >= 255 )
        {
            if ( firstTo255 == none )
                firstTo255 = color;
        }
        else
            value = newVal;
            
        if ( firstTo255 != none &&
             firstTo255 != color &&
             value > upperColorLimit ) 
            value = upperColorLimit;
        return value;
            
    }

    public void setColor( Color c )
    {
        this.color = c;
    }

    private Color secondary = null;
    
    public void setSecondaryColor( Color c )
    {
        this.secondary = c;
    }

    public Color getSecondaryColor()
    {
        return secondary;
    }
    
    
    
    public static void main( String[] args )
    {
        JFrame frame = new JFrame( "Pente Bead Test" );
        frame.setSize( 100, 100 );
        
        frame.addWindowListener( new WindowAdapter() 
            {
                public void windowClosing( WindowEvent we )
                {
                    System.exit(0);
                }
            });
        Bead b = new Bead( Color.cyan );
        b.setSize( 50, 50 );
        
        frame.getContentPane().add( b );
        frame.setVisible( true );
    }
}
