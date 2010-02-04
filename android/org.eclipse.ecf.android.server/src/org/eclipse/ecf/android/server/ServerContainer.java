
package org.eclipse.ecf.android.server;




import org.eclipse.ecf.android.server.IRemoteECFService;
import org.eclipse.ecf.android.server.IRemoteECFServiceCallback;
import org.eclipse.ecf.android.server.R;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ServerContainer extends Activity {

	protected static final String ECFTCP_URI = "ecftcp://localhost:3282/soladhoc";
	private static final String ECFTCP_INTENT="org.eclipse.ecf.android.server.REMOTE_SERVICE_SERVER";
	
	protected static final int BUMP_MSG = 0;

	NotificationManager nMgr;

	private Intent ecfIntent;

	private Button mStartServiceButton;

	private Button mStartContainerButton;

	private Button mBindButton;

	/**
	 * Standard initialization of this activity. Set up the UI, then wait for
	 * the user to poke it before doing anything.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		////////////////////////////////////////////
		ecfIntent = new Intent(ECFTCP_INTENT);
		ecfIntent.setData( Uri.parse(ECFTCP_URI));
		ecfIntent.setFlags(0);
		///////////////
		
		nMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		setContentView(R.layout.remote_service_binding);

		// Watch for button clicks.
		mStartServiceButton = (Button) findViewById(R.id.start);
		mStartServiceButton.setEnabled(false);
		mStartServiceButton.setOnClickListener(mStartService);

		mStartContainerButton = (Button) findViewById(R.id.connect);
		mStartContainerButton.setOnClickListener(mStartContainer);
		
		mBindButton = (Button) findViewById(R.id.bind_service);
		mBindButton.setOnClickListener(mBindListener);

        mCallbackText = (TextView)findViewById(R.id.callback);
        mCallbackText.setText("Not attached.");
	}

	private OnClickListener mStartService = new OnClickListener() {

		@Override
		public void onClick(View v) {
			startService(ecfIntent);
		}
	};
	
	private OnClickListener mStartContainer = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			try {
				Log.i("ServerContainer", "start TCP server container");
				mService.start();
			} catch (RemoteException e) {
				// TODO 
				e.printStackTrace();
			}
		}
	};

	private OnClickListener mBindListener = new OnClickListener() {
	    public void onClick(View v) {
	        bindService(new Intent(ecfIntent), mConnection, Context.BIND_AUTO_CREATE);
	        mIsBound = true;
	        mCallbackText.setText("Binding.");
	    }
	};

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className,
	            IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  We are communicating with our
	        // service through an IDL interface, so get a client-side
	        // representation of that from the raw service object.
	        mService = IRemoteECFService.Stub.asInterface(service);
	        mStartServiceButton.setEnabled(true);
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
	        Toast.makeText(ServerContainer.this, R.string.remote_service_connected,
	                Toast.LENGTH_SHORT).show();
	    }
	
	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        mService = null;
	        mStartServiceButton.setEnabled(false);
	        mCallbackText.setText("Disconnected.");
	
	        // As part of the sample, tell the user what happened.
	        Toast.makeText(ServerContainer.this, R.string.remote_service_disconnected,
	                Toast.LENGTH_SHORT).show();
	    }
	};

	/** The primary interface we will be calling on the service. */
	IRemoteECFService mService = null;

	TextView mCallbackText;

	private boolean mIsBound;

	Button mKillButton;

	// ----------------------------------------------------------------------
	// Code showing how to deal with callbacks.
	// ----------------------------------------------------------------------
	
	/**
	 * This implementation is used to receive callbacks from the remote
	 * service.
	 */
	private IRemoteECFServiceCallback mCallback = new IRemoteECFServiceCallback.Stub() {
	    /**
	     * This is called by the remote service regularly to tell us about
	     * new values.  Note that IPC calls are dispatched through a thread
	     * pool running in each process, so the code executing here will
	     * NOT be running in our main thread like most other things -- so,
	     * to update the UI, we need to use a Handler to hop over there.
	     */
	    public void valueChanged(int value) {
	        mHandler.sendMessage(mHandler.obtainMessage(BUMP_MSG, value, 0));
	    }

		@Override
		public void clientConnected(String client) throws RemoteException {
			Log.i("ServerContainer", "client connected");
			mHandler.sendMessage(mHandler.obtainMessage(BUMP_MSG, 0, 0));
		}

		@Override
		public void containerStarted() throws RemoteException {
			Log.i("ServerContainer", "container started");
		}
	};

	private Handler mHandler = new Handler() {
	    @Override public void handleMessage(Message msg) {
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
