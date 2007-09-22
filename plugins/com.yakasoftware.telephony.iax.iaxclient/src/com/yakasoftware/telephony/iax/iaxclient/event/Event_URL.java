/**
 * 
 */
package com.yakasoftware.telephony.iax.iaxclient.event;

/**
 * A URL event which will normally be generated at the native layer and propagated to java
 * 
 * @author Roland Ndaka Fru
 * 
 *
 */
public class Event_URL extends Callback_Event {
	
	private int callNo;
	private int type;
	private char [] url = new char[IAXC_EVENT_BUFSIZ];
	
	/**
	 * @param callNo
	 * @param type
	 * @param url
	 */
	public Event_URL(int callNo, int type, char[] url) {
		super();
		this.callNo = callNo;
		this.type = type;
		this.url = url;
	}

	/**
	 * @return the callNo
	 */
	public int getCallNo() {
		return callNo;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the url
	 */
	public char[] getUrl() {
		return url;
	}

}
