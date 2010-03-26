package com.example.android.notepad.sharednotepadclient;

import java.io.IOException;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.BaseSharedObject;
import org.eclipse.ecf.core.sharedobject.SharedObjectInitException;
import org.eclipse.ecf.core.sharedobject.SharedObjectMsg;
import org.eclipse.ecf.core.sharedobject.events.ISharedObjectActivatedEvent;
import org.eclipse.ecf.core.util.Event;
import org.eclipse.ecf.core.util.IEventProcessor;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class NotepadSharedObject extends BaseSharedObject {

	private static final String HANDLE_UPDATE_MSG = "handleUpdateMsg";
	private static final String HANDLE_LOCATION_MSG = "handleLocationMsg";

	private ISharedNotepadListener listener;

	private String username;
	private String localOriginalContent;

	private LocationManager locationManager;
	private LocationListener locationListener = new LocationListener() {

		public void onLocationChanged(Location location) {
			sendLocationChanged(location);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

	};

	public NotepadSharedObject(String username, String localOriginalContent,
			ISharedNotepadListener listener, LocationManager locationManager) {
		this.username = username;
		this.localOriginalContent = localOriginalContent;
		this.listener = listener;
		this.locationManager = locationManager;
		this.locationManager.requestLocationUpdates("gps", 0, 0,
				locationListener);
	}

	protected void initialize() throws SharedObjectInitException {
		super.initialize();
		addEventProcessor(new IEventProcessor() {
			public boolean processEvent(Event event) {
				if (event instanceof ISharedObjectActivatedEvent) {
					sendLocationChanged(locationManager.getLastKnownLocation("gps"));
				}
				return false;
			}});
	}
	
	public void dispose(ID containerID) {
		super.dispose(containerID);
		if (this.locationManager != null) {
			this.locationManager.removeUpdates(locationListener);
			this.locationManager = null;
		}
	}

	protected void sendLocationChanged(Location location) {
		System.out.println("onLocationChanged location=" + location);
		if (location == null) location = new Location("gps");
		try {
			sendSharedObjectMsgTo(null, SharedObjectMsg.createMsg(
					HANDLE_LOCATION_MSG, new Object[] { getLocalContainerID(),
							username, new Double(location.getLatitude()),
							new Double(location.getLongitude()),
							new Double(location.getAltitude()) }));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected boolean handleSharedObjectMsg(SharedObjectMsg msg) {
		try {
			msg.invoke(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getUsername() {
		return username;
	}

	public String getLocalOriginalContent() {
		return localOriginalContent;
	}

	public ISharedNotepadListener getSharedNotepadListener() {
		return listener;
	}

	public ID getConnectedID() {
		return super.getConnectedID();
	}

	public ID getClientID() {
		return getLocalContainerID();
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	// Message sender
	public void sendUpdate(String content) throws IOException {
		sendSharedObjectMsgTo(null, SharedObjectMsg.createMsg(
				HANDLE_UPDATE_MSG, new Object[] { getLocalContainerID(),
						username, content }));
	}

	// Message receiver
	protected void handleUpdateMsg(ID senderID, String username, String content) {
		if (listener != null) {
			listener.receiveUpdate(senderID, username, content);
		}
	}

	protected void handleLocationMsg(ID senderID, String username,
			Double latitude, Double longitude, Double altitude) {
		System.out.println("handleLocationMsg senderID=" + senderID
				+ " username=" + username + " lat=" + latitude + " lon="
				+ longitude + " alt=" + altitude);
	}
}
