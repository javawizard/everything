package net.sf.convergia.client.tools.games.othello;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import net.sf.convergia.client.IMenu;
import net.sf.convergia.client.IMenuItem;
import net.sf.convergia.client.tools.games.multiplayer.MultiplayerSequenceGame;


/**
 * @author Mark Boyd
 * @author Alexander Boyd
 */
public class Othello extends MultiplayerSequenceGame
{
	@Override
	protected Serializable createPlayerMetadata()
	{
		JColorChooser cc = new JColorChooser();
		Color color = null;
		while (color == null)
		{
			color = cc.showDialog(getWrapper().getWorkspace().getFrame(),
					"Choose your bead color",
					getGameParticipants().length == 0 ? Color.BLACK
							: Color.WHITE);
		}
		OthelloPlayerInfo info = new OthelloPlayerInfo();
		System.out.println("setting color " + color);
		info.setColor(color);
		return info;
	}

	@Override
	protected Serializable getDefaultBoardObject()
	{
		NetworkGrid networkGrid = new NetworkGrid();
		String[] players = getGameParticipants();
		if (players.length != 2)
			throw new RuntimeException("incorrect number of players present");
		String player1 = players[0];
		String player2 = players[1];
		networkGrid.set(3, 3, player1);
		networkGrid.set(4, 4, player1);
		networkGrid.set(3, 4, player2);
		networkGrid.set(4, 3, player2);
		networkGrid.copyTo(grid);
		return networkGrid;
	}

	private Grid grid;

	@Override
	protected JComponent getGameComponent()
	{
		return grid;
	}

