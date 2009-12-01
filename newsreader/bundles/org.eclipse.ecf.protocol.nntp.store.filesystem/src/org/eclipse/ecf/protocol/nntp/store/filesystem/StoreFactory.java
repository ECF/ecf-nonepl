package org.eclipse.ecf.protocol.nntp.store.filesystem;

import org.eclipse.ecf.protocol.nntp.model.IStore;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.eclipse.ecf.protocol.nntp.store.filesystem.internal.Store;

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
	 */
	public static IStore createStore(String root) {
		return new Store(root);
	}
}
