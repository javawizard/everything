package net.sf.opengroove.client.com;

/**
 * An interface for listening for subscription events. A
 * <code>SubscriptionListener</code> can be registered with a
 * {@link CommandCommunicator}, and will then be notified when an event happens
 * on a subscription.
 * 
 * @author Alexander Boyd
 * 
 */
public interface SubscriptionListener
{
    /**
     * Alerts the listener that an event has happened for the subscription
     * specified.
     * 
     * @param subscription
     *            The subscription that received an event. This object is
     *            usually shared among all listeners notified, so it should not
     *            be modified.
     */
    public void event(Subscription subscription);
}
