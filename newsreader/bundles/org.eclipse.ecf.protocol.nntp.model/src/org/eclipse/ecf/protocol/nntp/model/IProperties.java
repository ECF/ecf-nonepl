package org.eclipse.ecf.protocol.nntp.model;

import java.util.Map;

public interface IProperties {
	
	/**
	 * Sets an arbitrary property, an already existing property will be replaced
	 * and a null for the value removes the property.
	 * 
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, String value);

	/**
	 * Gets a previously set property.
	 * 
	 * @param key
	 * @return null if the property key does not exist
	 */
	public String getProperty(String key);

	/**
	 * @return all the specified properties in a map.
	 */
	public Map getProperties();


}
