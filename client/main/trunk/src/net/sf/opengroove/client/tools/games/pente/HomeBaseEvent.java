package net.sf.opengroove.client.tools.games.pente;



public class HomeBaseEvent 
{
    public int eventType = -1;
    public int row = -1;
    public int column = -1;
    public int startRow = -1;
    public int startColumn = -1;
    public int endRow = -1;
    public int endColumn = -1;

    // events generated
    public static final int PICKED_BEAD = 1;
    public static final int PLACED_BEAD = 2;
    public static final int GRANTED_TURN = 3;
    public static final int CAPTURED_BEAD = 4;
    public static final int GOT_5_IN_ROW = 5;
    public static final int GOT_5_BEAD_PAIRS = 6;
    public static final int STARTED_OVER = 6;

    private HomeBaseEvent ()
    {
    }
    
    public static HomeBaseEvent makeStartedOverEvent()
    {
        HomeBaseEvent e = new HomeBaseEvent();
        e.eventType = STARTED_OVER;
        return e;
    }
        
    public static HomeBaseEvent make5PairsEvent()
    {
        HomeBaseEvent e = new HomeBaseEvent();
        e.eventType = GOT_5_BEAD_PAIRS;
        return e;
    }
        
    public static HomeBaseEvent make5InRowEvent( int startRow, int startCol,
                                                 int endRow, int endCol )
    {
        HomeBaseEvent e = new HomeBaseEvent();
        e.eventType = GOT_5_IN_ROW;
        e.startRow = startRow;
        e.startColumn = startCol;
        e.endRow = endRow;
        e.endColumn = endCol;
        return e;
    }
        
    public static HomeBaseEvent makeCapturedBeadEvent()
    {
        HomeBaseEvent e = new HomeBaseEvent();
        e.eventType = CAPTURED_BEAD;
        //e.row = row;
        //e.column = col;
        return e;
    }

    public static HomeBaseEvent makeGrantedTurnEvent()
    {
        HomeBaseEvent e = new HomeBaseEvent();
        e.eventType = GRANTED_TURN;
        return e;
    }

    public static HomeBaseEvent makePickedBeadEvent()
    {
        HomeBaseEvent e = new HomeBaseEvent();
        e.eventType = PICKED_BEAD;
        return e;
    }

    public static HomeBaseEvent makePlacedBeadEvent( int row, int col )
    {
        HomeBaseEvent e = new HomeBaseEvent();
        e.eventType = PLACED_BEAD;
        e.row = row;
        e.column = col;
        return e;
    }
    
}
 
