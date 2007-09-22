/**
 * 
 */
package com.yakasoftware.telephony.iax.iaxclient.event;

import com.yakasoftware.telephony.iax.iaxclient.util.NetStats;

/**
 * A Net Statistics event which will normally be generated at the native layer and propagated to java
 * 
 * @author Roland Ndaka Fru
 * 
 *
 */
public class Event_NetStats extends Callback_Event {

	private int callNo;
	private int rtt;
	private NetStats local;
	private NetStats remote;
	
	/**
	 * @param callNo
	 * @param rtt
	 * @param local
	 * @param remote
	 */
	public Event_NetStats(int callNo, int rtt, NetStats local, NetStats remote) {
		super();
		this.callNo = callNo;
		this.rtt = rtt;
		this.local = local;
		this.remote = remote;
	}	
	
	/**
	 * @return the callNo
	 */
	public int getCallNo() {
		return callNo;
	}

	/**
	 * @return the rtt
	 */
	public int getRtt() {
		return rtt;
	}

	/**
	 * @return the local
	 */
	public NetStats getLocal() {
		return local;
	}

	/**
	 * @return the remote
	 */
	public NetStats getRemote() {
		return remote;
	}

}
