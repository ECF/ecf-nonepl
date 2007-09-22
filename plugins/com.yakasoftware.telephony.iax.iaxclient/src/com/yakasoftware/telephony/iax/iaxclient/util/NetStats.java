/**
 * 
 */
package com.yakasoftware.telephony.iax.iaxclient.util;

/**
 * Net Statistics
 * 
 * @author Roland Ndaka Fru
 *
 */
public class NetStats {
	
	private int jitter;
	private int losspct;
	private int losscnt;
	private int packets;
	private int delay;
	private int dropped;
	private int ooo;
	
	/**
	 * @param jitter
	 * @param losspct
	 * @param losscnt
	 * @param packets
	 * @param delay
	 * @param dropped
	 * @param ooo
	 */
	public NetStats(int jitter, int losspct, int losscnt, int packets,
			int delay, int dropped, int ooo) {
		super();
		this.jitter = jitter;
		this.losspct = losspct;
		this.losscnt = losscnt;
		this.packets = packets;
		this.delay = delay;
		this.dropped = dropped;
		this.ooo = ooo;
	}

	/**
	 * @return the jitter
	 */
	public int getJitter() {
		return jitter;
	}

	/**
	 * @return the losspct
	 */
	public int getLosspct() {
		return losspct;
	}

	/**
	 * @return the losscnt
	 */
	public int getLosscnt() {
		return losscnt;
	}

	/**
	 * @return the packets
	 */
	public int getPackets() {
		return packets;
	}

	/**
	 * @return the delay
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * @return the dropped
	 */
	public int getDropped() {
		return dropped;
	}

	/**
	 * @return the ooo
	 */
	public int getOoo() {
		return ooo;
	}
}
