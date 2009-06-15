package net.sf.opengroove.client.g3com;

/**
 * this exception is thrown when an incorrect username and/or password is
 * supplied to a LowLevelCommunicator.
 * 
 * @author Alexander Boyd
 * 
 */
public class AuthenticationException extends RuntimeException
{

	public AuthenticationException()
	{
		// TODO Auto-generated constructor stub
	}

	public AuthenticationException(String message)
	{
		super(message);
		// TODO Auto-generated constructor stub
	}

	public AuthenticationException(Throwable cause)
	{
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public AuthenticationException(String message, Throwable cause)
	{
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
