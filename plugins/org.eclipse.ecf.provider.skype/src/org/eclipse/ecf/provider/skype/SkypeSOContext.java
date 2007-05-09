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

package org.eclipse.ecf.provider.skype;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.util.IQueueEnqueue;
import org.eclipse.ecf.provider.generic.SOContainer;
import org.eclipse.ecf.provider.generic.SOContext;

import com.skype.Application;
import com.skype.ApplicationListener;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.Stream;

/**
 * 
 */
public class SkypeSOContext extends SOContext {

	private Application application = null;
	
	private Map streams = new Hashtable();

	private ApplicationListener applicationListener = new ApplicationListener() {
		public void connected(Stream stream) throws SkypeException {
			// TODO
			streams.put(stream.getId(),stream);
		}

		public void disconnected(Stream stream) throws SkypeException {
			// TODO Auto-generated method stub
			streams.remove(stream.getId());
		}
	};

	/**
	 * @param objID
	 * @param homeID
	 * @param cont
	 * @param props
	 * @param queue
	 */
	public SkypeSOContext(ID objID, ID homeID, SOContainer cont, Map props,
			IQueueEnqueue queue) {
		super(objID, homeID, cont, props, queue);
		try {
			application = Skype.addApplication(objID.getName());
			application.addApplicationListener(applicationListener);
		} catch (SkypeException e) {
			application = null;
		}
	}

	protected Stream getStreamForId(String id) throws IOException {
		if (application == null) throw new IOException("application not available");
		return (Stream) streams.get(id);
	}
	
	protected String sendApplicationMessage(Object data) throws Exception {
		return new String(serialize(data));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.generic.SOContext#sendMessage(org.eclipse.ecf.core.identity.ID,
	 *      java.lang.Object)
	 */
	public void sendMessage(ID toContainerID, Object data) throws IOException {
		if (toContainerID == null) throw new IOException("target cannot be null");
		Stream stream = getStreamForId(toContainerID.getName());
		if (stream != null) {
			try {
				stream.write(new String(serialize(data)));
			} catch (SkypeException e) {
				throw new IOException(e.getLocalizedMessage());
			}
		}
	}
	
	protected byte[] serialize(Object o) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(o);
		return bos.toByteArray();
	}

	protected Object deserialize(byte[] bytes) throws Exception {
		ByteArrayInputStream bins = new ByteArrayInputStream(bytes);
		ObjectInputStream oins = new ObjectInputStream(bins);
		return oins.readObject();
	}

}
