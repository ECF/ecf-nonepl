/**
 * 
 */
package com.yakasoftware.telephony.iax.iaxclient.event;

import com.yakasoftware.telephony.iax.iaxclient.util.VideoStats;

/**
 * A Video Statistics event which will normally be generated at the native layer and propagated to java
 * 
 * @author Roland Ndaka Fru
 * 
 *
 */
public class Event_VideoStats extends Callback_Event {
	
	private int callNo;
	private VideoStats stats;
	/**
	 * @param callNo
	 * @param stats
	 */
	public Event_VideoStats(int callNo, VideoStats stats) {
		super();
		this.callNo = callNo;
		this.stats = stats;
	}
	/**
	 * @return the callNo
	 */
	public int getCallNo() {
		return callNo;
	}
	/**
	 * @return the stats
	 */
	public VideoStats getStats() {
		return stats;
	}	

}
