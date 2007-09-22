/**
 * 
 */
package com.yakasoftware.telephony.iax.iaxclient.util;

/**
 * @author Roland Ndaka Fru
 *
 */
public class VideoFormat {
	
	/** preferred CODECS */
	private int preferred;
	/** allowed CODECS*/
	private int allowed;
	
	/**
	 * @param preferred
	 * @param allowed
	 */
	private VideoFormat(int preferred, int allowed) {
		super();
		this.preferred = preferred;
		this.allowed = allowed;
	}
	
	/**
	 * @return the preferred
	 */
	public int getPreferred() {
		return preferred;
	}
	/**
	 * @return the allowed
	 */
	public int getAllowed() {
		return allowed;
	}
}
