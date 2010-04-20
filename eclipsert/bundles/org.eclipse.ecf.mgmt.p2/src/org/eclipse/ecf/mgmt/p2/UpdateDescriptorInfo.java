/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.p2;

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;

import org.eclipse.equinox.p2.metadata.IUpdateDescriptor;

@SuppressWarnings("rawtypes")
public class UpdateDescriptorInfo implements IUpdateDescriptorInfo,
		Serializable {

	private static final long serialVersionUID = 6867054300450864175L;
	private URI location;
	private int severity;
	private String description;
	private Collection iusBeingUpdated;

	public UpdateDescriptorInfo(IUpdateDescriptor ud) {
		this.location = ud.getLocation();
		this.severity = ud.getSeverity();
		this.description = ud.getDescription();
		this.iusBeingUpdated = createIUsBeingUpdated(ud.getIUsBeingUpdated());
	}

	private Collection createIUsBeingUpdated(Collection iUsBeingUpdated2) {
		// TODO Auto-generated method stub
		return null;
	}

	public URI getLocation() {
		return location;
	}

	public Collection getIUSBeingUpdated() {
		return iusBeingUpdated;
	}

	public int getSeverity() {
		return severity;
	}

	public String getDescription() {
		return description;
	}

}
