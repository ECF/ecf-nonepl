package org.eclipse.ecf.android.server;

import org.eclipse.ecf.android.ECFException;
import org.eclipse.ecf.android.IDCreateException;

import org.eclipse.ecf.android.server.IRemoteECFService;
import org.eclipse.ecf.android.server.IRemoteECFServiceCallback;
import org.eclipse.ecf.android.server.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ServerConnection extends Activity {

	private static final String TAG="ServerConnection";
	protected static final int STARTED_MSG = 0;
	private IRemoteECFService mService = null;
	Button mKillButton;
	TextView mCallbackText;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			this.mCallbackText.setText("Binding to ecf container service");
		} catch (ECFException e) {
			Log.e("connect", e.getMessage(), e);
		} catch (IDCreateException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service. We are communicating with our
			// service through an IDL interface, so get a client-side
			// representation of that from the raw service object.
			mService = IRemoteECFService.Stub.asInterface(service);
			mKillButton.setEnabled(true);
			mCallbackText.setText("Attached.");

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

			// As part of the sample, tell the user what happened.
			Toast.makeText(ServerConnection.this,
					R.string.remote_service_connected, Toast.LENGTH_SHORT)
					.show();
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			mService = null;
			mKillButton.setEnabled(false);
			mCallbackText.setText("Disconnected.");

			// As part of the sample, tell the user what happened.
			Toast.makeText(ServerConnection.this,
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
			mHandler.sendMessage(mHandler.obtainMessage(BUMP_MSG, client));
		}

		@Override
		public void containerStarted() throws RemoteException {
			mHandler.sendMessage(mHandler.obtainMessage(STARTED_MSG, null));// TODO
		}
	};

	private static final int BUMP_MSG = 1;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BUMP_MSG:
				mCallbackText.setText("Received from service: " + msg.arg1);
				break;
			default:
				super.handleMessage(msg);
			}
		}

	};

}
