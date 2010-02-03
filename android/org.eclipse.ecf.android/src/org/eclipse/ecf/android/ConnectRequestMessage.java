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

package org.eclipse.ecf.android;

import java.io.Serializable;
import java.net.URI;

import android.os.Message;

public class ConnectRequestMessage implements Serializable  {
	private static final long serialVersionUID = 3257844363974226229L;
	URI target;
    Serializable data;
    Message message;
    
    public ConnectRequestMessage(URI target, Serializable data) {
        this.target = target;
        this.data = data;
    }

    public URI getTarget() {
        return target;
    }

    public Serializable getData() {
        return data;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("ConnectRequestMessage["); //$NON-NLS-1$
        buf.append(target).append(";").append(data).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
        return buf.toString();
    }
}