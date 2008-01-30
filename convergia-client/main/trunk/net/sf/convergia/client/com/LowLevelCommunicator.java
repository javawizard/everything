package net.sf.convergia.client.com;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.SSLSocketFactory;

/**
 * this class is a low-level communicator for communicating with an Convergia
 * server. it simply allows you to send lines to and from the server, and deals
 * with authentication and re-establishing lost connections.
 * 
 * @author Alexander Boyd
 * 
 */
public class LowLevelCommunicator
{
	private ArrayList<LowLevelMessageSink> sinks = new ArrayList<LowLevelMessageSink>();

	public static final int DEFAULT_PORT = 64482;

	public static final String DEFAULT_HOST = "localhost";

	public HashMap<String, Long> commandAmounts = new HashMap<String, Long>();

	public HashMap<String, Long> responseAmounts = new HashMap<String, Long>();

	private Socket socket;

	private InputStream in;

	private final Object ssMonitor = new Object();

	private OutputStream out;

	private String username;

	private String password;

	private String readLineN() throws IOException
	{
		return readLineBx(false);
	}

	private String readLineT() throws IOException
	{
		return readLineBx(true);
	}

	private String readLineBx(boolean throwEx) throws IOException
	{
		// System.out.println("about to acq monitor in thread "
		// + Thread.currentThread().getName());
		String toReturn = null;
		synchronized (in)
		{
			// System.out.println("acq'd monitor in thread "
			// + Thread.currentThread().getName());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int byteNum = 0;
			while (true)
			{
				int i = in.read();
				if ((byteNum++ % 25) == 0)
				{
					// System.out.println("read " + byteNum + " th byte");
				}
				if (baos.size() > 1000 * 1000)
					throw new RuntimeException("too many bytes read");
				if (i == -1 && throwEx)
				{
					if (baos.toByteArray().length == 0)
						throw new EOFException("end of input from socket");
					else
					{
						toReturn = new String(baos.toByteArray(), "ASCII");
						break;
					}
				} else if (i == -1)// && !throwEx, this is inferred because
				// of
				// the above if statement
				{
					if (baos.toByteArray().length == 0)
						break;
					else
					{
						toReturn = new String(baos.toByteArray(), "ASCII");
						break;
					}
				} else if (i == '\r')
					continue;
				else if (i == '\n')
				{
					toReturn = new String(baos.toByteArray(), "ASCII");
					break;
				} else
				{
					baos.write(i);
				}
			}
			// System.out.println("releasing monitor for thread "
			// + Thread.currentThread().getName());
		}
		// System.out.println("released monitor in thread "
		// + Thread.currentThread().getName());
		return toReturn;
	}

	public synchronized void sendMessage(String message) throws IOException
	{
		String cmd = message.split(" ")[0];
		if (commandAmounts.get(cmd) == null)
			commandAmounts.put(cmd, (long) 1);
		else
			commandAmounts.put(cmd, commandAmounts.get(cmd) + 1);
		sendLine(message);
	}

	private void sendLine(String line) throws IOException
	{
		// System.out.println("sending line "
		// + line.substring(0, Math.min(line.length(), 255)));
		synchronized (out)
		{
			out.write((line + (line.endsWith("\n") ? "" : "\r\n"))
					.getBytes("ASCII"));
			out.flush();
		}
	}

	/**
	 * creates a LowLevelCommunicator connecting to the default host
	 * (its.trivergia.com) and the default port (64482).
	 * 
	 * @param useSSL
	 * @throws IOException
	 */
	public LowLevelCommunicator(boolean useSSL) throws IOException
	{
		this(DEFAULT_HOST, useSSL);
	}

	/**
	 * creates a LowLevelCommunicator connecting to the specified host on the
	 * default port (64482). S
	 * 
	 * @param host
	 * @param useSSL
	 * @throws IOException
	 */
	public LowLevelCommunicator(String host, boolean useSSL) throws IOException
	{
		this(host, DEFAULT_PORT, useSSL);
	}

	protected boolean isActive = false;

	private String host;

	private int port;

	private boolean useSSL;

