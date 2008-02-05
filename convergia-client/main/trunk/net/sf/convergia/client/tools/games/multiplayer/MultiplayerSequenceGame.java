package net.sf.convergia.client.tools.games.multiplayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import net.sf.convergia.client.IMenu;
import net.sf.convergia.client.IMenuItem;
import net.sf.convergia.client.Convergia;
import net.sf.convergia.client.notification.NotificationAdapter;
import net.sf.convergia.client.notification.TaskbarNotification;
import net.sf.convergia.client.toolworkspace.Tool;

import base64.Base64Coder;

/**
 * A class providing additional functionality for multiplayer sequence games,
 * such as Tic-Tac-Toe, Pente, Othello, Blokus, Monopoly, Pentago, Billiards,
 * Chess, Checkers, Arimaa, etc. <br/><br/> A multiplayer sequence game is a
 * game where multiple players (optionally with a minimum and maximum number of
 * participants) participate in the game, and each one takes a "turn", usually
 * in sequence, although turns can be skipped. players are allowed to join the
 * game, when the game is reset, and when there is enough players, the game will
 * start. any players that are not participating can (usually) see the game in
 * progress. <br/><br/> this class allows it's subclasses to set a serializable
 * Object that represents the board. for example, in Chess, this could be an
 * int[][] representing the board, where each element in the array is a number
 * corresponding to a specific type of chess piece. anyway, the player who's
 * current turn it is can set this object, but players who are just observing or
 * who are participating in the game but do not have the current turn cannot
 * modify this object. whenever new updates to the object are received or set,
 * render(Object) is called, which should take the given object and update the
 * component returned from getGameComponent(). the object returned from
 * getGameComponent() should be complete with any mouse listeners or the like
 * needed to play the game, but these should only have effect if isMyTurn()
 * returns true. the component should not change over invocations, similar to
 * getComponent() in regular tools. see the javadoc on getComponent() in class
 * Tool for more info on what I mean. anyway, myTurn() will be called when play
 * passes to this player. you can also call isMyTurn() to see if it is your
 * turn. myTurn() should not wait very long to return, IE if it won't return
 * until the player is done moving then it should be wrapped in a Thread.
 * subclasses can call getGameParticipants() which returns all participants in
 * the current game, or null if the game is not in progress. sublasses can call
 * isGameRunning() to see if ithe game is in progress. when it is a player's
 * turn, the player can call winGame() to alert the rest of the players that
 * this player has won the game. the game is over at that point. when it is a
 * player's turn, the player can call forfeit() to forfeit the game. <br/><br/>
 * the player who starts the game is the game manager, and they are made aware
 * of that through a popup message. it will say something like "If you choose to
 * start the game, you will be the game manager. Are you sure you want to start
 * the game?", and if they choose yes, then they are the game manager. Game play
 * will not proceed when the game manager is offline. The game manager is in
 * charge of keeping track of who's a member of the game, who's turn it is,
 * relaying the board state, sending out game notifications, etc. The game
 * manager is not allowed to forfeit the game. calling forfeit() when
 * isGameManager() returns true will throw an IllegalStateException. <br/><br/>
 * when the game has not yet started, anyone can choose to join. this is taken
 * care of by this class, so implementations need not worry about it. if you
 * want to join the game, you can click "join". this class sends a message to
 * all workspace members telling them that you have joined the game. they then
 * update their UIs to show that you have joined. If this makes the amount of
 * players not within the minimum or maximum specified, the start button on all
 * member's computers is disabled. when the number of users comes back within
 * the minimum and maximum range specified, the start buttons are again enabled.
 * when a user comes online, they will send a message to all of the players,
 * asking if any of them is game manager for a current game. if none of them is,
 * it assumes that there is not a game in progress, and it then sends a message
 * to each member asking them if they have, so far, chosen to be in the current
 * game. it then adds all of those users into the display of who's in the
 * current game. then, every 45 seconds, it refreshes that list by asking all of
 * the users who's in the game, just in case it missed one of the broadcasts
 * telling it that a user had joined. when a user clicks on the start button,
 * before informing them that they will be the game manager, it checks to see if
 * they are online. then, it sends a message to all other users that are online,
 * asking them which ones of them are going to be members. if the number is not
 * inside the min and max of this game, it disables the start button and does
 * nothing. if it is, then this user becomes the game manager, and broadcasts a
 * message to all other users telling them that the game has been started, and
 * that we are the game manager. then, it assumes that we go first, and that
 * play proceeds in the order that users joined the game. it sends a message to
 * the players telling them that it is our turn. this message will be broadcast
 * once every 25 seconds as long as it is our turn. after our turn is over <br/>
 * 
 * @author Alexander Boyd
 * 
 */

