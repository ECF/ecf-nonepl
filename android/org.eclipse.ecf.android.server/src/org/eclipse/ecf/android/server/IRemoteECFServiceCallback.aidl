
package org.eclipse.ecf.android.server;

/**
 * Callbacks by IRemoteOSGiService to send
 * synchronous notifications back to its clients.  Note that this is a
 * one-way interface so the server does not block waiting for the client.
 */
oneway interface IRemoteECFServiceCallback {
    /**
	* Called when a new connection is active
	*
	*/
	void clientConnected(String client);
	
	/**
	 * Called when SOContainer has started
	 */
	 void containerStarted();
}
