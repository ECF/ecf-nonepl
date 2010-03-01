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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.BundleSpecification;
import org.eclipse.osgi.service.resolver.ExportPackageDescription;
import org.eclipse.osgi.service.resolver.ImportPackageSpecification;
import org.osgi.framework.Bundle;

public class BundleInfo implements IBundleInfo, Serializable {

	private static final long serialVersionUID = 4177563925164914277L;
	private BundleId bundleId;
	private String location;
	private long bundleid;
	private int state;
	private long lastModified;
	private Map manifest;
	private String failureMessage;
	private RequireBundleInfo requireBundles[];
	private ImportPackageInfo importPackages[];
	private ResolvedRequiredBundleInfo resolvedRequiredBundles[];
	private ResolvedImportedPackageInfo resolvedImportedPackages[];
	private ExportPackageInfo exportPackages[];
	private BundleId dependents[];
	private ExportPackageInfo selectedExports[];
	private boolean singleton;

	public BundleInfo(Bundle bundle, BundleDescription bundleDescription) {
		bundleId = new BundleId(bundle.getSymbolicName(),
				bundle.getVersion().toString());
		location = bundle.getLocation();
		bundleid = bundle.getBundleId();
		state = bundle.getState();
		lastModified = bundle.getLastModified();
		manifest = convertDictionaryToMap(bundle.getHeaders());
		
		singleton = bundleDescription.isSingleton();
		
		// require bundles
		BundleSpecification[] bdRequiredBundles = bundleDescription.getRequiredBundles();
		requireBundles = new RequireBundleInfo[bdRequiredBundles.length];
		for (int i = 0; i < bdRequiredBundles.length; i++) requireBundles[i] = new RequireBundleInfo(bdRequiredBundles[i]);

		// import packages
		ImportPackageSpecification[] bdImportPackages = bundleDescription.getImportPackages();
		importPackages = new ImportPackageInfo[bdImportPackages.length];
		for (int i = 0; i < bdImportPackages.length; i++) importPackages[i] = new ImportPackageInfo(bdImportPackages[i]);

		// resolved required
		BundleDescription bdResolvedRequires[] = bundleDescription.getResolvedRequires();
		resolvedRequiredBundles = new ResolvedRequiredBundleInfo[bdResolvedRequires.length];
		for (int i = 0; i < bdResolvedRequires.length; i++) resolvedRequiredBundles[i] = new ResolvedRequiredBundleInfo(bdResolvedRequires[i]);

		// resolved imports
		ExportPackageDescription bdResolvedImports[] = bundleDescription.getResolvedImports();
		resolvedImportedPackages = new ResolvedImportedPackageInfo[bdResolvedImports.length];
		for (int i = 0; i < bdResolvedImports.length; i++) resolvedImportedPackages[i] = new ResolvedImportedPackageInfo(bdResolvedImports[i]);

		// export packages
		ExportPackageDescription bdExportPackage[] = bundleDescription.getExportPackages();
		exportPackages = new ExportPackageInfo[bdExportPackage.length];
		for (int i = 0; i < bdExportPackage.length; i++) exportPackages[i] = new ExportPackageInfo(bdExportPackage[i]);

		// dependents
		BundleDescription bdDependents[] = bundleDescription.getDependents();
		dependents = new BundleId[bdDependents.length];
		for (int i = 0; i < bdDependents.length; i++) dependents[i] = new BundleId(bdDependents[i].getSymbolicName(),
				bdDependents[i].getVersion().toString());

		// selected export packages
		ExportPackageDescription bdSelectedExports[] = bundleDescription.getSelectedExports();
		selectedExports = new ExportPackageInfo[bdSelectedExports.length];
		for (int i = 0; i < bdSelectedExports.length; i++) selectedExports[i] = new ExportPackageInfo(bdSelectedExports[i]);

	}
	
	private Map convertDictionaryToMap(Dictionary dict) {
		Map result = new Properties();
		for (Enumeration e = dict.keys(); e.hasMoreElements(); ) {
			String key = (String) e.nextElement();
			result.put(key, dict.get(key));
		}
		return result;
	}
	
	public IBundleId getBundleId() {
		return bundleId;
	}

	public String getLocation() {
		return location;
	}

	public long getId() {
		return bundleid;
	}

	public int getState() {
		return state;
	}

	public long getLastModified() {
		return lastModified;
	}

	public Map getManifest() {
		return manifest;
	}

	public String getVersion() {
		return bundleId.getVersion();
	}

	public String getFragmentHost() {
		Object result = manifest.get("Fragment-Host"); //$NON-NLS-1$
		if (result instanceof String) return (String) result;
		return null;
	}

	public boolean isFragment() {
		return getFragmentHost() != null;
	}

	public boolean isSingleton() {
		return singleton;
	}

	public String getResolutionFailureMessage() {
		return failureMessage;
	}

	public IImportPackageInfo[] getImportPackages() {
		return importPackages;
	}

	public IRequireBundleInfo[] getRequireBundles() {
		return requireBundles;
	}

	public IResolvedImportedPackageInfo[] getResolvedImportedPackages() {
		return resolvedImportedPackages;
	}

	public IResolvedRequiredBundleInfo[] getResolvedRequiredBundles() {
		return resolvedRequiredBundles;
	}

	public IExportPackageInfo[] getExportPackages() {
		return exportPackages;
	}

	public IExportPackageInfo[] getSelectedExportPackages() {
		return selectedExports;
	}

	public IBundleId[] getDependents() {
		return dependents;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("BundleInfo[bundleId="); //$NON-NLS-1$
		buffer.append(bundleId);
		buffer.append(", location="); //$NON-NLS-1$
		buffer.append(location);
		buffer.append(", bundleid="); //$NON-NLS-1$
		buffer.append(bundleid);
		buffer.append(", state="); //$NON-NLS-1$
		buffer.append(state);
		buffer.append(", lastModified="); //$NON-NLS-1$
		buffer.append(lastModified);
		buffer.append(", manifest="); //$NON-NLS-1$
		buffer.append(manifest);
		buffer.append(", failureMessage="); //$NON-NLS-1$
		buffer.append(failureMessage);
		buffer.append(", requireBundles="); //$NON-NLS-1$
		buffer.append(requireBundles != null ? Arrays.asList(requireBundles)
				: null);
		buffer.append(", importPackages="); //$NON-NLS-1$
		buffer.append(importPackages != null ? Arrays.asList(importPackages)
				: null);
		buffer.append(", resolvedRequiredBundles="); //$NON-NLS-1$
		buffer.append(resolvedRequiredBundles != null ? Arrays
				.asList(resolvedRequiredBundles) : null);
		buffer.append(", resolvedImportedPackages="); //$NON-NLS-1$
		buffer.append(resolvedImportedPackages != null ? Arrays
				.asList(resolvedImportedPackages) : null);
		buffer.append(", exportPackages="); //$NON-NLS-1$
		buffer.append(exportPackages != null ? Arrays.asList(exportPackages)
				: null);
		buffer.append(", dependents="); //$NON-NLS-1$
		buffer.append(dependents != null ? Arrays.asList(dependents) : null);
		buffer.append(", selectedExports="); //$NON-NLS-1$
		buffer.append(selectedExports != null ? Arrays.asList(selectedExports)
				: null);
		buffer.append(", singleton="); //$NON-NLS-1$
		buffer.append(singleton);
		buffer.append("]"); //$NON-NLS-1$
		return buffer.toString();
	}

}
