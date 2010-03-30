/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.p2.profile.host;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.internal.mgmt.p2.profile.host.Activator;
import org.eclipse.ecf.mgmt.p2.profile.IProfileInfo;
import org.eclipse.ecf.mgmt.p2.profile.IProfileManager;
import org.eclipse.ecf.mgmt.p2.profile.ProfileInfo;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.osgi.service.environment.EnvironmentInfo;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class ProfileManager implements IProfileManager, IAdaptable {

	private BundleContext context;
	private IProvisioningAgent agent;
	private ServiceTracker environmentInfoTracker;

	public ProfileManager(BundleContext context, IProvisioningAgent agent) {
		this.context = context;
		this.agent = agent;
	}

	private EnvironmentInfo getEnvironmentInfo() {
		if (environmentInfoTracker == null) {
			environmentInfoTracker = new ServiceTracker(context,
					org.eclipse.osgi.service.environment.EnvironmentInfo.class
							.getName(), null);
			environmentInfoTracker.open();
		}
		return (EnvironmentInfo) environmentInfoTracker.getService();
	}

	public IStatus addProfile(String profileId, Map properties) {
		IProfileRegistry profileRegistry = (IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME);
		if (profileRegistry == null)
			return createErrorStatus("Profile registry is null");

		if (profileId == null) profileId = IProfileRegistry.SELF;
		
		if (properties == null)
			properties = new HashMap();
		if (properties.get("org.eclipse.equinox.p2.environments") == null) {
			EnvironmentInfo info = getEnvironmentInfo();
			if (info != null)
				properties.put("org.eclipse.equinox.p2.environments",
						"osgi.os=" + info.getOS() + ",osgi.ws=" + info.getWS()
								+ ",osgi.arch=" + info.getOSArch());
			else
				properties.put("org.eclipse.equinox.p2.environments", "");
		}

		try {
			profileRegistry.addProfile(profileId, properties);
			return new SerializableStatus(Status.OK_STATUS);
		} catch (ProvisionException e) {
			return createErrorStatus("Could not add profile id=" + profileId, e);
		}
	}

	public IStatus removeProfile(String profileId) {
		if (profileId == null)
			return createErrorStatus("Cannot remove self profile");
		IProfileRegistry profileRegistry = (IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME);
		if (profileRegistry == null)
			return createErrorStatus("Profile registry is null");
		profileRegistry.removeProfile(profileId);
		return new SerializableStatus(Status.OK_STATUS);
	}

	public String[] getProfileIds() {
		IProfileRegistry profileRegistry = (IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME);
		if (profileRegistry == null)
			return null;
		IProfile profiles[] = profileRegistry.getProfiles();
		if (profiles == null)
			return null;
		String ids[] = new String[profiles.length];
		for (int i = 0; i < profiles.length; i++)
			ids[i] = profiles[i].getProfileId();
		return ids;
	}

	public IProfileInfo getProfile(String profileId) {
		if (profileId == null)
			profileId = IProfileRegistry.SELF;
		IProfileRegistry profileRegistry = (IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME);
		if (profileRegistry == null)
			return null;
		IProfile profile = profileRegistry.getProfile(profileId);
		return profile == null ? null : new ProfileInfo(profile);
	}

	public IProfileInfo[] getProfiles() {
		IProfileRegistry profileRegistry = (IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME);
		if (profileRegistry == null)
			return null;
		IProfile profiles[] = profileRegistry.getProfiles();
		List results = new ArrayList();
		for (int i = 0; i < profiles.length; i++)
			if (profiles[i] != null)
				results.add(new ProfileInfo(profiles[i]));

		return (IProfileInfo[]) results.toArray(new IProfileInfo[0]);
	}

	public Object getAdapter(Class adapter) {
		if (adapter.isInstance(this)) {
			return this;
		}
		final IAdapterManager adapterManager = Activator.getDefault()
				.getAdapterManager();
		if (adapterManager == null)
			return null;
		return adapterManager.loadAdapter(this, adapter.getName());
	}

	public void close() {
		if (environmentInfoTracker != null) {
			environmentInfoTracker.close();
			environmentInfoTracker = null;
		}
		this.agent = null;
		this.context = null;
	}

	private IStatus createErrorStatus(String message, Throwable t) {
		return new SerializableStatus(IStatus.ERROR, Activator.PLUGIN_ID,
				IStatus.ERROR, message, t);
	}

	private IStatus createErrorStatus(String message) {
		return createErrorStatus(message, null);
	}

}
