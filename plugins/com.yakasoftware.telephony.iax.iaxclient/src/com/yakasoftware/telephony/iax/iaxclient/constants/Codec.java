package com.yakasoftware.telephony.iax.iaxclient.constants;


/**
 * @author Roland Ndaka Fru
 *
 */
public interface Codec {
	
	// Audio Codecs...	
    /*** G.723.1 compression */
    public static final int FORMAT_G723_1    = 1 << 0;
    /*** GSM compression */
    public static final int FORMAT_GSM       = 1 << 1;
    /** Raw mu-law data (G.711) */
    public static final int FORMAT_ULAW      = 1 << 2;
    /** Raw A-law data (G.711) */
    public static final int FORMAT_ALAW      = 1 << 3;
    /** ADPCM, 32kbps  */
    public static final int FORMAT_G726      = 1 << 4;
    /** ADPCM IMA */
    public static final int FORMAT_ADPCM     = 1 << 5;
    /** Raw 16-bit Signed Linear (8000 Hz) PCM */
    public static final int FORMAT_SLINEAR   = 1 << 6;
    /** LPC10, 180 samples/frame */
    public static final int FORMAT_LPC10     = 1 << 7;
    /** G.729a Audio */
    public static final int FORMAT_G729A     = 1 << 8;
    /** Speex Audio */
    public static final int FORMAT_SPEEX     = 1 << 9;
    /** iLBC Audio */
    public static final int FORMAT_ILBC      = 1 << 10;
    
    /** Maximum audio format */
    public static final int FORMAT_MAX_AUDIO = 1 << 15;
    
    // Image Codecs...
    
    /** JPEG Images */
    public static final int FORMAT_JPEG      = 1 << 16;
    /** PNG Images */
    public static final int FORMAT_PNG       = 1 << 17;
    
    // Video Codecs...
    
    /** H.261 Video */
    public static final int FORMAT_H261      = 1 << 18;
    /** H.263 Video */
    public static final int FORMAT_H263      = 1 << 19;
    /** H.263+ Video */
    public static final int FORMAT_H263_PLUS = 1 << 20;
    /** MPEG4 Video */
    public static final int FORMAT_MPEG4     = 1 << 21;
    /** H264 Video */
    public static final int FORMAT_H264      = 1 << 23;
    /** Theora Video */
    public static final int FORMAT_THEORA    = 1 << 24;	
	
}
