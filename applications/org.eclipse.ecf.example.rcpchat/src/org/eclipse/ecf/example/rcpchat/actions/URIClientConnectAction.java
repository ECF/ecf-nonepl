/*******************************************************************************
 * Copyright (c) 2005 Ed Burnette, Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ed Burnette, Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.example.rcpchat.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.example.rcpchat.RcpChatPlugin;
import org.eclipse.ecf.example.rcpchat.client.Client;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class URIClientConnectAction implements IWorkbenchWindowActionDelegate {
    
    protected String containerType = null;
    protected String uri = null;
    protected String nickname = null;
    protected Object data = null;
    protected String projectName = null;

    public URIClientConnectAction() {
    }
    public URIClientConnectAction(String containerType, String uri, String nickname, Object data) {
    	this();
    	this.containerType = containerType;
    	this.uri = uri;
    	this.nickname = nickname;
        this.data = data;
    }
    public class ClientMultiStatus extends MultiStatus {

		public ClientMultiStatus(String pluginId, int code, IStatus[] newChildren, String message, Throwable exception) {
			super(pluginId, code, newChildren, message, exception);
		}
		public ClientMultiStatus(String pluginId, int code, String message, Throwable exception) {
			super(pluginId, code, message, exception);
		}
    }
    protected void showExceptionInMultiStatus(int code, MultiStatus status, Throwable t) {
    	String msg = t.getMessage();
    	status.add(new Status(IStatus.ERROR,RcpChatPlugin.PLUGIN_ID,code++,msg,null));
    	StackTraceElement [] stack = t.getStackTrace();
    	for(int i=0; i < stack.length; i++) {
    		status.add(new Status(IStatus.ERROR,RcpChatPlugin.PLUGIN_ID,code++,"     "+stack[i],null));
    	}
    	Throwable cause = t.getCause();
    	if (cause != null) {
    		status.add(new Status(IStatus.ERROR,RcpChatPlugin.PLUGIN_ID,code++,"Caused By: ",null));
    		showExceptionInMultiStatus(code,status,cause);
    	}
    }
	public class ClientConnectJob extends Job {
        public ClientConnectJob(String name) {
            super(name);
        }
        public IStatus run(IProgressMonitor pm) {
        	String failMsg = "Connect to "+uri+" failed";
        	ClientMultiStatus status = new ClientMultiStatus(RcpChatPlugin.PLUGIN_ID, 0,
                    failMsg, null);
            try {
            	Client client = new Client();
                client.createAndConnectClient(containerType, uri,nickname, data);
                return status;
            } catch (Exception e) {
            	showExceptionInMultiStatus(15555,status,e);
                return status;
            }
        }        
    }
	public void run(IAction action) {
        ClientConnectJob clientConnect = new ClientConnectJob("Connect for "+projectName);
        clientConnect.schedule();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {

	}

	public void init(IWorkbenchWindow window) {
	}
}