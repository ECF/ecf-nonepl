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

package org.eclipse.ecf.provider.iax.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.ecf.core.AbstractContainer;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.events.ContainerConnectedEvent;
import org.eclipse.ecf.core.events.ContainerConnectingEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectedEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectingEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.iax.Activator;
import org.eclipse.ecf.internal.provider.iax.IAXDebugOptions;
import org.eclipse.ecf.provider.iax.identity.IAXCallNamespace;
import org.eclipse.ecf.provider.iax.identity.IAXNamespace;
import org.eclipse.ecf.telephony.call.CallException;
import org.eclipse.ecf.telephony.call.ICallSessionContainerAdapter;
import org.eclipse.ecf.telephony.call.ICallSessionListener;
import org.eclipse.ecf.telephony.call.ICallSessionRequestListener;
import org.eclipse.ecf.telephony.call.events.ICallSessionRequestEvent;

import com.yakasoftware.telephony.iax.iaxclient.IAXClient;
import com.yakasoftware.telephony.iax.iaxclient.IAXClientListener;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_Audio;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_Level;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_NetStats;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_Registration;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_State;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_Text;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_URL;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_Video;
import com.yakasoftware.telephony.iax.iaxclient.event.Event_VideoStats;

/**
 * IAX Container Adapter.
 */
public class IAXContainer extends AbstractContainer implements ICallSessionContainerAdapter {

	private ID targetID = null;

	private final IAXClient iaxclient;

	/*
	 * This is the ID for this container.  Returned via getID().
	 */
	private final ID containerID;

	private final List callSessionRequestListeners = new ArrayList();

