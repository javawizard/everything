package net.sf.convergia.client.tools.games.pente;


import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;


public class Grid
extends JComponent
{
    private int columns = 0;
    private int rows = 0;
    private GridCell[][] cells = null;
    private boolean drawGrid = true;
    
    private Color color = null;
    private static Dimension preferredSize = new Dimension( 400, 400 );
    
    public Grid ( int rows, int columns )
    {
        this.columns = columns;
        this.rows = rows;
        this.cells = new GridCell[rows][columns];
        this.setLayout( new GridLayout( rows, columns ) );
        this.setBorder( new EmptyBorder( 2,2,2,2 ) );

        // create grid cells and set up neighboring cell handles 
        for( int row=0; row<rows; row++ )
            for( int col=0; col<columns; col++ )
            {
                GridCell c = new GridCell( row, col );
                this.add( c );
                
                cells[row][col] = c;
                
                if ( col > 0 )
                {
                    c.setNeighborCell( c.WEST, cells[row][col-1] );
                    cells[row][col-1].setNeighborCell( c.EAST, c );

                    if ( row > 0 )
                    {
                        c.setNeighborCell( c.NORTHWEST, cells[row-1][col-1] );
                        cells[row-1][col-1].setNeighborCell( c.SOUTHEAST, c );
                    }
                }
                
                if ( row > 0 )
                {
                    c.setNeighborCell( c.NORTH, cells[row-1][col] );
                    cells[row-1][col].setNeighborCell( c.SOUTH, c );

                    if ( col + 1 < columns )
                    {
                        c.setNeighborCell( c.NORTHEAST, cells[row-1][col+1] );
                        cells[row-1][col+1].setNeighborCell( c.SOUTHWEST, c );
                    }
                }
            }
    }

    public void putCellsInFlasher( GridCellFlasher f )
    {
        for( int r=0; r<rows; r++ )
            for( int c=0; c<columns; c++ )
                f.add( cells[r][c] );
    }
    
    public void addMouseListener( MouseListener ml )
    {
        for( int r=0; r<rows; r++ )
            for( int c=0; c<columns; c++ )
                cells[r][c].addMouseListener( ml );
    }
    
    public boolean isFull()
    {
        for( int r=0; r<rows; r++)
            for( int c=0; c<columns; c++ )
                if( cells[r][c].getBead() == null )
                    return false;
        return true;
    }

    public void setDrawGrid( boolean newValue )
    {
        for( int r=0; r<rows; r++ )
            for( int c=0; c<columns; c++ )
                cells[r][c].setDrawGridLines( newValue );
    }
    
    public void putBead( Bead b, int row, int col )
    {
        cells[row][col].putBead( b );
    }

    public void putBead( Bead b )
    {
        for( int row=0; row<rows; row++ )
            for( int col=0; col<columns; col++ )
                if( cells[row][col].getBead() == null )
                {
                    putBead( b, row, col );
                    return;
                }

    }

    public Bead takeBead( int row, int col )
    {
        Bead b = cells[row][col].takeBead();
        return b;
    }

    public boolean hasBead( int row, int col )
    {
        return cells[row][col].getBead() != null;
    }

    public GridCell cellAt( int row, int col )
    {
        return cells[row][col];
    }
    
    
    public Bead beadAt( int row, int col )
    {
        return cells[row][col].getBead();
    }
    
    public Dimension getPreferredSize()
    {
        return preferredSize;
    }
        
    public void paintBorder( Graphics grfx )
    {
        Color origColor = grfx.getColor();
        Rectangle rect = getBounds();

        // paint background && border first
        grfx.setColor( Color.white );
        grfx.drawRect( 1, 1, rect.width-1, rect.height-1 );
        grfx.setColor( Color.gray );
        grfx.drawRect( 0, 0, rect.width-2, rect.height-2 );
        grfx.setColor( origColor );
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
        Grid g = new Grid( 10, 10 );
        g.putBead( new Bead( Color.red ),2 ,3 );
        g.putBead( new Bead( Color.green ), 3,5 );
        g.putBead( new Bead( Color.blue ), 5,8 );
        g.putBead( new Bead( Color.magenta ), 7,3 );
        g.putBead( new Bead( Color.yellow ), 8,6 );

        frame.getContentPane().add( g );
        frame.setVisible( true );
    }
}


