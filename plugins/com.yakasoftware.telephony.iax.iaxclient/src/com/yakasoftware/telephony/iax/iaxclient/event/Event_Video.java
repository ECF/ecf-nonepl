/**
 * 
 */
package com.yakasoftware.telephony.iax.iaxclient.event;

/**
 * A Video event which will normally be generated at the native layer and propagated to java
 * 
 * @author Roland Ndaka Fru
 * 
 *
 */
public class Event_Video extends Callback_Event {
	
	private int callNo;
	private int ts;
	private int format;
	private int width;
	private int height;
	private int encoded;
	private int source;
	private int size;
	private String data;
	
	/**
	 * @param callNo
	 * @param ts
	 * @param format
	 * @param width
	 * @param height
	 * @param encoded
	 * @param source
	 * @param size
	 * @param data
	 */
	public Event_Video(int callNo, int ts, int format, int width, int height,
			int encoded, int source, int size, String data) {
		super();
		this.callNo = callNo;
		this.ts = ts;
		this.format = format;
		this.width = width;
		this.height = height;
		this.encoded = encoded;
		this.source = source;
		this.size = size;
		this.data = data;
	}

	/**
	 * @return the callNo
	 */
	public int getCallNo() {
		return callNo;
	}

	/**
	 * @return the ts
	 */
	public int getTs() {
		return ts;
	}

	/**
	 * @return the format
	 */
	public int getFormat() {
		return format;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the encoded
	 */
	public int getEncoded() {
		return encoded;
	}

	/**
	 * @return the source
	 */
	public int getSource() {
		return source;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}
	
	
}
