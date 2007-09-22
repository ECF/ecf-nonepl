/**
 * 
 */
package com.yakasoftware.telephony.iax.iaxclient.event;

/**
 * An abstract class from which all events will be generated!
 * Events are normally generated at the native layer and propagated through to
 * the java layer via callbacks!
 * 
 * @author Roland Ndaka Fru
 *
 */
public abstract class Callback_Event {	
	protected static final int IAXC_EVENT_BUFSIZ = 256;
}
