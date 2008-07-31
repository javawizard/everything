package net.sf.opengroove.client.com;

/**
 * This classs is a listener interface for listeners that want to receive
 * notifications from a Communicator when the number of users logged in has been
 * updated (but not necesscarily changed) or the userStatusHash has been updated
 * (but not nessecarily chcanged).
 * 
 * @author Alexander Boyd
 */
public interface OldStatusListener
{
	/**
	 * called when the list of all users is updated.
	 */
	public void allUsersUpdated(OldCommunicator c);

	/**
	 * called when the list of online users has been updated.
	 */
	public void onlineUsersUpdated(OldCommunicator c);

	/**
	 * called when the list of offline users has been updated.
	 */
	public void offlineUsersUpdated(OldCommunicator c);

	/**
	 * called when any of the user lists are updated. this means that this
	 * method is called just after any invocation of allUsersChanged(),
	 * onlineUsersChanged(), and offlineUsersChanged().
	 * 
	 */
	public void anyUsersUpdated(OldCommunicator c);

	/**
	 * called when the user status hash has been updated.
	 * 
	 */
	public void userStatusHashUpdated(OldCommunicator c);

	/**
	 * called when the user status hash has changed. this means that the
	 * Communicator received the user status hash and it was different from the
	 * last one it had received.
	 * 
	 */
	public void userStatusHashChanged(OldCommunicator c);

	/**
	 * indicates that a user has signed on. in the future, this method will show
	 * which user has signed on.
	 * 
	 * @param c
	 * @param arguments
	 */
	public void userHere(OldCommunicator c, String arguments);

	/**
	 * indicates that a user has signed off. in the future, this mtehod will
	 * show which user has signed off.
	 * 
	 * @param c
	 * @param arguments
	 */
	public void userGone(OldCommunicator c, String arguments);
}
