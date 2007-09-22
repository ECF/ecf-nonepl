/**
 * 
 */
package com.yakasoftware.telephony.iax.iaxclient.event;

/**
 * A Level event which will normally be generated at the native layer and propagated to java
 * 
 * @author Roland Ndaka Fru
 * 
 *
 */
public class Event_Level extends Callback_Event {
	
	private float input;
	private float output;
	
	/**
	 * @param input
	 * @param output
	 */
	public Event_Level(float input, float output) {
		super();
		this.input = input;
		this.output = output;
	}
	
	/**
	 * @return the input
	 */
	public float getInput() {
		return input;
	}

	/**
	 * @return the output
	 */
	public float getOutput() {
		return output;
	}

}
