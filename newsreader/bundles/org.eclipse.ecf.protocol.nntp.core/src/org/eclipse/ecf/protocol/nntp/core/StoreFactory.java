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
package org.eclipse.ecf.protocol.nntp.core;

import java.util.HashMap;

import org.eclipse.ecf.protocol.nntp.model.IStore;


public class StoreFactory {

	private static StoreFactory factory;

	private HashMap stores = new HashMap();

	public void addStore(IStore store) {
		stores.put(store.getDescription(), store);
	}

	public IStore[] getStores() {
		return (IStore[]) stores.values().toArray(new IStore[0]);
	}

	public static StoreFactory instance() {
		if (factory == null)
			factory = new StoreFactory();
		return factory;
	}
}
