/**
 * 
 */
package com.yakasoftware.telephony.iax.iaxclient.constants;

/**
 * @author Roland Ndaka Fru
 *
 */
public interface Video {

	/*
	 * Acceptable range for video resolution
	 */
	public static final int IAXC_VIDEO_MAX_WIDTH    = 704;
	public static final int IAXC_VIDEO_MAX_HEIGHT   = 576;
	public static final int IAXC_VIDEO_MIN_WIDTH    = 80;
	public static final int IAXC_VIDEO_MIN_HEIGHT   = 60;

	/*
	 * Video callback preferences
	 * The client application can obtain any combination of
	 * remote/local, encoded/raw video through the event callback
	 * mechanism
	 * Use these flags to specify what kind of video do you want to receive
	 */

	public static final int IAXC_VIDEO_PREF_RECV_LOCAL_RAW      = 1 << 0;
	public static final int IAXC_VIDEO_PREF_RECV_LOCAL_ENCODED  = 1 << 1;
	public static final int IAXC_VIDEO_PREF_RECV_REMOTE_RAW     = 1 << 2;
	public static final int IAXC_VIDEO_PREF_RECV_REMOTE_ENCODED = 1 << 3;
	public static final int IAXC_VIDEO_PREF_SEND_DISABLE        = 1 << 4;
	/*
	 * Use this flag to specify that you want raw video in RGB32 format
	 * RGB32: FFRRGGBB aligned 4 bytes per pixel
	 * When this flag is set, iaxclient will convert YUV420 raw video into
	 * RGB32 before passing it to the main app.
	 */
	public static final int IAXC_VIDEO_PREF_RECV_RGB32          = 1 << 5;

	/** Use this flag to disable/enable camera hardware */
	public static final int IAXC_VIDEO_PREF_CAPTURE_DISABLE     = 1 << 6;
}
