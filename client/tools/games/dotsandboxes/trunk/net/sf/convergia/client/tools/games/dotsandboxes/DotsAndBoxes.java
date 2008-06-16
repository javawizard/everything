package net.sf.convergia.client.tools.games.dotsandboxes;

import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.sf.convergia.client.tools.games.multiplayer.MultiplayerSequenceGame;

public class DotsAndBoxes extends MultiplayerSequenceGame
{
	private Board board;

	@Override
	protected Serializable createPlayerMetadata()
	{
		String letter = JOptionPane.showInputDialog(getParentFrame(),
				"Choose your letter. It is reccomended to type only 1 letter.",
				"" + getUsername().toUpperCase().charAt(0));
		PlayerInfo info = new PlayerInfo();
		info.setUsername(getUsername());
		info.setIdLetter(letter);
		return info;
	}

	@Override
	protected Serializable getDefaultBoardObject()
	{
		String widthString = JOptionPane.showInputDialog(getParentFrame(),
				"Type the width you want for the board, in dots.", "10");
		String heightString = JOptionPane.showInputDialog(getParentFrame(),
				"Type the height you want for the board, in dots.", "10");
		int w;
		int h;
		try
		{
			h = Integer.parseInt(widthString);
			w = Integer.parseInt(heightString);
		} catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(getParentFrame(),
					"One of those values wan't a number.");
			return null;
		}
		if (h < 2 || w < 2)
		{
			JOptionPane.showMessageDialog(getParentFrame(),
					"Width and height must both be more than 2.");
			return null;

		}
		return new NetworkBoard(w - 1, h - 1);
	}

	@Override
	protected JComponent getGameComponent()
	{
		// TODO Auto-generated method stub
		return board;
	}

	@Override
	protected int getMaxPlayers()
	{
		return 200;// unlimited so use a fairly large value
	}

	@Override
	protected int getMinPlayers()
	{
		return 1;
	}

	@Override
	protected void initializeGame()
	{
		board = new Board(1, 1, new PlayerInfo[0]);
		board.addMouseMotionListener(new MouseMotionListener()
		{

			public void mouseDragged(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}

			public void mouseMoved(MouseEvent e)
			{
				if (isMyTurn())
				{
					LineLocation location = board.getPos(e.getX(), e.getY());
					if (location != null)
					{
						if (!board.getPhantomLine(location.getR(), location
								.getC(), location.getHorizontal()))
						{
							board.clearPhantomLines();
							board.setPhantomLine(location.getR(), location
									.getC(), location.getHorizontal(), true);
							board.repaint();
						}
					} else
					{
						board.clearPhantomLines();
						board.repaint();
					}
				}
			}
		});
		board.addMouseListener(new MouseListener()
		{

			public void mouseClicked(MouseEvent e)
			{
				if (isMyTurn())
				{
					LineLocation location = board.getPos(e.getX(), e.getY());
					if (location == null)
					{
						return;
					}
					if (board.getLine(location.getR(), location.getC(),
							location.getHorizontal()))
					{
						JOptionPane
								.showMessageDialog(getParentFrame(),
										"There's already a line there. Pick another spot to go.");
						return;
					}
					if (!(location.isHorizontal() && location.getC() == board
							.getCols()))
						board.setLine(location.getR(), location.getC(),
								location.getHorizontal(), true);
					board.clearPhantomLines();
					NetworkBoard nb = new NetworkBoard(1, 1);
					nb.getFrom(board);
					setBoard(nb);
					board.repaint();
					if (board.captureBoxes(getUsername())
							&& board.countTotalBoxes() < (board.getRows() * board
									.getCols()))
					{
						board.repaint();
						nb = new NetworkBoard(1, 1);
						nb.getFrom(board);
						setBoard(nb);
						JOptionPane.showMessageDialog(getParentFrame(),
								"You captured a box! You get another turn.");
						return;
					} else
					{
						nb = new NetworkBoard(1, 1);
						nb.getFrom(board);
						setBoard(nb);
						new Thread()
						{
							public void run()
							{
								finishedTurn();
							}
						}.start();
					}
				}
			}

			public void mouseEntered(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}

			public void mouseExited(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}

			public void mousePressed(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}

			public void mouseReleased(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	protected void myTurn()
	{
		if (!(board.countTotalBoxes() < (board.getRows() * board.getCols())))// endgame
		{
			// FIXME: this only draws if all the players had the same score, if
			// 2 had the same in 3 player game then one of them would win
			// instead of it being a draw
			int myScore = board.countBoxesForPlayer(getUsername());
			int maxScore = 0;
			boolean isDraw = true;
			String[] players = getGameParticipants();
			for (int i = 0; i < players.length; i++)
			{
				int pnum = board.countBoxesForPlayer(players[i]);
				if (pnum != myScore)
					isDraw = false;
				maxScore = Math.max(maxScore, pnum);
			}
			if (isDraw)
			{
				drawGame();
				return;
			} else if (maxScore == myScore)
			{
				winGame();
			} else
			{
				finishedTurn();
				return;
			}
		}
	}

	@Override
	protected void renderBoard(Serializable networkBoard)
	{
		String[] players = getGameParticipants();
		PlayerInfo[] infos = new PlayerInfo[players.length];
		for (int i = 0; i < infos.length; i++)
		{
			infos[i] = (PlayerInfo) getPlayerMetadata(players[i]);
		}
		board.setPlayers(infos);
		((NetworkBoard) networkBoard).sendTo(board);
		board.repaint();
	}

	@Override
	protected JComponent renderPlayerInfo(String name)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2));
		panel.add(new JLabel("Letter: "));
		PlayerInfo info = (PlayerInfo) getPlayerMetadata(name);
		if (info != null)
			panel.add(new JLabel(info.getIdLetter()));
		else
			panel.add(new JLabel("unknown"));
		return panel;
	}

}
