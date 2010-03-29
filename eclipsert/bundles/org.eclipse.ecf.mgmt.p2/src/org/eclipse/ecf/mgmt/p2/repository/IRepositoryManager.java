/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.p2.repository;

import java.net.URI;

import org.eclipse.core.runtime.IStatus;

public interface IRepositoryManager {

	public URI[] getKnownMetadataRepositories(Integer flags);

	public URI[] getKnownMetadataRepositories();

	public URI[] getKnownArtifactRepositories(Integer flags);

	public URI[] getKnownArtifactRepositories();

	public IStatus addArtifactRepository(URI location, Integer flags);

	public IStatus addArtifactRepository(URI location);

	public IStatus addMetadataRepository(URI location, Integer flags);

	public IStatus addMetadataRepository(URI location);

	public IStatus removeArtifactRepository(URI location);

	public IStatus removeMetadataRepository(URI location);

	public IStatus addRepository(URI location, Integer flags);

	public IStatus addRepository(URI location);

	public IStatus removeRepository(URI location);

	public IStatus refreshArtifactRepository(URI location);

	public IStatus refreshMetadataRepository(URI location);

	public IStatus refreshRepository(URI location);

	public IRepositoryInfo[] getArtifactRepositoryInfo(Integer flags);

	public IRepositoryInfo[] getArtifactRepositoryInfo();

	public IRepositoryInfo getArtifactRepositoryInfo(URI location, Integer flags);

	public IRepositoryInfo getArtifactRepositoryInfo(URI location);

	public IRepositoryInfo[] getMetadataRepositoryInfo(Integer flags);

	public IRepositoryInfo[] getMetadataRepositoryInfo();

	public IRepositoryInfo getMetadataRepositoryInfo(URI location, Integer flags);

	public IRepositoryInfo getMetadataRepositoryInfo(URI location);

	public IInstallableUnitInfo[] getFeatures(URI location);
}