	public IAXContainer() throws IDCreateException {
		super();
		this.containerID = IDFactory.getDefault().createGUID();
		this.iaxclient = IAXClient.getIAXClient(true, 1);
		this.iaxclient.addIAXClientListener(new IAXClientListenerImpl(this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainer#connect(org.eclipse.ecf.core.identity.ID,
	 *      org.eclipse.ecf.core.security.IConnectContext)
	 */
	public void connect(ID targetID, IConnectContext connectContext) throws ContainerConnectException {
		if (!targetID.getNamespace().getName().equals(getConnectNamespace().getName()))
			throw new ContainerConnectException("targetID not of appropriate Namespace");

		fireContainerEvent(new ContainerConnectingEvent(getID(), targetID));

		// XXX connect to remote service here

		this.targetID = targetID;
		fireContainerEvent(new ContainerConnectedEvent(getID(), targetID));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainer#disconnect()
	 */
	public void disconnect() {
		fireContainerEvent(new ContainerDisconnectingEvent(getID(), targetID));

		final ID oldID = targetID;

		// XXX disconnect here

		fireContainerEvent(new ContainerDisconnectedEvent(getID(), oldID));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainer#getConnectNamespace()
	 */
	public Namespace getConnectNamespace() {
		return IDFactory.getDefault().getNamespaceByName(IAXNamespace.NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainer#getConnectedID()
	 */
	public ID getConnectedID() {
		return targetID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.IIdentifiable#getID()
	 */
	public ID getID() {
		return containerID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.AbstractContainer#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class serviceType) {
		/*
		 * See AbstractContainer.getAdapter() implementation.
		 */
		return super.getAdapter(serviceType);
	}

	/**
	 * @param fired_EventVideoStats
	 */
	protected void handleEventVideoStats(Event_VideoStats fired_EventVideoStats) {
		Trace.entering(Activator.PLUGIN_ID, IAXDebugOptions.METHODS_ENTERING, this.getClass(), "handleEventVideoStats", fired_EventVideoStats);
		// TODO Auto-generated method stub
	}

	/**
	 * @param fired_EventVideo
	 */
	protected void handleEventVideo(Event_Video fired_EventVideo) {
		Trace.entering(Activator.PLUGIN_ID, IAXDebugOptions.METHODS_ENTERING, this.getClass(), "handleEventVideo", fired_EventVideo);
		// TODO Auto-generated method stub
	}

	/**
	 * @param fired_EventURL
	 */
	protected void handleEventURL(Event_URL fired_EventURL) {
		Trace.entering(Activator.PLUGIN_ID, IAXDebugOptions.METHODS_ENTERING, this.getClass(), "handleEventURL", fired_EventURL);
		// TODO Auto-generated method stub
	}

	/**
	 * @param fired_EventText
	 */
	protected void handleEventText(Event_Text fired_EventText) {
		Trace.entering(Activator.PLUGIN_ID, IAXDebugOptions.METHODS_ENTERING, this.getClass(), "handleEventText", fired_EventText);
		// TODO Auto-generated method stub
	}

	/**
	 * @param fired_EventRegistration
	 */
	protected void handleEventRegistration(Event_Registration fired_EventRegistration) {
		Trace.entering(Activator.PLUGIN_ID, IAXDebugOptions.METHODS_ENTERING, this.getClass(), "handleEventRegistration", fired_EventRegistration);
		// TODO Auto-generated method stub
	}

	/**
	 * @param fired_EventNetStats
	 */
	protected void handleEventNetStats(Event_NetStats fired_EventNetStats) {
		Trace.entering(Activator.PLUGIN_ID, IAXDebugOptions.METHODS_ENTERING, this.getClass(), "handleEventNetStats", fired_EventNetStats);
		// TODO Auto-generated method stub
	}

	/**
	 * @param fired_EventLevel
	 */
	protected void handleEventLevel(Event_Level fired_EventLevel) {
		Trace.entering(Activator.PLUGIN_ID, IAXDebugOptions.METHODS_ENTERING, this.getClass(), "handleEventLevel", fired_EventLevel);
		// TODO Auto-generated method stub
	}

	/**
	 * @param fired_EventCallState
	 */
	protected void handleEventCallState(Event_State fired_EventCallState) {
		Trace.entering(Activator.PLUGIN_ID, IAXDebugOptions.METHODS_ENTERING, this.getClass(), "handleEventCallState", fired_EventCallState);
		// TODO Auto-generated method stub
	}

	/**
	 * @param fired_EventAudio
	 */
	protected void handleEventAudio(Event_Audio fired_EventAudio) {
		Trace.entering(Activator.PLUGIN_ID, IAXDebugOptions.METHODS_ENTERING, this.getClass(), "handleEventAudio", fired_EventAudio);
		// TODO Auto-generated method stub
	}

	protected void fireCallSessionRequestListeners(ICallSessionRequestEvent event) {
		List entries = null;
		synchronized (callSessionRequestListeners) {
			entries = new ArrayList(callSessionRequestListeners);
		}
		for (final Iterator i = entries.iterator(); i.hasNext();) {
			final ICallSessionRequestListener l = (ICallSessionRequestListener) i.next();
			l.handleCallSessionRequest(event);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.telephony.call.ICallSessionContainerAdapter#addCallSessionRequestListener(org.eclipse.ecf.telephony.call.ICallSessionRequestListener)
	 */
	public void addCallSessionRequestListener(ICallSessionRequestListener listener) {
		if (listener == null)
			return;
		synchronized (callSessionRequestListeners) {
			callSessionRequestListeners.add(listener);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.telephony.call.ICallSessionContainerAdapter#getReceiverNamespace()
	 */
	public Namespace getReceiverNamespace() {
		return IDFactory.getDefault().getNamespaceByName(IAXCallNamespace.NAME);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.telephony.call.ICallSessionContainerAdapter#removeCallSessionRequestListener(org.eclipse.ecf.telephony.call.ICallSessionRequestListener)
	 */
	public void removeCallSessionRequestListener(ICallSessionRequestListener listener) {
		if (listener == null)
			return;
		synchronized (callSessionRequestListeners) {
			callSessionRequestListeners.remove(listener);
		}

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.telephony.call.ICallSessionContainerAdapter#sendCallRequest(org.eclipse.ecf.core.identity.ID[], org.eclipse.ecf.telephony.call.ICallSessionListener, java.util.Map)
	 */
	public void sendCallRequest(ID[] receivers, ICallSessionListener listener, Map properties) throws CallException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.telephony.call.ICallSessionContainerAdapter#sendCallRequest(org.eclipse.ecf.core.identity.ID, org.eclipse.ecf.telephony.call.ICallSessionListener, java.util.Map)
	 */
	public void sendCallRequest(ID receiver, ICallSessionListener listener, Map properties) throws CallException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.AbstractContainer#dispose()
	 */
	public void dispose() {
		super.dispose();
		// XXX anything to do here?
	}
}
