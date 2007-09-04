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

package org.eclipse.ecf.internal.server.jgroups;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.provider.generic.SOContainerConfig;
import org.eclipse.ecf.provider.jgroups.container.JGroupsManagerContainer;
import org.eclipse.ecf.provider.jgroups.identity.JGroupsID;
import org.eclipse.ecf.provider.jgroups.identity.JGroupsNamespace;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * JGroups Manager Application.
 */
public class JGroupsManager implements IApplication {

	private JGroupsManagerContainer managerContainer = null;

	private String[] mungeArguments(String originalArgs[]) {
		if (originalArgs == null)
			return new String[0];
		final List<String> l = new ArrayList<String>();
		for (int i = 0; i < originalArgs.length; i++)
			if (!originalArgs[i].equals("-pdelaunch")) //$NON-NLS-1$
				l.add(originalArgs[i]);
		return l.toArray(new String[] {});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {
		final String[] args = mungeArguments((String[]) context.getArguments().get("application.args")); //$NON-NLS-1$
		if (args.length < 1) {
			usage();
			return IApplication.EXIT_OK;
		} else {
			// Create manager ID
			final JGroupsID managerID = (JGroupsID) IDFactory.getDefault().createID(IDFactory.getDefault().getNamespaceByName(JGroupsNamespace.NAME), args[0]);
			// Create config
			final SOContainerConfig config = new SOContainerConfig(managerID);

			synchronized (this) {
				managerContainer = new JGroupsManagerContainer(config);
				managerContainer.start();
				System.out.println("JGroups Manager started with id=" + managerID);
				// Wait until stopped
				this.wait();
			}
			return IApplication.EXIT_OK;

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		synchronized (this) {
			if (managerContainer != null) {
				managerContainer.dispose();
				managerContainer = null;
				this.notifyAll();
			}
		}
	}

	private void usage() {
		System.out.println("Usage: eclipse.exe -application " //$NON-NLS-1$
				+ this.getClass().getName() + "jgroupsChannelName"); //$NON-NLS-1$
		System.out.println("   Examples: eclipse -application org.eclipse.ecf.provider.jgroups.JGroupsManager jgroupsChannel"); //$NON-NLS-1$
	}

}
