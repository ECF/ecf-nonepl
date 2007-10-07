/**
 * 
 */
package org.eclipse.ecf.provider.iax.container;

import com.yakasoftware.telephony.iax.iaxclient.IAXClientListener;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_Audio;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_Level;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_NetStats;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_Registration;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_State;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_Text;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_URL;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_Video;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_VideoStats;

/**
 * @author pedros09
 *
 */
public class IAXClientListenerImpl implements IAXClientListener {

	private IAXContainer iaxContainer;
	/**
	 * 
	 */
	public IAXClientListenerImpl(IAXContainer iaxContainer) {
		super();
		this.iaxContainer = iaxContainer;
	}

	public void onEvent_Audio(Event_Audio fired_EventAudio) {
		this.iaxContainer.handleEventAudio(fired_EventAudio);
	}

	public void onEvent_CallState(Event_State fired_EventCallState) {
		this.iaxContainer.handleEventCallState(fired_EventCallState);
	}

	public void onEvent_Level(Event_Level fired_EventLevel) {
		this.iaxContainer.handleEventLevel(fired_EventLevel);
	}

	public void onEvent_NetStats(Event_NetStats fired_EventNetStats) {
		this.iaxContainer.handleEventNetStats(fired_EventNetStats);
	}

	public void onEvent_Registration(Event_Registration fired_EventRegistration) {
		this.iaxContainer.handleEventRegistration(fired_EventRegistration);
	}

	public void onEvent_Text(Event_Text fired_EventText) {
		this.iaxContainer.handleEventText(fired_EventText);
	}

	public void onEvent_URL(Event_URL fired_EventURL) {
		this.iaxContainer.handleEventURL(fired_EventURL);
	}

	public void onEvent_Video(Event_Video fired_EventVideo) {
		this.iaxContainer.handleEventVideo(fired_EventVideo);
	}

	public void onEvent_VideoStats(Event_VideoStats fired_EventVideoStats) {
		this.iaxContainer.handleEventVideoStats(fired_EventVideoStats);
	}

}
