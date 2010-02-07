package com.example.android.genericclient;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainer;
import org.eclipse.ecf.provider.generic.SOContainerConfig;
import org.eclipse.ecf.provider.generic.TCPClientSOContainer;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;

public class SharedNotepadService extends Service implements ISharedNotepadService {

	private HashMap<ID,ISharedObjectContainer> clients = new HashMap<ID,ISharedObjectContainer>();
	
	private static final String NOTEPAD_SHARED_OBJECT_NAME = "com.composent.genericclient.notepad.sharedobject";
	private static final ID NOTEPAD_SHARED_OBJECT_ID = IDFactory.getDefault().createStringID(NOTEPAD_SHARED_OBJECT_NAME);
	
	private IBinder binder;
	
	public void onCreate() {
		super.onCreate();
		binder = new SharedNotepadBinder(this);
	}

	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	public IBinder onBind(Intent intent) {
		ComponentName componentName = intent.getComponent();
		if (componentName.getClassName().equals(this.getClass().getName())) {
			return binder;
		}
		return null;
	}

	public boolean onUnbind(Intent intent) {
		
		return super.onUnbind(intent);
	}
	
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}
	
	public void onDestroy() {
		super.onDestroy();
		disposeClients();
	}

	private void disposeClients() {
		synchronized (clients) {
			for(Iterator<ID> i=clients.keySet().iterator(); i.hasNext(); ) {
				ISharedObjectContainer container = removeClient((ID) i.next());
				if (container != null) {
					container.dispose();
				}
			}
			clients.clear();
		}
	}
	
	protected void addClient(ISharedObjectContainer clientContainer) {
		clients.put(clientContainer.getID(), clientContainer);
	}
	
	protected ISharedObjectContainer getClientContainer(ID clientID) {
		return (ISharedObjectContainer) clients.get(clientID);
	}
	
	protected ISharedObjectContainer removeClient(ID clientID) {
		return (ISharedObjectContainer) clients.remove(clientID);
	}
	
	public ISharedNotepadClient getClient(ID clientID) {
		ISharedObjectContainer container = getClientContainer(clientID);
		if (container == null) return null;
		return getNotepadSharedObject(container);
	}
	
	public ISharedNotepadClient createAndConnectClient(String targetId, String username, String originalLocalContent, ISharedNotepadListener listener) {
		ID clientID = IDFactory.getDefault().createGUID();
		ISharedObjectContainer container = getClientContainer(clientID);
		if (container == null) {
			container = createContainer(clientID, username, originalLocalContent, listener);
		}
		ID connectedID = container.getConnectedID();
		if (connectedID == null) {
			connectContainer(container, targetId);
		}
		return getNotepadSharedObject(container);
	}

	private void connectContainer(ISharedObjectContainer container,
			String targetId) {
		try {
			container.connect(IDFactory.getDefault().createStringID(targetId), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ISharedNotepadClient getNotepadSharedObject(ISharedObjectContainer container) {
		return (ISharedNotepadClient) container.getSharedObjectManager().getSharedObject(NOTEPAD_SHARED_OBJECT_ID);
	}
	
	private ISharedObjectContainer createContainer(ID clientID, String username, String originalLocalContent, ISharedNotepadListener listener) {
		TCPClientSOContainer container = new TCPClientSOContainer(new SOContainerConfig(clientID));
		try {
			container.getSharedObjectManager().addSharedObject(NOTEPAD_SHARED_OBJECT_ID, new NotepadSharedObject(username, originalLocalContent, listener), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return container;
	}
}
