package org.eclipse.ecf.android.server;

import java.io.IOException;

import org.eclipse.ecf.android.Connector;
import org.eclipse.ecf.android.ID;
import org.eclipse.ecf.android.IDFactory;
import org.eclipse.ecf.android.NamedGroup;
import org.eclipse.ecf.android.SOContainerConfig;
import org.eclipse.ecf.android.TCPServerSOContainer;
import org.eclipse.ecf.android.TCPServerSOContainerGroup;

import org.eclipse.ecf.android.server.IRemoteECFService;
import org.eclipse.ecf.android.server.IRemoteECFServiceCallback;
import org.eclipse.ecf.android.server.R;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class SOService extends Service {

	private static final String TAG="SOSService";
	protected static final int BUMP_MSG = 0;
	protected static final int STARTED_MSG = 1;
	protected 	Uri data;
	private 	TCPServerSOContainer server;
	private 	ID serverID;
	protected 	TCPServerSOContainerGroup serverGroup;
	protected 	Connector connector;
	private Intent intent;
	
	
	/**
	 * This is a list of callbacks that have been registered with the
	 * service.  Note that this is package scoped (instead of private) so
	 * that it can be accessed more efficiently from inner classes.
	 */
	final RemoteCallbackList<IRemoteECFServiceCallback> mCallbacks
	        = new RemoteCallbackList<IRemoteECFServiceCallback>();
	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
	
		private IRemoteECFService mService;
	
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service. We are communicating with our
			// service through an IDL interface, so get a client-side
			// representation of that from the raw service object.
			mService = IRemoteECFService.Stub.asInterface(service);
	
			// We want to monitor the service for as long as we are
			// connected to it.
			try {
				mService.registerCallback(mCallback);
			} catch (RemoteException e) {
				// In this case the service has crashed before we could even
				// do anything with it; we can count on soon being
				// disconnected (and then reconnected if it can be restarted)
				// so there is no need to do anything here.
			}
	
			Log.i(TAG, "remote binded");
			// As part of the sample, tell the user what happened.
			Toast.makeText(getApplicationContext(),
					R.string.remote_service_connected, Toast.LENGTH_SHORT)
					.show();
		}
	
		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			mService = null;
			Log.i(TAG, "remote unbound");
			// As part of the sample, tell the user what happened.
			Toast.makeText(getApplicationContext(),
					R.string.remote_service_disconnected, Toast.LENGTH_SHORT)
					.show();
		}
	};
	/**
	 * This implementation is used to receive callbacks from the remote service.
	 */
	private IRemoteECFServiceCallback mCallback = new IRemoteECFServiceCallback.Stub() {
		/**
		 * Note that IPC calls are dispatched through a thread pool
		 * running in each process, so the code executing here will NOT be
		 * running in our main thread like most other things -- so, to update
		 * the UI, we need to use a Handler to hop over there.
		 */
		@Override
		public void clientConnected(String client) throws RemoteException {
			Log.i(TAG, "ecf client connected");
			Toast.makeText(getApplicationContext(),
					R.string.ecf_client_connected, Toast.LENGTH_SHORT)
					.show();
		}
	
		@Override
		public void containerStarted() throws RemoteException {
			Log.i(TAG, "ecf container started");
			Toast.makeText(getApplicationContext(),
					R.string.ecf_container_started, Toast.LENGTH_SHORT)
					.show();
		}
	};
	/**
	 * The IRemoteInterface is defined through IDL
	 */
	private final IRemoteECFService.Stub mBinder = new IRemoteECFService.Stub() {
	    public void registerCallback(IRemoteECFServiceCallback cb) {
	        if (cb != null) mCallbacks.register(cb);
	    }
	    public void unregisterCallback(IRemoteECFServiceCallback cb) {
	        if (cb != null) mCallbacks.unregister(cb);
	    }
		@Override
		public void connect() throws RemoteException {
			Log.i(TAG, "Cant't connect server to himself");
		}
		@Override
		public boolean start() throws RemoteException {
			data = intent.getData();
			Log.i(TAG, "intent data="+data);
			if ("org.eclipse.ecf.android.server.REMOTE_SERVICE_SERVER".equals(intent.getAction())) {
				// create ECF TCP Server
				connector = new Connector(data.getAuthority(), data.getHost(), data
						.getPort(), 5000);

				serverGroup = new TCPServerSOContainerGroup(
						connector.getHostname(), Thread.currentThread()
								.getThreadGroup(), connector.getPort());

				NamedGroup namedGroup = new NamedGroup(data.getEncodedPath());
				namedGroup.setParent(connector);
				
				serverID = IDFactory.getDefault().createStringID(data.toString());

				SOContainerConfig config = new SOContainerConfig(serverID);

				server = new TCPServerSOContainer(config, serverGroup, data
						.getEncodedPath(), 5000);
				
				try {
					serverGroup.putOnTheAir();
				} catch (IOException e) {
					return false;
				}
				Log.i(TAG, "Putting server "
						+ connector.getHostname() + " on the air");
				return true;
			}
			return false;
		}
	};
	@Override
	public IBinder onBind(Intent intent) {
		Log.i("SOService", "Binding sos");
		this.intent = intent;
		return this.mBinder;
	}
}