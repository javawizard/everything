package net.sf.convergia.client.tools.games.pente;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Pente
{
    Grid playingGrid = null;
    HomeBase home1 = null;
    HomeBase home2 = null;
    HomeBase currentHome = null;
    HomeBase opposingHome = null;
    Bead loadedBead = null;
    boolean gameOver = false;
    boolean gameStarted = false;
    static Pente instance = null;
    GridCellFlasher flasher = null;
    
    public static void main( String[] args )
    {
        JFrame frame = new JFrame( "Pente" );
        frame.setSize( 500, 500 );
        
        frame.addWindowListener( new WindowAdapter() 
            {
                public void windowClosing( WindowEvent we )
                {
                    System.exit(0);
                }
            });
        Board board = new Board();
        instance = new Pente( board );
        
        frame.getContentPane().add( board );
        frame.setVisible( true );
        instance.play();
        //System.exit(0);
    }

    private  void play()
    {
    }

    private void setCurrentHome( HomeBase home )
    {
        opposingHome = currentHome;
        currentHome = home;
        home.setEnabled( true );
    }
    
    private Pente( Board b )
    {
        playingGrid = b.grid;
        home1 = b.home1;
        home2 = b.home2;
        currentHome = home1;
        opposingHome = home2;
        home1.setEnabled( true );
        
        home1.beads.beadAt(0,0).addMouseListener( new MouseAdapter()
            {
                public void mouseClicked( MouseEvent me )
                {
                    if ( home1.isEnabled() )
                    {
                        gameStarted = true;
                        loadedBead = new Bead( home1.color );
                    }
                    home1.setEnabled( false );
                }
            });
        home2.beads.beadAt(0,0).addMouseListener( new MouseAdapter()
            {
                public void mouseClicked( MouseEvent me )
                {
                    if ( home2.isEnabled() )
                    {
                        gameStarted = true;
                        loadedBead = new Bead( home2.color );
                    }
                    home2.setEnabled( false );
                }
            });
        playingGrid.addMouseListener( new MouseAdapter()
            {
                public void mouseClicked( MouseEvent me )
                {
                    if ( gameOver )
                        return;
                    
                    Component c = me.getComponent();
                    System.out.println( "Component clicked = '" +
                                        c.getClass().getName() + "'" );
                    if( c instanceof GridCell )
                    {
                        GridCell gc = (GridCell) c;
                        if ( gc.getBead() != null || 
                             loadedBead == null )
                            return;
                        
                        playingGrid.putBead( loadedBead,
                                             gc.row, gc.column );
                        checkForCapturedBeads( gc );
                        if ( fiveInARow( gc ) )
                        {
                            System.out.println( "Good Job. You won with five in a row!" );
                            gameOver = true;
                            return;
                        }
                        if ( currentHome.capturedBeads.isFull() )
                        {
                            System.out.println( "Good Job. You won by capturing 10 beads!" );
                            setUpCapturedBeadsFlasher();
                            gameOver = true;
                            return;
                        }
                              
                        loadedBead = null;

                        if( currentHome == home1 )
                            setCurrentHome( home2 );
                        else
                            setCurrentHome( home1 );
                    }
                    
                }
            });
    }

    public void setUpCapturedBeadsFlasher()
    {
        GridCellFlasher f = new GridCellFlasher();
        currentHome.capturedBeads.putCellsInFlasher( f );
        setFlasher( f );
    }
    
    public boolean fiveInARow( GridCell gc )
    {
        for( int dir = gc.NORTH; 
             dir <= gc.SOUTHEAST; 
             dir++ )
        {
            GridCellFlasher f = new GridCellFlasher();
            
            int oppositeDir = dir + 4;
            
            f.add( gc );
            gc.getCellsWithSameColorInDirection( dir, f );
            gc.getCellsWithSameColorInDirection( oppositeDir, f );
            
            if ( f.size() >= 5 )
            {
                setFlasher( f );
                return true;
            }
        }
        return false;
    }

    public synchronized void setFlasher( GridCellFlasher f )
    {
        if ( flasher == null )
        {
            flasher = f;
            flasher.flash();
        }
        else
            f.stopFlashing();
    }
    
    public void checkForCapturedBeads( GridCell gc )
    {
        for( int direction = gc.NORTH; 
             direction <= gc.NORTHWEST; 
             direction++ )
        {
            GridCell oneAway = gc.getNeighborCell( direction );
            GridCell twoAway = 
            ( oneAway != null ? 
              oneAway.getNeighborCell( direction ):
              null );
            GridCell threeAway = 
            ( twoAway != null ? 
              twoAway.getNeighborCell( direction ):
              null );

            if ( oneAway != null &&
                 twoAway != null &&
                 threeAway != null &&
                 oneAway.getBead() != null &&
                 twoAway.getBead() != null &&
                 threeAway.getBead() != null &&
                 !oneAway.getBead().getColor() 
                 .equals( loadedBead.getColor() )&&
                 !twoAway.getBead().getColor() 
                 .equals( loadedBead.getColor() )&&
                 threeAway.getBead().getColor() 
                 .equals( loadedBead.getColor()) )
            {
                                // capture these beads
                currentHome.capturedBeads.putBead(
                    oneAway.takeBead() );
                currentHome.capturedBeads.putBead(
                    twoAway.takeBead() );
            }
        }
    }
    
    
}

