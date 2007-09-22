/**
 * 
 */
package com.yakasoftware.telephony.iax.iaxclient.event;

/**
 * A Call State event which will normally be generated at the native layer and propagated to java
 * 
 * @author Roland Ndaka Fru
 * 
 *
 */
public class Event_State extends Callback_Event {

	private int callNo;
	private int state;
	private int format;
	private int vformat;
	private char [] remote = new char[IAXC_EVENT_BUFSIZ] ;
	private char [] remote_name = new char[IAXC_EVENT_BUFSIZ] ;
	private char [] local = new char[IAXC_EVENT_BUFSIZ] ;
	private char [] local_context = new char[IAXC_EVENT_BUFSIZ] ;
	
	/**
	 * @param callNo
	 * @param state
	 * @param format
	 * @param vformat
	 * @param remote
	 * @param remote_name
	 * @param local
	 * @param local_context
	 */
	public Event_State(int callNo, int state, int format, int vformat,
			char[] remote, char[] remote_name, char[] local,
			char[] local_context) {
		super();
		this.callNo = callNo;
		this.state = state;
		this.format = format;
		this.vformat = vformat;
		this.remote = remote;
		this.remote_name = remote_name;
		this.local = local;
		this.local_context = local_context;
	}

	/**
	 * @return the callNo
	 */
	public int getCallNo() {
		return callNo;
	}

	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}

	/**
	 * @return the format
	 */
	public int getFormat() {
		return format;
	}

	/**
	 * @return the vformat
	 */
	public int getVformat() {
		return vformat;
	}

	/**
	 * @return the remote
	 */
	public char[] getRemote() {
		return remote;
	}

	/**
	 * @return the remote_name
	 */
	public char[] getRemote_name() {
		return remote_name;
	}

	/**
	 * @return the local
	 */
	public char[] getLocal() {
		return local;
	}

	/**
	 * @return the local_context
	 */
	public char[] getLocal_context() {
		return local_context;
	}
}
