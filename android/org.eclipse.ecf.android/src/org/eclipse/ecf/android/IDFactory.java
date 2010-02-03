/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.android;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.util.Log;

/**
 * A factory class for creating ID instances. This is the factory for plugins to
 * manufacture ID instances.
 * 
 */
public class IDFactory implements IIDFactory {
	public static final String SECURITY_PROPERTY = IDFactory.class.getName()
			+ ".security"; //$NON-NLS-1$


	private static final int IDENTITY_CREATION_ERRORCODE = 2001;

	private static Hashtable namespaces = new Hashtable();
//	private static Bundle namespaces = new Bundle();
	
	private static boolean securityEnabled = false;

	protected static IIDFactory instance = null;

	static {
		instance = new IDFactory();
		addNamespace0(new StringID.StringIDNamespace());
		addNamespace0(new GUID.GUIDNamespace());
		addNamespace0(new LongID.LongNamespace());
	}

	private synchronized static void initialize() {
		if (!initialized) {
			final Namespace ns = new StringID.StringIDNamespace("ecftcp", "Name space for ecftcp");
			Log.i("IDFactory", "Name space init: "+ns.getName());
			if( !namespaces.contains(ns))
				IDFactory.addNamespace0(ns);
			initialized = true;
		}
	}

	private static boolean initialized = false;

	public synchronized static IIDFactory getDefault() {
		return instance;
	}

	private IDFactory() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.core.identity.IIDFactory#addNamespace(org.eclipse.ecf
	 * .core.identity.Namespace)
	 */
	public Namespace addNamespace(Namespace namespace) throws SecurityException {
		if (namespace == null)
			return null;
		checkPermission(new NamespacePermission(namespace.toString(),
				NamespacePermission.ADD_NAMESPACE));
		initialize();
		Namespace result = addNamespace0(namespace);
		return result;
	}

	public final static Namespace addNamespace0(Namespace namespace) {
		if (namespace == null)
			return null;
		return (Namespace) namespaces.put(namespace.getName(), namespace);
	}

	protected final static void checkPermission(
			NamespacePermission namespacepermission) throws SecurityException {
		if (securityEnabled)
			AccessController.checkPermission(namespacepermission);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.core.identity.IIDFactory#containsNamespace(org.eclipse
	 * .ecf.core.identity.Namespace)
	 */
	public boolean containsNamespace(Namespace namespace)
			throws SecurityException {
		if (namespace == null)
			return false;
		checkPermission(new NamespacePermission(namespace.toString(),
				NamespacePermission.CONTAINS_NAMESPACE));
		initialize();
		boolean result = containsNamespace0(namespace);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.IIDFactory#getNamespaces()
	 */
	public List getNamespaces() {
		initialize();
		return new ArrayList(namespaces.values());
	}

	public final static boolean containsNamespace0(Namespace n) {
		if (n == null)
			return false;
		return namespaces.containsKey(n.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.core.identity.IIDFactory#getNamespace(org.eclipse.ecf
	 * .core.identity.Namespace)
	 */
	public Namespace getNamespace(Namespace namespace) throws SecurityException {
		if (namespace == null)
			return null;
		checkPermission(new NamespacePermission(namespace.toString(),
				NamespacePermission.GET_NAMESPACE));
		initialize();
		Namespace result = getNamespace0(namespace);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.core.identity.IIDFactory#getNamespaceByName(java.lang
	 * .String)
	 */
	public Namespace getNamespaceByName(String name) throws SecurityException {
		initialize();
		Namespace result = getNamespace0(name);
		Log.i("IDFactory", result.toString());
		return result;
	}

	protected final static Namespace getNamespace0(Namespace n) {
		if (n == null)
			return null;
		return (Namespace) namespaces.get(n.getName());
	}

	protected final static Namespace getNamespace0(String name) {
		if (name == null)
			return null;
		return (Namespace) namespaces.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.IIDFactory#createGUID()
	 */
	public ID createGUID() throws IDCreateException {
		return createGUID(GUID.DEFAULT_BYTE_LENGTH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.IIDFactory#createGUID(int)
	 */
	public ID createGUID(int length) throws IDCreateException {
		Namespace namespace = new GUID.GUIDNamespace();
		ID result = createID(namespace, new Integer[] { new Integer(length) });
		return result;
	}

	protected static void logAndThrow(String s, Throwable t)
			throws IDCreateException {
		IDCreateException e = null;
		if (t != null) {
			e = new IDCreateException(s + ": " + t.getClass().getName() + ": " //$NON-NLS-1$ //$NON-NLS-2$
					+ t.getMessage(), t);
		} else {
			e = new IDCreateException(s);
		}
		Log.e("IDFactory", s, e);
		throw e;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.core.identity.IIDFactory#createID(org.eclipse.ecf.core
	 * .identity.Namespace, java.lang.Object[])
	 */
	public ID createID(Namespace n, Object[] args) throws IDCreateException {
		// Verify namespace is non-null
		if (n == null)
			logAndThrow("Namespace cannot be null", null); //$NON-NLS-1$
		initialize();
		// Make sure that namespace is in table of known namespace. If not,
		// throw...we don't create any instances that we don't know about!
		Namespace ns = getNamespace0(n);
		if (ns == null)
			logAndThrow(NLS.bind("Namespace {0} not found", n.getName()), null); //$NON-NLS-1$
		// We're OK, go ahead and setup array of classes for call to
		// instantiator
		// Ask instantiator to actually create instance
		ID result = ns.createInstance(args);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.IIDFactory#createID(java.lang.String,
	 * java.lang.Object[])
	 */
	public ID createID(String namespaceName, Object[] args)
			throws IDCreateException {
		Namespace n = getNamespaceByName(namespaceName);
		if (n == null)
			throw new IDCreateException(NLS.bind(
					"Namespace {0} not found", namespaceName)); //$NON-NLS-1$
		return createID(n, args);
	}

	public ID createID(Namespace namespace, String uri)
			throws IDCreateException {
		return createID(namespace, new Object[] { uri });
	}

	public ID createID(String namespace, String uri) throws IDCreateException {
		return createID(namespace, new Object[] { uri });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.core.identity.IIDFactory#createStringID(java.lang.String)
	 */
	public ID createStringID(String idstring) throws IDCreateException {
		if (idstring == null)
			throw new IDCreateException("StringID cannot be null"); //$NON-NLS-1$
		Namespace n = new StringID.StringIDNamespace();
		return createID(n, new String[] { idstring });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.IIDFactory#createLongID(long)
	 */
	public ID createLongID(long l) throws IDCreateException {
		Namespace n = new LongID.LongNamespace();
		return createID(n, new Long[] { new Long(l) });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.core.identity.IIDFactory#removeNamespace(org.eclipse.
	 * ecf.core.identity.Namespace)
	 */
	public Namespace removeNamespace(Namespace n) throws SecurityException {
		if (n == null)
			return null;
		checkPermission(new NamespacePermission(n.toString(),
				NamespacePermission.REMOVE_NAMESPACE));
		initialize();
		Namespace result = removeNamespace0(n);
		return result;
	}

	protected final static Namespace removeNamespace0(Namespace n) {
		if (n == null)
			return null;
		return (Namespace) namespaces.remove(n.getName());
	}
}