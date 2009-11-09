/****************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.example.collab;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.remoteservice.eventadmin.DistributedEventAdmin;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The main plug-in class to be used in the desktop.
 */
public class ClientPlugin extends AbstractUIPlugin implements ClientPluginConstants {
	public static final String PLUGIN_ID = "org.eclipse.ecf.example.collab"; //$NON-NLS-1$

	public static final String COLLABORATION_IMAGE = "collaboration"; //$NON-NLS-1$
	public static final String DEFAULT_TOPIC = "defaultTopic";

	// The shared instance.
	private static ClientPlugin plugin;

	private FontRegistry fontRegistry = null;

	private ServerStartup serverStartup = null;

	// processArgs
	protected String containerType = "ecf.jgroups.client";
	protected ID containerID = null;
	protected String targetId = "jgroups:///soladhoc";
	protected String topic = "admin";

	private BundleContext context;
	private final Object appLock = new Object();
	protected boolean done = false;

	private DistributedEventAdmin eventAdminImpl = null;
	protected ServiceTracker containerManagerTracker;
	protected ServiceRegistration eventAdminRegistration;

	//	My Moderator
	protected IContainer jGroupsModerator;
	public IContainerManager containerManager;

	public EventAdmin getEventAdminImpl() {
		return eventAdminImpl;
	}

	public static void log(String message) {
		getDefault().getLog().log(new Status(IStatus.OK, ClientPlugin.getDefault().getBundle().getSymbolicName(), IStatus.OK, message, null));
	}

	public static void log(String message, Throwable e) {
		getDefault().getLog().log(new Status(IStatus.ERROR, ClientPlugin.getDefault().getBundle().getSymbolicName(), IStatus.OK, message, e));
	}

	/**
	 * The constructor.
	 */
	public ClientPlugin() {
		super();
		plugin = this;
		this.fontRegistry = new FontRegistry();
	}

	protected void setPreferenceDefaults() {
		this.getPreferenceStore().setDefault(ClientPlugin.PREF_USE_CHAT_WINDOW, false);
		this.getPreferenceStore().setDefault(ClientPlugin.PREF_DISPLAY_TIMESTAMP, true);
		this.getPreferenceStore().setDefault(ClientPlugin.DEFAULT_TOPIC, true);
		this.getPreferenceStore().setDefault(ClientPlugin.PREF_CONFIRM_FILE_SEND, true);
		this.getPreferenceStore().setDefault(ClientPlugin.PREF_CONFIRM_REMOTE_VIEW, true);
		this.getPreferenceStore().setDefault(ClientPlugin.PREF_START_SERVER, false);
		this.getPreferenceStore().setDefault(ClientPlugin.PREF_REGISTER_SERVER, false);
		this.getPreferenceStore().setDefault(ClientPlugin.PREF_SHAREDEDITOR_PLAY_EVENTS_IMMEDIATELY, true);
		this.getPreferenceStore().setDefault(ClientPlugin.PREF_SHAREDEDITOR_ASK_RECEIVER, true);
		//		this.getPreferenceStore().setDefault(ClientPlugin.imageDescriptorFromPlugin(pluginId, imageFilePath), true);
	}

	/**
	 * This method is called upon plug-in activation
	 * @param ctxt 
	 * @throws Exception 
	 */
	public void start(BundleContext ctxt) throws Exception {
		super.start(ctxt);
		//		this.containerManager = this.getContainerManager();
		setPreferenceDefaults(); //AFAIRE [pierre] why is it compulsory
		this.context = ctxt;
	}

	public BundleContext getContext() {
		return context;
	}

	public synchronized void initServer() throws Exception {
		if (serverStartup == null) {
			serverStartup = new ServerStartup();
		}
	}

	public synchronized boolean isServerActive() {
		if (serverStartup == null)
			return false;
		else
			return serverStartup.isActive();
	}

	public synchronized void disposeServer() {
		if (serverStartup != null) {
			serverStartup.dispose();
			serverStartup = null;
		}
	}

	/**
	 * This method is called when the plug-in is stopped
	 * @param context 
	 * @throws Exception 
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		context = null;
		stop();
	}

	public FontRegistry getFontRegistry() {
		return this.fontRegistry;
	}

	public Shell getActiveShell() {
		return this.getWorkbench().getDisplay().getActiveShell();
	}

	protected void initializeImageRegistry(ImageRegistry registry) {
		registry.put(COLLABORATION_IMAGE, AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, "icons/collaboration.gif")); //$NON-NLS-1$
	}

	public IContainerManager getContainerManager() {
		if (containerManagerTracker == null) {
			containerManagerTracker = new ServiceTracker(plugin.getContext(), IContainerManager.class.getName(), null);
			containerManagerTracker.open();
		}
		return (IContainerManager) containerManagerTracker.getService();
	}

	protected void stop() {
		if (eventAdminRegistration != null) {
			eventAdminRegistration.unregister();
			eventAdminRegistration = null;
		}
		if (jGroupsModerator != null) {
			jGroupsModerator.dispose();
			getContainerManager().removeAllContainers();
			jGroupsModerator = null;
		}
		if (containerManagerTracker != null) {
			containerManagerTracker.close();
			containerManagerTracker = null;
		}
		synchronized (appLock) {
			done = true;
			appLock.notifyAll();
		}
		context = null;
	}

	/**
	 * Returns the shared instance.
	 * @return default client plugin
	 */
	public static ClientPlugin getDefault() {
		return plugin;
	}
}
