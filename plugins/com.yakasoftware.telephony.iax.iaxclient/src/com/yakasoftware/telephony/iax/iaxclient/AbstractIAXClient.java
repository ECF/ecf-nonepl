/**
 * 
 */
package com.yakasoftware.telephony.iax.iaxclient;

import java.util.HashSet;
import java.util.Set;

import com.yakasoftware.telephony.iax.iaxclient.event.Callback_Event;
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
 * @author Roland Ndaka Fru
 *
 */
public abstract class AbstractIAXClient {

	static {
		System.loadLibrary("jIAXClient");
	}

	private final Set<IAXClientListener> listeners = new HashSet<IAXClientListener>();

	public void addIAXClientListener(IAXClientListener listener) {
		listeners.add(listener);
	}

	public void removeIAXClientListener(IAXClientListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		} else {
			// listener wasn't registered!
		}
	}

	protected void eventCallback(Callback_Event callbackEvent) {
		if (callbackEvent instanceof Event_Audio) {
			final Event_Audio fired_Event_Audio = (Event_Audio) callbackEvent;
			for (final IAXClientListener listener : listeners) {
				listener.onEvent_Audio(fired_Event_Audio);
			}
		} else if (callbackEvent instanceof Event_State) {
			final Event_State fired_Event_CallState = (Event_State) callbackEvent;
			for (final IAXClientListener listener : listeners) {
				listener.onEvent_CallState(fired_Event_CallState);
			}
		} else if (callbackEvent instanceof Event_Level) {
			final Event_Level fired_Event_Level = (Event_Level) callbackEvent;
			for (final IAXClientListener listener : listeners) {
				listener.onEvent_Level(fired_Event_Level);
			}
		} else if (callbackEvent instanceof Event_NetStats) {
			final Event_NetStats fired_Event_NetStats = (Event_NetStats) callbackEvent;
			for (final IAXClientListener listener : listeners) {
				listener.onEvent_NetStats(fired_Event_NetStats);
			}
		} else if (callbackEvent instanceof Event_Registration) {
			final Event_Registration fired_Event_Registration = (Event_Registration) callbackEvent;
			for (final IAXClientListener listener : listeners) {
				listener.onEvent_Registration(fired_Event_Registration);
			}
		} else if (callbackEvent instanceof Event_Text) {
			final Event_Text fired_Event_Text = (Event_Text) callbackEvent;
			for (final IAXClientListener listener : listeners) {
				listener.onEvent_Text(fired_Event_Text);
			}
		} else if (callbackEvent instanceof Event_URL) {
			final Event_URL fired_Event_URL = (Event_URL) callbackEvent;
			for (final IAXClientListener listener : listeners) {
				listener.onEvent_URL(fired_Event_URL);
			}
		} else if (callbackEvent instanceof Event_Video) {
			final Event_Video fired_Event_Video = (Event_Video) callbackEvent;
			for (final IAXClientListener listener : listeners) {
				listener.onEvent_Video(fired_Event_Video);
			}
		} else if (callbackEvent instanceof Event_VideoStats) {
			final Event_VideoStats fired_Event_VideoStats = (Event_VideoStats) callbackEvent;
			for (final IAXClientListener listener : listeners) {
				listener.onEvent_VideoStats(fired_Event_VideoStats);
			}
		} else {
			// An unknown event Type
		}
	}

	/**
	 * initializes the IAXClient instance which should be a singleton i.e. calling
	 * this method once should be enough!
	 * 
	 * @param num_calls the number of calls that can be handled
	 * @return int
	 */
	public native int initialize(int num_calls);

	public native void shutdown();

	/**
	 * Set Preferred UDP Port:
	 * 0: Use the default port (4569)
	 * <0: Use a dynamically assigned port
	 * >0: Try to bind to the specified port
	 * NOTE: must be called before iaxc_initialize()
	 * 
	 * @param sourceUdpPort the preferred udp port
	 */
	public native void iaxc_set_preferred_source_udp_port(int sourceUdpPort);

	public native short iaxc_get_bind_port();

	public native void iaxc_set_formats(int preferred, int allowed);

	public native void iaxc_set_min_outgoing_framesize(int samples);

	public native void iaxc_set_callerid(final String name, final String number);

	public native int iaxc_start_processing_thread();

	public native int iaxc_stop_processing_thread();

	public native int iaxc_call(final String num);

	public native int iaxc_unregister(int id);

	public native int iaxc_register(final String user, final String pass, final String host);

	public native void iaxc_send_busy_on_incoming_call(int callNo);

	public native void iaxc_answer_call(int callNo);

	public native void iaxc_blind_transfer_call(int callNo, final String number);

	public native void iaxc_dump_all_calls();

	public native void iaxc_dump_call();

	public native void iaxc_reject_call();

	public native void iaxc_reject_call_number(int callNo);

	public native void iaxc_send_dtmf(char digit);

	public native void iaxc_send_text(final String text);

	/** link == 1 ? AST_HTML_LINKURL : AST_HTML_URL 
	 * @param url 
	 * @param link 
	 */
	public native void iaxc_send_url(final String url, int link);

	public native void iaxc_millisleep(long ms);

	public native void iaxc_set_silence_threshold(float thr);

	public native void iaxc_set_audio_output(int mode);

	public native int iaxc_select_call(int callNo);

	public native int iaxc_first_free_call();

	public native int iaxc_selected_call();

	public native int iaxc_quelch(int callNo, int MOH);

	public native int iaxc_unquelch(int call);

	public native int iaxc_mic_boost_get();

	public native int iaxc_mic_boost_set(int enable);

	public native String iaxc_version(final String ver);

	/** Fine tune jitterbuffer control 
	 * @param value */
	public native void iaxc_set_jb_target_extra(long value);

	//	/** application-defined networking; give substiture sendto and recvfrom functions,
	//	 * must be called before iaxc_initialize!
	//	 */
	//	public native void iaxc_set_networking(InetSocketAddress st, InetSocketAddress rf) ;
	//
	//	/** wrapper for libiax2 get_netstats */
	//	public native int iaxc_get_netstats(int call, int rtt, NetStats local, NetStats remote);

	/* Get audio device information:
	 *    **devs: a pointer to an array of device structures, as declared above.  function
	 *    will give you a pointer to the proper array, which will be valid as long as iaxc is
	 *    initialized.
	 *
	 *    *nDevs: a pointer to an int, to which the count of devices in the array devs will be
	 *    written
	 *
	 *    *input, *output, *ring: the currently selected devices for input, output, ring will
	 *    be written to the int pointed to by these pointers.
	 */
	//	public native int iaxc_audio_devices_get(AudioDevice devs, int nDevs, int input, int output, int ring);
	public native int iaxc_audio_devices_set(int input, int output, int ring);

	public native float iaxc_input_level_get();

	public native float iaxc_output_level_get();

	public native int iaxc_input_level_set(float level);

	public native int iaxc_output_level_set(float level);

	/** play a sound.  sound = an iaxc_sound structure, ring: 0: play through output device; 1: play through "ring" device */
	//	public native int iaxc_play_sound(Sound sound, int ring);
	/** stop sound with ID "id" 
	 * @param id 
	 * @return int
	 */
	public native int iaxc_stop_sound(int id);

	public native int iaxc_get_filters();

	public native void iaxc_set_filters(int filters);

	//	public native int iaxc_set_files(File input, File output);

	/** speex specific codec settings
	 * a good choice is (1,-1,-1,0,8000,3): 8kbps ABR
	 * Decode options:
	 *   decode_enhance: 1/0  perceptual enhancement for decoder
	 *   quality: Generally, set either quality (0-9) or bitrate.
	 *      -1 for "default"
	 *   bitrate: in kbps.  Applies to CBR only; -1 for default.
	 *      (overrides "quality" for CBR mode)
	 *   vbr: Variable bitrate mode:  0/1
	 *   abr mode/rate:  0 for not ABR, bitrate for ABR mode
	 *   complexity:  algorithmic complexity.  Think -N for gzip.
	 *      Higher numbers take more CPU for better quality.  3 is
	 *      default and good choice.
	 * @param decode_enhance 
	 * @param quality 
	 * @param bitrate 
	 * @param vbr 
	 * @param abr 
	 * @param complexity 
	 */
	public native void iaxc_set_speex_settings(int decode_enhance, float quality, int bitrate, int vbr, int abr, int complexity);

	/** Get various audio delivery preferences.
	 * Returns 0 on success and -1 on error.
	 * @return 0 on success and -1 on error.
	 */
	public native int iaxc_get_audio_prefs();

	/** Set various audio delivery preferences.
	 * Returns 0 on success and -1 on error.
	 * @param prefs 
	 * @return 0 on success and -1 on error.
	 */
	public native int iaxc_set_audio_prefs(int prefs);

	/**
	 * Get video preferences.
	 *
	 * Please note that this overwrites all previous preferences. In other
	 * words, a read-modify-write must be done to change a single preference.
	 * @return int
	 */
	public native int iaxc_get_video_prefs();

	/**
	 * Get video preferences.
	 *
	 * Please note that this overwrites all previous preferences. In other
	 * words, a read-modify-write must be done to change a single preference.
	 * @param prefs 
	 * @return int
	 */
	public native int iaxc_set_video_prefs(int prefs);

	//	public native int listVidCapDevices(String buff, int buffSize);

	/**
	 * Video format settings
	 * @param preferred 
	 * @param allowed 
	 */
	//	public native void iaxc_video_format_get_cap(int preferred, int allowed);
	//	public native VideoFormat iaxc_video_format_get_cap();
	public native void iaxc_video_format_set_cap(int preferred, int allowed);

	/** set allowed/preferred video encodings 
	 * @param preferred 
	 * @param allowed 
	 * @param framerate 
	 * @param bitrate 
	 * @param width 
	 * @param height 
	 * @param fs */
	public native void iaxc_video_format_set(int preferred, int allowed, int framerate, int bitrate, int width, int height, int fs);

	/**
	 * Change video params for the current call on the fly
	 * This will destroy the existing encoder and create a new one
	 * use negative values for parameters that should not change
	 * @param framerate 
	 * @param bitrate 
	 * @param width 
	 * @param height 
	 * @param fs 
	 */
	public native void iaxc_video_params_change(int framerate, int bitrate, int width, int height, int fs);

	/** Set holding frame to be used in some kind of video calls 
	 * @param jitter 
	 * @return int
	 */
	//	public native int iaxc_set_holding_frame(char frame);
	/* Helper function to control use of jitter buffer for video events */
	/* TODO: make this a video pref, perhaps? */
	public native int iaxc_video_bypass_jitter(int jitter);

	/*
	 * Check if the default camera is working
	 */
	public native int iaxc_is_camera_working();

	public native void iaxc_YUV420_to_RGB32(int width, int height, String src, String dest);

}
