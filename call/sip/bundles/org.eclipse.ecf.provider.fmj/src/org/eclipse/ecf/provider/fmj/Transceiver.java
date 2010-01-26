/***********************************************************************************
 * Copyright (c) 2009 Harshana Eranga Martin and others. All rights reserved. This 
 * program and the accompanying materials are made available under the terms of 
 * the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Harshana Eranga Martin <harshana05@gmail.com> - initial API and implementation
************************************************************************************/
package org.eclipse.ecf.provider.fmj;

import java.awt.Dimension;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Vector;
import javax.media.*;
import javax.media.control.QualityControl;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.*;
import javax.media.rtp.*;
import javax.media.rtp.event.*;

public class Transceiver implements ReceiveStreamListener, SessionListener, RemoteListener {

	RTPManager[] managers;
	RTPManager manager;
	String remoteAudioAddress;
	int remoteAudioPort;
	int localAudioPort = 6022;
	MediaLocator locator;
	private Integer stateLock = new Integer(0);
	private boolean failed = false;
	Processor processor;
	DataSource dataOutput;

	Transmitter transmitter;
	Receiver receiver;

	/**
	 * 
	 */
	public Transceiver(String remoteAddress, String remotePort) {
		remoteAudioAddress = remoteAddress;
		remoteAudioPort = Integer.parseInt(remotePort);

		//Added to fix bug reported by Mr. Moritz Post
		new net.sf.fmj.media.cdp.javasound.CaptureDevicePlugger().addCaptureDevices();

		//Media Locator 
		locator = new MediaLocator("javasound://0");

	}

	public void initiateMediaSession() {
		String result = createProcessor();
		System.out.println("Result after creating processor= " + result);
		initiateRTPManager();

		for (int i = 0; i < managers.length; i++) {
			manager = RTPManager.newInstance();

			manager.addSessionListener(this);
			manager.addReceiveStreamListener(this);
			manager.addRemoteListener(this);

			///Init Transmitter
			result = initiateTransmitter();

			System.out.println("Result after Init Transmitter= " + result);

			// Init Receiver
			result = initiateReceiver();
			System.out.println("Result after Init Receiver= " + result);

		}

	}

