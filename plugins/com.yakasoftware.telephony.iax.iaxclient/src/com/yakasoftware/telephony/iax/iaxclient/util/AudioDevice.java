/**
 * 
 */
package com.yakasoftware.telephony.iax.iaxclient.util;


/**
 * Audio Device
 * 
 * @author Roland Ndaka Fru
 *
 */
public class AudioDevice {
	
    /** name of the device */	
	private String name;
    /** flags, defined above */	
	private long capabilities;
    /** driver-specific ID */	
	private int devID;
	
	/**
	 * @param name name of the device 
	 * @param capabilities flags, defined in com.bitsvalley.telephony.iax.iaxclient.constants.Audio 
	 * @param devID driver-specific ID
	 */
	public AudioDevice(String name, long capabilities, int devID) {
		super();
		this.name = name;
		this.capabilities = capabilities;
		this.devID = devID;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the capabilities
	 */
	public long getCapabilities() {
		return capabilities;
	}

	/**
	 * @return the devID
	 */
	public int getDevID() {
		return devID;
	}
	
	
}
