package org.eclipse.ecf.protocol.nntp.store.filesystem;

import org.eclipse.ecf.protocol.nntp.model.IStore;
import org.eclipse.ecf.protocol.nntp.store.filesystem.internal.Store;

public class StoreFactory {

	public static IStore createStore(String root) {
		return new Store(root);
	}
}
