package net.sf.convergia.client.tools.games.othello;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author Mark Boyd
 *
 */
public class GameController
{

	private Grid grid = null;

	private static final int NORTH = 0;

	private static final int NORTH_EAST = 1;

	private static final int EAST = 2;

	private static final int SOUTH_EAST = 3;

	private static final int SOUTH = 4;

	private static final int SOUTH_WEST = 5;

	private static final int WEST = 6;

	private static final int NORTH_WEST = 7;

	private static final int STARTING_DIR = NORTH;

	private static final int ENDING_DIR = NORTH_WEST;

	private static final boolean CAPTURE = true;

	private static final boolean DONT_CAPTURE = false;

	private static final int CELL_TO_JUMP = 0;

	private static final int TERMINATING_CELL = 1;

	private static final ValidMove[] VALID_MOVES_ARRAY = new ValidMove[]
	{};

	private Panel bubblePanel;

	public String getDir(int dir)
	{
		switch (dir)
		{
		case NORTH:
			return "N";
		case NORTH_EAST:
			return "NE";
		case EAST:
			return "E";
		case SOUTH_EAST:
			return "SE";
		case SOUTH:
			return "S";
		case SOUTH_WEST:
			return "SW";
		case WEST:
			return "W";
		case NORTH_WEST:
			return "NW";
		default:
			return "?";
		}
	}

	public GameController(Grid grid)
	{
		this.grid = grid;
	}

	public int getNextRow(int row, int direction)
	{
		if (direction == NORTH || direction == NORTH_WEST
				|| direction == NORTH_EAST)
			return row - 1;
		if (direction == SOUTH || direction == SOUTH_WEST
				|| direction == SOUTH_EAST)
			return row + 1;
		return row;
	}

	public int getNextCol(int col, int direction)
	{
		if (direction == EAST || direction == NORTH_EAST
				|| direction == SOUTH_EAST)
			return col + 1;
		if (direction == WEST || direction == NORTH_WEST
				|| direction == SOUTH_WEST)
			return col - 1;
		return col;
	}

	/**
	 * @param string
	 * @param string2
	 * @return
	 */
	private String chooseName(String prefix, String suffix)
	{
		int i = 2;
		while (new File(prefix + i + suffix).exists())
			i++;
		return prefix + i + suffix;
	}

	/**
	 * @param validMoves
	 * @return
	 */
	private boolean isValidMove(CellLocation move, ValidMove[] validMoves)
	{
		for (int i = 0; i < validMoves.length; i++)
		{
			if (validMoves[i].getRow() == move.row
					&& validMoves[i].getColumn() == move.column)
				return true;
		}
		return false;
	}

	private boolean canMove(String p)
	{
		for (int row = 0; grid.isValidRow(row); row++)
			for (int col = 0; grid.isValidColumn(col); col++)
				if (isValidMoveForPlayer(row, col, p))
					return true;
		return false;
	}

	ValidMove[] getValidMoves(String player)
	{
		List moves = new ArrayList();
		for (int col = 0; col < Grid.NUM_COLS; col++)
		{
			for (int row = 0; row < Grid.NUM_ROWS; row++)
			{
				if (grid.getCellOwner(row, col) == null) // find for empty
															// cells
				{
					int captures = getPotentialCapturesForPlayer(row, col,
							player);
					if (captures > 0)
						moves.add(new ValidMove(row, col, captures));
				}
			}
		}
		return (ValidMove[]) moves.toArray(VALID_MOVES_ARRAY);
	}

	public int getPotentialCapturesForPlayer(int row, int col, String player)
	{
		int total = 0;

		for (int dir = STARTING_DIR; dir <= ENDING_DIR; dir++)
		{
			CellLocation firstCapture = getFirstOpponentCell(row, col, dir,
					player);
			if (firstCapture != null)
			{
				int captures = getDirectionalPotentialCaptures(
						firstCapture.row, firstCapture.column, dir, player);
				if (captures != -1) // -1 = none capturable in that direction
					total += captures + 1; // add 1 for starting opponent cell
			}
		}
		return total;
	}