/*
 * OBSOLETE DOCUMENTATION (RETAINED FOR HISTORICAL PURPOSES)
 * 
 * if a player goes offline while it is their turn and stays offline for more
 * than 30 seconds, the situation is treated as if they had forfeited the game.
 * when they come back online, they are just an observer. if enough people
 * forfeit the game so that the number of players drops below the minimum
 * required to play, the game is declared a draw, unless the minimum is 2
 * (meaning that there is 1 player left at that point), in which case the
 * remaining player wins. This should change sometime in the future so that
 * implementations are asked who won (for instance, in a game with a minimum of
 * 3 people that uses game pieces, the player with the most pieces of the 2
 * remaining should win).
 * 
 * the way offline checking works is this: when the current player chooses to
 * pass the play on to the next player, then this class sends a message to the
 * next player. If they respond within 30 seconds indicating that they know it's
 * their turn, then play is passed on to that player, and this player sends a
 * message to all players indicating that it is now the next player's turn. this
 * is redundant, as the next player will have sent the same message, but it
 * makes sure that the message is received. if a response is not received within
 * 30 seconds, the current player sends a message to all players indicating that
 * the next player has forfeited. the player after the next alerting them that
 * they are the next player, and so on. anyway, while it is the current player's
 * turn, the next player frequently checks their list of online users (once
 * every 15 seconds), and if the current player is offline for more than 30
 * seconds, then the next player sends messages to all players indicating that
 * they are now the current player, and that the now-pervious player has
 * forfeited.
 * 
 * when a player is online but calls forfeit(), the class sends a message to all
 * of the other players indicating that this player has forfeited.
 * 
 * a player should periodically check to see who's a member of the game. it does
 * this by sending a message to each of the other players, asking them if they
 * are a member of the current game.
 */
public abstract class MultiplayerSequenceGame extends Tool
{
	private int lastTurnIndex;

	private boolean hasPlayedGame = false;

	private boolean alertedWon = false;

	private boolean isMyTurn;

	@Override
	public JComponent getComponent()
	{
		return outerPanel;
	}

	private TaskbarNotification myTurnNotification;

	private JLabel myTurnNotificationLabel;

