/**
 * 
 */
package com.yakasoftware.telephony.iax.iaxclient.constants;

/**
 * @author Roland Ndaka Fru
 *
 */
public interface CallState {	
	public static final int IAXC_CALL_STATE_FREE     = 0;
	public static final int IAXC_CALL_STATE_ACTIVE   = 1<<1;
	public static final int IAXC_CALL_STATE_OUTGOING = 1<<2;
	public static final int IAXC_CALL_STATE_RINGING  = 1<<3;
	public static final int IAXC_CALL_STATE_COMPLETE = 1<<4;
	public static final int IAXC_CALL_STATE_SELECTED = 1<<5;
	public static final int IAXC_CALL_STATE_BUSY     = 1<<6;
	public static final int IAXC_CALL_STATE_TRANSFER = 1<<7;
}
