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

import java.awt.*;
import java.util.Vector;
import javax.media.*;
import javax.media.control.BufferControl;
import javax.media.protocol.DataSource;
import javax.media.rtp.*;
import javax.media.rtp.event.*;

public class Receiver implements Runnable, ControllerListener {
	RTPManager manager;
	Object dataSync = new Object();
	boolean dataReceived = false;
	Vector playerWindows = new Vector();
	PlayerWindow pw;

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		BufferControl bc = (BufferControl) manager.getControl("javax.media.control.BufferControl");
		if (bc != null)
			bc.setBufferLength(10);

		// Wait for data to arrive before moving on.

		long then = System.currentTimeMillis();
		long waitingPeriod = Integer.MAX_VALUE;

		try {
			synchronized (dataSync) {
				while (!dataReceived && System.currentTimeMillis() - then < waitingPeriod) {
					if (!dataReceived)
						System.err.println("  - Waiting for RTP data to arrive...");
					dataSync.wait(10000);
				}
			}
		} catch (Exception e) {
		}

		if (!dataReceived) {
			System.err.println("No RTP data was received.");
			close();
		}
	}

	public boolean isDone() {
		return playerWindows.size() == 0;
	}

	protected void close() {

		for (int i = 0; i < playerWindows.size(); i++) {
			try {
				((PlayerWindow) playerWindows.elementAt(i)).close();
			} catch (Exception e) {
			}
		}

		playerWindows.removeAllElements();

		// close the RTP session.
		//		for (int i = 0; i < mgrs.length; i++) {
		if (manager != null) {
			manager.removeTargets("Closing session from AVReceive2");
			manager.dispose();
			manager = null;
		}
		//		}
	}

	public void update(ReceiveStreamEvent evt) {

		RTPManager mgr = (RTPManager) evt.getSource();
		Participant participant = null;
		ReceiveStream stream = null;

		try {
			participant = evt.getParticipant(); // could be null.
			stream = evt.getReceiveStream(); // could be null.
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (evt instanceof RemotePayloadChangeEvent) {

			System.err.println("  - Received an RTP PayloadChangeEvent.");
			System.err.println("Sorry, cannot handle payload change.");
			System.exit(0);

		}

		else if (evt instanceof NewReceiveStreamEvent) {

			try {
				stream = ((NewReceiveStreamEvent) evt).getReceiveStream();
				DataSource ds = stream.getDataSource();

				// Find out the formats.
				RTPControl ctl = (RTPControl) ds.getControl("javax.media.rtp.RTPControl");
				if (ctl != null) {
					System.err.println("  - Recevied new RTP stream: " + ctl.getFormat());
				} else
					System.err.println("  - Recevied new RTP stream");

				if (participant == null)
					System.err.println("      The sender of this stream had yet to be identified.");
				else {
					System.err.println("      The stream comes from: " + participant.getCNAME());
				}

				// create a player by passing datasource to the Media Manager
				Player p = javax.media.Manager.createPlayer(ds);
				if (p == null)
					return;

				p.addControllerListener(this);
				p.realize();
				pw = new PlayerWindow(p, stream);
				playerWindows.addElement(pw);

				// Notify intialize() that a new stream had arrived.
				synchronized (dataSync) {
					dataReceived = true;
					dataSync.notifyAll();
				}

			} catch (Exception e) {
				System.err.println("NewReceiveStreamEvent exception " + e.getMessage());
				return;
			}

		}

		else if (evt instanceof StreamMappedEvent) {

			if (stream != null && stream.getDataSource() != null) {
				DataSource ds = stream.getDataSource();
				// Find out the formats.
				RTPControl ctl = (RTPControl) ds.getControl("javax.media.rtp.RTPControl");
				System.err.println("  - The previously unidentified stream ");
				if (ctl != null)
					System.err.println("      " + ctl.getFormat());
				System.err.println("      had now been identified as sent by: " + participant.getCNAME());
			}
		}

		else if (evt instanceof ByeEvent) {

			System.err.println("  - Got \"bye\" from: " + participant.getCNAME());
			PlayerWindow pw = find(stream);
			if (pw != null) {
				pw.close();
				playerWindows.removeElement(pw);
			}
		}

	}

	/**
	 * ControllerListener for the Players.
	 */
	public synchronized void controllerUpdate(ControllerEvent ce) {

		Player p = (Player) ce.getSourceController();

		if (p == null)
			return;

		// Get this when the internal players are realized.
		if (ce instanceof RealizeCompleteEvent) {
			PlayerWindow pw = find(p);
			if (pw == null) {
				// Some strange happened.
				System.err.println("Internal error!");
				System.exit(-1);
			}
			pw.initialize();
			pw.setVisible(true);
			p.start();
		}

		if (ce instanceof ControllerErrorEvent) {
			p.removeControllerListener(this);
			PlayerWindow pw = find(p);
			if (pw != null) {
				pw.close();
				playerWindows.removeElement(pw);
			}
			System.err.println("AVReceive2 internal error: " + ce);
		}

	}

	PlayerWindow find(Player p) {
		for (int i = 0; i < playerWindows.size(); i++) {
			PlayerWindow pw = (PlayerWindow) playerWindows.elementAt(i);
			if (pw.player == p)
				return pw;
		}
		return null;
	}

	PlayerWindow find(ReceiveStream strm) {
		for (int i = 0; i < playerWindows.size(); i++) {
			PlayerWindow pw = (PlayerWindow) playerWindows.elementAt(i);
			if (pw.stream == strm)
				return pw;
		}
		return null;
	}

	class PlayerWindow extends Frame {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6438239207830887808L;
		Player player;
		ReceiveStream stream;

		PlayerWindow(Player p, ReceiveStream strm) {
			player = p;
			stream = strm;
		}

		public void initialize() {
			add(new PlayerPanel(player));
		}

		public void close() {

			setVisible(false);
			player.close();
			dispose();
		}

		public void addNotify() {
			super.addNotify();
			pack();
		}
	}

	class PlayerPanel extends Panel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2374473632913217568L;
		Component vc, cc;

		PlayerPanel(Player p) {
			setLayout(new BorderLayout());
			if ((vc = p.getVisualComponent()) != null)
				add("Center", vc);
			if ((cc = p.getControlPanelComponent()) != null)
				add("South", cc);
		}

		public Dimension getPreferredSize() {
			int w = 0, h = 0;
			if (vc != null) {
				Dimension size = vc.getPreferredSize();
				w = size.width;
				h = size.height;
			}
			if (cc != null) {
				Dimension size = cc.getPreferredSize();
				if (w == 0)
					w = size.width;
				h += size.height;
			}
			if (w < 160)
				w = 160;
			return new Dimension(w, h);
		}
	}

	public RTPManager getManager() {
		return manager;
	}

	public void setManager(RTPManager manager) {
		this.manager = manager;
	}

	public void disposePlayerWindow() {
		if (pw != null) {
			pw.close();
			playerWindows.removeElement(pw);
			pw = null;
		}
	}

}
