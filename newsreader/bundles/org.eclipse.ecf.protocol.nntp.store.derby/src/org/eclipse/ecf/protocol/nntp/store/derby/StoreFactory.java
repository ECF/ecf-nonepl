/*******************************************************************************
 *  Copyright (c) 2010 Weltevree Beheer BV, Nederland
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     Wim Jongman - initial API and implementation
 *
 *
 *******************************************************************************/
package org.eclipse.ecf.protocol.nntp.store.derby;

import org.eclipse.ecf.protocol.nntp.model.IStore;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.eclipse.ecf.protocol.nntp.model.StoreException;
import org.eclipse.ecf.protocol.nntp.store.derby.internal.Store;

/**
 * The store factory will create {@link IStore} implementations.
 * 
 * @author Wim Jongman
 * 
 */
public class StoreFactory {

	/**
	 * This will create a store that will persist information on the specified
	 * root in the {@link SALVO#SALVO_HOME} directory.
	 * 
	 * @param root
	 * @return
	 * @throws StoreException
	 */
	public static IStore createStore(String root) throws StoreException {
		return new Store(root);
	}
}
