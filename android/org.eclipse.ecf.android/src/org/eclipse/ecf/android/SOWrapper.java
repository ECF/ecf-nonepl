/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.android;

import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class SOWrapper {
	protected ISharedObject sharedObject;
	private final SOConfig sharedObjectConfig;
	ID sharedObjectID;
	private final SOContainer container;
	private final ID containerID;
	private Thread thread;
	SimpleFIFOQueue queue;

	protected SOWrapper(SOContainer.LoadingSharedObject obj, SOContainer cont) {
		sharedObjectID = obj.getID();
		sharedObject = obj;
		container = cont;
		containerID = cont.getID();
		sharedObjectConfig = null;
		thread = null;
		queue = new SimpleFIFOQueue();
	}

	public SOWrapper(SOConfig aConfig, ISharedObject obj, SOContainer cont) {
		sharedObjectConfig = aConfig;
		sharedObjectID = sharedObjectConfig.getSharedObjectID();
		sharedObject = obj;
		container = cont;
		containerID = cont.getID();
		thread = null;
		queue = new SimpleFIFOQueue();
	}

	protected void init() throws SharedObjectInitException {
		debug("init()"); //$NON-NLS-1$
		sharedObjectConfig.makeActive(new QueueEnqueueImpl(queue));
		sharedObject.init(sharedObjectConfig);
	}

	protected ID getObjID() {
		return sharedObjectConfig.getSharedObjectID();
	}

	protected ID getHomeID() {
		return sharedObjectConfig.getHomeContainerID();
	}

	protected SOConfig getConfig() {
		return sharedObjectConfig;
	}

	@SuppressWarnings("unchecked")
	protected void activated() {
		thread = (Thread) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				Thread aThread = getThread();
				return aThread;
			}
		});
		// Notify container and listeners
		container.notifySharedObjectActivated(sharedObjectID);
		// Start thread
		thread.start();
		// Send message
		send(new SharedObjectActivatedEvent(containerID, sharedObjectID));
	}

	protected void deactivated() {
		container.notifySharedObjectDeactivated(sharedObjectID);
		send(new SharedObjectDeactivatedEvent(containerID, sharedObjectID));
		destroyed();
	}

	protected void destroyed() {
		if (!queue.isStopped()) {
			if (thread != null)
				queue.enqueue(new DisposeEvent());
			queue.close();
		}
	}

	protected void otherChanged(ID otherID, boolean activated) {
		if (activated && thread != null) {
			send(new SharedObjectActivatedEvent(containerID, otherID));
		} else {
			send(new SharedObjectDeactivatedEvent(containerID, otherID));
		}
	}

	protected void memberChanged(Member m, boolean add) {
		if (thread != null) {
			if (add) {
				send(new ContainerConnectedEvent(containerID, m.getID()));
			} else {
				send(new ContainerDisconnectedEvent(containerID, m.getID()));
			}
		}
	}

	protected Thread getThread() {
		return container.getNewSharedObjectThread(sharedObjectID, new Runnable() {
			public void run() {
				debug("runner(" + sharedObjectID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
				Event evt = null;
				for (;;) {
					if (Thread.currentThread().isInterrupted())
						break;
					evt = (Event) queue.dequeue();
					if (Thread.currentThread().isInterrupted() || evt == null)
						break;
					try {
						if (evt instanceof ProcEvent) {
							SOWrapper.this.svc(((ProcEvent) evt).getEvent());
						} else if (evt instanceof DisposeEvent) {
							SOWrapper.this.doDestroy();
						} else {
							SOWrapper.this.svc(evt);
						}
					} catch (Throwable t) {
						handleRuntimeException(t);
					}
				}
				if (Thread.currentThread().isInterrupted()) {
					debug("runner(" + sharedObjectID //$NON-NLS-1$
							+ ") terminating interrupted"); //$NON-NLS-1$
				} else {
					debug("runner(" + sharedObjectID //$NON-NLS-1$
							+ ") terminating normally"); //$NON-NLS-1$
				}
			}
		});
	}

	private void send(Event evt) {
		queue.enqueue(new ProcEvent(evt));
	}

	public static class ProcEvent implements Event {
		private static final long serialVersionUID = 3257002142513378616L;
		Event theEvent = null;

		public ProcEvent(Event event) {
			theEvent = event;
		}

		Event getEvent() {
			return theEvent;
		}
	}

	protected static class DisposeEvent implements Event {
		private static final long serialVersionUID = 3688503311859135536L;

		DisposeEvent() {
			//
		}
	}

	protected void svc(Event evt) {
		sharedObject.handleEvent(evt);
	}

	protected void doDestroy() {
		sharedObject.dispose(containerID);
		// make config inactive
		sharedObjectConfig.makeInactive();
	}

	protected void deliverSharedObjectMessage(ID fromID, Serializable data) {
		send(new RemoteSharedObjectEvent(getObjID(), fromID, data));
	}

	protected void deliverCreateResponse(ID fromID, ContainerMessage.CreateResponseMessage resp) {
		send(new RemoteSharedObjectCreateResponseEvent(resp.getSharedObjectID(), fromID, resp.getSequence(), resp.getException()));
	}

	public void deliverEvent(Event evt) {
		send(evt);
	}

	protected void destroySelf() {
		debug("destroySelf()"); //$NON-NLS-1$
		send(new DisposeEvent());
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("SharedObjectWrapper[").append(getObjID()).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
		return sb.toString();
	}

	protected void debug(String msg) {
//		TODO [pierre] log Trace.trace(ProviderPlugin.PLUGIN_ID, ECFProviderDebugOptions.SHAREDOBJECTWRAPPER, msg + ":oID=" + sharedObjectID + ":cID=" + container.getID()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected void traceStack(String msg, Throwable e) {
//		TODO [pierre] log 		Trace.catching(ProviderPlugin.PLUGIN_ID, ECFProviderDebugOptions.EXCEPTIONS_CATCHING, SOContainerGMM.class, container.getID() + ":" + msg, e); //$NON-NLS-1$
	}

	protected void handleRuntimeException(Throwable except) {
		except.printStackTrace(System.err);
		traceStack("runner:handleRuntimeException(" + sharedObjectID.getName() + ")", //$NON-NLS-1$ //$NON-NLS-2$
				except);
	}

	protected ISharedObject getSharedObject() {
		return sharedObject;
	}

	public SimpleFIFOQueue getQueue() {
		return queue;
	}
}