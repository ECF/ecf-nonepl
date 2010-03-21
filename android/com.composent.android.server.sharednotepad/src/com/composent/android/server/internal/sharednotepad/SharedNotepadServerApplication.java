package com.composent.android.server.internal.sharednotepad;

import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainer;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class SharedNotepadServerApplication implements IApplication {

	private final ID notepadSharedObjectID = IDFactory.getDefault().createStringID("com.composent.android.sharednotepad.sharedobject");
	
	private String serverContainerId = "ecftcp://10.0.2.2:3282/server";
	private ISharedObjectContainer container;
	private final Object appLock = new Object();
	private boolean done = false;

	public Object start(IApplicationContext context) throws Exception {
		IContainerManager containerManager = Activator.getDefault().getContainerManager();
		container = (ISharedObjectContainer) containerManager.getContainerFactory().createContainer("ecf.generic.server", serverContainerId);
		// add shared object with name that is constant
		container.getSharedObjectManager().addSharedObject(notepadSharedObjectID, new ServerNotepadSharedObject(), null);
		
		System.out.println("SharedNotepad server id="+serverContainerId+" listening for client connections ");
		// wait until stopped
		waitForDone();

		return IApplication.EXIT_OK;
	}

	public void stop() {
		if (container != null) {
			container.dispose();
			container = null;
		}
		synchronized (appLock) {
			done = true;
			notifyAll();
		}
	}

	private void waitForDone() {
		// then just wait here
		synchronized (appLock) {
			while (!done) {
				try {
					appLock.wait();
				} catch (InterruptedException e) {
					// do nothing
				}
			}
		}
	}

}
