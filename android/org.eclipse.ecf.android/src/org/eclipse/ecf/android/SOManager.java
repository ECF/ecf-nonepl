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

/*
 * Created on Dec 20, 2004
 *  
 */
package org.eclipse.ecf.android;

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import junit.framework.Assert;
import android.os.Bundle;
import android.util.Log;

/**
 * 
 */
public class SOManager implements ISharedObjectManager {
	private static final int GUID_SIZE = 20;

	private static final String TAG = SOManager.class.getSimpleName();

	SOContainer container = null;

	Vector connectors = null;

	/**
	 * Use a bundle to store all parcelables Instances of Parcelable can be
	 * written to and restored from a parcel
	 */
	private final Bundle b = new Bundle();

	public SOManager(SOContainer cont) {
		super();
		this.container = cont;
		connectors = new Vector();
	}

	protected void debug(String msg) {
		Log.d(TAG, msg + ":" + container.getID());
	}

	protected void traceStack(String msg, Throwable e) {
		Log.e(TAG, container.getID() + ":" + msg, e);
	}

	protected void addConnector(ISharedObjectConnector conn) {
		connectors.add(conn);
	}

	protected boolean removeConnector(ISharedObjectConnector conn) {
		return connectors.remove(conn);
	}

	protected List getConnectors() {
		return connectors;
	}

	protected Class[] getArgTypes(String[] argTypes, Object[] args,
			ClassLoader cl) throws ClassNotFoundException {
		return AbstractFactory.getClassesForTypes(argTypes, args, cl);
	}

	@SuppressWarnings("unchecked")
	protected ISharedObject createSharedObjectInstance(final Class newClass,
			final Class[] argTypes, final Object[] args) throws Exception {
		Object newObject = null;
		try {
			newObject = AccessController
					.doPrivileged(new PrivilegedExceptionAction() {
						public Object run() throws Exception {
							Constructor aConstructor = newClass
									.getConstructor(argTypes);
							aConstructor.setAccessible(true);
							return aConstructor.newInstance(args);
						}
					});
		} catch (java.security.PrivilegedActionException e) {
			throw e.getException();
		}
		return verifySharedObject(newObject);
	}

	protected ISharedObject verifySharedObject(Object newSharedObject) {
		if (newSharedObject instanceof ISharedObject)
			return (ISharedObject) newSharedObject;
		throw new ClassCastException(Messages.SOManager_Object
				+ newSharedObject.toString()
				+ Messages.SOManager_Does_Not_Implement
				+ ISharedObject.class.getName());
	}

