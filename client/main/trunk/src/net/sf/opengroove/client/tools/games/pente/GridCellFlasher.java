package net.sf.opengroove.client.tools.games.pente;


import java.util.*;

public class GridCellFlasher
implements Runnable
{
    Hashtable beads = new Hashtable();
    Vector cells = new Vector();
    boolean on = true;
    boolean flashing = false;
    Thread thread = null;

    public void add( GridCell gc )
    {
        if ( ! cells.contains( gc ) && 
             gc.getBead() != null )
            cells.add( gc );
    }

    public int size()
    {
        return cells.size();
    }
    
    public void run()
    {
        flashing = true;
        
        while( flashing )
        {
            GridCell[] c = (GridCell[]) cells.toArray( 
                new GridCell[cells.size()] );
            for ( int i=0; i<c.length; i++ )
            {
                if( on )
                    beads.put( c[i], c[i].takeBead() );
                else
                    c[i].putBead( (Bead) beads.remove( c[i] ) );
            }
            on = !on;
            wait( 900 );
        }
        thread = null;
    }

    private void wait( int millis )
    {
        try
        {
            Thread.currentThread().sleep( millis );
        }
        catch( Exception e )
        {
        }
    }
    
    public void flash()
    {
        if ( thread == null )
        {
            thread = new Thread( this );
            thread.start();
        }
    }

    public void stopFlashing()
    {
        flashing = false;
    }
    
    public GridCell[] getCells()
    {
    	return (GridCell[]) cells.toArray(new GridCell[0]);
    }
}
