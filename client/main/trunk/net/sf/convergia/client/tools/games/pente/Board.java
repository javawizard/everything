package net.sf.convergia.client.tools.games.pente;



import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class Board
    extends JPanel
{
    HomeBase home1 = null;
    HomeBase home2 = null;
    Grid grid = null;
    
    public Board()
    {
        buildBoard();
    }

    private void buildBoard()
    {
        home1 = new HomeBase( Color.green );
        home2 = new HomeBase( Color.blue );
        grid = new Grid( 10, 10 );
        
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        
        this.setLayout( gridBag );
        
        // add home 1
        cons.gridx = 0;
        cons.gridy = 0;
        cons.gridwidth = 1;
        cons.gridheight = 2;
        cons.fill = cons.BOTH;
        cons.anchor = cons.CENTER;
        cons.weightx = 1.0/5.0;
        cons.weighty = 2.0/4.0;
        gridBag.setConstraints( home1, cons );
        add( home1 );

        // add home 2
        cons.gridx = 0;
        cons.gridy = 2;
        cons.gridwidth = 1;
        cons.gridheight = 2;
        cons.fill = cons.BOTH;
        cons.anchor = cons.CENTER;
        cons.weightx = 1.0/5.0;
        cons.weighty = 2.0/4.0;
        gridBag.setConstraints( home2, cons );
        add( home2 );

        // add grid
        cons.gridx = 1;
        cons.gridy = 0;
        cons.gridwidth = 4;
        cons.gridheight = 4;
        cons.fill = cons.BOTH;
        cons.anchor = cons.CENTER;
        cons.weightx = 4.0/5.0;
        cons.weighty = 1.0;
        gridBag.setConstraints( grid, cons );
        add( grid );
    }

    public static void main( String[] args )
    {
        JFrame frame = new JFrame( "Pente HomeBase Test" );
        frame.setSize( 500, 500 );
        
        frame.addWindowListener( new WindowAdapter() 
            {
                public void windowClosing( WindowEvent we )
                {
                    System.exit(0);
                }
            });
        final Board b = new Board();
        for( int i=0; i<10; i++ )
            b.home1.putCapturedBead( new Bead( Color.blue ) );
        for( int i=0; i<6; i++ )
            b.home2.putCapturedBead( new Bead( Color.green ) );
        
        Thread t = new Thread()
            {
                public void run()
                {
                    while( b.grid.isFull() == false )
                    {
                        try
                        {
                            this.sleep( 800 );
                            b.grid.putBead( new Bead( Color.magenta ) );
                            b.home1.setEnabled( ! b.home1.isEnabled() );

                            boolean enabled = b.home2.capturedBeads
                            .cellAt( 0,0 ).hasFocus();

                            if ( enabled )
                                b.home2.capturedBeads.cellAt( 0,0 )
                                .setFocus( false );
                            else
                                b.home2.capturedBeads.cellAt( 0,0 )
                                .setFocus( false );
                        }
                        catch( Exception e )
                        {
                        }
                    }
                }
            };
        t.start();
        
        frame.getContentPane().add( b );
        frame.setVisible( true );
    }
}