	protected ISharedObject loadSharedObject(SharedObjectDescription sd)
			throws Exception {
		Assert
				.assertNotNull(
						Messages.SOManager_Exception_Shared_Object_Description_Not_Null,
						sd);
		// Then get args array from properties
		Object[] args = container.getArgsFromProperties(sd);
		// And arg types
		String[] types = container.getArgTypesFromProperties(sd);
		ISharedObject res = null;
		SharedObjectTypeDescription typeDesc = sd.getTypeDescription();
		String descName = typeDesc.getName();
		if (descName == null) {
			// First get classloader
			ClassLoader cl = container.getClassLoaderForSharedObject(sd);
			final Class newClass = Class.forName(typeDesc.getClassName(), true,
					cl);
			Class[] argTypes = getArgTypes(types, args, cl);
			res = createSharedObjectInstance(newClass, argTypes, args);
			// 'new style'
		} else {
			res = SharedObjectFactory.getDefault().createSharedObject(typeDesc,
					args);
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectManager#getSharedObjectIDs()
	 */
	public ID[] getSharedObjectIDs() {
		return container.getSharedObjectIDs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.core.ISharedObjectManager#createSharedObject(org.eclipse
	 * .ecf.core.SharedObjectDescription)
	 */
	public ID createSharedObject(SharedObjectDescription sd)
			throws SharedObjectCreateException {
		debug("createSharedObject(" + sd + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		// notify listeners
		if (sd == null)
			throw new SharedObjectCreateException(
					Messages.SOManager_Exception_Shared_Object_Description_Not_Null);
		ISharedObject newObject = null;
		ID result = null;
		try {
			newObject = loadSharedObject(sd);
			ID newID = createNewSharedObjectID(sd, newObject);
			container
					.fireDelegateContainerEvent(new SharedObjectManagerCreateEvent(
							container.getID(), newID));
			result = addSharedObject(newID, newObject, sd.getProperties());
		} catch (Exception e) {
			traceStack("Exception in createSharedObject", e); //$NON-NLS-1$
			SharedObjectCreateException newExcept = new SharedObjectCreateException(
					Messages.SOManager_Container
							+ container.getID()
							+ Messages.SOManager_Exception_Creating_Shared_Object
							+ sd.getID() + ": " + e.getClass().getName() + ": " //$NON-NLS-1$ //$NON-NLS-2$
							+ e.getMessage());
			newExcept.setStackTrace(e.getStackTrace());
			throw newExcept;
		}
		return result;
	}

	protected ID createNewSharedObjectID(SharedObjectDescription sd,
			ISharedObject newObject) throws IDCreateException {
		ID descID = sd.getID();
		if (descID == null) {
			return IDFactory.getDefault().createGUID(GUID_SIZE);
		}
		return descID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.core.ISharedObjectManager#addSharedObject(org.eclipse
	 * .ecf.core.ISharedObject, java.util.Map,
	 * org.eclipse.ecf.core.ISharedObjectContainerTransaction)
	 */
	public ID addSharedObject(ID sharedObjectID, ISharedObject sharedObject,
			Map properties) throws SharedObjectAddException {
		debug("addSharedObject(" + sharedObjectID + "," + sharedObject + ","
				+ properties + ")");
		// notify listeners
		container.fireDelegateContainerEvent(new SharedObjectManagerAddEvent(
				container.getID(), sharedObjectID));
		ID result = sharedObjectID;
		try {
			ISharedObject so = sharedObject;
			container.addSharedObjectAndWait(sharedObjectID, so, properties);
		} catch (Exception e) {
			traceStack("Exception in addSharedObject", e); //$NON-NLS-1$
			SharedObjectAddException newExcept = new SharedObjectAddException(
					Messages.SOManager_Container + container.getID()
							+ Messages.SOManager_Exception_Adding_Shared_Object
							+ sharedObjectID + ": " + e.getClass().getName() //$NON-NLS-1$
							+ ": " + e.getMessage()); //$NON-NLS-1$
			newExcept.setStackTrace(e.getStackTrace());
			throw newExcept;
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.core.ISharedObjectManager#getSharedObject(org.eclipse
	 * .ecf.core.identity.ID)
	 */
	public ISharedObject getSharedObject(ID sharedObjectID) {
		return container.getSharedObject(sharedObjectID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.core.ISharedObjectManager#removeSharedObject(org.eclipse
	 * .ecf.core.identity.ID)
	 */
	public ISharedObject removeSharedObject(ID sharedObjectID) {
		debug("removeSharedObject(" + sharedObjectID + ")"); 
		// notify listeners
		container
				.fireDelegateContainerEvent(new SharedObjectManagerRemoveEvent(
						container.getID(), sharedObjectID));
		return container.removeSharedObject(sharedObjectID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.core.ISharedObjectManager#connectSharedObjects(org.eclipse
	 * .ecf.core.identity.ID, org.eclipse.ecf.core.identity.ID[])
	 */
	public ISharedObjectConnector connectSharedObjects(ID sharedObjectFrom,
			ID[] sharedObjectsTo) throws SharedObjectConnectException {
		debug("connectSharedObjects(" + sharedObjectFrom + "," //$NON-NLS-1$ //$NON-NLS-2$
				+ sharedObjectsTo + ")"); //$NON-NLS-1$
		if (sharedObjectFrom == null)
			throw new SharedObjectConnectException(
					Messages.SOManager_Exception_Sender_Not_Null);
		if (sharedObjectsTo == null)
			throw new SharedObjectConnectException(
					Messages.SOManager_Exception_Receivers_Not_Null);
		ISharedObjectConnector result = null;
		synchronized (container.getGroupMembershipLock()) {
			// Get from to create sure it's there
			SOWrapper wrap = container.getSharedObjectWrapper(sharedObjectFrom);
			if (wrap == null)
				throw new SharedObjectConnectException(
						Messages.SOManager_Sender_Object
								+ sharedObjectFrom.getName()
								+ Messages.SOManager_Not_Found);
			IQueueEnqueue[] queues = new IQueueEnqueue[sharedObjectsTo.length];
			for (int i = 0; i < sharedObjectsTo.length; i++) {
				SOWrapper w = container
						.getSharedObjectWrapper(sharedObjectsTo[i]);
				if (w == null)
					throw new SharedObjectConnectException(
							Messages.SOManager_Receiver_Object
									+ sharedObjectsTo[i].getName()
									+ Messages.SOManager_Not_Found);
				queues[i] = new QueueEnqueueImpl(w.getQueue());
			}
			// OK now we've got ids and wrappers, create a connector
			result = new SOConnector(sharedObjectFrom, sharedObjectsTo, queues);
			addConnector(result);
			// notify listeners
			container
					.fireDelegateContainerEvent(new SharedObjectManagerConnectEvent(
							container.getID(), result));
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.core.ISharedObjectManager#disconnectSharedObjects(org
	 * .eclipse.ecf.core.ISharedObjectConnector)
	 */
	public void disconnectSharedObjects(ISharedObjectConnector connector)
			throws SharedObjectDisconnectException {
		if (connector == null)
			throw new SharedObjectDisconnectException(
					Messages.SOManager_Exception_Connector_Not_Null);
		debug("disconnectSharedObjects(" + connector.getSenderID() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		if (!removeConnector(connector)) {
			throw new SharedObjectDisconnectException(
					Messages.SOManager_Connector + connector
							+ Messages.SOManager_Not_Found);
		}
		connector.dispose();
		container
				.fireDelegateContainerEvent(new SharedObjectManagerDisconnectEvent(
						container.getID(), connector));
	}

	protected void dispose() {
		debug("dispose()"); //$NON-NLS-1$
		for (Enumeration e = connectors.elements(); e.hasMoreElements();) {
			ISharedObjectConnector conn = (ISharedObjectConnector) e
					.nextElement();
			conn.dispose();
		}
		connectors.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.core.ISharedObjectManager#getSharedObjectConnectors(org
	 * .eclipse.ecf.core.identity.ID)
	 */
	public List getSharedObjectConnectors(ID sharedObjectFrom) {
		debug("getSharedObjectConnectors(" + sharedObjectFrom + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		List results = new ArrayList();
		for (Enumeration e = connectors.elements(); e.hasMoreElements();) {
			ISharedObjectConnector conn = (ISharedObjectConnector) e
					.nextElement();
			if (sharedObjectFrom.equals(conn.getSenderID())) {
				results.add(conn);
			}
		}
		return results;
	}

	public static Class[] getClassesForTypes(String[] argTypes, Object[] args,
			ClassLoader cl) throws ClassNotFoundException {
		Class clazzes[] = null;
		if (args == null || args.length == 0)
			clazzes = new Class[0];
		else if (argTypes != null) {
			clazzes = new Class[argTypes.length];
			for (int i = 0; i < argTypes.length; i++) {
				clazzes[i] = Class.forName(argTypes[i], true, cl);
			}
		} else {
			clazzes = new Class[args.length];
			for (int i = 0; i < args.length; i++) {
				if (args[i] == null)
					clazzes[i] = null;
				else
					clazzes[i] = args[i].getClass();
			}
		}
		return clazzes;
	}

	public void setRemoteAddPolicy(ISharedObjectPolicy policy) {
		container.setRemoteAddPolicy(policy);
	}
}