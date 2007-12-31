/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient */

#ifndef _Included_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
#define _Included_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    initialize
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_initialize
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    shutdown
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_shutdown
  (JNIEnv *, jobject);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_set_preferred_source_udp_port
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1set_1preferred_1source_1udp_1port
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_get_bind_port
 * Signature: ()S
 */
JNIEXPORT jshort JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1get_1bind_1port
  (JNIEnv *, jobject);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_set_formats
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1set_1formats
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_set_min_outgoing_framesize
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1set_1min_1outgoing_1framesize
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_set_callerid
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1set_1callerid
  (JNIEnv *, jobject, jstring, jstring);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_start_processing_thread
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1start_1processing_1thread
  (JNIEnv *, jobject);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_stop_processing_thread
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1stop_1processing_1thread
  (JNIEnv *, jobject);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_call
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1call
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_unregister
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1unregister
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_register
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1register
  (JNIEnv *, jobject, jstring, jstring, jstring);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_send_busy_on_incoming_call
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1send_1busy_1on_1incoming_1call
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_answer_call
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1answer_1call
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_blind_transfer_call
 * Signature: (ILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1blind_1transfer_1call
  (JNIEnv *, jobject, jint, jstring);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_dump_all_calls
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1dump_1all_1calls
  (JNIEnv *, jobject);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_dump_call
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1dump_1call
  (JNIEnv *, jobject);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_reject_call
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1reject_1call
  (JNIEnv *, jobject);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_reject_call_number
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1reject_1call_1number
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_send_dtmf
 * Signature: (C)V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1send_1dtmf
  (JNIEnv *, jobject, jchar);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_send_text
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1send_1text
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_send_url
 * Signature: (Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1send_1url
  (JNIEnv *, jobject, jstring, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_millisleep
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1millisleep
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_set_silence_threshold
 * Signature: (F)V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1set_1silence_1threshold
  (JNIEnv *, jobject, jfloat);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_set_audio_output
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1set_1audio_1output
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_select_call
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1select_1call
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_first_free_call
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1first_1free_1call
  (JNIEnv *, jobject);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_selected_call
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1selected_1call
  (JNIEnv *, jobject);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_quelch
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1quelch
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_unquelch
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1unquelch
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_mic_boost_get
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1mic_1boost_1get
  (JNIEnv *, jobject);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_mic_boost_set
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1mic_1boost_1set
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_version
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1version
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_set_jb_target_extra
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1set_1jb_1target_1extra
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_audio_devices_set
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1audio_1devices_1set
  (JNIEnv *, jobject, jint, jint, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_input_level_get
 * Signature: ()F
 */
JNIEXPORT jfloat JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1input_1level_1get
  (JNIEnv *, jobject);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_output_level_get
 * Signature: ()F
 */
JNIEXPORT jfloat JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1output_1level_1get
  (JNIEnv *, jobject);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_input_level_set
 * Signature: (F)I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1input_1level_1set
  (JNIEnv *, jobject, jfloat);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_output_level_set
 * Signature: (F)I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1output_1level_1set
  (JNIEnv *, jobject, jfloat);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_stop_sound
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1stop_1sound
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_get_filters
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1get_1filters
  (JNIEnv *, jobject);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_set_filters
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1set_1filters
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_set_speex_settings
 * Signature: (IFIIII)V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1set_1speex_1settings
  (JNIEnv *, jobject, jint, jfloat, jint, jint, jint, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_get_audio_prefs
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1get_1audio_1prefs
  (JNIEnv *, jobject);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_set_audio_prefs
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1set_1audio_1prefs
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_get_video_prefs
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1get_1video_1prefs
  (JNIEnv *, jobject);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_set_video_prefs
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1set_1video_1prefs
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_video_format_set_cap
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1video_1format_1set_1cap
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_video_format_set
 * Signature: (IIIIIII)V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1video_1format_1set
  (JNIEnv *, jobject, jint, jint, jint, jint, jint, jint, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_video_params_change
 * Signature: (IIIII)V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1video_1params_1change
  (JNIEnv *, jobject, jint, jint, jint, jint, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_video_bypass_jitter
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1video_1bypass_1jitter
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_is_camera_working
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1is_1camera_1working
  (JNIEnv *, jobject);

/*
 * Class:     com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient
 * Method:    iaxc_YUV420_to_RGB32
 * Signature: (IILjava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_yakasoftware_telephony_iax_iaxclient_AbstractIAXClient_iaxc_1YUV420_1to_1RGB32
  (JNIEnv *, jobject, jint, jint, jstring, jstring);

#ifdef __cplusplus
}
#endif
#endif
