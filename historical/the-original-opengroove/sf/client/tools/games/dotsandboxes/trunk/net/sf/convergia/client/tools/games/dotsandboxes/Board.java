package net.sf.convergia.client.tools.games.dotsandboxes;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class Board extends JComponent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4798783882010049466L;

	private int rows;

	private int cols;

	private String[][] boxes;// who owns a box

	private boolean[][] hLines; // which horizontal lines have been placed

	private boolean[][] vLines; // which vertical lines have been placed

	private boolean[][] hPhantom;

	private boolean[][] vPhantom;

	private PlayerInfo[] players;

	public void clearAndSetSize(int rows, int cols)
	{
		this.rows = rows;
		this.cols = cols;
		this.boxes = new String[rows][cols];
		this.hLines = new boolean[rows + 1][cols + 1];
		this.vLines = new boolean[rows + 1][cols + 1];
		this.hPhantom = new boolean[rows + 1][cols + 1];
		this.vPhantom = new boolean[rows + 1][cols + 1];
	}

	public Board(int rows, int cols, PlayerInfo[] players)
	{
		this.rows = rows;
		this.cols = cols;
		this.players = players;
		this.boxes = new String[rows][cols];
		this.hLines = new boolean[rows + 1][cols + 1];
		this.vLines = new boolean[rows + 1][cols + 1];
		this.hPhantom = new boolean[rows + 1][cols + 1];
		this.vPhantom = new boolean[rows + 1][cols + 1];
		setForeground(Color.BLACK);
		setBackground(Color.WHITE);
	}

	private static final int DOT_WIDTH = 4;

	private static final int DOT_HEIGHT = 4;

	// TODO: make this and the above variable non-final and non-static, and
	// configurable via setters or constructor arguments

	public void paintComponent(Graphics g)
	{
		int width = getWidth();
		int height = getHeight();
		// pad with 1 cell of width and height on either side, hence the + 2 in
		// the expressions below
		int cellWidth = width / (cols + 2);
		int cellHeight = height / (rows + 2);
		int startX = cellWidth;
		int startY = cellHeight;
		g.setFont(getFont()
				.deriveFont(Math.min(cellWidth, cellHeight) * 0.666f));
		g.setFont(g.getFont().deriveFont(Font.BOLD));
		if (isOpaque())
		{
			System.out.println("background=" + getBackground());
			g.setColor(getBackground());
			g.fillRect(0, 0, width, height);
		}
		for (int r = 0; r < (rows + 1); r++)
		{
			for (int c = 0; c < (cols + 1); c++)
			{
				int cx = startX + (cellWidth * c);
				int cy = startY + (cellHeight * r);
				g.setColor(getForeground());
				g.fillOval(cx - (DOT_WIDTH / 2), cy - (DOT_HEIGHT - 2),
						DOT_WIDTH, DOT_HEIGHT);
				if (hLines[r][c])
					g.drawLine(cx, cy, cx + cellWidth, cy);
				if (vLines[r][c])
					g.drawLine(cx, cy, cx, cy + cellHeight);
				g.setColor(new Color(getForeground().getRed(), getForeground()
						.getGreen(), getForeground().getBlue(), 128));
				if (hPhantom[r][c])
					g.drawLine(cx, cy, cx + cellWidth, cy);
				if (vPhantom[r][c])
					g.drawLine(cx, cy, cx, cy + cellHeight);
				if (r != rows && c != cols)
				{
					if (hLines[r][c])
						g.drawLine(cx, cy, cx + cellWidth, cy);
					if (vLines[r][c])
						g.drawLine(cx, cy, cx, cy + cellHeight);
					if (boxes[r][c] != null)
					{
						String id = null;
						for (PlayerInfo info : players)
						{
							if (info.getUsername().equals(boxes[r][c]))
								id = info.getIdLetter();
						}
						if (id == null)
							id = "?";
						FontMetrics mt = g.getFontMetrics();
						Rectangle2D textSize = mt.getStringBounds(id, g);
						int ctw = cx + (cellWidth / 2);
						int cth = cy + (cellHeight / 2);
						// ctw and cth are the center coordinates of the box
						int stw = (int) (ctw - (textSize.getWidth() / 2));
						int sth = (int) (cth + (textSize.getHeight() / 2));
						g.setColor(getForeground());
						g.drawString(id, stw, sth);
					}
				}
			}
		}
	}

	/**
	 * sets the status of a line, in otherwords, if there is a line or not at a
	 * certain position.
	 * 
	 * @param x
	 *            the x of the position
	 * @param y
	 *            the y of the position
	 * @param horizontal
	 *            whether we are setting a horizontal or vertical line
	 * @param set
	 *            true to set a line here, false to make it so that there is no
	 *            line here
	 */
	public void setPhantomLine(int r, int c, boolean horizontal, boolean set)
	{
		if (horizontal)
			hPhantom[r][c] = set;
		else
			vPhantom[r][c] = set;
	}

	/**
	 * returns the line closest to the points specified, or null if those points
	 * aren't near a line. the line doesn't have to exist (IE setLine() could
	 * have been called to hide the line), and this method will still work.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public LineLocation getPos(int x, int y)
	{
		int width = getWidth();
		int height = getHeight();
		// pad with 1 cell of width and height on either side, hence the + 2 in
		// the expressions below
		int cellWidth = width / (cols + 2);
		int cellHeight = height / (rows + 2);
		int startX = cellWidth;
		int startY = cellHeight;
		for (int r = 0; r < rows + 1; r++)
		{
			for (int c = 0; c < cols + 1; c++)
			{
				int cx = startX + (cellWidth * c);
				int cy = startY + (cellHeight * r);
				// the following are meant to be just lone blocks, because they
				// use
				// the same variables but are just different enough to make
				// creating
				// a method for them impractical
				{
					// horizontal line testing
					int nx = cx + cellWidth;
					int ny = cy;
					Point pc = getCenter(r - 1, c);
					Point tc = getCenter(r, c);
					Polygon polygon = new Polygon(new int[]
					{ cx, pc.x, nx, tc.x }, new int[]
					{ cy, pc.y, ny, tc.y }, 4);
					if (polygon.contains(x, y) && c != cols)
						return new LineLocation(r, c, true);
				}
				{
					// vertical line testing
					int nx = cx;
					int ny = cy + cellHeight;
					Point pc = getCenter(r, c - 1);
					Point tc = getCenter(r, c);
					Polygon polygon = new Polygon(new int[]
					{ cx, pc.x, nx, tc.x }, new int[]
					{ cy, pc.y, ny, tc.y }, 4);
					if (polygon.contains(x, y) && r != rows)
						return new LineLocation(r, c, false);
				}
			}
		}
		return null;
	}

	private Point getCenter(int r, int c)
	{
		int width = getWidth();
		int height = getHeight();
		// pad with 1 cell of width and height on either side, hence the + 2 in
		// the expressions below
		int cellWidth = width / (cols + 2);
		int cellHeight = height / (rows + 2);
		int startX = cellWidth;
		int startY = cellHeight;
		int cx = startX + (cellWidth * c);
		int cy = startY + (cellHeight * r);
		int ctw = cx + (cellWidth / 2);
		int cth = cy + (cellHeight / 2);
		return new Point(ctw, cth);
	}

	/**
	 * sets the status of a line, in otherwords, if there is a line or not at a
	 * certain position.
	 * 
	 * @param x
	 *            the x of the position
	 * @param y
	 *            the y of the position
	 * @param horizontal
	 *            whether we are setting a horizontal or vertical line
	 * @param set
	 *            true to set a line here, false to make it so that there is no
	 *            line here
	 */
	public void setLine(int r, int c, boolean horizontal, boolean set)
	{
		if (horizontal)
			hLines[r][c] = set;
		else
			vLines[r][c] = set;
	}

	public void clearPhantomLines()
	{
		hPhantom = new boolean[rows + 1][cols + 1];
		vPhantom = new boolean[rows + 1][cols + 1];
	}

	public void setBox(int r, int c, String username)
	{
		boxes[r][c] = username;
	}

	public void surroundBox(int r, int c)
	{
		setLine(r, c, false, true);
		setLine(r, c, true, true);
		setLine(r, c + 1, false, true);
		setLine(r + 1, c, true, true);
	}

	/**
	 * main method to test a sample board
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setSize(400, 300);
		frame.setLocationRelativeTo(null);
		frame.show();
		PlayerInfo player1 = new PlayerInfo();
		player1.setUsername("test1");
		player1.setIdLetter("A");
		PlayerInfo player2 = new PlayerInfo();
		player2.setUsername("test2");
		player2.setIdLetter("M");
		final Board board = new Board(8, 12, new PlayerInfo[]
		{ player1, player2 });
		board.setOpaque(true);
		board.setForeground(Color.BLACK);
		board.setBackground(Color.WHITE);
		board.setLine(2, 3, true, true);
		board.setBox(3, 3, "test2");
		board.setBox(4, 5, "test1");
		board.surroundBox(3, 3);
		board.surroundBox(4, 5);
		board.addMouseMotionListener(new MouseMotionListener()
		{

			public void mouseDragged(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}

			public void mouseMoved(MouseEvent e)
			{
				LineLocation location = board.getPos(e.getX(), e.getY());
				// System.out.println("location is " + location);
				if (location != null)
				{
					if (!board.getPhantomLine(location.getR(), location.getC(),
							location.getHorizontal()))
					{
						board.clearPhantomLines();
						board.setPhantomLine(location.getR(), location.getC(),
								location.getHorizontal(), true);
						board.repaint();
					}
				}
			}
		});
		frame.getContentPane().add(board);
		frame.show();
		frame.invalidate();
		frame.validate();
		frame.repaint();
	}

	/**
	 * sets any boxes that are surrounded on all 4 sides by lines but have no
	 * username to the specified username. returns true if there were any such
	 * boxes.
	 * 
	 * @param username
	 * @return
	 */
	public boolean captureBoxes(String username)
	{
		boolean hadBoxes = false;
		for (int r = 0; r < rows; r++)
		{
			for (int c = 0; c < cols; c++)
			{
				if (hLines[r][c] && vLines[r][c] && hLines[r + 1][c]
						&& vLines[r][c + 1] && boxes[r][c] == null)
				{
					boxes[r][c] = username;
					hadBoxes = true;
				}
			}
		}
		return hadBoxes;
	}

	protected boolean getPhantomLine(int r, int c, boolean horizontal)
	{
		if (horizontal)
			return hPhantom[r][c];
		else
			return vPhantom[r][c];
	}

	protected boolean getLine(int r, int c, boolean horizontal)
	{
		if (horizontal)
			return hLines[r][c];
		else
			return vLines[r][c];
	}

	public String[][] getBoxes()
	{
		return boxes;
	}

	public void setBoxes(String[][] boxes)
	{
		this.boxes = boxes;
	}

	public boolean[][] getHLines()
	{
		return hLines;
	}

	public void setHLines(boolean[][] lines)
	{
		hLines = lines;
	}

	public boolean[][] getVLines()
	{
		return vLines;
	}

	public void setVLines(boolean[][] lines)
	{
		vLines = lines;
	}

	public int getCols()
	{
		return cols;
	}

	public int getRows()
	{
		return rows;
	}

	public PlayerInfo[] getPlayers()
	{
		return players;
	}

	public void setPlayers(PlayerInfo[] players)
	{
		this.players = players;
	}

	public boolean hasPlacesToGo()
	{
		for (int r = 0; r < rows + 1; r++)
		{
			for (int c = 0; c < cols + 1; c++)
			{
				if (!hLines[r][c])
					return true;
				if (!vLines[r][c])
					return true;
			}
		}
		return false;
	}

	public int countBoxesForPlayer(String player)
	{
		int boxNumbers = 0;
		for (int r = 0; r < rows; r++)
		{
			for (int c = 0; c < cols; c++)
			{
				if (player.equals(boxes[r][c]))
					boxNumbers++;
			}
		}
		return boxNumbers;
	}

	public int countTotalBoxes()
	{
		int boxNumbers = 0;
		for (int r = 0; r < rows; r++)
		{
			for (int c = 0; c < cols; c++)
			{
				if (boxes[r][c] != null)
					boxNumbers++;
			}
		}
		return boxNumbers;
	}
}
