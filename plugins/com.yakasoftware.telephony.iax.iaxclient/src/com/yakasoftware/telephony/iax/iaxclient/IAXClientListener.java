/**
 * 
 */
package com.yakasoftware.telephony.iax.iaxclient;

import com.yakasoftware.telephony.iax.iaxclient.event.Event_Audio;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_State;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_Level;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_NetStats;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_Registration;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_Text;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_URL;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_Video;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_VideoStats;

/**
 * Interface to be implemented by anyone interested in telephony callback from the native layer
 * 
 * @author Roland Ndaka Fru
 *
 */
public interface IAXClientListener {
	
	public void onEvent_Audio(Event_Audio fired_EventAudio);
	public void onEvent_CallState(Event_State fired_EventCallState);
	public void onEvent_Level(Event_Level fired_EventLevel);
	public void onEvent_NetStats(Event_NetStats fired_EventNetStats);
	public void onEvent_Registration(Event_Registration fired_EventRegistration);
	public void onEvent_Text(Event_Text fired_EventText);
	public void onEvent_URL(Event_URL fired_EventURL);
	public void onEvent_Video(Event_Video fired_EventVideo);
	public void onEvent_VideoStats(Event_VideoStats fired_EventVideoStats);
}
