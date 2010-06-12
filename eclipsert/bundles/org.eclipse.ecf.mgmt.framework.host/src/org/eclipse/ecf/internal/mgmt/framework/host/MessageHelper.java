/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.mgmt.framework.host;

import org.eclipse.osgi.service.resolver.BundleSpecification;
import org.eclipse.osgi.service.resolver.ImportPackageSpecification;
import org.eclipse.osgi.service.resolver.VersionConstraint;
import org.eclipse.osgi.util.NLS;

public class MessageHelper {

	public MessageHelper() {
	}

	public static String getResolutionFailureMessage(
			VersionConstraint unsatisfied) {
		if (unsatisfied.isResolved())
			throw new IllegalArgumentException();
		if (unsatisfied instanceof ImportPackageSpecification)
			return NLS.bind("Missing imported package {0}", //$NON-NLS-1$
					toString(unsatisfied));
		if (unsatisfied instanceof BundleSpecification) {
			if (((BundleSpecification) unsatisfied).isOptional())
				return NLS.bind("Missing optionally required bundle {0}", //$NON-NLS-1$
						toString(unsatisfied));
			else
				return NLS.bind("Missing required bundle {0}", //$NON-NLS-1$
						toString(unsatisfied));
		} else {
			return NLS.bind("Missing host {0}", toString(unsatisfied)); //$NON-NLS-1$
		}
	}

	private static String toString(VersionConstraint constraint) {
		org.eclipse.osgi.service.resolver.VersionRange versionRange = constraint
				.getVersionRange();
		if (versionRange == null)
			return constraint.getName();
		else
			return constraint.getName() + '_' + versionRange;
	}

}