	public void closeMediaSession() {

		locator = null;

		processor.stop();
		processor.close();
		processor = null;

		try {
			dataOutput.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dataOutput.disconnect();
		dataOutput = null;

		managers = null;
		manager.removeTargets("All the targets removed");
		manager.dispose();
		manager = null;

		receiver.disposePlayerWindow();

		transmitter = null;
		receiver = null;
	}

	public void initiateRTPManager() {
		PushBufferDataSource pbds = (PushBufferDataSource) dataOutput;
		PushBufferStream pbss[] = pbds.getStreams();

		managers = new RTPManager[pbss.length];

	}

	private String initiateTransmitter() {
		try {
			SessionAddress localAddress = new SessionAddress(InetAddress.getLocalHost(), localAudioPort, 64);
			SessionAddress remoteAddress = new SessionAddress(InetAddress.getByName(remoteAudioAddress), remoteAudioPort, 64);

			manager.initialize(localAddress);
			manager.addTarget(remoteAddress);

			transmitter = new Transmitter();
			transmitter.initTransmitter(remoteAudioAddress, dataOutput, manager);

			transmitter.setProcessor(processor);
			Thread t = new Thread(transmitter);
			t.start();

			System.out.println("Start transmission after send stream creation");
			//			processor.start();

			//TODO Remove
			Vector receivingStream = manager.getReceiveStreams();
			System.out.println("receivingStream=" + receivingStream);

			return null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return e.getMessage();
		}
	}

	private String initiateReceiver() {
		try {

			//			Thread.currentThread().sleep(10000);

			//			SessionAddress localAddress=new SessionAddress(InetAddress.getLocalHost(), localAudioPort,64);
			//			SessionAddress remoteAddress=new SessionAddress(InetAddress.getByName(remoteAudioAddress),remoteAudioPort,64);			
			//			manager.removeTargets("Remote Listening point removed");			
			//			remoteAddress=new SessionAddress(InetAddress.getLocalHost(),remoteAudioPort+2,64);
			//			manager.initialize(localAddress);
			//			manager.addTarget(remoteAddress);

			receiver = new Receiver();
			receiver.setManager(manager);

			Thread t1 = new Thread(receiver);
			t1.start();

			//TODO Remove
			Vector receivingStream = manager.getReceiveStreams();
			System.out.println("receivingStream=" + receivingStream);

			return null;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return e.getMessage();
		}
	}

	private String createProcessor() {
		if (locator == null)
			return "Locator is null";

		DataSource ds;
		//		DataSource clone;

		try {
			ds = javax.media.Manager.createDataSource(locator);
		} catch (Exception e) {
			return "Couldn't create DataSource";
		}

		// Try to create a processor to handle the input media locator
		try {
			processor = javax.media.Manager.createProcessor(ds);
		} catch (NoProcessorException npe) {
			return "Couldn't create processor";
		} catch (IOException ioe) {
			return "IOException creating processor";
		}

		// Wait for it to configure
		boolean result = waitForState(processor, Processor.Configured);
		if (result == false)
			return "Couldn't configure processor";

		// Get the tracks from the processor
		TrackControl[] tracks = processor.getTrackControls();

		// Do we have atleast one track?
		if (tracks == null || tracks.length < 1)
			return "Couldn't find tracks in processor";

		// Set the output content descriptor to RAW_RTP
		// This will limit the supported formats reported from
		// Track.getSupportedFormats to only valid RTP formats.
		ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW_RTP);
		processor.setContentDescriptor(cd);

		Format supported[];
		Format chosen;
		boolean atLeastOneTrack = false;

		// Program the tracks.
		for (int i = 0; i < tracks.length; i++) {
			//		    Format format = tracks[i].getFormat();
			if (tracks[i].isEnabled()) {

				supported = tracks[i].getSupportedFormats();

				// We've set the output content to the RAW_RTP.
				// So all the supported formats should work with RTP.
				// We'll just pick the first one.

				if (supported.length > 0) {
					if (supported[0] instanceof VideoFormat) {
						// For video formats, we should double check the
						// sizes since not all formats work in all sizes.
						chosen = checkForVideoSizes(tracks[i].getFormat(), supported[0]);
					} else {
						//After fixing the bug reported by Moritz post, updated original
						chosen = new AudioFormat(AudioFormat.ULAW_RTP, 8000, 8, 1);//supported[0];
					}
					tracks[i].setFormat(chosen);
					System.err.println("Track " + i + " is set to transmit as:");
					System.err.println("  " + chosen);
					atLeastOneTrack = true;
				} else
					tracks[i].setEnabled(false);
			} else
				tracks[i].setEnabled(false);
		}

		if (!atLeastOneTrack)
			return "Couldn't set any of the tracks to a valid RTP format";

		// Realize the processor. This will internally create a flow
		// graph and attempt to create an output datasource for JPEG/RTP
		// audio frames.
		result = waitForState(processor, Controller.Realized);
		if (result == false)
			return "Couldn't realize processor";

		// Set the JPEG quality to .5.
		setJPEGQuality(processor, 0.5f);

		// Get the output data source of the processor
		dataOutput = processor.getDataOutput();

		return null;
	}

	/**
	 * For JPEG and H263, we know that they only work for particular
	 * sizes.  So we'll perform extra checking here to make sure they
	 * are of the right sizes.
	 */
	Format checkForVideoSizes(Format original, Format supported) {

		int width, height;
		Dimension size = ((VideoFormat) original).getSize();
		Format jpegFmt = new Format(VideoFormat.JPEG_RTP);
		Format h263Fmt = new Format(VideoFormat.H263_RTP);

		if (supported.matches(jpegFmt)) {
			// For JPEG, make sure width and height are divisible by 8.
			width = (size.width % 8 == 0 ? size.width : (int) (size.width / 8) * 8);
			height = (size.height % 8 == 0 ? size.height : (int) (size.height / 8) * 8);
		} else if (supported.matches(h263Fmt)) {
			// For H.263, we only support some specific sizes.
			if (size.width < 128) {
				width = 128;
				height = 96;
			} else if (size.width < 176) {
				width = 176;
				height = 144;
			} else {
				width = 352;
				height = 288;
			}
		} else {
			// We don't know this particular format.  We'll just
			// leave it alone then.
			return supported;
		}

		return (new VideoFormat(null, new Dimension(width, height), Format.NOT_SPECIFIED, null, Format.NOT_SPECIFIED)).intersects(supported);
	}

