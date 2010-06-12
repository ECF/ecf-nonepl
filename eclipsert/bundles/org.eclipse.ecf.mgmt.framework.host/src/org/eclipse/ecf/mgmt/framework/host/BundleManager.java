/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.host;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.status.SerializableMultiStatus;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.internal.mgmt.framework.host.Activator;
import org.eclipse.ecf.internal.mgmt.framework.host.MessageHelper;
import org.eclipse.ecf.mgmt.framework.BundleInfo;
import org.eclipse.ecf.mgmt.framework.IBundleId;
import org.eclipse.ecf.mgmt.framework.IBundleInfo;
import org.eclipse.ecf.mgmt.framework.IBundleManager;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.PlatformAdmin;
import org.eclipse.osgi.service.resolver.ResolverError;
import org.eclipse.osgi.service.resolver.State;
import org.eclipse.osgi.service.resolver.VersionConstraint;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.log.LogService;

public class BundleManager extends AbstractFrameworkManager implements
		IBundleManager, IAdaptable {

	public BundleManager(BundleContext context, LogService logger) {
		super(context, logger);
	}

	public BundleManager(BundleContext context) {
		this(context, null);
	}

	public String[] getBundleSymbolicIds() {
		Bundle bundles[] = getAllBundles();
		if (bundles == null)
			return null;
		String ids[] = new String[bundles.length];
		for (int i = 0; i < ids.length; i++)
			ids[i] = bundles[i].getSymbolicName();

		return ids;
	}

	public IBundleInfo[] getBundles(IBundleId bundleId) {
		Bundle bundles[] = (bundleId == null) ? getAllBundles()
				: internalGetBundles(bundleId.getSymbolicName(),
						bundleId.getVersion());
		if (bundles == null)
			return null;
		State state = getPlatformState();
		if (state == null)
			return null;
		List results = new ArrayList();
		for (int i = 0; i < bundles.length; i++)
			results.add(new BundleInfo(bundles[i], state.getBundle(bundles[i]
					.getBundleId())));

		return (IBundleInfo[]) results.toArray(new IBundleInfo[] {});
	}

	public IBundleInfo[] getBundles() {
		return getBundles((IBundleId) null);
	}

	public IBundleInfo getBundle(Long bundleid) {
		if (bundleid == null)
			return null;
		Bundle bundles[] = getAllBundles();
		if (bundles == null)
			return null;
		State state = getPlatformState();
		if (state == null)
			return null;
		for (int i = 0; i < bundles.length; i++)
			if (bundleid.longValue() == bundles[i].getBundleId())
				return new BundleInfo(bundles[i], state.getBundle(bundles[i]
						.getBundleId()));
		return null;
	}

	public IStatus start(IBundleId bundleId) {
		if (bundleId == null)
			return createErrorStatus("bundleId parameter cannot be null"); //$NON-NLS-1$
		String symbolicId = bundleId.getSymbolicName();
		String version = bundleId.getVersion();
		Bundle bs[] = internalGetBundles(symbolicId, version);
		if (bs == null || bs.length == 0)
			return createErrorStatus(symbolicId
					+ " with version " + version + " could not be found"); //$NON-NLS-1$//$NON-NLS-2$
		if (bs.length > 1)
			return createErrorStatus(symbolicId
					+ " with version " + version + " resulted in multiple bundles"); //$NON-NLS-1$ //$NON-NLS-2$

		return start(bs[0]);
	}

	public IStatus stop(IBundleId bundleId) {
		if (bundleId == null)
			return createErrorStatus("bundleId parameter cannot be null"); //$NON-NLS-1$
		String symbolicId = bundleId.getSymbolicName();
		String version = bundleId.getVersion();
		Bundle bs[] = internalGetBundles(symbolicId, version);
		if (bs == null || bs.length == 0)
			return createErrorStatus(symbolicId
					+ " with version " + version + " could not be found"); //$NON-NLS-1$//$NON-NLS-2$
		if (bs.length > 1)
			return createErrorStatus(symbolicId
					+ " with version " + version + " resulted in multiple bundles"); //$NON-NLS-1$ //$NON-NLS-2$
		return stop(bs[0]);
	}

	private IStatus start(Bundle bundle) {
		try {
			bundle.start();
			return new SerializableStatus(Status.OK_STATUS);
		} catch (BundleException e) {
			return createErrorStatus(
					"Exception starting " + bundle.getSymbolicName() + " version " + bundle.getVersion().toString(), e); //$NON-NLS-1$ //$NON-NLS-2$
		}

	}

	private IStatus stop(Bundle bundle) {
		try {
			bundle.stop();
			return new SerializableStatus(Status.OK_STATUS);
		} catch (BundleException e) {
			return createErrorStatus(
					"Exception stoping " + bundle.getSymbolicName() + " version " + bundle.getVersion().toString(), e); //$NON-NLS-1$ //$NON-NLS-2$
		}

	}

	public IStatus diagnose(IBundleId bundleId) {
		if (bundleId == null)
			return null;
		PlatformAdmin platformAdmin = getPlatformAdmin();
		if (platformAdmin == null)
			return null;
		String symbolicName = bundleId.getSymbolicName();
		String version = bundleId.getVersion();
		Bundle bs[] = internalGetBundles(symbolicName, bundleId.getVersion());
		if (bs == null || bs.length == 0)
			return createErrorStatus(symbolicName
					+ " with version " + version + " could not be found"); //$NON-NLS-1$//$NON-NLS-2$
		if (bs.length > 1)
			return createErrorStatus(symbolicName
					+ " with version " + version + " resulted in multiple bundles"); //$NON-NLS-1$ //$NON-NLS-2$
		BundleDescription desc = platformAdmin.getState(false).getBundle(
				bs[0].getBundleId());
		ResolverError resolverErrors[] = platformAdmin.getState(false)
				.getResolverErrors(desc);
		SerializableMultiStatus problems = new SerializableMultiStatus(
				Activator.PLUGIN_ID, IStatus.INFO,
				"The following problems were found:", null); //$NON-NLS-1$
		for (int i = 0; i < resolverErrors.length; i++) {
			SerializableStatus status = new SerializableStatus(IStatus.WARNING,
					Activator.PLUGIN_ID, resolverErrors[i].toString());
			problems.add(status);
		}

		VersionConstraint unsatisfied[] = platformAdmin.getStateHelper()
				.getUnsatisfiedConstraints(desc);
		for (int i = 0; i < unsatisfied.length; i++) {
			SerializableStatus status = new SerializableStatus(2,
					Activator.PLUGIN_ID,
					MessageHelper.getResolutionFailureMessage(unsatisfied[i]));
			problems.add(status);
		}

		return problems;
	}

	protected Bundle[] internalGetBundles(String bundleSymbolicId,
			String version) {
		Bundle bundles[] = getAllBundles();
		if (bundles == null)
			return null;
		List results = new ArrayList();
		for (int i = 0; i < bundles.length; i++)
			if (bundleSymbolicId != null
					&& bundles[i].getSymbolicName().equals(bundleSymbolicId))
				if (version != null) {
					String bundleVersion = getBundleVersion(bundles[i]);
					if (bundleVersion != null && bundleVersion.equals(version))
						results.add(bundles[i]);
				} else {
					results.add(bundles[i]);
				}

		return (Bundle[]) results.toArray(new Bundle[0]);
	}

	protected Bundle internalGetBundle(long bundleId) {
		Bundle bundles[] = getAllBundles();
		if (bundles == null)
			return null;
		for (int i = 0; i < bundles.length; i++) {
			if (bundles[i].getBundleId() == bundleId)
				return bundles[i];
		}
		return null;
	}

	public IStatus start(Long bundleId) {
		if (bundleId == null)
			return createErrorStatus("bundleId parameter cannot be null"); //$NON-NLS-1$
		Bundle b = internalGetBundle(bundleId.longValue());
		if (b == null)
			createErrorStatus("Cannot find bundle with id=" + bundleId); //$NON-NLS-1$
		return start(b);
	}

	public IStatus stop(Long bundleId) {
		if (bundleId == null)
			return createErrorStatus("bundleId parameter cannot be null"); //$NON-NLS-1$
		Bundle b = internalGetBundle(bundleId.longValue());
		if (b == null)
			createErrorStatus("Cannot find bundle with id=" + bundleId); //$NON-NLS-1$
		return stop(b);
	}

}
