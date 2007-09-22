/**
 * 
 */
package com.yakasoftware.telephony.iax.iaxclient.util;

/**
 * Video Statistics
 * 
 * @author Roland Ndaka Fru
 *
 */
public class VideoStats {
	
	/** Number of received slices */
	private long received_slices;
	/** Accumulated size of inbound slices */
	private long acc_recv_size;
    /** Number of sent slices */
	private long sent_slices;
    /** Accumulated size of outbound slices */
	private long acc_sent_size;
	/** Number of frames dropped by the codec (incomplete frames */
	private long dropped_frames;
	/** Number of frames decoded by the codec (complete frames) */	
	private long inbound_frames;
	/** Number of frames sent to the encoder */	
	private long outbound_frames;
	/** Average fps of inbound complete frames */
	private float avg_inbound_fps;
	/** Average inbound bitrate */	
	private long avg_inbound_bps;
	/** Average fps of outbound frames */	
	private float avg_outbound_fps;
	/** Average outbound bitrate */	
	private long avg_outbound_bps;
    /** Timestamp of the moment we started measuring */
	private long start_time;
	
	/**
	 * @param received_slices
	 * @param acc_recv_size
	 * @param sent_slices
	 * @param acc_sent_size
	 * @param dropped_frames
	 * @param inbound_frames
	 * @param outbound_frames
	 * @param avg_inbound_fps
	 * @param avg_inbound_bps
	 * @param avg_outbound_fps
	 * @param avg_outbound_bps
	 * @param start_time
	 */
	public VideoStats(long received_slices, long acc_recv_size,
			long sent_slices, long acc_sent_size, long dropped_frames,
			long inbound_frames, long outbound_frames, float avg_inbound_fps,
			long avg_inbound_bps, float avg_outbound_fps,
			long avg_outbound_bps, long start_time) {
		super();
		this.received_slices = received_slices;
		this.acc_recv_size = acc_recv_size;
		this.sent_slices = sent_slices;
		this.acc_sent_size = acc_sent_size;
		this.dropped_frames = dropped_frames;
		this.inbound_frames = inbound_frames;
		this.outbound_frames = outbound_frames;
		this.avg_inbound_fps = avg_inbound_fps;
		this.avg_inbound_bps = avg_inbound_bps;
		this.avg_outbound_fps = avg_outbound_fps;
		this.avg_outbound_bps = avg_outbound_bps;
		this.start_time = start_time;
	}

	/**
	 * @return the received_slices
	 */
	public long getReceived_slices() {
		return received_slices;
	}

	/**
	 * @return the acc_recv_size
	 */
	public long getAcc_recv_size() {
		return acc_recv_size;
	}

	/**
	 * @return the sent_slices
	 */
	public long getSent_slices() {
		return sent_slices;
	}

	/**
	 * @return the acc_sent_size
	 */
	public long getAcc_sent_size() {
		return acc_sent_size;
	}

	/**
	 * @return the dropped_frames
	 */
	public long getDropped_frames() {
		return dropped_frames;
	}

	/**
	 * @return the inbound_frames
	 */
	public long getInbound_frames() {
		return inbound_frames;
	}

	/**
	 * @return the outbound_frames
	 */
	public long getOutbound_frames() {
		return outbound_frames;
	}

	/**
	 * @return the avg_inbound_fps
	 */
	public float getAvg_inbound_fps() {
		return avg_inbound_fps;
	}

	/**
	 * @return the avg_inbound_bps
	 */
	public long getAvg_inbound_bps() {
		return avg_inbound_bps;
	}

	/**
	 * @return the avg_outbound_fps
	 */
	public float getAvg_outbound_fps() {
		return avg_outbound_fps;
	}

	/**
	 * @return the avg_outbound_bps
	 */
	public long getAvg_outbound_bps() {
		return avg_outbound_bps;
	}

	/**
	 * @return the start_time
	 */
	public long getStart_time() {
		return start_time;
	}
	
	
	
}
