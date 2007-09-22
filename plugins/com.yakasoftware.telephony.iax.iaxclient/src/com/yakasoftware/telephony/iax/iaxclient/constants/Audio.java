/**
 * 
 */
package com.yakasoftware.telephony.iax.iaxclient.constants;

/**
 * @author Roland Ndaka Fru
 *
 */
public interface Audio {

	public static final int IAXC_AD_INPUT           = 1<<0;
	public static final int IAXC_AD_OUTPUT          = 1<<1;
	public static final int IAXC_AD_RING            = 1<<2;
	public static final int IAXC_AD_INPUT_DEFAULT   = 1<<3;
	public static final int IAXC_AD_OUTPUT_DEFAULT  = 1<<4;
	public static final int IAXC_AD_RING_DEFAULT    = 1<<5;
	
	public static final int IAXC_FILTER_DENOISE     = 1<<0;
	public static final int IAXC_FILTER_AGC         = 1<<1;
	public static final int IAXC_FILTER_ECHO        = 1<<2;
	public static final int IAXC_FILTER_AAGC        = 1<<3; /* Analog = mixer-based; AGC */
	public static final int IAXC_FILTER_CN          = 1<<4; /* Send CN frames when silence detected */
	
	/*
	 * Functions and flags for setting and getting audio callback preferences
	 * The application can request to receive local/remote, raw/encoded audio
	 * through the callback mechanism. Please note that changing callback
	 * settings will overwrite all previous settings.
	 */
	public static final int IAXC_AUDIO_PREF_RECV_LOCAL_RAW      = 1 << 0;
	public static final int IAXC_AUDIO_PREF_RECV_LOCAL_ENCODED  = 1 << 1;
	public static final int IAXC_AUDIO_PREF_RECV_REMOTE_RAW     = 1 << 2;
	public static final int IAXC_AUDIO_PREF_RECV_REMOTE_ENCODED = 1 << 3;
	public static final int IAXC_AUDIO_PREF_SEND_DISABLE        = 1 << 4;	

}
