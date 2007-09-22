/**
 * 
 */
package com.yakasoftware.telephony.iax.iaxclient.constants;

/**
 * @author Roland Ndaka Fru
 *
 */
public interface Global {
	
	public static final int IAXC_TEXT_TYPE_STATUS     = 1;
	public static final int IAXC_TEXT_TYPE_NOTICE     = 2;
	public static final int IAXC_TEXT_TYPE_ERROR      = 3;
	/** FATAL ERROR: User Agent should probably display error, then die. */
	public static final int IAXC_TEXT_TYPE_FATALERROR = 4;
	public static final int IAXC_TEXT_TYPE_IAX        = 5;

	/** URL received */
	public static final int IAXC_URL_URL              = 1;
	/** URL loading complete */
	public static final int IAXC_URL_LDCOMPLETE       = 2;
	/** URL link request */
	public static final int IAXC_URL_LINKURL          = 3;
	/** URL link reject */
	public static final int IAXC_URL_LINKREJECT       = 4;
	/** URL unlink */
	public static final int IAXC_URL_UNLINK           = 5;

	/* The source of the video or audio data triggering the event. */
	public static final int IAXC_SOURCE_LOCAL  = 1;
	public static final int IAXC_SOURCE_REMOTE = 2;

}
