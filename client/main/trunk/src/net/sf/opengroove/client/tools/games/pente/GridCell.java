package net.sf.opengroove.client.tools.games.pente;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;


public class GridCell
    extends JPanel
{
    private Bead bead = null;
    private boolean drawGridLines = true;
    private boolean hasFocus = false;
    public int row = -1;
    public int column = -1;
    
    public static final int NORTH     = 0;
    public static final int NORTHEAST = 1;
    public static final int EAST      = 2;
    public static final int SOUTHEAST = 3;
    public static final int SOUTH     = 4;
    public static final int SOUTHWEST = 5;
    public static final int WEST      = 6;
    public static final int NORTHWEST = 7;

    // handles to cells surrounding this one are laid out in the 
    // neighborCells array with N being in index 0, NE in index 1, etc. 
    private GridCell[] neighborCells = new GridCell[8];
    
    public GridCell ()
    {
        this( -1, -1 );
    }
    
    public GridCell ( int row, int column )
    {
        this.row = row;
        this.column = column;
        
        setLayout( new BorderLayout() );
        setBorder( new EmptyBorder( 5,5,5,5 ) );
        setBackground( Color.white );
        setOpaque( true );
    }

    public void setFocus( boolean newValue )
    {
        hasFocus = newValue;
        revalidate();
        repaint();
    }
    
    public boolean hasFocus()
    {
        return hasFocus;
    }
    
    public void setDrawGridLines( boolean newValue )
    {
        drawGridLines = newValue;
    }
    
    public void setNeighborCell( int which, GridCell cell )
    {
        neighborCells[which] = cell;
    }
    
    public GridCell getNeighborCell( int which )
    {
        return neighborCells[which];
    }

    public Bead getBead()
    {
        return bead;
    }
    
    public void putBead( Bead b )
    {
        if ( b == null )
            return;
        
        bead = b;
        removeAll();
        add( b, BorderLayout.CENTER );
        //paintImmediately( 0,0,getBounds().width,getBounds().height );
        revalidate();
        repaint();
    }
    
    public Bead takeBead()
    {
        Bead b = bead;
        bead = null;
        removeAll();
        
        //paintImmediately( 0,0,getBounds().width,getBounds().height );
        revalidate();
        repaint();
        return b;
    }
    
    public void getCellsWithSameColorInDirection( int dir, 
                                                  GridCellFlasher f )
    {
        GridCell neighbor = getNeighborCell( dir );
        
        if ( this.getBead() != null &&
             neighbor != null &&
             neighbor.getBead() != null &&
             neighbor.getBead().getColor() == this.getBead().getColor() )
        {
            f.add( neighbor );
            neighbor.getCellsWithSameColorInDirection( dir, f );
        }
    }
    
    public void paintBorder( Graphics grfx )
    {
        if ( hasFocus == false )
            return;

        Rectangle rect = getBounds();

        grfx.setColor( Color.black );
        grfx.drawRect( 2,2, rect.width-5, rect.height-5 );
    }
    
    public void paintComponent( Graphics grfx )
    {
        // super paints background
        super.paintComponent( grfx );

        //System.out.println( "printing gridcell " + ( bead == null ?
        //                                             "" : " with Bead" ) );
        
        if ( drawGridLines == false )
            return;

        Rectangle rect = getBounds();
        int halfWidth = rect.width/2;
        int halfHeight = rect.height/2;

        grfx.setColor( Color.lightGray );
        grfx.drawLine( 0, halfHeight, rect.width, halfHeight );
        grfx.setColor( Color.gray );
        grfx.drawLine( 0, halfHeight+1, rect.width, halfHeight+1 );
        
        grfx.setColor( Color.lightGray );
        grfx.drawLine( halfWidth, 0, halfWidth, rect.height );
        grfx.setColor( Color.gray );
        grfx.drawLine( halfWidth+1, 0, halfWidth+1, rect.height );
    }
    
    public static void main( String[] args )
    {
        JFrame frame = new JFrame( "Pente Grid Test" );
        frame.setSize( 400, 400 );
        
        frame.addWindowListener( new WindowAdapter() 
            {
                public void windowClosing( WindowEvent we )
                {
                    System.exit(0);
                }
            });
        GridCell c = new GridCell()
            {
                public void paintComponent( Graphics grfx )
                {
                    Color origColor = grfx.getColor();
                    Rectangle rect = getBounds();

                    // paint background && border first
                    grfx.setColor( Color.white );
                    grfx.fillRect( 0, 0, rect.width, rect.height );
                    grfx.drawRect( 1, 1, rect.width, rect.height );
                    grfx.setColor( Color.gray );
                    grfx.drawRect( 0, 0, rect.width-2, rect.height-2 );
        
                    int midX = rect.width/2;
                    int midY = rect.height/2;

                    // draw middle row
                    grfx.setColor( Color.lightGray );
                    grfx.drawLine( 0, midY, rect.width-2, midY );
                    grfx.setColor( Color.gray );
                    grfx.drawLine( 0, midY+1, rect.width-2, midY+1 );
        
                    // draw middle column
                    grfx.setColor( Color.lightGray );
                    grfx.drawLine( midX, 0, midX, rect.height-2 );
                    grfx.setColor( Color.gray );
                    grfx.drawLine( midX+1, 0, midX+1, rect.height-2 );
 
                    grfx.setColor( origColor );
                }
            };
        Bead b = new Bead( Color.red );
        b.setOpaque( false );
        
        c.putBead( b );
        frame.getContentPane().add( c );
        frame.setVisible( true );
    }
    
}












