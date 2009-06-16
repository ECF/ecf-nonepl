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

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.provider.jgroups.container.JGroupsManagerContainer;
import org.eclipse.ecf.provider.jgroups.identity.JGroupsNamespace;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * JGroups Manager Application.
 */
public class JGroupsManager implements IApplication, IJGroupsManager {

	private IContainer managerContainer = null;
	private IApplicationContext appContext=null;
	private static String jgURL;
	private static Object lock=new Object();
	
	private String[] mungeArguments(String originalArgs[]) {
		if (originalArgs == null)
			return new String[0];
		final List<String> l = new ArrayList<String>();
		for (int i = 0; i < originalArgs.length; i++)
			if (!originalArgs[i].equals("-pdelaunch")) //$NON-NLS-1$
				l.add(originalArgs[i]);
		return l.toArray(new String[] {});
	}

	public Object start(IApplicationContext context) throws Exception {
		final String[] args = mungeArguments((String[]) context.getArguments().get("application.args")); //$NON-NLS-1$
		if (args.length < 1) {
			usage();
			return IApplication.EXIT_OK;
		} else {
			this.appContext=context;
			jgURL=args[0];
			
			synchronized (this ) {
				managerContainer = createServer();
				System.out.println("JGroups Manager started with id=" + ((JGroupsManagerContainer)managerContainer).getID());
				this.wait( );
			}
			return IApplication.EXIT_OK;
		}
	}

    private static ID getServerIdentity() throws IDCreateException, URISyntaxException {
    	return IDFactory.getDefault().createID("ecf.namespace.jgroupsid",jgURL);
    }

    protected static IContainer createServer() throws Exception {
    	final ID myID=getServerIdentity();
        return ContainerFactory.getDefault().createContainer(getServerContainerName(), new Object[] { myID });
    }
    private static String getServerContainerName() {
        return "ecf.jgroups.manager";
    }


	public void stop() {
		synchronized (this) {
			if (managerContainer != null) {
				managerContainer.dispose();
				managerContainer = null;
				this.notifyAll();
			}
		}
		appContext=null;
	}

	private void usage() {
		System.out.println("Usage: eclipse.exe -application " //$NON-NLS-1$
				+ this.getClass().getName() + " jgroups://host/<jgroupsChannelName>&stackName=<stackName>"); //$NON-NLS-1$
		System.out.println("   Examples: eclipse -application org.eclipse.ecf.provider.jgroups.JGroupsManager jgroups:///jgroupsChannel"); //$NON-NLS-1$
	}

	public ID getManagerID() {
		return this.managerContainer.getID();
	}

}