	/**
	 * creates a LowLevelCommunicator connecting to the specified host on the
	 * specified port.
	 * 
	 * @param host
	 *            the host to connect to
	 * @param port
	 *            the port to connect to
	 * @param useSSL
	 *            true to use SSL to connect, false to use plain sockets. you
	 *            will need to look up the information on the server you are
	 *            using for what to set as this value. you can also try using
	 *            SSL and if you get an SSL-related protocol exception, default
	 *            to plain sockets.
	 * @throws IOException
	 */
	public LowLevelCommunicator(String host, int port, boolean useSSL)
			throws IOException
	{
		this.host = host;
		this.port = port;
		this.useSSL = useSSL;
		new Thread()
		{

			public void run()
			{
				StringBuilder currentGroupBuilder = null;
				while (true)
				{
					try
					{
						String line = readLineT();
						// System.out.println("received line "
						// + line.substring(0, Math
						// .min(line.length(), 255)));
						if (currentGroupBuilder == null)
							currentGroupBuilder = new StringBuilder();
						if (line.equals(""))
						{
							final String group = currentGroupBuilder.toString();
							currentGroupBuilder = null;
							if (group.length() > 0)
							{
								final int firstSpaceIndex = group.trim()
										.indexOf(" ");
								String cmd = group.trim().split(" ")[0];
								if (responseAmounts.get(cmd) == null)
									responseAmounts.put(cmd, (long) 0);
								else
									responseAmounts.put(cmd, responseAmounts
											.get(cmd) + 1);
								String cx2;
								String ax2;
								if(firstSpaceIndex != -1)
								{
								cx2 = group.trim()
										.substring(0, firstSpaceIndex);
								ax2 = group.trim().substring(
										firstSpaceIndex + 1);
								}
								else
								{
									cx2 = group.trim();
									ax2 = "";
								}
								final String cx = cx2;
								final String ax = ax2;
								for (final LowLevelMessageSink sink : sinks)
								{
									new Thread()
									{
										public void run()
										{
											sink.process(cx, ax);
										}
									}.start();
								}
							}
						} else
						{
							currentGroupBuilder.append(line + "\r\n");
						}
					} catch (Exception e)
					{
						isActive = false;
						e.printStackTrace();
						if (isOpened)
						{
							socket = null;
							try
							{
								synchronized (ssMonitor)
								{
									ssMonitor.wait(10 * 1000);
								}
							} catch (InterruptedException e1)
							{
							}
							try
							{
								// the following line is soley to raise an NPE
								// if username is null
								username.equals("");
								setUpSocket();
								isActive = true;
							} catch (Exception e2)
							{
								e2.printStackTrace();
							}
						} else
						{
							return;
						}
					}
				}
			}
		}.start();
		new Thread()
		{
			public void run()
			{
				while (true)
				{
					System.out.println("waiting 45 seconds, then no-oping");
					try
					{
						Thread.sleep(45 * 1000);
						System.out.println("no-oping");
						sendLine("nop n");// send NOPs every 15 seconds so
						// that we will detect if we're
						// offline
					} catch (Exception ex1)
					{
						ex1.printStackTrace();
					}
				}
			}
		}.start();
	}

	private synchronized void setUpSocket() throws IOException

	{
		if (socket != null && !socket.isClosed())
			return;
		socket = null;
		while (socket == null)
		{
			connectedHost = host;
			if (useSSL)
				socket = SSLSocketFactory.getDefault().createSocket(host, port);
			else
				socket = new Socket(host, port);
			in = socket.getInputStream();
			out = socket.getOutputStream();
			String greetingLine = readLineT();
			if (greetingLine.startsWith("OK"))
				break;
			else if (greetingLine.startsWith("REDIRECT"))
			{
				String hostAndPort = greetingLine.substring("REDIRECT "
						.length());
				host = hostAndPort.substring(0, hostAndPort.indexOf(":"));
				port = Integer.parseInt(hostAndPort.substring(hostAndPort
						.indexOf(":") + 1));
				socket = null;
				System.out.println("redirecting to host " + host + " on port "
						+ port);
			}
		}
		in = socket.getInputStream();
		out = socket.getOutputStream();
		sendLine(username);
		sendLine(password);
		String authResponseString = readLineT();
		if (!authResponseString.startsWith("OK"))
			throw new AuthenticationException(
					"Logging in failed with the following message from the server: "
							+ authResponseString);
		sendLine("command");
		synchronized (ssMonitor)
		{
			ssMonitor.notifyAll();
		}
	}

	public synchronized void authenticate(String username, String password)
			throws IOException, AuthenticationException
	{
		if (socket != null)
			socket.close();
		this.username = username;
		this.password = password;
		try
		{
			setUpSocket();
		} catch (IOException e)
		{
			throw e;
		} catch (AuthenticationException e)
		{
			username = null;
			password = null;
			socket = null;
			throw e;
		}
	}

	public void addSink(LowLevelMessageSink sink)
	{
		sinks.add(sink);
	}

	public void removeSink(LowLevelMessageSink sink)
	{
		sinks.remove(sink);
	}

	public LowLevelMessageSink[] listSinks()

	{
		return sinks.toArray(new LowLevelMessageSink[0]);
	}

	private boolean isOpened = true;

	public void close() throws IOException
	{
		isOpened = false;
		sendLine("QUIT");
		socket.close();
	}

	/**
	 * returns whether this LowLevelCommunicator has an active connection to the
	 * server. if this is false, all of the sendXXX methods will throw
	 * exceptions, no messages will be received, and this communicator is
	 * attempting to re-establish a connection (unless this communicator has
	 * been close()d)
	 * 
	 * @return
	 */
	public boolean isActive()
	{
		return socket != null && !socket.isClosed();
	}

	public Socket getSocket()
	{
		if (!isActive())
			return null;
		return socket;
	}

	private String connectedHost = null;

	public String getConnectedHost()
	{
		return connectedHost;
	}

	public int getConnectedPort()
	{
		// TODO Auto-generated method stub
		return port;
	}
}