	@Override
	protected int getMaxPlayers()
	{
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	protected int getMinPlayers()
	{
		// TODO Auto-generated method stub
		return 2;
	}

	private GameController controller;

	private JCheckBoxMenuItem showValidMovesCheckbox;

	@Override
	protected void initializeGame()
	{
		showValidMovesCheckbox = new JCheckBoxMenuItem("Show valid moves");
		showValidMovesCheckbox.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				if (isMyTurn())
				{
					if (showValidMovesCheckbox.isSelected())
						addValidMovesToGrid();
					else
						removeValidMovesFromGrid();
				}
			}
		});
		getMenuBar().add(new IMenu("Othello", new JMenuItem[]
		{ showValidMovesCheckbox }));
		grid = new Grid(this);
		controller = new GameController(grid);
		final String myUsername = getUsername();
		grid.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent me)
			{
				int row = grid.getCellRow(me.getY());
				int col = grid.getCellColumn(me.getX());
				System.out.println("clicked " + row + "," + col);
				if (isMyTurn()
						&& controller.isValidMoveForPlayer(row, col,
								getUsername()))
				{
					grid.setCursor(Cursor
							.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					controller.captureCellsForPlayer(row, col, getUsername());
					grid.repaint();
					new Thread()
					{
						public void run()
						{
							NetworkGrid ng = new NetworkGrid();
							ng.copyFrom(grid);
							setBoard(ng);
							opponentCache = null;
							finishedTurn();
							grid.clearPhantomCells();
							grid.clearCellColors();
							grid.clearCellNumbers();
							removeValidMovesFromGrid();
							grid.repaint();
						}
					}.start();
				}
			}
		});
		grid.addMouseMotionListener(new MouseMotionListener()
		{

			public void mouseDragged(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}

			public void mouseMoved(MouseEvent me)
			{
				if (!isMyTurn())
					return;
				int row = grid.getCellRow(me.getY());
				int col = grid.getCellColumn(me.getX());
				if (row > 7 || col > 7 || row < 0 || col < 0)// not in a cell
				{
					grid.clearPhantomCells();
					if (showValidMovesCheckbox.isSelected())
					{
						removeValidMovesFromGrid();
						addValidMovesToGrid();
					}
					grid.repaint();
				} else if (!myUsername.equals(grid.getPhantomCell(row, col)))
				{
					grid.clearPhantomCells();
					grid.setPhantomCell(row, col, myUsername);
					if (showValidMovesCheckbox.isSelected())
					{
						removeValidMovesFromGrid();
						addValidMovesToGrid();
						if (controller.isValidMoveForPlayer(row, col,
								getUsername()))
							addOpponentValidMovesToGrid(row, col);
					}
					grid.repaint();
				}
			}
		});
	}

	private HashMap<String, JLabel> userScoreLabels = new HashMap<String, JLabel>();

	@Override
	protected void myTurn()
	{
		opponentCache = null;
		grid.clearPhantomCells();
		System.out.println("MY TURN");
		System.out.println("VALID MOVES LENGTH:"
				+ controller.getValidMoves(getUsername()).length);
		if (controller.getValidMoves(getUsername()).length == 0
				&& controller.getValidMoves(getOpponent()).length == 0)
		{
			// end of game (neither person can go) so see if we won. if we did,
			// call winGame(). if not, play will proceed to the other player who
			// will detect the same situation and call winGame().
			//
			// UPDATE: if both us and the other player got the same number of
			// points, then drawGame() will be called.
			int myPoints = grid.getCellsFor(getUsername());
			int theirPoints = grid.getCellsFor(getOpponent());
			if (myPoints == theirPoints)
			{
				System.out.println("ABOUT TO DRAW GAME");
				new Thread()
				{
					public void run()
					{
						try
						{
							Thread.sleep(3000);
						} catch (Exception ex1)
						{
							ex1.printStackTrace();
						}
						drawGame();
					}
				}.start();
				return;
			} else if (myPoints > theirPoints)
			{
				System.out.println("ABOUT TO WIN GAME");
				new Thread()
				{
					public void run()
					{
						try
						{
							Thread.sleep(3000);
						} catch (Exception ex1)
						{
							ex1.printStackTrace();
						}

						winGame();
					}
				}.start();
				return;
			}
		}
		if (controller.getValidMoves(getUsername()).length == 0)
		{
			System.out.println("NO VALID MOVES");
			new Thread()
			{
				public void run()
				{
					try
					{
						Thread.sleep(3000);
					} catch (Exception ex1)
					{
						ex1.printStackTrace();
					}
					opponentCache = null;
					finishedTurn();
				}
			}.start();
			return;
		}
		grid.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		if (showValidMovesCheckbox.isSelected())
		{
			addValidMovesToGrid();
		}
	}

	public static final Color MY_MOVE_COLOR = new Color(255, 70, 70, 128);

	public static final Color THEIR_MOVE_COLOR = new Color(255, 255, 0, 128);

	private void addValidMovesToGrid()
	{
		for (ValidMove move : controller.getValidMoves(getUsername()))
		{
			grid.setCellNumber(move.getRow(), move.getColumn(), move
					.getCaptures());
			if (grid.getCellColor(move.getRow(), move.getColumn()) != THEIR_MOVE_COLOR)
				grid.setCellColor(move.getRow(), move.getColumn(),
						MY_MOVE_COLOR);
		}
	}

	private int cohRow = -1;

	private int cohCol = -1;

	/**
	 * adds coor squares for all the moves the opponent could make if we went at
	 * the position specified by row,col
	 * 
	 * @param row
	 * @param col
	 */
	private void addOpponentValidMovesToGrid(int row, int col)
	{
		if (cohRow == row && cohCol == col)
			return;
		Grid newGrid = grid.copyOf();
		GameController newController = new GameController(newGrid);
		newController.captureCellsForPlayer(row, col, getUsername());
		ValidMove[] moves = newController.getValidMoves(getOpponent());
		for (ValidMove move : moves)
		{
			grid.setCellNumber(move.getRow(), move.getColumn(), move
					.getCaptures());
			grid
					.setCellColor(move.getRow(), move.getColumn(),
							THEIR_MOVE_COLOR);
		}
		cohRow = row;
		cohCol = col;
	}

	private void removeValidMovesFromGrid()
	{
		grid.clearCellNumbers();
		grid.clearCellColors();
		cohRow = -1;
		cohCol = -1;
	}

	private HashMap<String, OthelloPlayerInfo> playerInfoCache = new HashMap<String, OthelloPlayerInfo>();

	Color getBeadColor(String name)
	{
		if (playerInfoCache.get(name) == null)
			playerInfoCache.put(name,
					(OthelloPlayerInfo) getPlayerMetadata(name));
		return playerInfoCache.get(name).getColor();
	}

	@Override
	protected JComponent renderPlayerInfo(String name)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2));
		JLabel colorLabel = new JLabel("Color: ");
		panel.add(colorLabel);
		JLabel colorValue = new JLabel("      ");
		OthelloPlayerInfo info = (OthelloPlayerInfo) getPlayerMetadata(name);
		System.out.println("info is " + info);
		System.out.println("setting color " + info.getColor());
		if (info != null)
		{
			colorValue.setBackground(info.getColor());
			colorValue.setOpaque(true);
			playerInfoCache.put(name, info);
		} else
		{
			colorValue.setText("uknown");
		}
		panel.add(colorValue);
		JLabel scoreLabel = new JLabel("Score: ");
		panel.add(scoreLabel);
		JLabel scoreValue = new JLabel("?");
		panel.add(scoreValue);
		userScoreLabels.put(name, scoreValue);
		return panel;
	}

	@Override
	protected void renderBoard(Serializable board)
	{
		NetworkGrid networkGrid = (NetworkGrid) board;
		networkGrid.copyTo(grid);
		for (String u : getGameParticipants())
		{
			if (userScoreLabels.get(u) != null)
				userScoreLabels.get(u).setText("" + grid.getCellsFor(u));
		}
	}
	
	private String opponentCache = null;

	private String getOpponent()
	{
		if(opponentCache != null)
			return opponentCache;
		String[] players = getGameParticipants();
		for (String p : players)
		{
			if (!p.equals(getUsername()))
			{
				opponentCache = p;
				return p;
			}
		}
		throw new RuntimeException("");
	}

}
