/*******************************************************************************
 * Copyright (c) 2009 Nuwan Samarasekera, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Nuwan Sam <nuwansam@gmail.com> - initial API and implementation
 ******************************************************************************/

/*
 * @since 3.0
 */

package org.eclipse.ecf.internal.provider.google.ui;

import java.util.PriorityQueue;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.filetransfer.IIncomingFileTransferRequestListener;
import org.eclipse.ecf.filetransfer.IncomingFileTransferException;
import org.eclipse.ecf.filetransfer.events.IFileTransferRequestEvent;
import org.eclipse.ecf.provider.google.GoogleContainer;
import org.eclipse.ecf.provider.google.INotificationListener;
import org.eclipse.ecf.provider.google.events.NotificationEvent;
import org.eclipse.ecf.telephony.call.CallException;
import org.eclipse.ecf.telephony.call.ICallSessionRequestListener;
import org.eclipse.ecf.telephony.call.events.ICallSessionRequestEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;

public class GoogleNotificationUIDelegator implements INotificationListener, ICallSessionRequestListener, IIncomingFileTransferRequestListener {

	private NotifierDialog popup;
	/*
	 * private final Set<NotificationEvent> notifications = new
	 * HashSet<NotificationEvent>();
	 * 
	 * private final Set<NotificationEvent> currentlyNotifying = Collections
	 * .synchronizedSet(notifications);
	 * 
	 * private final WeakHashMap<Object, Object> cancelledTokens = new
	 * WeakHashMap<Object, Object>();
	 */

	private final Job openJob = new Job("Notify_job") {
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {

				if (Platform.isRunning() && PlatformUI.getWorkbench() != null && PlatformUI.getWorkbench().getDisplay() != null && !PlatformUI.getWorkbench().getDisplay().isDisposed()) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

						public void run() {

							// synchronized
							// (GoogleNotificationUIDelegator.class) {
							// if (currentlyNotifying.size() > 0) {
							// popup.close();
							showPopup();
							// }
							// }
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();

			} finally {

				if (popup != null) {

					// schedule(popup.getDelayClose() / 2);
				} else {
					// schedule(DELAY_OPEN);
				}
			}

			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}

			return Status.OK_STATUS;
		}

	};

	private PriorityQueue<NotificationEvent> notificationQueue = new PriorityQueue<NotificationEvent>();

	private GoogleContainer container;

	public GoogleNotificationUIDelegator(GoogleContainer container) {
		this.container = container;
		container.getCallSessionContainerAdapter().addCallSessionRequestListener(this);
		container.getFileTransferAdaptor().addListener(this);
		container.setUserSettingSerializer(new GoogleUserSettingSerializer(container));
	}

	public void showPopup() {
		// Shell shell = new Shell(PlatformUI.getWorkbench().getDisplay());
		NotificationEvent event = notificationQueue.poll();
		NotifierDialog.getDefault(container).notify(event);

		/*
		 * if (popup != null) { popup.close(); }
		 * 
		 * Shell shell = new Shell(PlatformUI.getWorkbench().getDisplay());
		 * popup = new NotificationPopup(shell, container); synchronized
		 * (notificationQueue) {
		 * popup.setNotification(notificationQueue.poll()); }
		 * popup.setDelayClose(3000); popup.setFadingEnabled(false);
		 * 
		 * List<NotificationEvent> toDisplay = new ArrayList<NotificationEvent>(
		 * currentlyNotifying); // Collections.sort(toDisplay); //
		 * popup.setContents(toDisplay); cleanNotified();
		 * popup.setBlockOnOpen(false); popup.open();
		 */}

	/** public for testing */

	public void startNotification(long initialStartupTime) {
		while (!notificationQueue.isEmpty()) {
			if (true) {
				if (!openJob.cancel()) {
					try {
						openJob.join();
					} catch (InterruptedException e) {
						// ignore
					}
				}
				// openJob.setSystem(runSystem);
				openJob.schedule(initialStartupTime);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void stopNotification() {
		openJob.cancel();
		// closeJob.cancel();
		// if (popup != null) {
		// popup.close();
		// }
	}

	public void fireNotificationEvent(NotificationEvent event) {
		printNotification(event);

		if (event.getType().equals(NotificationEvent.TYPE_INCOMING_CALL)) {
			System.out.println("INCOMING_CALL_REC1");
		}

		if (notificationQueue.isEmpty()) {
			notificationQueue.offer(event);
			startNotification(0);

		} else {
			synchronized (notificationQueue) {
				notificationQueue.offer(event);
			}
			printNotification(event);
		}

	}

	private void printNotification(NotificationEvent event) {
		System.out.println("NOTIFY: " + event.getNotification());
	}

	public void handleCallSessionRequest(ICallSessionRequestEvent callSessionRequestEvent) {

		Display display = new Display();
		Shell shell = new Shell(display);
		MessageBox mb = new MessageBox(shell/*
											 * Display.getDefault().getActiveShell
											 * ()
											 */, SWT.OK | SWT.CANCEL);
		mb.setMessage("Do you want to accept incoming call from " + callSessionRequestEvent.getInitiator().getName());
		mb.setText("Accept ...");
		int val = mb.open();
		if (val == SWT.OK) {
			try {
				callSessionRequestEvent.accept(null, null);
			} catch (CallException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			callSessionRequestEvent.reject();
		}

		/*
		 * String jid = callSessionRequestEvent.getInitiator().getName();
		 * Hashtable<String, Object> properties = new Hashtable<String,
		 * Object>(); properties.put(NotificationEvent.JID, jid);
		 * properties.put(NotificationEvent.NOTIFICATION_STRING,
		 * "Incoming Call from " + jid);
		 * properties.put(NotificationEvent.INCOMING_CALL_EVENT,
		 * callSessionRequestEvent); NotificationEvent event = new
		 * NotificationEvent( NotificationEvent.TYPE_INCOMING_CALL, properties);
		 * 
		 * this.fireNotificationEvent(event);
		 */}

	public void handleFileTransferRequest(IFileTransferRequestEvent fileRequestEvent) {

		Display display = new Display();
		Shell shell = new Shell(display);
		MessageBox mb = new MessageBox(shell/*
											 * Display.getDefault().getActiveShell
											 * ()
											 */, SWT.OK | SWT.CANCEL);
		mb.setMessage("Do you want to accept file" + " from " + fileRequestEvent.getRequesterID().getName());
		mb.setText("Accept File...");
		int val = mb.open();
		try {
			if (val == SWT.OK) {
				fileRequestEvent.accept(null, null);
			} else {
				fileRequestEvent.reject();
			}
		} catch (IncomingFileTransferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * String jid = fileRequestEvent.getRequesterID().getName();
		 * Hashtable<String, Object> properties = new Hashtable<String,
		 * Object>(); properties.put(NotificationEvent.JID, jid);
		 * properties.put(NotificationEvent.NOTIFICATION_STRING,
		 * "Incoming File from " + jid);
		 * properties.put(NotificationEvent.INCOMING_FILE_EVENT,
		 * fileRequestEvent); NotificationEvent event = new NotificationEvent(
		 * NotificationEvent.TYPE_INCOMING_FILE, properties);
		 * 
		 * this.fireNotificationEvent(event);
		 */
	}

	/**
	 * public for testing purposes
	 */
	/*
	 * public Set<AbstractNotification> getNotifications() { synchronized
	 * (GoogleNotificationUIDelegator.class) { return currentlyNotifying; } }
	 */

}
