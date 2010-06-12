/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework;

import java.util.Map;

public interface IBundleInfo {

	public IBundleId getBundleId();

	public long getId();

	public int getState();

	public String getLocation();

	public long getLastModified();

	public Map getManifest();

	public boolean isFragment();

	public boolean isSingleton();

	public String getFragmentHost();

	public String getResolutionFailureMessage();

	public IRequireBundleInfo[] getRequireBundles();

	public IImportPackageInfo[] getImportPackages();

	public IResolvedRequiredBundleInfo[] getResolvedRequiredBundles();

	public IResolvedImportedPackageInfo[] getResolvedImportedPackages();

	public IExportPackageInfo[] getExportPackages();

	public IExportPackageInfo[] getSelectedExportPackages();

	public IBundleId[] getDependents();
}
