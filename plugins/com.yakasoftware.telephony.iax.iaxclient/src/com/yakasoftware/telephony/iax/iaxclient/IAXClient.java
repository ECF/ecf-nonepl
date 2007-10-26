/**
 * 
 */
package com.yakasoftware.telephony.iax.iaxclient;

/**
 * @author Roland Ndaka Fru
 *
 */
public class IAXClient extends AbstractIAXClient {

	private static IAXClient theOnlyInstance = null;

	/**
	 * 
	 */
	private IAXClient() {
		// Private constructor for Singleton support!
	}

	/**
	 * @param initialize true if this IAXClient be initialized, otherwise false!
	 * @param numOfCalls 
	 * @return an IAXClient singleton. Note: You can only have one IAXClient per Java Virtual Machine
	 */
	public static IAXClient getIAXClient(boolean initialize, int numOfCalls) {
		if (IAXClient.theOnlyInstance == null) {
			IAXClient.theOnlyInstance = new IAXClient();
			if (initialize) {
				System.loadLibrary("jIAXClient"); // Loading again causes no harm				
				IAXClient.theOnlyInstance.initialize(numOfCalls);
			}
		} else {
			// Instance already exists and will be returned!
		}

		return IAXClient.theOnlyInstance;

	}
}
