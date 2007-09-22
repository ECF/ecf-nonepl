package com.yakasoftware.telephony.iax.iaxclient.constants;

/**
 * @author Roland Ndaka Fru
 *
 */
public interface Event {
	
	public static final int IAXC_EVENT_TEXT          = 1;
	public static final int IAXC_EVENT_LEVELS        = 2;
	public static final int IAXC_EVENT_STATE         = 3;
	public static final int IAXC_EVENT_NETSTAT       = 4;

	/** URL push via IAX(2) */
	public static final int IAXC_EVENT_URL           = 5;
	public static final int IAXC_EVENT_VIDEO         = 6;
	public static final int IAXC_EVENT_REGISTRATION  = 8;
	public static final int IAXC_EVENT_DTMF          = 9;
	public static final int IAXC_EVENT_AUDIO         = 10;
	public static final int IAXC_EVENT_VIDEOSTATS    = 11;
	
	// registration replys, corresponding to IAX_EVENTs
	/** corresponds too IAX_EVENT_REGACC  */
	public static final int IAXC_REGISTRATION_REPLY_ACK     = 18;
	/** corresponds too IAX_EVENT_REGREJ  */
	public static final int IAXC_REGISTRATION_REPLY_REJ     = 30;
    /** corresponds too IAX_EVENT_TIMEOUT */
	public static final int IAXC_REGISTRATION_REPLY_TIMEOUT = 6;	

}
