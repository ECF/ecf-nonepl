/*******************************************************************************
 * Copyright (c) 2009 Weltevree Beheer BV, Nederland (34187613)                   
 *                                                                      
 * All rights reserved. This program and the accompanying materials     
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at             
 * http://www.eclipse.org/legal/epl-v10.html                            
 *                                                                      
 * Contributors:                                                        
 *    Wim Jongman - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.protocol.nntp.model;

public class StoreEvent implements IStoreEvent {

	private final Object object;
	private final int eventType;

	public StoreEvent(Object object, int eventType) {
		this.object = object;
		this.eventType = eventType;
	}

	public Object getEventObject() {
		return object;
	}

	public int getEventType() {
		return eventType;
	}

	public String toString() {
		return object.toString().concat(eventType + "");
	}

	public boolean equals(Object obj) {
		return this.toString().equals(obj.toString());
	}

	public int hashCode() {
		return toString().hashCode();
	}
}
