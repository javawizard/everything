package net.sf.convergia.client.tools.games.othello;

/**
 * Represents the location of a cell on an othello board.
 * 
 * @author Mark Boyd
 *
 */
public class CellLocation
{
    public int row = 0;
    public int column = 0;
    
    public CellLocation(int row, int col)
    {
        this.row = row;
        this.column = col;
    }
}