	private CellLocation getFirstOpponentCell(int row, int col, int direction,
			String player)
	{
		int nextRow = getNextRow(row, direction);
		int nextCol = getNextCol(col, direction);

		if (grid.isValidCell(nextRow, nextCol) == false)
			return null;

		String cellOwner = grid.getCellOwner(nextRow, nextCol);

		if (cellOwner == null || // no bead in that direction
				cellOwner .equals( player) )// current player bead in that dir
			return null;
		else
			return new CellLocation(nextRow, nextCol);
	}

	private int getDirectionalPotentialCaptures(int row, int col,
			int direction, String player)
	{
		int nextRow = getNextRow(row, direction);
		int nextCol = getNextCol(col, direction);

		if (grid.isValidCell(nextRow, nextCol) == false)
			return -1; // didn't find terminating cell, return -1 to abort

		String cellOwner = grid.getCellOwner(nextRow, nextCol);

		if (cellOwner == null)
			return -1; // didn't find terminating cell, return -1 to abort
		else if (cellOwner .equals( player))
			return 0; // we found the terminating cell, return 0 for addition
		else
		{
			int count = getDirectionalPotentialCaptures(nextRow, nextCol,
					direction, player);

			if (count == -1) // no terminating cell found, return -1 to abort
				return count;
			return count + 1; // terminator found so add one for this cell
		}
	}

	public boolean isValidMoveForPlayer(int row, int col, String p)
	{
		if (grid.getCellOwner(row, col) != null) // someone already there
			return false;

		for (int dir = STARTING_DIR; dir <= ENDING_DIR; dir++)
			if (canCaptureCellsForPlayer(row, col, dir, p))
				return true;
		return false;
	}

	void captureCellsForPlayer(int row, int col, String p)
	{
		for (int dir = STARTING_DIR; dir <= ENDING_DIR; dir++)
			scanForFirstOpponentCell(row, col, dir, CAPTURE, p);
	}

	private boolean canCaptureCellsForPlayer(int row, int col, int dir, String p)
	{
		return scanForFirstOpponentCell(row, col, dir, DONT_CAPTURE, p);
	}

	private boolean scanForTerminatingCell(int row, int col, int direction,
			boolean captureCells, String player)
	{
		int nextRow = getNextRow(row, direction);
		int nextCol = getNextCol(col, direction);

		if (grid.isValidCell(nextRow, nextCol) == false)
			return false;

		String cellOwner = grid.getCellOwner(nextRow, nextCol);

		if (cellOwner == null)
			return false;
		else if (cellOwner .equals( player))
		{
			if (captureCells)
				grid.setCell(row, col, player);

			return true;
		} else
		{
			boolean canCapture = scanForTerminatingCell(nextRow, nextCol,
					direction, captureCells, player);
			System.out.println("sftc " + (canCapture ? "can " : " can't ")
					+ " capture ( " + nextRow + ", " + nextCol + ")");

			if (canCapture && captureCells)
				grid.setCell(row, col, player);

			return canCapture;
		}
	}

	private boolean scanForFirstOpponentCell(int row, int col, int direction,
			boolean captureCells, String player)
	{
		int nextRow = getNextRow(row, direction);
		int nextCol = getNextCol(col, direction);

		if (grid.isValidCell(nextRow, nextCol) == false)
			return false;

		String cellOwner = grid.getCellOwner(nextRow, nextCol);

		if (cellOwner == null || // no bead in that direction
				cellOwner .equals( player)) // current player bead in that dir
			return false;
		else
		{
			boolean canCapture = scanForTerminatingCell(nextRow, nextCol,
					direction, captureCells, player);
			System.out.println("sfoc " + (canCapture ? "can " : " can't ")
					+ " capture ( " + nextRow + ", " + nextCol + ")");

			if (canCapture && captureCells)
				grid.setCell(row, col, player);

			return canCapture;
		}
	}

	public static void test(String message)
	{
		Frame f = new Frame();
		final Dialog dialog = new Dialog(f);
		dialog.setSize(100, 100);
		dialog.add(new TextArea(message));
		Button button = new Button("O.K.");
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				dialog.dispose();
			}
		});
		dialog.add(button);
		dialog.show();

	}
}