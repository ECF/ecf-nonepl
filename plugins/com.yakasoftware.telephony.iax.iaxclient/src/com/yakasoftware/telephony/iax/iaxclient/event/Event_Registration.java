/**
 * 
 */
package com.yakasoftware.telephony.iax.iaxclient.event;

/**
 * A Registration event which will normally be generated at the native layer and propagated to java
 * 
 * @author Roland Ndaka Fru
 * 
 *
 */
public class Event_Registration extends Callback_Event {
	
	private int id;
	private int reply;
	private int msgcount;
	
	/**
	 * @param id
	 * @param reply
	 * @param msgcount
	 */
	public Event_Registration(int id, int reply, int msgcount) {
		super();
		this.id = id;
		this.reply = reply;
		this.msgcount = msgcount;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the reply
	 */
	public int getReply() {
		return reply;
	}

	/**
	 * @return the msgcount
	 */
	public int getMsgcount() {
		return msgcount;
	}
	
}