	/**
	 * Setting the encoding quality to the specified value on the JPEG encoder.
	 * 0.5 is a good default.
	 */
	void setJPEGQuality(Player p, float val) {

		Control cs[] = p.getControls();
		QualityControl qc = null;
		VideoFormat jpegFmt = new VideoFormat(VideoFormat.JPEG);

		// Loop through the controls to find the Quality control for
		// the JPEG encoder.
		for (int i = 0; i < cs.length; i++) {

			if (cs[i] instanceof QualityControl && cs[i] instanceof Owned) {
				Object owner = ((Owned) cs[i]).getOwner();

				// Check to see if the owner is a Codec.
				// Then check for the output format.
				if (owner instanceof Codec) {
					Format fmts[] = ((Codec) owner).getSupportedOutputFormats(null);
					for (int j = 0; j < fmts.length; j++) {
						if (fmts[j].matches(jpegFmt)) {
							qc = (QualityControl) cs[i];
							qc.setQuality(val);
							System.err.println("- Setting quality to " + val + " on " + qc);
							break;
						}
					}
				}
				if (qc != null)
					break;
			}
		}
	}

	private synchronized boolean waitForState(Processor p, int state) {
		p.addControllerListener(new StateListener());
		failed = false;

		// Call the required method on the processor
		if (state == Processor.Configured) {
			p.configure();
		} else if (state == Processor.Realized) {
			p.realize();
		}

		// Wait until we get an event that confirms the
		// success of the method, or a failure event.
		// See StateListener inner class
		while (p.getState() < state && !failed) {
			synchronized (getStateLock()) {
				try {
					getStateLock().wait();
				} catch (InterruptedException ie) {
					return false;
				}
			}
		}

		if (failed)
			return false;
		else
			return true;
	}

	class StateListener implements ControllerListener {

		public void controllerUpdate(ControllerEvent ce) {

			// If there was an error during configure or
			// realize, the processor will be closed
			if (ce instanceof ControllerClosedEvent)
				setFailed();

			// All controller events, send a notification
			// to the waiting thread in waitForState method.
			if (ce instanceof ControllerEvent) {
				synchronized (getStateLock()) {
					getStateLock().notifyAll();
				}
			}
		}
	}

	Integer getStateLock() {
		return stateLock;
	}

	void setFailed() {
		failed = true;
	}

	/* (non-Javadoc)
	 * @see javax.media.rtp.ReceiveStreamListener#update(javax.media.rtp.event.ReceiveStreamEvent)
	 */
	public void update(ReceiveStreamEvent arg0) {
		try {
			receiver.update(arg0);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see javax.media.rtp.SessionListener#update(javax.media.rtp.event.SessionEvent)
	 */
	public void update(SessionEvent evt) {
		if (evt instanceof NewParticipantEvent) {
			Participant p = ((NewParticipantEvent) evt).getParticipant();
			System.err.println("  - A new participant had just joined: " + p.getCNAME());
		}
	}

	/* (non-Javadoc)
	 * @see javax.media.rtp.RemoteListener#update(javax.media.rtp.event.RemoteEvent)
	 */
	public void update(RemoteEvent evt) {
		System.out.println("New Report ....");
		if (evt instanceof ReceiverReportEvent) {
			System.out.println("Receiver Report Recieved.");
		} else if (evt instanceof SenderReportEvent) {
			System.out.println("Sender Report Received.");
		}
	}

	public static void Main(String[] args) {
		Transceiver t = new Transceiver("192.168.1.5", "52000");
		t.initiateMediaSession();
	}

}
