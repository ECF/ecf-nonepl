package org.eclipse.ecf.protocol.nntp.model;

public interface ISubscribable {

	/**
	 * Subscribes or unsubscribes to this subscribable.
	 * 
	 * @param subscribe
	 */
	public void setSubscribed(boolean subscribe);

	/**
	 * Returns if the user is subscribed to the subscribeable. Clients use this
	 * to show the or hide it from the active view.
	 * 
	 * @return
	 */
	public boolean isSubscribed();

}
