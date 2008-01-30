package net.sf.convergia.client.com;

/**
 * This classs is a listener interface for listeners that want to receive
 * notifications from a Communicator when the number of users logged in has been
 * updated (but not necesscarily changed) or the userStatusHash has been updated
 * (but not nessecarily chcanged).
 * 
 * @author Alexander Boyd
 */
public interface StatusListener
{
	/**
	 * called when the list of all users is updated.
	 */
	public void allUsersUpdated(Communicator c);

	/**
	 * called when the list of online users has been updated.
	 */
	public void onlineUsersUpdated(Communicator c);

	/**
	 * called when the list of offline users has been updated.
	 */
	public void offlineUsersUpdated(Communicator c);

	/**
	 * called when any of the user lists are updated. this means that this
	 * method is called just after any invocation of allUsersChanged(),
	 * onlineUsersChanged(), and offlineUsersChanged().
	 * 
	 */
	public void anyUsersUpdated(Communicator c);

	/**
	 * called when the user status hash has been updated.
	 * 
	 */
	public void userStatusHashUpdated(Communicator c);

	/**
	 * called when the user status hash has changed. this means that the
	 * Communicator received the user status hash and it was different from the
	 * last one it had received.
	 * 
	 */
	public void userStatusHashChanged(Communicator c);

	/**
	 * indicates that a user has signed on. in the future, this method will show
	 * which user has signed on.
	 * 
	 * @param c
	 * @param arguments
	 */
	public void userHere(Communicator c, String arguments);

	/**
	 * indicates that a user has signed off. in the future, this mtehod will
	 * show which user has signed off.
	 * 
	 * @param c
	 * @param arguments
	 */
	public void userGone(Communicator c, String arguments);
}