	@Override
	public final void initialize()
	{
		myTurnNotificationLabel = new JLabel();
		myTurnNotification = new NotificationAdapter(myTurnNotificationLabel,
				true, false)
		{
			public void clicked()
			{
				switchTo();
				removeNotification(this);
			}
		};
		new Thread()
		{
			public void run()
			{
				try
				{
					Thread.sleep(3000);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				initMenu();
				try
				{
					initializeGame();
				} catch (Exception ex1)
				{
					ex1.printStackTrace();
				}
				initComponents();
				reloadGameState();
			}
		}.start();
	}

	/**
	 * returns the game's main menu bar. when initializeGame is called,
	 * MultiplayerSequenceGame will have already added to this menubar all of
	 * the menus it needs to add, so the implementation of this class can add
	 * any menus they want to this menu bar when initializeGame() is called.<br/><br/>
	 * 
	 * The general convention is to add all menu items for the game under a menu
	 * called the name of the game, for example for menu items in the game
	 * Othello, they would all be under the Othello menu.
	 * 
	 * @return
	 */
	protected JMenuBar getMenuBar()
	{
		return menubar;
	}

	protected abstract void initializeGame();

	/**
	 * returns the minimum number of players required to play a game. this
	 * should be the same accross multiple invocations and should be the same
	 * between computers.
	 * 
	 * @return
	 */
	protected abstract int getMinPlayers();

	/**
	 * returns the maximum number of players allowed to play at a time.
	 * 
	 * @return
	 */
	protected abstract int getMaxPlayers();

	private JoinGamePanel joinGamePanel = new JoinGamePanel();

	private JPanel joinGameParticipantsPanel = joinGamePanel
			.getParticipantPanel();

	private JButton startGameButton = joinGamePanel.getStartButton();

	private JButton joinGameButton = joinGamePanel.getJoinButton();

	private JButton leaveGameButton = joinGamePanel.getLeaveButton();

	private JPanel playGamePanel = new JPanel(new BorderLayout());

	private Serializable boardObject;

	private JPanel playGameParticipantsPanel = new JPanel();

	private JPanel outerPanel = new JPanel(new BorderLayout());

	private JPanel mainPanel = new JPanel(new BorderLayout());

	private JMenuBar menubar = new JMenuBar();

	private final void initMenu()
	{
		menubar.add(new IMenu("Game", new IMenuItem[]
		{}));
		menubar.add(new IMenu("Advanced", new IMenuItem[]
		{ new IMenuItem("Reload State")
		{

			public void actionPerformed(ActionEvent e)
			{
				reloadGameState();
			}
		}, new IMenuItem("Re-Render Board")
		{

			public void actionPerformed(ActionEvent e)
			{
				reRenderBoard();
			}
		}, new IMenuItem("Broadcast Reload State")
		{

			public void actionPerformed(ActionEvent e)
			{
				broadcastReloadState();
			}
		}, new IMenuItem("Broadcast Re-Render Board")
		{

			public void actionPerformed(ActionEvent e)
			{
				broadcastRenderBoard();
			}
		}, new IMenuItem("Upload Board Object")
		{

			public void actionPerformed(ActionEvent e)
			{
				if (!getUsername().equals(getProperty("currentplayer")))
				{
					JOptionPane
							.showMessageDialog(getParentFrame(),
									"This action is only available when it is your turn.");
				}
			}
		} }));

	}

	private final void initComponents()
	{
		playGameParticipantsPanel.setLayout(new BoxLayout(
				playGameParticipantsPanel, BoxLayout.X_AXIS));
		JPanel playGameParticipantsWrapper = new JPanel();
		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.LEFT);
		playGameParticipantsWrapper.setLayout(fl);
		playGameParticipantsWrapper.add(playGameParticipantsPanel);
		playGamePanel.add(new JScrollPane(playGameParticipantsWrapper),
				BorderLayout.SOUTH);
		playGamePanel.add(getGameComponent(), BorderLayout.CENTER);
		outerPanel.add(menubar, BorderLayout.NORTH);
		outerPanel.add(mainPanel, BorderLayout.CENTER);
		joinGameButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				reloadGameState();
				if (!joinGameButton.isEnabled())
					return;
				Serializable playerMd = createPlayerMetadata();
				reloadGameState();
				if (!joinGameButton.isEnabled())
					return;
				setProperty("participant_" + getUsername(),
						serializeToString(playerMd));
				reloadGameState();
				broadcastReloadState();
			}
		});
		leaveGameButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				reloadGameState();
				if (!leaveGameButton.isEnabled())
					return;
				setProperty("participant_" + getUsername(), null);
				reloadGameState();
				broadcastReloadState();
			}
		});
		startGameButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				reloadGameState();
				if (!startGameButton.isEnabled())
					return;
				if (JOptionPane.showConfirmDialog(getParentFrame(),
						"Are you sure you want to start the game?", null,
						JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
				{
					return;
				}
				reloadGameState();
				if (!startGameButton.isEnabled())
					return;
				if (getProperty("currentplayer") != null)// somebody beat us
					// to starting the
					// game
					return;
				Serializable boardObject = getDefaultBoardObject();
				if (boardObject == null)
					return;
				lastTurnIndex = 0;
				setProperty("winner", null);
				setProperty("turnindex", "1");
				setProperty("board", serializeToString(boardObject));
				setProperty("currentplayer", getUsername());
				reRenderBoard();
				broadcastReloadState();
				reloadGameState();
			}
		});
		joinGamePanel.getHelpButton().addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				Convergia
						.showHelpTopic("/help/workspaces/toolworkspaces/builtInTools/games/msgHowto");
			}
		});
		menubar.add(Box.createHorizontalGlue());
		menubar.add(joinGamePanel.getHelpButton());
	}

	protected void broadcastReloadState()
	{
		try
		{
			for (String u : listOnlineUsers())
			{
				if (!u.equals(getUsername()))
				{
					try
					{
						sendMessage(u, "reloadstate");
					} catch (Exception ex1)
					{
						ex1.printStackTrace();
					}
				}
			}
		} catch (Exception ex1)
		{
			ex1.printStackTrace();
		}
	}

	protected void broadcastRenderBoard()
	{
		try
		{
			for (String u : listOnlineUsers())
			{
				if (!u.equals(getUsername()))
				{
					try
					{
						sendMessage(u, "renderboard");
					} catch (Exception ex1)
					{
						ex1.printStackTrace();
					}
				}
			}
		} catch (Exception ex1)
		{
			ex1.printStackTrace();
		}
	}

	/**
	 * sets the current board object. this must only be called when it is your
	 * turn, otherwise an IllegalStateException will be thrown. this method DOES
	 * NOT call renderBoard for the current player, it only calls it for all of
	 * the other players.
	 * 
	 * @param boardObject
	 */
	protected void setBoard(Serializable boardObject)
	{
		if (!getUsername().equals(getProperty("currentplayer")))
			throw new IllegalStateException(
					"It's not your turn, so you can't modify the board");
		this.boardObject = boardObject;
		uploadBoard();
		broadcastRenderBoard();
	}

	private void uploadBoard()
	{
		if (!getUsername().equals(getProperty("currentplayer")))
			throw new IllegalStateException(
					"It's not your turn, so you can't modify the board");
		setProperty("board", serializeToString(boardObject));
	}

	/**
	 * this method reloads the game state from the server. this includes getting
	 * who is the current player, seeing if a game is running, showing who's
	 * joined the game, enabling or disabling the start game button based on how
	 * many players are in the game and the min and max allowed, if the game is
	 * over then showing the name of the winner, etc.
	 * 
	 * <br/><br/>this method should not be called excessively, as it creates a
	 * bunch of new components and removes some new components. it should only
	 * be called when a reloadstate message is received (which should only be
	 * sent when you are done with a turn, you won, you joined or left a game,
	 * etc). this method DOES NOT re-render the board. to re-render the board,
	 * you must call reRenderBoard() which calls r
	 * 
	 */
	@SuppressWarnings("deprecation")
	private void reloadGameState()
	{
		if (!isOnline())
		{
			mainPanel.removeAll();
			mainPanel
					.add(new JLabel(
							"You are not connected to the internet. Connect to the internet, then try again."));
		}
		String currentPlayer = getProperty("currentplayer");
		if (currentPlayer != null)
			alertedWon = false;
		else if (getProperty("winner") != null && !alertedWon)
		// && currentPlayer == null, inferred by above if statement
		{
			alertedWon = true;
			String winner = getProperty("winner");
			String wonMessage0;
			if (winner.equals("__draw"))
			{
				wonMessage0 = "<html>The game was a draw.<br/>Click OK to continue.";
			} else
			{
				final String wonAlertPrefix = (winner.equals(getUsername()) ? "You have"
						: winner + " has");
				wonMessage0 = "<html>" + wonAlertPrefix
						+ " won the game!<br/>Click OK to continue.";
			}
			final String wonMessage = wonMessage0;
			// if you won then the message should be You have won the game,
			// otherwise it should be something like Bob has won the game, etc
			new Thread()
			{
				public void run()
				{
					try
					{
						Thread.sleep(500);
					} catch (InterruptedException e)
					{
						// TODO Jan 11, 2008 Auto-generated catch block
						throw new RuntimeException(
								"TODO auto generated on Jan 11, 2008 : "
										+ e.getClass().getName() + " - "
										+ e.getMessage(), e);
					}
					if (hasPlayedGame)
						JOptionPane.showMessageDialog(getParentFrame(),
								wonMessage);
				}
			}.start();
		}
		if (currentPlayer == null)
		{
			// show join game panel
			if (mainPanel.getComponentCount() > 0
					&& mainPanel.getComponent(0) == joinGamePanel)
			{

			} else
			{
				mainPanel.removeAll();
				mainPanel.add(joinGamePanel);
				mainPanel.invalidate();
				mainPanel.validate();
				mainPanel.repaint();
			}
			lastTurnIndex = 0;
			String[] participants = getGameParticipants();
			// TODO: instead of replacing all components, only update the ones
			// that need updating
			joinGameParticipantsPanel.removeAll();
			for (String u : participants)
			{
				JComponent c = createPlayerComponent(u, false);
				joinGameParticipantsPanel.add(c);
			}
			joinGameParticipantsPanel.invalidate();
			joinGameParticipantsPanel.validate();
			joinGameParticipantsPanel.repaint();
			getParentFrame().invalidate();
			getParentFrame().validate();
			getParentFrame().repaint();
			System.out.println("refreshed join game participants panel");
			boolean amIParticipating = getProperty("participant_"
					+ getUsername()) != null;
			joinGameButton.setEnabled(!amIParticipating);
			leaveGameButton.setEnabled(amIParticipating);
			startGameButton.setEnabled(amIParticipating
					&& participants.length >= getMinPlayers()
					&& participants.length <= getMaxPlayers());
		} else
		{
			hasPlayedGame = true;
			// show play game panel
			if (mainPanel.getComponentCount() > 0
					&& mainPanel.getComponent(0) == playGamePanel)
			{

			} else
			{
				mainPanel.removeAll();
				mainPanel.add(playGamePanel);
				mainPanel.invalidate();
				mainPanel.validate();
				mainPanel.repaint();
			}
			String[] participants = getGameParticipants();
			// TODO: instead of replacing all components, only update the ones
			// that need updating
			playGameParticipantsPanel.removeAll();
			for (String u : participants)
			{
				System.out.println("currentplayer: " + currentPlayer);
				System.out.println("user: " + u);
				JComponent c = createPlayerComponent(u, currentPlayer.equals(u));
				playGameParticipantsPanel.add(c);
			}
			playGameParticipantsPanel.invalidate();
			playGameParticipantsPanel.validate();
			playGameParticipantsPanel.repaint();
			getParentFrame().invalidate();
			getParentFrame().validate();
			getParentFrame().repaint();
			if (currentPlayer.equals(getUsername()))
				isMyTurn = true;
			else
				isMyTurn = false;
			try
			{
				reRenderBoard();
			} catch (Exception ex1)
			{
				ex1.printStackTrace();
			}
			if (isMyTurn)
			{
				if (!getParentFrame().isActive())
				{
					myTurnNotificationLabel.setText("It's your turn in "
							+ getName());
					addNotification(myTurnNotification, true);
				}
				String turnIndexString = getProperty("turnindex");
				if (turnIndexString == null)
				{
					System.err
							.println("TURN INDEX IS NULL BUT THE GAME IS IN PROGRESS");
				} else
				{
					int turnIndex = Integer.parseInt(turnIndexString);
					if (turnIndex > lastTurnIndex)
					{
						lastTurnIndex = turnIndex;
						try
						{
							myTurn();
						} catch (Exception ex1)
						{
							ex1.printStackTrace();
						}
					}
				}
			}
		}
	}

	private void reRenderBoard()
	{
		String boardString = getProperty("board");
		if (boardString == null)
			System.err
					.println("boardstring is null but the game is in progress");
		else
		{
			boardObject = deserializeFromString(boardString);
			try
			{
				renderBoard(boardObject);
			} catch (Exception ex1)
			{
				ex1.printStackTrace();
			}
		}
	}

	/**
	 * creates the player component. this is the component that shows up in the
	 * panel at the bottom of the screen, describing each player. it has the
	 * player's name in bold at top (currently' it's a JPanel with a
	 * BorderLayout) and renderPlayerInfo(name) as the center component. if
	 * isCurrent is true, a red raised etched border is added to the panel. if
	 * not, an empty border of the same size is added.
	 * 
	 * @param name
	 * @param isCurrent
	 * @return
	 */
	private JComponent createPlayerComponent(String name, boolean isCurrent)
	{
		System.out.println("NAME:" + name + ",ISCURRENT:" + isCurrent);
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.setAlignmentX(0);
		p.setAlignmentY(0);
		String displayName;
		if (name.equals(getUsername()))
			displayName = "<html><font color=\"#004488\">Me</font>";
		else
			displayName = "<html><font color=\"#000000\">" + name + "</font>";
		p.add(new JLabel(displayName), BorderLayout.NORTH);
		p.add(renderPlayerInfo(name), BorderLayout.CENTER);
		if (isCurrent)
		{
			Border outBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
			Border middleBorder = BorderFactory.createEtchedBorder(
					EtchedBorder.RAISED, new Color(255, 50, 50).brighter(),
					new Color(255, 50, 50).darker());
			Border inBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
			Border middleOutBorder = BorderFactory.createCompoundBorder(
					outBorder, middleBorder);
			Border border = BorderFactory.createCompoundBorder(middleOutBorder,
					inBorder);
			p.setBorder(border);
		} else
		{
			Border outBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
			Border middleBorder = BorderFactory.createEtchedBorder(
					EtchedBorder.RAISED, new Color(238, 238, 238).brighter(),
					new Color(238, 238, 238).darker());
			Border inBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
			Border middleOutBorder = BorderFactory.createCompoundBorder(
					outBorder, middleBorder);
			Border border = BorderFactory.createCompoundBorder(middleOutBorder,
					inBorder);
			p.setBorder(border);
		}
		return p;
	}

	@Override
	public void receiveMessage(String from, String message)
	{
		if (message.equals("reloadstate"))
			reloadGameState();
		else if (message.equals("renderboard"))
			reRenderBoard();
	}

	@Override
	public void shutdown()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void userStatusChanged()
	{
		// TODO Auto-generated method stub

	}

	/**
	 * called when it is this user's turn in the game. this will be called only
	 * once per turn. you can also use isMyTurn() to see if it is your turn.
	 * 
	 */
	protected abstract void myTurn();

	/**
	 * returns the game component, IE the component that represents the board,
	 * and any controls needed for the board to function. this does not need to
	 * include a list of users in the game, or who the current user is;
	 * MultiplayerSequenceGame takes care of that for you.<br/><br/>
	 * 
	 * This method is called right after initializeGame() and never again, so it
	 * should return a constant. When updates to the board are available (except
	 * when it is the current player's turn; the board must be manually updated
	 * then), renderBoard() is called, with the board object being passed in.
	 * this should just update the component returned from this method.<br/><br/>
	 * 
	 * You can initialize this component from within initializeGame().
	 * 
	 * @return
	 */
	protected abstract JComponent getGameComponent();

	protected boolean isMyTurn()
	{
		return isMyTurn;
	}

	/**
	 * returns the players who are participating in the current game.
	 * 
	 * this method may be called before a game starts, in which case it returns
	 * the users who have joined the game so far. if the game has not started
	 * yet, the list of users returned from this method may be less than
	 * getMinPlayers().
	 * 
	 * @return
	 */
	protected String[] getGameParticipants()
	{
		String[] ps = listProperties("participant_");
		String[] participants = new String[ps.length];
		for (int i = 0; i < ps.length; i++)
		{
			participants[i] = ps[i].substring("participant_".length());
		}
		return participants;
	}

	/**
	 * wins the current game. the game is over, the board is removed, a panel
	 * allowing players to join in the next game is shown, and a popup appears
	 * on each player's screen telling them that you have won.
	 * 
	 * this can only be called when it's your turn, so if a player needs to win
	 * when it's not their turn, then you should call finishedTurn(int) and pass
	 * the number of players needed to advance so that it's the player's turn
	 * who should win, and then that player should call winGame.
	 * 
	 * the above restriction should be lifted soon, so keep an eye out.
	 * 
	 */
	protected void winGame()
	{
		if (!getUsername().equals(getProperty("currentplayer")))
			throw new IllegalStateException("It's not your turn right now.");
		setProperty("currentplayer", null);
		setProperty("winner", getUsername());
		for (String u : getGameParticipants())
		{
			setProperty("participant_" + u, null);
		}
		reloadGameState();
		broadcastReloadState();
	}

	protected void drawGame()
	{
		if (!getUsername().equals(getProperty("currentplayer")))
			throw new IllegalStateException("It's not your turn right now.");
		setProperty("currentplayer", null);
		setProperty("winner", "__draw");
		for (String u : getGameParticipants())
		{
			setProperty("participant_" + u, null);
		}
		reloadGameState();
		broadcastReloadState();
	}

	/**
	 * returns whether or not a game is in progress. this method contacts the
	 * server, so it is a bit expensive to use. when i ran a test on the speed,
	 * it took about 200 milliseconds to return.
	 * 
	 * @return
	 */
	protected boolean isGameRunning()
	{
		return getProperty("currentplayer") != null;
	}

	/**
	 * gets the default board object. the board is reset to this at the
	 * beginning of each game. for example, in Othello, this should return some
	 * sort of board representation with 2 white beads and 2 black beads in the
	 * traditional starting arrangement.
	 * 
	 * @return
	 */
	protected abstract Serializable getDefaultBoardObject();

	/**
	 * creates this local player's metadata. this method should block if it
	 * needs user input until the input is provided. for example, in pente or
	 * othello, this method should pop open a JColorChooser to ask the user for
	 * their bead color, and then return some sort of object representing that
	 * player with the color in it. future calls to renderBoard could then get
	 * the bead color off of this to draw.
	 * 
	 * @return
	 */
	protected abstract Serializable createPlayerMetadata();

	/**
	 * renders the player info for the specified player. this method should call
	 * getPlayerMetadata and return a component suitable for showing that
	 * metadata, in a small view. this component need not contain the name of
	 * the player, MultiplayerSequenceGame takes care of that for you.
	 * 
	 * @param name
	 * @return
	 */
	protected abstract JComponent renderPlayerInfo(String name);

	/**
	 * returns the player's metadata (as generated by createPlayerMetadata() on
	 * their computer when they chose to join the game), or null if they are not
	 * a game participant. this can be called before the game starts, and may be
	 * useful in determining allowed metadata. for example, in Pente, you
	 * wouldn't want 2 people to have the same color of bead, so you would check
	 * this for each other player before allowing the player to choose their
	 * bead color.
	 * 
	 * @return
	 */
	protected Serializable getPlayerMetadata(String name)
	{
		String pa = getProperty("participant_" + name);
		if (pa == null)
			return null;
		return deserializeFromString(pa);
	}

	private static String serializeToString(Serializable object)
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			oos.flush();
			oos.close();
			return new String(Base64Coder.encode(baos.toByteArray()));
		} catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

	private static Serializable deserializeFromString(String s)
	{
		try
		{
			ObjectInputStream ois = new ObjectInputStream(
					new ByteArrayInputStream(Base64Coder.decode(s)));
			Object object = ois.readObject();
			ois.close();
			return (Serializable) object;
		} catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

	/**
	 * forfeits the game. this may only be called when it is your turn. this
	 * removes you from the list of players in the game (by removing your
	 * participant_ key). it then changes the current player on to the next
	 * player. if forfeiting means that the number of players drops below the
	 * minimum, then the game is ended, and no-one wins, unless their is only
	 * one player left (IE getMinPlayers() returns 2), in which case that player
	 * wins.
	 * 
	 * FIXME: this still needs to be implemented
	 * 
	 * 
	 */
	protected void forfeit()
	{
	}

	/**
	 * indicates that you are finished with your turn. the board is uploaded
	 * (this is usually redundant since when you set the board last the board
	 * was uploaded), and play passes to the next player.
	 * 
	 * this is the same as finishedTurn(0).
	 */
	protected void finishedTurn()
	{
		finishedTurn(0);
	}

	/**
	 * indicates that you are finished with your turn. the board is uploaded
	 * (this is usually redundant since when you set the board last the board
	 * was uploaded), and play passes to the next player, or the player after
	 * that, etc., depending on the skip parameter.
	 * 
	 * @param skip
	 *            the number of players to skip. if this is 0, play is passed to
	 *            the next player. if this number is 1, the next player is
	 *            skipped, and play passes to the player after that, etc.
	 */
	protected void finishedTurn(int skip)
	{
		skip++;
		isMyTurn = false;
		removeNotification(myTurnNotification);
		if (!getUsername().equals(getProperty("currentplayer")))
			throw new IllegalStateException("It's not your turn right now");
		uploadBoard();
		// we don't need to broadcastRenderBoard() because
		// broadcastReloadState() below will do that for us (part of the
		// reloadState() method is calling reRenderBoard())
		String[] players = getGameParticipants();
		int myIndex = -1;
		for (int i = 0; i < players.length; i++)
		{
			if (players[i].equals(getUsername()))
			{
				myIndex = i;
				break;
			}
		}
		if (myIndex == -1)
		{
			throw new RuntimeException(
					"An error has occured. You are the current player, but you are not "
							+ "participating in the game. This indicates an error in"
							+ " MultiplayerSequenceGame. Please report this to webmaster@trivergia.com, "
							+ "along with the stack trace and any details you might have.");
		}
		int nextIndex = myIndex + skip;
		while (nextIndex >= players.length)
		{
			nextIndex -= players.length;// rotate down until we are within the
			// player range, this will only have to loop more than once if skip
			// is greater than the number of players playing
		}
		String nextPlayer = players[nextIndex];
		try
		{
			int turnIndex = Integer.parseInt(getProperty("turnindex"));
			setProperty("turnindex", "" + (turnIndex + 1));
		} catch (Exception ex1)
		{
			ex1.printStackTrace();
			setProperty("turnindex", "3");
		}
		setProperty("currentplayer", nextPlayer);
		reloadGameState();
		broadcastReloadState();
	}

	protected abstract void renderBoard(Serializable board);

}
